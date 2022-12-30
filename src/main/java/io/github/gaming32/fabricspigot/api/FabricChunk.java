package io.github.gaming32.fabricspigot.api;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import io.github.gaming32.fabricspigot.api.block.FabricBlock;
import io.github.gaming32.fabricspigot.ext.ServerWorldExt;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.*;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.storage.EntityChunkDataAccess;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;

public class FabricChunk implements Chunk {
    private static final PalettedContainer<BlockState> EMPTY_BLOCK_IDS = new PalettedContainer<>(
        Block.STATE_IDS, Blocks.AIR.getDefaultState(), PalettedContainer.PaletteProvider.BLOCK_STATE
    );
    private static final byte[] EMPTY_LIGHT = new byte[2048];

    static {
        Arrays.fill(EMPTY_LIGHT, (byte)0xff);
    }

    private WeakReference<WorldChunk> weakChunk;
    private final ServerWorld serverWorld;
    private final int x;
    private final int z;

    public FabricChunk(WorldChunk chunk) {
        weakChunk = new WeakReference<>(chunk);
        serverWorld = (ServerWorld)getHandle().getWorld();
        x = getHandle().getPos().x;
        z = getHandle().getPos().z;
    }

    public FabricChunk(ServerWorld serverWorld, int x, int z) {
        weakChunk = new WeakReference<>(null);
        this.serverWorld = serverWorld;
        this.x = x;
        this.z = z;
    }

    @NotNull
    @Override
    public World getWorld() {
        return ((ServerWorldExt)serverWorld).getBukkitWorld();
    }

    public FabricWorld getFabricWorld() {
        return (FabricWorld)getWorld();
    }

    public WorldChunk getHandle() {
        WorldChunk chunk = weakChunk.get();
        if (chunk == null) {
            chunk = serverWorld.getChunk(x, z);
            weakChunk = new WeakReference<>(chunk);
        }
        return chunk;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getZ() {
        return z;
    }

    @NotNull
    @Override
    public org.bukkit.block.Block getBlock(int x, int y, int z) {
        validateChunkCoordinates(getHandle().getBottomY(), getHandle().getTopY(), x, y, z);
        return new FabricBlock(serverWorld, new BlockPos(this.x << 4 | x, y, this.z << 4 | z));
    }

    @Override
    public boolean isEntitiesLoaded() {
        return getFabricWorld().getHandle().entityManager.isLoaded(ChunkPos.toLong(x, z));
    }

    @NotNull
    @Override
    public Entity[] getEntities() {
        if (!isLoaded()) {
            getWorld().getChunkAt(x, z);
        }

        final ServerEntityManager<net.minecraft.entity.Entity> entityManager = getFabricWorld().getHandle().entityManager;
        final long pair = ChunkPos.toLong(x, z);

        if (entityManager.isLoaded(pair)) {
            throw new NotImplementedYet("ServerEntityManager.getEntities");
        }
        entityManager.readIfFresh(pair);

        final TaskExecutor<Runnable> mailbox = ((EntityChunkDataAccess)entityManager.dataAccess).taskExecutor;
        final BooleanSupplier supplier = () -> {
            if (entityManager.isLoaded(pair)) {
                return true;
            }

            throw new NotImplementedYet("ServerEntityManager.isPending");
        };

        while (!supplier.getAsBoolean()) {
            if (mailbox.getQueueSize() != 0) {
                mailbox.run();
            } else {
                Thread.yield();
                LockSupport.parkNanos("waiting for entity loading", 100000L);
            }
        }

        throw new NotImplementedYet("ServerEntityManager.getEntities");
    }

    @NotNull
    @Override
    public org.bukkit.block.BlockState[] getTileEntities() {
        if (!isLoaded()) {
            getWorld().getChunkAt(x, z);
        }
        int index = 0;
        final WorldChunk chunk = getHandle();

        final org.bukkit.block.BlockState[] entities = new org.bukkit.block.BlockState[chunk.getBlockEntities().size()];

        for (final Object obj : chunk.getBlockEntities().keySet().toArray()) {
            if (!(obj instanceof BlockPos position)) continue;
            entities[index++] = ((ServerWorldExt)serverWorld).getBukkitWorld().getBlockAt(position.getX(), position.getY(), position.getZ()).getState();
        }

        return entities;
    }

    @Override
    public boolean isLoaded() {
        return getWorld().isChunkLoaded(this);
    }

    @Override
    public boolean load() {
        return getWorld().loadChunk(getX(), getZ(), true);
    }

    @Override
    public boolean load(boolean generate) {
        return getWorld().loadChunk(getX(), getZ(), generate);
    }

    @Override
    public boolean unload() {
        return getWorld().unloadChunk(getX(), getZ());
    }

    @Override
    public boolean isSlimeChunk() {
        return ChunkRandom.getSlimeRandom(getX(), getZ(), getWorld().getSeed(), 987234911L).nextInt(10) == 0;
    }

    @Override
    public boolean unload(boolean save) {
        return getWorld().unloadChunk(getX(), getZ(), save);
    }

    @Override
    public boolean isForceLoaded() {
        return getWorld().isChunkForceLoaded(getX(), getZ());
    }

    @Override
    public void setForceLoaded(boolean forced) {
        getWorld().setChunkForceLoaded(getX(), getZ(), forced);
    }

    @Override
    public boolean addPluginChunkTicket(@NotNull Plugin plugin) {
        return getWorld().addPluginChunkTicket(getX(), getZ(), plugin);
    }

    @Override
    public boolean removePluginChunkTicket(@NotNull Plugin plugin) {
        return getWorld().removePluginChunkTicket(getX(), getZ(), plugin);
    }

    @NotNull
    @Override
    public Collection<Plugin> getPluginChunkTickets() {
        return getWorld().getPluginChunkTickets(getX(), getZ());
    }

    @Override
    public long getInhabitedTime() {
        return getHandle().getInhabitedTime();
    }

    @Override
    public void setInhabitedTime(long ticks) {
        Preconditions.checkArgument(ticks >= 0, "ticks cannot be negative");

        getHandle().setInhabitedTime(ticks);
    }

    @Override
    public boolean contains(@NotNull BlockData block) {
        //noinspection ConstantValue
        Preconditions.checkArgument(block != null, "Block cannot be null");

        throw new NotImplementedYet("FabricBlockData");
//        final Predicate<BlockState> nms = Predicates.equalTo(((FabricBlockData)block).getState());
    }

    @NotNull
    @Override
    public ChunkSnapshot getChunkSnapshot() {
        return getChunkSnapshot(true, false, false);
    }

    @NotNull
    @Override
    public ChunkSnapshot getChunkSnapshot(boolean includeMaxblocky, boolean includeBiome, boolean includeBiomeTempRain) {
        final WorldChunk chunk = getHandle();

        final ChunkSection[] sections = chunk.getSectionArray();
        final PalettedContainer[] sectionBlockIDs = new PalettedContainer[sections.length];
        final byte[][] sectionSkyLights = new byte[sections.length][];
        final byte[][] sectionEmitLights = new byte[sections.length][];
        final boolean[] sectionEmpty = new boolean[sections.length];
        @SuppressWarnings("unchecked")
        final ReadableContainer<RegistryEntry<Biome>>[] biome = (includeBiome || includeBiomeTempRain) ? new ReadableContainer[sections.length] : null;

        final Registry<Biome> registry = serverWorld.getRegistryManager().get(RegistryKeys.BIOME);
        final Codec<ReadableContainer<RegistryEntry<Biome>>> biomeCodec = PalettedContainer.createReadableContainerCodec(
            registry.getIndexedEntries(),
            registry.createEntryCodec(),
            PalettedContainer.PaletteProvider.BIOME,
            registry.entryOf(BiomeKeys.PLAINS)
        );

        for (int i = 0; i < sections.length; i++) {
            final NbtCompound data = new NbtCompound();

            //noinspection OptionalGetWithoutIsPresent
            data.put("block_states", ChunkSerializer.CODEC.encodeStart(NbtOps.INSTANCE, sections[i].getBlockStateContainer()).get().left().get());
            //noinspection OptionalGetWithoutIsPresent
            sectionBlockIDs[i] = ChunkSerializer.CODEC.parse(NbtOps.INSTANCE, data.getCompound("block_states")).get().left().get();

            final LightingProvider engine = chunk.getWorld().getLightingProvider();
            final ChunkNibbleArray skyLightArray = engine.get(LightType.SKY).getLightSection(ChunkSectionPos.from(x, i, z));
            if (skyLightArray == null) {
                sectionSkyLights[i] = EMPTY_LIGHT;
            } else {
                sectionSkyLights[i] = Arrays.copyOf(skyLightArray.asByteArray(), 2048);
            }

            final ChunkNibbleArray emitLightArray = engine.get(LightType.BLOCK).getLightSection(ChunkSectionPos.from(x, i, z));
            if (emitLightArray == null) {
                sectionEmitLights[i] = EMPTY_LIGHT;
            } else {
                sectionEmitLights[i] = Arrays.copyOf(emitLightArray.asByteArray(), 2048);
            }

            if (biome != null) {
                //noinspection OptionalGetWithoutIsPresent
                data.put("biomes", biomeCodec.encodeStart(NbtOps.INSTANCE, sections[i].getBiomeContainer()).get().left().get());
                //noinspection OptionalGetWithoutIsPresent
                biome[i] = biomeCodec.parse(NbtOps.INSTANCE, data.get("biomes")).get().left().get();
            }
        }

        Heightmap hmap = null;

        if (includeMaxblocky) {
            hmap = new Heightmap(chunk, Heightmap.Type.MOTION_BLOCKING);
            hmap.setTo(chunk, Heightmap.Type.MOTION_BLOCKING, chunk.heightmaps.get(Heightmap.Type.MOTION_BLOCKING).asLongArray());
        }

        final World world = getWorld();
        throw new NotImplementedYet("FabricChunkSnapshot");
    }

    @NotNull
    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        throw new NotImplementedYet("WorldChunk.getPersistentDatContainer");
//        return getHandle()
    }

    static void validateChunkCoordinates(int minY, int maxY, int x, int y, int z) {
        Preconditions.checkArgument(0 <= x && x <= 15, "x out of range (expected 0-15, got %s)", x);
        Preconditions.checkArgument(minY <= y && y <= maxY, "y out of range (expected %s-%s, got %s)", minY, maxY, y);
        Preconditions.checkArgument(0 <= z && z <= 15, "z out of range (expected 0-15, got %s)", z);
    }
}
