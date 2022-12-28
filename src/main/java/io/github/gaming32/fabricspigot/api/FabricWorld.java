package io.github.gaming32.fabricspigot.api;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.gaming32.fabricspigot.api.inventory.FabricItemStack;
import io.github.gaming32.fabricspigot.util.BukkitTicket;
import io.github.gaming32.fabricspigot.util.Conversion;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import io.github.gaming32.fabricspigot.vanillaimpl.ServerWorldExt;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkTicket;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.collection.SortedArraySet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.ServerWorldProperties;
import org.apache.commons.lang3.Validate;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.SpawnChangeEvent;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.util.Vector;
import org.bukkit.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

public class FabricWorld extends FabricRegionAccessor implements World {
    private static final Random BUKKIT_RANDOM = new Random();

    private final Spigot spigot = new Spigot() {
    };
    private final ServerWorld world;
    private final FabricServer server;
    private final List<BlockPopulator> populators = new ArrayList<>();

    public FabricWorld(ServerWorld world) {
        this.world = world;
        server = world.getServer().getBukkitServer();
    }

    public ServerWorld getHandle() {
        return world;
    }

    @NotNull
    @Override
    public Block getBlockAt(int x, int y, int z) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Block getBlockAt(@NotNull Location location) {
        throw new NotImplementedYet();
    }

    @Override
    public int getHighestBlockYAt(int x, int z) {
        return getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING);
    }

    @Override
    public int getHighestBlockYAt(@NotNull Location location) {
        return getHighestBlockYAt(location.getBlockX(), location.getBlockZ());
    }

    @NotNull
    @Override
    public Block getHighestBlockAt(int x, int z) {
        return getBlockAt(x, getHighestBlockYAt(x, z), z);
    }

    @NotNull
    @Override
    public Block getHighestBlockAt(@NotNull Location location) {
        return getHighestBlockAt(location.getBlockX(), location.getBlockZ());
    }

    @Override
    public int getHighestBlockYAt(int x, int z, @NotNull HeightMap heightMap) {
        return world.getChunk(x >> 4, z >> 4).sampleHeightmap(Conversion.toHeightmapType(heightMap), x, z);
    }

    @Override
    public int getHighestBlockYAt(@NotNull Location location, @NotNull HeightMap heightMap) {
        return getHighestBlockYAt(location.getBlockX(), location.getBlockZ(), heightMap);
    }

    @NotNull
    @Override
    public Block getHighestBlockAt(int x, int z, @NotNull HeightMap heightMap) {
        return getBlockAt(x, getHighestBlockYAt(x, z, heightMap), z);
    }

    @NotNull
    @Override
    public Block getHighestBlockAt(@NotNull Location location, @NotNull HeightMap heightMap) {
        return getHighestBlockAt(location.getBlockX(), location.getBlockZ(), heightMap);
    }

    @NotNull
    @Override
    public Chunk getChunkAt(int x, int z) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Chunk getChunkAt(@NotNull Location location) {
        return getChunkAt(location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    @NotNull
    @Override
    public Chunk getChunkAt(@NotNull Block block) {
        //noinspection ConstantValue
        Preconditions.checkArgument(block != null, "null block");
        return getChunkAt(block.getX() >> 4, block.getZ() >> 4);
    }

    @Override
    public boolean isChunkLoaded(@NotNull Chunk chunk) {
        //noinspection ConstantValue
        Preconditions.checkArgument(chunk != null, "null chunk");
        return isChunkLoaded(chunk.getX(), chunk.getZ());
    }

    @NotNull
    @Override
    public Chunk[] getLoadedChunks() {
        throw new NotImplementedYet();
    }

    @Override
    public void loadChunk(@NotNull Chunk chunk) {
        //noinspection ConstantValue
        Preconditions.checkArgument(chunk != null, "null chunk");
        loadChunk(chunk.getX(), chunk.getZ());
        throw new NotImplementedYet();
    }

    @Override
    public boolean isChunkLoaded(int x, int z) {
        return world.getChunkManager().isChunkLoaded(x, z);
    }

    @Override
    public boolean isChunkGenerated(int x, int z) {
        try {
            return isChunkLoaded(x, z) || world.getChunkManager().threadedAnvilChunkStorage.getUpdatedChunkNbt(new ChunkPos(x, z)).get().isPresent();
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean isChunkInUse(int x, int z) {
        return isChunkLoaded(x, z);
    }

    @Override
    public void loadChunk(int x, int z) {
        loadChunk(x, z, true);
    }

    @Override
    public boolean loadChunk(int x, int z, boolean generate) {
        net.minecraft.world.chunk.Chunk chunk = world.getChunkManager().getChunk(x, z, generate ? ChunkStatus.FULL : ChunkStatus.EMPTY, true);
        if (chunk instanceof ReadOnlyChunk) {
            chunk = world.getChunkManager().getChunk(x, z, ChunkStatus.FULL, true);
        }
        if (chunk instanceof WorldChunk) {
            world.getChunkManager().addTicket(BukkitTicket.PLUGIN, new ChunkPos(x, z), 1, Unit.INSTANCE);
            return true;
        }
        return false;
    }

    @Override
    public boolean unloadChunk(@NotNull Chunk chunk) {
        return unloadChunk(chunk.getX(), chunk.getZ());
    }

    @Override
    public boolean unloadChunk(int x, int z) {
        return unloadChunk(x, z, true);
    }

    @Override
    public boolean unloadChunk(int x, int z, boolean save) {
        if (!isChunkLoaded(x, z)) {
            return true;
        }
        final WorldChunk chunk = world.getChunk(x, z);
        throw new NotImplementedYet();
    }

    @Override
    public boolean unloadChunkRequest(int x, int z) {
        if (isChunkLoaded(x, z)) {
            world.getChunkManager().removeTicket(BukkitTicket.PLUGIN, new ChunkPos(x, z), 1, Unit.INSTANCE);
        }
        return true;
    }

    @Override
    public boolean regenerateChunk(int x, int z) {
        throw new UnsupportedOperationException("Not supported in this Minecraft version or Spigot implementation. If they fix this on base Spigot, let me know.");
    }

    @Override
    public boolean refreshChunk(int x, int z) {
        final ChunkHolder playerChunk = world.getChunkManager().threadedAnvilChunkStorage.chunkHolders.get(ChunkPos.toLong(x, z));
        if (playerChunk == null) return false;
        playerChunk.getTickingFuture().thenAccept(either -> {
            either.left().ifPresent(chunk -> {
                final List<ServerPlayerEntity> playersInRange = playerChunk.playersWatchingChunkProvider.getPlayersWatchingChunk(playerChunk.getPos(), false);
                if (playersInRange.isEmpty()) return;
                final ChunkDataS2CPacket refreshPacket = new ChunkDataS2CPacket(chunk, world.getLightingProvider(), null, null, true);
                for (final ServerPlayerEntity player : playersInRange) {
                    if (player.networkHandler == null) continue;
                    player.networkHandler.sendPacket(refreshPacket);
                }
            });
        });
        return true;
    }

    @Override
    public boolean isChunkForceLoaded(int x, int z) {
        return getHandle().getForcedChunks().contains(ChunkPos.toLong(x, z));
    }

    @Override
    public void setChunkForceLoaded(int x, int z, boolean forced) {
        getHandle().setChunkForced(x, z, forced);
    }

    @NotNull
    @Override
    public Collection<Chunk> getForceLoadedChunks() {
        final Set<Chunk> chunks = new HashSet<>();
        for (final long coord : getHandle().getForcedChunks()) {
            chunks.add(getChunkAt(ChunkPos.getPackedX(coord), ChunkPos.getPackedZ(coord)));
        }
        return Collections.unmodifiableCollection(chunks);
    }

    @Override
    public boolean addPluginChunkTicket(int x, int z, @NotNull Plugin plugin) {
        //noinspection ConstantValue
        Preconditions.checkArgument(plugin != null, "null plugin");
        Preconditions.checkArgument(plugin.isEnabled(), "plugin is not enabled");
        final ChunkTicketManager chunkDistanceManager = world.getChunkManager().threadedAnvilChunkStorage.getTicketManager();
//        if (chunkDistanceManager.addTicketWithLevel(BukkitTicket.PLUGIN_TICKET, new ChunkPos(x, z), 2, plugin)) {
//        }
        throw new NotImplementedYet();
    }

    @Override
    public boolean removePluginChunkTicket(int x, int z, @NotNull Plugin plugin) {
        Preconditions.checkNotNull(plugin, "null plugin");
        final ChunkTicketManager chunkDistanceManager = world.getChunkManager().threadedAnvilChunkStorage.getTicketManager();
//        return chunkDistanceManager.removeTicketWithLevel(BukkitTicket.PLUGIN_TICKET, new ChunkPos(x, z), 2, plugin);
        throw new NotImplementedYet();
    }

    @Override
    public void removePluginChunkTickets(@NotNull Plugin plugin) {
        Preconditions.checkNotNull(plugin, "null plugin");
        final ChunkTicketManager chunkDistanceManager = world.getChunkManager().threadedAnvilChunkStorage.getTicketManager();
//        chunkDistanceManager.removeAllTicketsFor(BukkitTicket.PLUGIN_TICKET, 31, plugin);
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Collection<Plugin> getPluginChunkTickets(int x, int z) {
        final ChunkTicketManager chunkDistanceManager = world.getChunkManager().threadedAnvilChunkStorage.getTicketManager();
        final SortedArraySet<ChunkTicket<?>> tickets = chunkDistanceManager.ticketsByPosition.get(ChunkPos.toLong(x, z));
        if (tickets == null) return Collections.emptyList();

        final ImmutableList.Builder<Plugin> result = ImmutableList.builder();
        for (final ChunkTicket<?> ticket : tickets) {
            if (ticket.getType() == BukkitTicket.PLUGIN_TICKET) {
                result.add((Plugin)ticket.argument);
            }
        }
        return result.build();
    }

    @NotNull
    @Override
    public Map<Plugin, Collection<Chunk>> getPluginChunkTickets() {
        final Map<Plugin, ImmutableList.Builder<Chunk>> result = new HashMap<>();
        final ChunkTicketManager chunkDistanceManager = world.getChunkManager().threadedAnvilChunkStorage.getTicketManager();
        for (final var chunkTickets : chunkDistanceManager.ticketsByPosition.long2ObjectEntrySet()) {
            final long chunkKey = chunkTickets.getLongKey();
            final SortedArraySet<ChunkTicket<?>> tickets = chunkTickets.getValue();
            Chunk chunk = null;
            for (final ChunkTicket<?> ticket : tickets) {
                if (ticket.getType() != BukkitTicket.PLUGIN_TICKET) continue;
                if (chunk == null) {
                    chunk = getChunkAt(ChunkPos.getPackedX(chunkKey), ChunkPos.getPackedZ(chunkKey));
                }
                result.computeIfAbsent((Plugin)ticket.argument, key -> ImmutableList.builder()).add(chunk);
            }
        }
        return result.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> entry.getValue().build()));
    }

    @NotNull
    @Override
    public Item dropItem(@NotNull Location location, @NotNull ItemStack item) {
        return dropItem(location, item, null);
    }

    @NotNull
    @Override
    public Item dropItem(@NotNull Location location, @NotNull ItemStack item, @Nullable Consumer<Item> function) {
        Validate.notNull(item, "Cannot drop a Null item.");
        final ItemEntity entity = new ItemEntity(world, location.getX(), location.getY(), location.getZ(), FabricItemStack.toVanilla(item));
        entity.setPickupDelay(10);
        if (function != null) {
            throw new NotImplementedYet();
        }
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Item dropItemNaturally(@NotNull Location location, @NotNull ItemStack item) {
        return dropItemNaturally(location, item, null);
    }

    @NotNull
    @Override
    public Item dropItemNaturally(@NotNull Location location, @NotNull ItemStack item, @Nullable Consumer<Item> function) {
        final double xs = world.random.nextFloat() * 0.5f + 0.25;
        final double ys = world.random.nextFloat() * 0.5f + 0.25;
        final double zs = world.random.nextFloat() * 0.5f + 0.25;
        location = location.clone();
        location.setX(location.getX() + xs);
        location.setY(location.getY() + ys);
        location.setZ(location.getZ() + zs);
        return dropItem(location, item, function);
    }

    @NotNull
    @Override
    public Arrow spawnArrow(@NotNull Location location, @NotNull Vector direction, float speed, float spread) {
        return spawnArrow(location, direction, speed, spread, Arrow.class);
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public <T extends AbstractArrow> T spawnArrow(@NotNull Location location, @NotNull Vector direction, float speed, float spread, @NotNull Class<T> clazz) {
        Validate.notNull(location, "Can not spawn arrow with a null location");
        Validate.notNull(direction, "Can not spawn arrow with a null velocity");
        Validate.notNull(clazz, "Can not spawn an arrow with no class");

        final PersistentProjectileEntity arrow;
        if (TippedArrow.class.isAssignableFrom(clazz)) {
            arrow = EntityType.ARROW.create(world);
            throw new NotImplementedYet("TippedArrow");
        } else if (SpectralArrow.class.isAssignableFrom(clazz)) {
            arrow = EntityType.SPECTRAL_ARROW.create(world);
        } else if (Trident.class.isAssignableFrom(clazz)) {
            arrow = EntityType.TRIDENT.create(world);
        } else {
            arrow = EntityType.ARROW.create(world);
        }

        assert arrow != null;
        arrow.refreshPositionAndAngles(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        arrow.setVelocity(direction.getX(), direction.getY(), direction.getZ(), speed, spread);
        throw new NotImplementedYet();
    }

    @Override
    public boolean generateTree(@NotNull Location location, @NotNull TreeType type) {
        return generateTree(location, BUKKIT_RANDOM, type);
    }

    @Override
    public boolean generateTree(@NotNull Location loc, @NotNull TreeType type, @NotNull BlockChangeDelegate delegate) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public LightningStrike strikeLightning(@NotNull Location loc) {
        final LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
        assert lightning != null;
        lightning.refreshPositionAfterTeleport(loc.getX(), loc.getY(), loc.getZ());
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public LightningStrike strikeLightningEffect(@NotNull Location loc) {
        final LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
        assert lightning != null;
        lightning.refreshPositionAfterTeleport(loc.getX(), loc.getY(), loc.getZ());
        lightning.setCosmetic(true);
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public List<Player> getPlayers() {
        final List<Player> result = new ArrayList<>(world.getPlayers().size());
        for (final PlayerEntity player : world.getPlayers()) {
            throw new NotImplementedYet();
        }
        return result;
    }

    @NotNull
    @Override
    public Collection<Entity> getNearbyEntities(@NotNull Location location, double x, double y, double z) {
        return getNearbyEntities(location, x, y, z, null);
    }

    @NotNull
    @Override
    public Collection<Entity> getNearbyEntities(@NotNull Location location, double x, double y, double z, @Nullable Predicate<Entity> filter) {
        Validate.notNull(location, "Location is null!");
        Validate.isTrue(equals(location.getWorld()), "Location is from a different world!");
        final BoundingBox aabb = BoundingBox.of(location, x, y, z);
        return getNearbyEntities(aabb, filter);
    }

    @NotNull
    @Override
    public Collection<Entity> getNearbyEntities(@NotNull BoundingBox boundingBox) {
        return getNearbyEntities(boundingBox, null);
    }

    @NotNull
    @Override
    public Collection<Entity> getNearbyEntities(@NotNull BoundingBox boundingBox, @Nullable Predicate<Entity> filter) {
        Validate.notNull(boundingBox, "Bounding box is null!");
        final Box box = new Box(boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ(), boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ());
        final List<net.minecraft.entity.Entity> entities = getHandle().getOtherEntities(null, box, entity -> true);
        final List<Entity> bukkitEntities = new ArrayList<>(entities.size());
        for (final net.minecraft.entity.Entity entity : entities) {
            throw new NotImplementedYet();
        }
        return bukkitEntities;
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceEntities(@NotNull Location start, @NotNull Vector direction, double maxDistance) {
        return rayTraceEntities(start, direction, maxDistance, null);
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceEntities(@NotNull Location start, @NotNull Vector direction, double maxDistance, double raySize) {
        return rayTraceEntities(start, direction, maxDistance, raySize, null);
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceEntities(@NotNull Location start, @NotNull Vector direction, double maxDistance, @Nullable Predicate<Entity> filter) {
        return rayTraceEntities(start, direction, maxDistance, 0, filter);
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceEntities(@NotNull Location start, @NotNull Vector direction, double maxDistance, double raySize, @Nullable Predicate<Entity> filter) {
        Validate.notNull(start, "Start location is null!");
        Validate.isTrue(equals(start.getWorld()), "Start location is from another world!");
        start.checkFinite();

        Validate.notNull(direction, "Direction is null!");
        direction.checkFinite();

        Validate.isTrue(direction.lengthSquared() > 0, "Direction's magnitude is 0!");

        if (maxDistance < 0) return null;

        final Vector startPos = start.toVector();
        final Vector dir = direction.clone().normalize().multiply(maxDistance);
        final BoundingBox aabb = BoundingBox.of(startPos, startPos).expandDirectional(dir).expand(raySize);
        final Collection<Entity> entities = getNearbyEntities(aabb, filter);

        Entity nearestHitEntity = null;
        RayTraceResult nearestHitResult = null;
        double nearestDistanceSq = Double.MAX_VALUE;

        for (final Entity entity : entities) {
            final BoundingBox boundingBox = entity.getBoundingBox().expand(raySize);
            final RayTraceResult hitResult = boundingBox.rayTrace(startPos, direction, maxDistance);

            if (hitResult != null) {
                final double distanceSq = startPos.distanceSquared(hitResult.getHitPosition());

                if (distanceSq < nearestDistanceSq) {
                    nearestHitEntity = entity;
                    nearestHitResult = hitResult;
                    nearestDistanceSq = distanceSq;
                }
            }
        }

        return nearestHitEntity == null ? null : new RayTraceResult(nearestHitResult.getHitPosition(), nearestHitEntity, nearestHitResult.getHitBlockFace());
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceBlocks(@NotNull Location start, @NotNull Vector direction, double maxDistance) {
        return rayTraceBlocks(start, direction, maxDistance, FluidCollisionMode.NEVER, false);
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceBlocks(@NotNull Location start, @NotNull Vector direction, double maxDistance, @NotNull FluidCollisionMode fluidCollisionMode) {
        return rayTraceBlocks(start, direction, maxDistance, fluidCollisionMode, false);
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceBlocks(@NotNull Location start, @NotNull Vector direction, double maxDistance, @NotNull FluidCollisionMode fluidCollisionMode, boolean ignorePassableBlocks) {
        Validate.notNull(start, "Start location is null!");
        Validate.isTrue(this.equals(start.getWorld()), "Start location is from different world!");
        start.checkFinite();

        Validate.notNull(direction, "Direction is null!");
        direction.checkFinite();

        Validate.isTrue(direction.lengthSquared() > 0, "Direction's magnitude is 0!");
        Validate.notNull(fluidCollisionMode, "Fluid collision mode is null!");

        if (maxDistance < 0.0D) return null;

        final Vector dir = direction.clone().normalize().multiply(maxDistance);
        final Vec3d startPos = new Vec3d(start.getX(), start.getY(), start.getZ());
        final Vec3d endPos = new Vec3d(start.getX() + dir.getX(), start.getY() + dir.getY(), start.getZ() + dir.getZ());
        throw new NotImplementedYet();
//        final HitResult nmsHitResult = getHandle().raycast(new RaycastContext(startPos, endPos, ignorePassableBlocks ? RaycastContext.ShapeType.COLLIDER : RaycastContext.ShapeType.OUTLINE))
    }

    @Nullable
    @Override
    public RayTraceResult rayTrace(@NotNull Location start, @NotNull Vector direction, double maxDistance, @NotNull FluidCollisionMode fluidCollisionMode, boolean ignorePassableBlocks, double raySize, @Nullable Predicate<Entity> filter) {
        final RayTraceResult blockHit = rayTraceBlocks(start, direction, maxDistance, fluidCollisionMode, ignorePassableBlocks);
        Vector startVec = null;
        double blockHitDistance = maxDistance;

        if (blockHit != null) {
            startVec = start.toVector();
            blockHitDistance = startVec.distance(blockHit.getHitPosition());
        }

        final RayTraceResult entityHit = rayTraceEntities(start, direction, blockHitDistance, raySize, filter);
        if (blockHit == null) {
            return entityHit;
        }

        if (entityHit == null) {
            return blockHit;
        }

        final double entityHitDistanceSquared = startVec.distanceSquared(entityHit.getHitPosition());
        if (entityHitDistanceSquared < blockHitDistance * blockHitDistance) {
            return entityHit;
        }

        return blockHit;
    }

    @NotNull
    @Override
    public Location getSpawnLocation() {
        final BlockPos spawn = world.getSpawnPos();
        final float yaw = world.getSpawnAngle();
        return new Location(this, spawn.getX(), spawn.getY(), spawn.getZ(), yaw, 0);
    }

    @Override
    public boolean setSpawnLocation(@NotNull Location location) {
        //noinspection ConstantValue
        Preconditions.checkArgument(location != null, "location");
        return equals(location.getWorld()) && setSpawnLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getYaw());
    }

    @Override
    public boolean setSpawnLocation(int x, int y, int z, float angle) {
        try {
            final Location previousLocation = getSpawnLocation();
            world.setSpawnPos(new BlockPos(x, y, z), angle);

            final SpawnChangeEvent event = new SpawnChangeEvent(this, previousLocation);
            server.getPluginManager().callEvent(event);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean setSpawnLocation(int x, int y, int z) {
        return setSpawnLocation(x, y, z, 0);
    }

    @Override
    public long getTime() {
        long time = getFullTime() % 24000;
        if (time < 0) time += 24000;
        return time;
    }

    @Override
    public void setTime(long time) {
        long margin = (time - getFullTime()) % 24000;
        if (margin < 0) margin += 24000;
        setFullTime(getFullTime() + margin);
    }

    @Override
    public long getFullTime() {
        return world.getTimeOfDay();
    }

    @Override
    public void setFullTime(long time) {
        final TimeSkipEvent event = new TimeSkipEvent(this, TimeSkipEvent.SkipReason.CUSTOM, time - world.getTimeOfDay());
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        world.setTimeOfDay(world.getTimeOfDay() + event.getSkipAmount());

        for (final Player player : getPlayers()) {
            throw new NotImplementedYet();
        }
    }

    @Override
    public long getGameTime() {
        return world.getTime();
    }

    @Override
    public boolean hasStorm() {
        return world.getLevelProperties().isRaining();
    }

    @Override
    public void setStorm(boolean hasStorm) {
        world.getLevelProperties().setRaining(hasStorm);
        setWeatherDuration(0);
        setClearWeatherDuration(0);
    }

    @Override
    public int getWeatherDuration() {
        return ((ServerWorldProperties)world.getLevelProperties()).getRainTime();
    }

    @Override
    public void setWeatherDuration(int duration) {
        ((ServerWorldProperties)world.getLevelProperties()).setRainTime(duration);
    }

    @Override
    public boolean isThundering() {
        return world.getLevelProperties().isThundering();
    }

    @Override
    public void setThundering(boolean thundering) {
        ((ServerWorldProperties)world.getLevelProperties()).setThundering(thundering);
        setThunderDuration(0);
        setClearWeatherDuration(0);
    }

    @Override
    public int getThunderDuration() {
        return ((ServerWorldProperties)world.getLevelProperties()).getThunderTime();
    }

    @Override
    public void setThunderDuration(int duration) {
        ((ServerWorldProperties)world.getLevelProperties()).setThunderTime(duration);
    }

    @Override
    public boolean isClearWeather() {
        return !hasStorm() && !isThundering();
    }

    @Override
    public void setClearWeatherDuration(int duration) {
        ((ServerWorldProperties)world.getLevelProperties()).setClearWeatherTime(duration);
    }

    @Override
    public int getClearWeatherDuration() {
        return ((ServerWorldProperties)world.getLevelProperties()).getClearWeatherTime();
    }

    @Override
    public boolean createExplosion(double x, double y, double z, float power) {
        return createExplosion(x, y, z, power, false, true);
    }

    @Override
    public boolean createExplosion(double x, double y, double z, float power, boolean setFire) {
        return createExplosion(x, y, z, power, setFire, true);
    }

    @Override
    public boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks) {
        return createExplosion(x, y, z, power, setFire, breakBlocks, null);
    }

    @Override
    public boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks, @Nullable Entity source) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean createExplosion(@NotNull Location loc, float power) {
        return createExplosion(loc, power, false);
    }

    @Override
    public boolean createExplosion(@NotNull Location loc, float power, boolean setFire) {
        return createExplosion(loc, power, setFire, true);
    }

    @Override
    public boolean createExplosion(@NotNull Location loc, float power, boolean setFire, boolean breakBlocks) {
        return createExplosion(loc, power, setFire, breakBlocks, null);
    }

    @Override
    public boolean createExplosion(@NotNull Location loc, float power, boolean setFire, boolean breakBlocks, @Nullable Entity source) {
        //noinspection ConstantValue
        Preconditions.checkArgument(loc != null, "Location is null");
        Preconditions.checkArgument(equals(loc.getWorld()), "Location not in world");

        return createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, setFire, breakBlocks, source);
    }

    @Override
    public boolean getPVP() {
        throw new NotImplementedYet();
    }

    @Override
    public void setPVP(boolean pvp) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public ChunkGenerator getGenerator() {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public BiomeProvider getBiomeProvider() {
        throw new NotImplementedYet();
    }

    @Override
    public void save() {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public List<BlockPopulator> getPopulators() {
        return populators;
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public FallingBlock spawnFallingBlock(@NotNull Location location, @NotNull MaterialData data) throws IllegalArgumentException {
        Validate.notNull(data, "MaterialData cannot be null");
        return spawnFallingBlock(location, data.getItemType(), data.getData());
    }

    @NotNull
    @Override
    public FallingBlock spawnFallingBlock(@NotNull Location location, @NotNull BlockData data) throws IllegalArgumentException {
        Validate.notNull(location, "Location cannot be null");
        Validate.notNull(data, "BlockData cannot be null");
        throw new NotImplementedYet();
//        final FallingBlockEntity entity = FallingBlockEntity.spawnFromBlock(world, new BlockPos(location.getX(), location.getY(), location.getZ()))
    }

    @NotNull
    @Override
    public FallingBlock spawnFallingBlock(@NotNull Location location, @NotNull Material material, byte data) throws IllegalArgumentException {
        Validate.notNull(location, "Location cannot be null");
        Validate.notNull(material, "Material cannot be null");
        Validate.isTrue(material.isBlock(), "Material must be a block");

        throw new NotImplementedYet();
//        final FallingBlockEntity entity = FallingBlockEntity.spawnFromBlock(world, new BlockPos(location.getX(), location.getY(), location.getZ()), FabricUnsafeValues.getBlock(material).getDefaultState(), SpawnReason);
    }

    @Override
    public void playEffect(@NotNull Location location, @NotNull Effect effect, int data) {
        playEffect(location, effect, data, 64);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void playEffect(@NotNull Location location, @NotNull Effect effect, int data, int radius) {
        Validate.notNull(location, "Location cannot be null");
        Validate.notNull(effect, "Effect cannot be null");
        Validate.notNull(location.getWorld(), "World cannot be null");
        final int packetData = effect.getId();
        final WorldEventS2CPacket packet = new WorldEventS2CPacket(packetData, new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ()), data, false);
        int distance;
        radius *= radius;

        for (Player player : getPlayers()) {
            throw new NotImplementedYet();
        }
    }

    @Override
    public <T> void playEffect(@NotNull Location location, @NotNull Effect effect, @Nullable T data) {
        playEffect(location, effect, data, 64);
    }

    @Override
    public <T> void playEffect(@NotNull Location location, @NotNull Effect effect, @Nullable T data, int radius) {
        if (data != null) {
            Validate.isTrue(effect.getData() != null && effect.getData().isAssignableFrom(data.getClass()), "Wrong kind of data for this effect!");
        } else {
            Validate.isTrue(effect.getData() == null || effect == Effect.ELECTRIC_SPARK, "Wrong kind of data for this effect!");
        }

        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public ChunkSnapshot getEmptyChunkSnapshot(int x, int z, boolean includeBiome, boolean includeBiomeTemp) {
        throw new NotImplementedYet();
    }

    @Override
    public void setSpawnFlags(boolean allowMonsters, boolean allowAnimals) {
        world.setMobSpawnOptions(allowMonsters, allowAnimals);
    }

    @Override
    public boolean getAllowAnimals() {
        return world.getChunkManager().spawnAnimals;
    }

    @Override
    public boolean getAllowMonsters() {
        return world.getChunkManager().spawnMonsters;
    }

    @NotNull
    @Override
    public Biome getBiome(int x, int z) {
        return getBiome(x, 0, z);
    }

    @Override
    public void setBiome(int x, int z, @NotNull Biome bio) {
        for (int y = getMinHeight(); y < getMaxHeight(); y++) {
            setBiome(x, y, z, bio);
        }
    }

    @Override
    public double getTemperature(int x, int z) {
        return getTemperature(x, 0, z);
    }

    @Override
    public double getTemperature(int x, int y, int z) {
        final BlockPos pos = new BlockPos(x, y, z);
        return world.getBiomeForNoiseGen(x >> 2, y >> 2, z >> 2).value().getTemperature(pos);
    }

    @Override
    public double getHumidity(int x, int z) {
        return getHumidity(x, 0, z);
    }

    @Override
    public double getHumidity(int x, int y, int z) {
        return world.getBiomeForNoiseGen(x >> 2, y >> 2, z >> 2).value().getDownfall();
    }

    @Override
    public int getLogicalHeight() {
        return world.getLogicalHeight();
    }

    @Override
    public boolean isNatural() {
        return world.getDimension().natural();
    }

    @Override
    public boolean isBedWorks() {
        return world.getDimension().bedWorks();
    }

    @Override
    public boolean hasSkyLight() {
        return world.getDimension().hasSkyLight();
    }

    @Override
    public boolean hasCeiling() {
        return world.getDimension().hasCeiling();
    }

    @Override
    public boolean isPiglinSafe() {
        return world.getDimension().piglinSafe();
    }

    @Override
    public boolean isRespawnAnchorWorks() {
        return world.getDimension().respawnAnchorWorks();
    }

    @Override
    public boolean hasRaids() {
        return world.getDimension().hasRaids();
    }

    @Override
    public boolean isUltraWarm() {
        return world.getDimension().ultrawarm();
    }

    @Override
    public int getSeaLevel() {
        return world.getSeaLevel();
    }

    @Override
    public boolean getKeepSpawnInMemory() {
        throw new NotImplementedYet();
    }

    @Override
    public void setKeepSpawnInMemory(boolean keepLoaded) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isAutoSave() {
        return !world.savingDisabled;
    }

    @Override
    public void setAutoSave(boolean value) {
        world.savingDisabled = value;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setDifficulty(@NotNull Difficulty difficulty) {
        ((SaveProperties)getHandle().getLevelProperties()).setDifficulty(net.minecraft.world.Difficulty.byId(difficulty.getValue()));
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public Difficulty getDifficulty() {
        //noinspection DataFlowIssue
        return Difficulty.getByValue(world.getDifficulty().getId());
    }

    @NotNull
    @Override
    public File getWorldFolder() {
        return ((ServerWorldExt)world).getSession().getDirectory(WorldSavePath.ROOT).toFile();
    }

    @Nullable
    @Override
    public WorldType getWorldType() {
        return world.isFlat() ? WorldType.FLAT : WorldType.NORMAL;
    }

    @Override
    public boolean canGenerateStructures() {
        return ((SaveProperties)world.getLevelProperties()).getGeneratorOptions().shouldGenerateStructures();
    }

    @Override
    public boolean isHardcore() {
        return world.getLevelProperties().isHardcore();
    }

    @Override
    public void setHardcore(boolean hardcore) {
        ((LevelProperties)world.getLevelProperties()).levelInfo.hardcore = true;
    }

    @Override
    @Deprecated
    public long getTicksPerAnimalSpawns() {
        return getTicksPerSpawns(SpawnCategory.ANIMAL);
    }

    @Override
    @Deprecated
    public void setTicksPerAnimalSpawns(int ticksPerAnimalSpawns) {
        setTicksPerSpawns(SpawnCategory.ANIMAL, ticksPerAnimalSpawns);
    }

    @Override
    @Deprecated
    public long getTicksPerMonsterSpawns() {
        return getTicksPerSpawns(SpawnCategory.MONSTER);
    }

    @Override
    @Deprecated
    public void setTicksPerMonsterSpawns(int ticksPerMonsterSpawns) {
        setTicksPerSpawns(SpawnCategory.MONSTER, ticksPerMonsterSpawns);
    }

    @Override
    @Deprecated
    public long getTicksPerWaterSpawns() {
        return getTicksPerSpawns(SpawnCategory.WATER_ANIMAL);
    }

    @Override
    @Deprecated
    public void setTicksPerWaterSpawns(int ticksPerWaterSpawns) {
        setTicksPerSpawns(SpawnCategory.WATER_ANIMAL, ticksPerWaterSpawns);
    }

    @Override
    @Deprecated
    public long getTicksPerWaterAmbientSpawns() {
        return getTicksPerSpawns(SpawnCategory.WATER_AMBIENT);
    }

    @Override
    @Deprecated
    public void setTicksPerWaterAmbientSpawns(int ticksPerWaterAmbientSpawns) {
        setTicksPerSpawns(SpawnCategory.WATER_AMBIENT, ticksPerWaterAmbientSpawns);
    }

    @Override
    @Deprecated
    public long getTicksPerWaterUndergroundCreatureSpawns() {
        return getTicksPerSpawns(SpawnCategory.WATER_UNDERGROUND_CREATURE);
    }

    @Override
    @Deprecated
    public void setTicksPerWaterUndergroundCreatureSpawns(int ticksPerWaterUndergroundCreatureSpawns) {
        setTicksPerSpawns(SpawnCategory.WATER_UNDERGROUND_CREATURE, ticksPerWaterUndergroundCreatureSpawns);
    }

    @Override
    @Deprecated
    public long getTicksPerAmbientSpawns() {
        return getTicksPerSpawns(SpawnCategory.AMBIENT);
    }

    @Override
    @Deprecated
    public void setTicksPerAmbientSpawns(int ticksPerAmbientSpawns) {
        setTicksPerSpawns(SpawnCategory.AMBIENT, ticksPerAmbientSpawns);
    }

    @Override
    public void setTicksPerSpawns(@NotNull SpawnCategory spawnCategory, int ticksPerCategorySpawn) {
        throw new NotImplementedYet();
    }

    @Override
    public long getTicksPerSpawns(@NotNull SpawnCategory spawnCategory) {
        throw new NotImplementedYet();
    }

    @Override
    @Deprecated
    public int getMonsterSpawnLimit() {
        return getSpawnLimit(SpawnCategory.MONSTER);
    }

    @Override
    @Deprecated
    public void setMonsterSpawnLimit(int limit) {
        setSpawnLimit(SpawnCategory.MONSTER, limit);
    }

    @Override
    @Deprecated
    public int getAnimalSpawnLimit() {
        return getSpawnLimit(SpawnCategory.ANIMAL);
    }

    @Override
    @Deprecated
    public void setAnimalSpawnLimit(int limit) {
        setSpawnLimit(SpawnCategory.ANIMAL, limit);
    }

    @Override
    @Deprecated
    public int getWaterAnimalSpawnLimit() {
        return getSpawnLimit(SpawnCategory.WATER_ANIMAL);
    }

    @Override
    @Deprecated
    public void setWaterAnimalSpawnLimit(int limit) {
        setSpawnLimit(SpawnCategory.WATER_ANIMAL, limit);
    }

    @Override
    @Deprecated
    public int getWaterAmbientSpawnLimit() {
        return getSpawnLimit(SpawnCategory.WATER_AMBIENT);
    }

    @Override
    @Deprecated
    public void setWaterAmbientSpawnLimit(int limit) {
        setSpawnLimit(SpawnCategory.WATER_AMBIENT, limit);
    }

    @Override
    @Deprecated
    public int getWaterUndergroundCreatureSpawnLimit() {
        return getSpawnLimit(SpawnCategory.WATER_UNDERGROUND_CREATURE);
    }

    @Override
    @Deprecated
    public void setWaterUndergroundCreatureSpawnLimit(int limit) {
        setSpawnLimit(SpawnCategory.WATER_UNDERGROUND_CREATURE, limit);
    }

    @Override
    @Deprecated
    public int getAmbientSpawnLimit() {
        return getSpawnLimit(SpawnCategory.AMBIENT);
    }

    @Override
    @Deprecated
    public void setAmbientSpawnLimit(int limit) {
        setSpawnLimit(SpawnCategory.AMBIENT, limit);
    }

    @Override
    public int getSpawnLimit(@NotNull SpawnCategory spawnCategory) {
        throw new NotImplementedYet();
    }

    @Override
    public void setSpawnLimit(@NotNull SpawnCategory spawnCategory, int limit) {
        throw new NotImplementedYet();
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull Sound sound, float volume, float pitch) {
        playSound(location, sound, SoundCategory.MASTER, volume, pitch);
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull String sound, float volume, float pitch) {
        playSound(location, sound, SoundCategory.MASTER, volume, pitch);
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull Sound sound, @NotNull SoundCategory category, float volume, float pitch) {
        //noinspection ConstantValue
        if (location == null || sound == null || category == null) return;

        final double x = location.getX();
        final double y = location.getY();
        final double z = location.getZ();

        throw new NotImplementedYet();
//        getHandle().playSound(null, x, y, z, CraftSound.getSoundEffect(sound), SoundCategory.valueOf(category.name()), volume, pitch);
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull String sound, @NotNull SoundCategory category, float volume, float pitch) {
        //noinspection ConstantValue
        if (location == null || sound == null || category == null) return;

        final double x = location.getX();
        final double y = location.getY();
        final double z = location.getZ();

        final PlaySoundS2CPacket packet = new PlaySoundS2CPacket(RegistryEntry.of(SoundEvent.of(new Identifier(sound))), net.minecraft.sound.SoundCategory.valueOf(category.name()), x, y, z, volume, pitch, getHandle().getRandom().nextLong());
        world.getServer().getPlayerManager().sendToAround(null, x, y, z, volume > 1.0F ? 16.0F * volume : 16.0D, world.getRegistryKey(), packet);
    }

    @Override
    public void playSound(@NotNull Entity entity, @NotNull Sound sound, float volume, float pitch) {
        playSound(entity, sound, SoundCategory.MASTER, volume, pitch);
    }

    @Override
    public void playSound(@NotNull Entity entity, @NotNull Sound sound, @NotNull SoundCategory category, float volume, float pitch) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public String[] getGameRules() {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public String getGameRuleValue(@Nullable String rule) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean setGameRuleValue(@NotNull String rule, @NotNull String value) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isGameRule(@NotNull String rule) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public <T> T getGameRuleValue(@NotNull GameRule<T> rule) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public <T> T getGameRuleDefault(@NotNull GameRule<T> rule) {
        throw new NotImplementedYet();
    }

    @Override
    public <T> boolean setGameRule(@NotNull GameRule<T> rule, @NotNull T newValue) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public WorldBorder getWorldBorder() {
        throw new NotImplementedYet();
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count) {
        throw new NotImplementedYet();
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count, @Nullable T data) {
        throw new NotImplementedYet();
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, double x, double y, double z, int count) {
        throw new NotImplementedYet();
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, double x, double y, double z, int count, @Nullable T data) {
        throw new NotImplementedYet();
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count, double offsetX, double offsetY, double offsetZ) {
        throw new NotImplementedYet();
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count, double offsetX, double offsetY, double offsetZ, @Nullable T data) {
        throw new NotImplementedYet();
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ) {
        throw new NotImplementedYet();
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        throw new NotImplementedYet();
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, @Nullable T data) {
        throw new NotImplementedYet();
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable T data) {
        throw new NotImplementedYet();
    }

    @Override
    public void spawnParticle(@NotNull Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        throw new NotImplementedYet();
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable T data) {
        throw new NotImplementedYet();
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable T data, boolean force) {
        throw new NotImplementedYet();
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, @Nullable T data, boolean force) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public StructureSearchResult locateNearestStructure(@NotNull Location origin, @NotNull Structure structure, int radius, boolean findUnexplored) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public StructureSearchResult locateNearestStructure(@NotNull Location origin, @NotNull StructureType structureType, int radius, boolean findUnexplored) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    @SuppressWarnings("deprecation")
    public Location locateNearestStructure(@NotNull Location origin, @NotNull org.bukkit.StructureType structureType, int radius, boolean findUnexplored) {
        throw new NotImplementedYet();
    }

    @Override
    public int getViewDistance() {
        throw new NotImplementedYet();
    }

    @Override
    public int getSimulationDistance() {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Spigot spigot() {
        return spigot;
    }

    @Nullable
    @Override
    public Raid locateNearestRaid(@NotNull Location location, int radius) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public List<Raid> getRaids() {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public DragonBattle getEnderDragonBattle() {
        throw new NotImplementedYet();
    }

    @Override
    public void sendPluginMessage(@NotNull Plugin source, @NotNull String channel, byte @NotNull [] message) {
        StandardMessenger.validatePluginMessage(server.getMessenger(), source, channel, message);
        for (final Player player : getPlayers()) {
            player.sendPluginMessage(source, channel, message);
        }
    }

    @Override
    public void setBiome(int x, int y, int z, RegistryEntry<net.minecraft.world.biome.Biome> biomeBase) {
        throw new NotImplementedYet();
    }

    @Override
    public Iterable<net.minecraft.entity.Entity> getNMSEntities() {
        throw new NotImplementedYet();
    }

    @Override
    public void addEntityToWorld(net.minecraft.entity.Entity entity, CreatureSpawnEvent.SpawnReason reason) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Entity> Collection<T> getEntitiesByClass(@NotNull Class<T>... classes) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public String getName() {
        return world.getRegistryKey().getValue().toString();
    }

    @NotNull
    @Override
    public UUID getUID() {
        return UUID.nameUUIDFromBytes(getName().getBytes(StandardCharsets.UTF_8));
    }

    @NotNull
    @Override
    public Environment getEnvironment() {
        throw new NotImplementedYet();
    }

    @Override
    public long getSeed() {
        return world.getSeed();
    }

    @Override
    public int getMinHeight() {
        return world.getBottomY();
    }

    @Override
    public int getMaxHeight() {
        return world.getTopY();
    }

    @NotNull
    @Override
    public Set<String> getListeningPluginChannels() {
        throw new NotImplementedYet();
    }

    @Override
    public void setMetadata(@NotNull String metadataKey, @NotNull MetadataValue newMetadataValue) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public List<MetadataValue> getMetadata(@NotNull String metadataKey) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean hasMetadata(@NotNull String metadataKey) {
        throw new NotImplementedYet();
    }

    @Override
    public void removeMetadata(@NotNull String metadataKey, @NotNull Plugin owningPlugin) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return Conversion.toNamespacedKey(world.getRegistryKey().getValue());
    }
}
