package io.github.gaming32.fabricspigot.api.entity;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.GameProfile;
import io.github.gaming32.fabricspigot.api.FabricOfflinePlayer;
import io.github.gaming32.fabricspigot.api.FabricServer;
import io.github.gaming32.fabricspigot.api.FabricSound;
import io.github.gaming32.fabricspigot.api.FabricWorld;
import io.github.gaming32.fabricspigot.api.conversations.ConversationTracker;
import io.github.gaming32.fabricspigot.ext.ServerWorldExt;
import io.github.gaming32.fabricspigot.util.ChatMessageConversion;
import io.github.gaming32.fabricspigot.util.Conversion;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.Validate;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.*;

@DelegateDeserialization(FabricOfflinePlayer.class)
public class FabricPlayer extends FabricHumanEntity implements Player {
    private final Player.Spigot spigot = new Player.Spigot() {
        @Override
        public void sendMessage(@NotNull BaseComponent component) {
          sendMessage(new BaseComponent[] {component});
        }

        @Override
        public void sendMessage(BaseComponent... components) {
           sendMessage(ChatMessageType.SYSTEM, components);
        }

        @Override
        public void sendMessage(UUID sender, @NotNull BaseComponent component) {
            sendMessage(ChatMessageType.CHAT, sender, component);
        }

        @Override
        public void sendMessage(UUID sender, BaseComponent... components) {
            sendMessage(ChatMessageType.CHAT, sender, components);
        }

        @Override
        public void sendMessage(@NotNull ChatMessageType position, @NotNull BaseComponent component) {
            sendMessage(position, new BaseComponent[] {component});
        }

        @Override
        public void sendMessage(@NotNull ChatMessageType position, BaseComponent... components) {
            sendMessage(position, null, components);
        }

        @Override
        public void sendMessage(@NotNull ChatMessageType position, UUID sender, @NotNull BaseComponent component) {
            sendMessage( position, sender, new BaseComponent[] {component} );
        }

        @Override
        public void sendMessage(@NotNull ChatMessageType position, @Nullable UUID sender, @NotNull BaseComponent... components) {
            if (getHandle().networkHandler == null) return;
            getHandle().networkHandler.sendPacket(new GameMessageS2CPacket(
                Conversion.toText(components), position == ChatMessageType.ACTION_BAR
            ));
        }
    };
    private final ConversationTracker conversationTracker = new ConversationTracker();
    private int hash;

    public FabricPlayer(FabricServer server, ServerPlayerEntity entity) {
        super(server, entity);
    }

    @Override
    public ServerPlayerEntity getHandle() {
        return (ServerPlayerEntity)entity;
    }

    @Override
    public void setRotation(float yaw, float pitch) {
        throw new UnsupportedOperationException("Cannot set rotation of players. Consider teleporting instead.");
    }

    @Override
    public boolean teleport(@NotNull Location location, @NotNull PlayerTeleportEvent.TeleportCause cause) {
        //noinspection ConstantValue
        Preconditions.checkArgument(location != null, "location");
        Preconditions.checkArgument(location.getWorld() != null, "location.world");
        location.checkFinite();

        final ServerPlayerEntity entity = getHandle();

        if (getHealth() == 0 || entity.isRemoved()) {
            return false;
        }

        if (entity.networkHandler == null) {
            return false;
        }

        if (entity.hasPassengers()) {
            return false;
        }

        Location from = getLocation();
        Location to = location;
        final PlayerTeleportEvent event = new PlayerTeleportEvent(this, from, to, cause);
        server.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        entity.stopRiding();

        if (isSleeping()) {
            wakeup(false);
        }

        from = event.getFrom();
        to = event.getTo();
        final ServerWorld fromWorld = ((FabricWorld)Objects.requireNonNull(from.getWorld())).getHandle();
        assert to != null;
        final ServerWorld toWorld = ((FabricWorld)Objects.requireNonNull(to.getWorld())).getHandle();

        if (getHandle().currentScreenHandler != getHandle().playerScreenHandler) {
            getHandle().closeHandledScreen();
        }

        if (fromWorld == toWorld) {
            entity.networkHandler.requestTeleport(to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch()); // TODO: Teleport cause
        } else {
            throw new NotImplementedYet("World.respawn?");
        }
        return true;
    }

    @NotNull
    @Override
    public EntityType getType() {
        return EntityType.PLAYER;
    }

    @NotNull
    @Override
    public Player.Spigot spigot() {
        return spigot;
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

    @Override
    public void sendMessage(@NotNull String message) {
        if (!conversationTracker.isConversingModaly()) {
            sendRawMessage(message);
        }
    }

    @Override
    public void sendMessage(@NotNull String... messages) {
        for (final String message : messages) {
            sendMessage(message);
        }
    }

    @Override
    public void sendMessage(@Nullable UUID sender, @NotNull String message) {
        if (!conversationTracker.isConversingModaly()) {
            sendRawMessage(sender, message);
        }
    }

    @Override
    public void sendMessage(@Nullable UUID sender, @NotNull String... messages) {
        for (final String message : messages) {
            sendMessage(sender, message);
        }
    }

    public GameProfile getProfile() {
        return getHandle().getGameProfile();
    }

    @Override
    public boolean isOp() {
        return server.getHandle().getPlayerManager().isOperator(getProfile());
    }

    @Override
    public void setOp(boolean value) {
        if (value == isOp()) return;

        if (value) {
            server.getHandle().getPlayerManager().addToOperators(getProfile());
        } else {
            server.getHandle().getPlayerManager().removeFromOperators(getProfile());
        }

        perm.recalculatePermissions();
    }

    @Override
    public double getEyeHeight(boolean ignorePose) {
        if (ignorePose) {
            return 1.62;
        }
        return getEyeHeight();
    }

    @Override
    public int getNoDamageTicks() {
        if (getHandle().joinInvulnerabilityTicks > 0) {
            return Math.max(getHandle().joinInvulnerabilityTicks, getHandle().timeUntilRegen);
        } else {
            return getHandle().timeUntilRegen;
        }
    }

    @Override
    public void setNoDamageTicks(int ticks) {
        super.setNoDamageTicks(ticks);
        getHandle().joinInvulnerabilityTicks = ticks;
    }

    @Override
    public void setMaxHealth(double health) {
        super.setMaxHealth(health);
        getHandle().markHealthDirty();
    }

    @Override
    public boolean setWindowProperty(@NotNull InventoryView.Property prop, int value) {
        final ScreenHandler container = getHandle().currentScreenHandler;
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Location getBedLocation() {
        Preconditions.checkState(isSleeping(), "Not sleeping");

        final BlockPos bed = getHandle().getSpawnPointPosition();
        assert bed != null;
        return new Location(getWorld(), bed.getX(), bed.getY(), bed.getZ());
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public GameMode getGameMode() {
        //noinspection DataFlowIssue
        return GameMode.getByValue(getHandle().interactionManager.getGameMode().getId());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setGameMode(@NotNull GameMode mode) {
        if (getHandle().networkHandler == null) return;

        //noinspection ConstantValue
        if (mode == null) {
            throw new IllegalArgumentException("Mode cannot be null");
        }

        getHandle().changeGameMode(net.minecraft.world.GameMode.byId(mode.getValue()));
    }

    @Override
    public boolean hasDiscoveredRecipe(@NotNull NamespacedKey recipe) {
        //noinspection ConstantValue
        Preconditions.checkArgument(recipe != null, "recipe cannot be null");
        return getHandle().getRecipeBook().contains(Conversion.toIdentifier(recipe));
    }

    @Override
    public @NotNull Set<NamespacedKey> getDiscoveredRecipes() {
        final ImmutableSet.Builder<NamespacedKey> bukkitRecipeKeys = ImmutableSet.builder();
        getHandle().getRecipeBook().recipes.forEach(key -> bukkitRecipeKeys.add(Conversion.toNamespacedKey(key)));
        return bukkitRecipeKeys.build();
    }

    @NotNull
    @Override
    public String getDisplayName() {
        throw new NotImplementedYet("displayName");
    }

    @Override
    public void setDisplayName(@Nullable String name) {
        throw new NotImplementedYet("displayName");
    }

    @NotNull
    @Override
    public String getPlayerListName() {
        throw new NotImplementedYet("listName");
    }

    @Override
    public void setPlayerListName(@Nullable String name) {
        if (name == null) {
            name = getName();
        }
        throw new NotImplementedYet("listName");
    }

    @Nullable
    @Override
    public String getPlayerListHeader() {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public String getPlayerListFooter() {
        throw new NotImplementedYet();
    }

    @Override
    public void setPlayerListHeader(@Nullable String header) {
        throw new NotImplementedYet();
    }

    @Override
    public void setPlayerListFooter(@Nullable String footer) {
        throw new NotImplementedYet();
    }

    @Override
    public void setPlayerListHeaderFooter(@Nullable String header, @Nullable String footer) {
        throw new NotImplementedYet();
    }

    @Override
    public void setCompassTarget(@NotNull Location loc) {
        if (getHandle().networkHandler == null) return;

        getHandle().networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), loc.getYaw()));
    }

    @NotNull
    @Override
    public Location getCompassTarget() {
        throw new NotImplementedYet("compassTarget");
    }

    @Nullable
    @Override
    public InetSocketAddress getAddress() {
        if (getHandle().networkHandler == null) return null;
        if (getHandle().networkHandler.connection.getAddress() instanceof InetSocketAddress inetAddr) {
            return inetAddr;
        }
        return null;
    }

    @Override
    public void sendRawMessage(@NotNull String message) {
        if (getHandle().networkHandler == null) return;

        for (final Text component : ChatMessageConversion.fromString(message)) {
            getHandle().sendMessage(component);
        }
    }

    @Override
    public void sendRawMessage(@Nullable UUID sender, @NotNull String message) {
        if (getHandle().networkHandler == null) return;

        for (final Text component : ChatMessageConversion.fromString(message)) {
            getHandle().sendMessage(component);
        }
    }

    @Override
    public void kickPlayer(@Nullable String message) {
        if (getHandle().networkHandler == null) return;

        throw new NotImplementedYet("disconnect");
    }

    @Override
    public void chat(@NotNull String msg) {
        if (getHandle().networkHandler == null) return;

        throw new NotImplementedYet("chat");
    }

    @Override
    public boolean performCommand(@NotNull String command) {
        return server.dispatchCommand(this, command);
    }

    @Override
    public boolean isSneaking() {
        return getHandle().isSneaking();
    }

    @Override
    public void setSneaking(boolean sneak) {
        getHandle().setSneaking(sneak);
    }

    @Override
    public boolean isSprinting() {
        return getHandle().isSprinting();
    }

    @Override
    public void setSprinting(boolean sprinting) {
        getHandle().setSprinting(sprinting);
    }

    @Override
    public void saveData() {
        server.getHandle().getSaveHandler().savePlayerData(getHandle());
    }

    @Override
    public void loadData() {
        server.getHandle().getSaveHandler().loadPlayerData(getHandle());
    }

    @Override
    public void setSleepingIgnored(boolean isSleeping) {
        throw new NotImplementedYet("fauxSleeping");
    }

    @Override
    public boolean isSleepingIgnored() {
        throw new NotImplementedYet("fauxSleeping");
    }

    @Nullable
    @Override
    public Location getBedSpawnLocation() {
        final ServerWorld world = getHandle().server.getWorld(getHandle().getSpawnPointDimension());
        final BlockPos bed = getHandle().getSpawnPointPosition();

        if (world != null && bed != null) {
            final Optional<Vec3d> spawnLoc = PlayerEntity.findRespawnPosition(world, bed, getHandle().getSpawnAngle(), getHandle().isSpawnForced(), true);
            if (spawnLoc.isPresent()) {
                final Vec3d vec = spawnLoc.get();
                return new Location(((ServerWorldExt)world).getBukkitWorld(), vec.x, vec.y, vec.z, getHandle().getSpawnAngle(), 0);
            }
        }
        return null;
    }

    @Override
    public void setBedSpawnLocation(@Nullable Location location) {
        setBedSpawnLocation(location, false);
    }

    @Override
    public void setBedSpawnLocation(@Nullable Location location, boolean force) {
        if (location == null) {
            getHandle().setSpawnPoint(null, null, 0f, force, false);
        } else {
            //noinspection DataFlowIssue
            getHandle().setSpawnPoint(
                ((FabricWorld)location.getWorld()).getHandle().getRegistryKey(),
                new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ()),
                location.getYaw(), force, false
            );
        }
    }

    @Override
    public void playNote(@NotNull Location loc, byte instrument, byte note) {
        if (getHandle().networkHandler == null) return;
        playNote0(loc, note, switch (instrument) {
            case 0 -> "harp";
            case 1 -> "basedrum";
            case 2 -> "snare";
            case 3 -> "hat";
            case 4 -> "bass";
            case 5 -> "flute";
            case 6 -> "bell";
            case 7 -> "guitar";
            case 8 -> "chime";
            case 9 -> "xylophone";
            default -> null;
        });
    }

    @Override
    @SuppressWarnings("deprecation")
    public void playNote(@NotNull Location loc, @NotNull Instrument instrument, @NotNull Note note) {
        if (getHandle().networkHandler == null) return;
        playNote0(loc, note.getId(), switch (instrument.ordinal()) {
            case 0 -> "harp";
            case 1 -> "basedrum";
            case 2 -> "snare";
            case 3 -> "hat";
            case 4 -> "bass";
            case 5 -> "flute";
            case 6 -> "bell";
            case 7 -> "guitar";
            case 8 -> "chime";
            case 9, 16 -> "xylophone";
            case 10 -> "iron_xylophone";
            case 11 -> "cow_bell";
            case 12 -> "didgeridoo";
            case 13 -> "bit";
            case 14 -> "banjo";
            case 15 -> "pling";
            default -> null;
        });
    }

    private void playNote0(Location location, int note, String instrumentName) {
        final float f = (float)Math.pow(2, (note - 12.0) / 12.0);
        throw new NotImplementedYet("getSoundEffect");
//        getHandle().networkHandler.sendPacket(new PlaySoundS2CPacket(
//            Registries.SOUND_EVENT.getEntry()
//        ));
    }

    @Override
    public void playSound(@NotNull Entity entity, @NotNull Sound sound, float volume, float pitch) {
        throw new NotImplementedYet();
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull Sound sound, @NotNull SoundCategory category, float volume, float pitch) {
        //noinspection ConstantValue
        if (location == null || sound == null || category == null || getHandle().networkHandler == null) return;

        final PlaySoundS2CPacket packet = new PlaySoundS2CPacket(
            Registries.SOUND_EVENT.getEntry(FabricSound.getSoundEffect(sound)),
            net.minecraft.sound.SoundCategory.valueOf(category.name()),
            location.getX(), location.getY(), location.getZ(),
            volume, pitch, getHandle().getRandom().nextLong()
        );
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull String sound, float volume, float pitch) {
        throw new NotImplementedYet();
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull Sound sound, float volume, float pitch) {
        playSound(location, sound, SoundCategory.MASTER, volume, pitch);
    }

    @Override
    public void playSound(@NotNull Entity entity, @NotNull Sound sound, @NotNull SoundCategory category, float volume, float pitch) {
        throw new NotImplementedYet();
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull String sound, @NotNull SoundCategory category, float volume, float pitch) {
        throw new NotImplementedYet();
    }

    @Override
    public void stopSound(@NotNull Sound sound) {
        throw new NotImplementedYet();
    }

    @Override
    public void stopSound(@NotNull String sound) {
        throw new NotImplementedYet();
    }

    @Override
    public void stopSound(@NotNull SoundCategory category) {
        throw new NotImplementedYet();
    }

    @Override
    public void stopSound(@NotNull Sound sound, @Nullable SoundCategory category) {
        throw new NotImplementedYet();
    }

    @Override
    public void stopSound(@NotNull String sound, @Nullable SoundCategory category) {
        throw new NotImplementedYet();
    }

    @Override
    public void stopAllSounds() {
        throw new NotImplementedYet();
    }

    @Override
    public void playEffect(@NotNull Location loc, @NotNull Effect effect, int data) {
        throw new NotImplementedYet();
    }

    @Override
    public <T> void playEffect(@NotNull Location loc, @NotNull Effect effect, @Nullable T data) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean breakBlock(@NotNull Block block) {
        throw new NotImplementedYet();
    }

    @Override
    public void sendBlockChange(@NotNull Location loc, @NotNull BlockData block) {
        throw new NotImplementedYet();
    }

    @Override
    public void sendBlockChanges(@NotNull Collection<BlockState> blocks, boolean suppressLightUpdates) {
        throw new NotImplementedYet();
    }

    @Override
    public void sendBlockDamage(@NotNull Location loc, float progress) {
        throw new NotImplementedYet();
    }

    @Override
    public void sendBlockChange(@NotNull Location loc, @NotNull Material material, byte data) {
        throw new NotImplementedYet();
    }

    @Override
    public void sendEquipmentChange(@NotNull LivingEntity entity, @NotNull EquipmentSlot slot, @NotNull ItemStack item) {
        throw new NotImplementedYet();
    }

    @Override
    public void sendSignChange(@NotNull Location loc, @Nullable String[] lines) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void sendSignChange(@NotNull Location loc, @Nullable String[] lines, @NotNull DyeColor dyeColor) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void sendSignChange(@NotNull Location loc, @Nullable String[] lines, @NotNull DyeColor dyeColor, boolean hasGlowingText) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void sendMap(@NotNull MapView map) {
        throw new NotImplementedYet();
    }

    @Override
    public void updateCommands() {
        throw new NotImplementedYet();
    }

    @Override
    public void updateInventory() {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public GameMode getPreviousGameMode() {
        throw new NotImplementedYet();
    }

    @Override
    public void setPlayerTime(long time, boolean relative) {
        throw new NotImplementedYet();
    }

    @Override
    public long getPlayerTime() {
        throw new NotImplementedYet();
    }

    @Override
    public long getPlayerTimeOffset() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isPlayerTimeRelative() {
        throw new NotImplementedYet();
    }

    @Override
    public void resetPlayerTime() {
        throw new NotImplementedYet();
    }

    @Override
    public void setPlayerWeather(@NotNull WeatherType type) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public WeatherType getPlayerWeather() {
        throw new NotImplementedYet();
    }

    @Override
    public void resetPlayerWeather() {
        throw new NotImplementedYet();
    }

    @Override
    public void giveExp(int amount) {
        throw new NotImplementedYet();
    }

    @Override
    public void giveExpLevels(int amount) {
        throw new NotImplementedYet();
    }

    @Override
    public float getExp() {
        throw new NotImplementedYet();
    }

    @Override
    public void setExp(float exp) {
        Preconditions.checkArgument(exp >= 0.0 && exp <= 1.0, "Experience progress must be between 0.0 and 1.0 (was %s)", exp);
        getHandle().experienceProgress = exp;
        getHandle().syncedExperience = -1;
    }

    @Override
    public int getFoodLevel() {
        throw new NotImplementedYet();
    }

    @Override
    public int getLevel() {
        throw new NotImplementedYet();
    }

    @Override
    public void setLevel(int level) {
        Preconditions.checkArgument(level >= 0, "Experience level must not be negative (was %s)", level);
        getHandle().experienceLevel = level;
        getHandle().syncedExperience = -1;
    }

    @Override
    public int getTotalExperience() {
        throw new NotImplementedYet();
    }

    @Override
    public void setTotalExperience(int exp) {
        throw new NotImplementedYet();
    }

    @Override
    public void sendExperienceChange(float progress) {
        throw new NotImplementedYet();
    }

    @Override
    public void sendExperienceChange(float progress, int level) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean getAllowFlight() {
        throw new NotImplementedYet();
    }

    @Override
    public void setAllowFlight(boolean flight) {
        throw new NotImplementedYet();
    }

    @Override
    public void hidePlayer(@NotNull Plugin plugin, @NotNull Player player) {
        throw new NotImplementedYet();
    }

    @Override
    public void hidePlayer(@NotNull Player player) {
        throw new NotImplementedYet();
    }

    @Override
    public void showPlayer(@NotNull Plugin plugin, @NotNull Player player) {
        throw new NotImplementedYet();
    }

    @Override
    public void showPlayer(@NotNull Player player) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean canSee(@NotNull Player player) {
        return true; // TODO: Implement properly
    }

    @Override
    public void hideEntity(@NotNull Plugin plugin, @NotNull Entity entity) {
        throw new NotImplementedYet();
    }

    @Override
    public void showEntity(@NotNull Plugin plugin, @NotNull Entity entity) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean canSee(@NotNull Entity entity) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isFlying() {
        throw new NotImplementedYet();
    }

    @Override
    public void setFlying(boolean value) {
        throw new NotImplementedYet();
    }

    @Override
    public void setFlySpeed(float value) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void setWalkSpeed(float value) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public float getFlySpeed() {
        throw new NotImplementedYet();
    }

    @Override
    public float getWalkSpeed() {
        throw new NotImplementedYet();
    }

    @Override
    public void setTexturePack(@NotNull String url) {
        throw new NotImplementedYet();
    }

    @Override
    public void setResourcePack(@NotNull String url) {
        throw new NotImplementedYet();
    }

    @Override
    public void setResourcePack(@NotNull String url, byte @Nullable [] hash) {
        throw new NotImplementedYet();
    }

    @Override
    public void setResourcePack(@NotNull String url, byte @Nullable [] hash, boolean force) {
        throw new NotImplementedYet();
    }

    @Override
    public void setResourcePack(@NotNull String url, byte @Nullable [] hash, @Nullable String prompt) {
        throw new NotImplementedYet();
    }

    @Override
    public void setResourcePack(@NotNull String url, byte @Nullable [] hash, @Nullable String prompt, boolean force) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Scoreboard getScoreboard() {
        throw new NotImplementedYet();
    }

    @Override
    public void setScoreboard(@NotNull Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException {
        Validate.notNull(scoreboard, "Scoreboard cannot be null");
        final ServerPlayNetworkHandler playerConnection = getHandle().networkHandler;
        if (playerConnection == null) {
            throw new IllegalStateException("Cannot set scoreboard yet");
        }
        if (!playerConnection.connection.isOpen()) { // TODO: player.joining
            throw new IllegalStateException("Cannot set scoreboard for invalid FabricPlayer");
        }
        assert server.getScoreboardManager() != null;
        server.getScoreboardManager().setPlayerBoard(this, scoreboard);
    }

    @Nullable
    @Override
    public WorldBorder getWorldBorder() {
        throw new NotImplementedYet();
    }

    @Override
    public void setWorldBorder(@Nullable WorldBorder border) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isHealthScaled() {
        throw new NotImplementedYet();
    }

    @Override
    public void setHealthScaled(boolean scale) {
        throw new NotImplementedYet();
    }

    @Override
    public void setHealthScale(double scale) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public double getHealthScale() {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public Entity getSpectatorTarget() {
        throw new NotImplementedYet();
    }

    @Override
    public void setSpectatorTarget(@Nullable Entity entity) {
        throw new NotImplementedYet();
    }

    @Override
    public void sendTitle(@Nullable String title, @Nullable String subtitle, int fadeIn, int stay, int fadeOut) {
        getHandle().networkHandler.sendPacket(new TitleFadeS2CPacket(fadeIn, stay, fadeOut));
        if (title != null) {
            getHandle().networkHandler.sendPacket(new TitleS2CPacket(ChatMessageConversion.fromString(title)[0]));
        }
        if (subtitle != null) {
            getHandle().networkHandler.sendPacket(new SubtitleS2CPacket(ChatMessageConversion.fromString(subtitle)[0]));
        }
    }

    @Override
    public void sendTitle(@Nullable String title, @Nullable String subtitle) {
        sendTitle(title, subtitle, 10, 70, 20);
    }

    @Override
    public void resetTitle() {
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

    @NotNull
    @Override
    public AdvancementProgress getAdvancementProgress(@NotNull Advancement advancement) {
        throw new NotImplementedYet();
    }

    @Override
    public int getClientViewDistance() {
        throw new NotImplementedYet();
    }

    @Override
    public int getPing() {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public String getLocale() {
        throw new NotImplementedYet();
    }

    @Override
    public void openBook(@NotNull ItemStack book) {
        throw new NotImplementedYet();
    }

    @Override
    public void openSign(@NotNull Sign sign) {
        throw new NotImplementedYet();
    }

    @Override
    public void showDemoScreen() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isAllowingServerListings() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isConversing() {
        throw new NotImplementedYet();
    }

    @Override
    public void acceptConversationInput(@NotNull String input) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean beginConversation(@NotNull Conversation conversation) {
        throw new NotImplementedYet();
    }

    @Override
    public void abandonConversation(@NotNull Conversation conversation) {
        throw new NotImplementedYet();
    }

    @Override
    public void abandonConversation(@NotNull Conversation conversation, @NotNull ConversationAbandonedEvent details) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isOnline() {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public PlayerProfile getPlayerProfile() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isBanned() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isWhitelisted() {
        throw new NotImplementedYet();
    }

    @Override
    public void setWhitelisted(boolean value) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public Player getPlayer() {
        throw new NotImplementedYet();
    }

    @Override
    public long getFirstPlayed() {
        throw new NotImplementedYet();
    }

    @Override
    public long getLastPlayed() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean hasPlayedBefore() {
        throw new NotImplementedYet();
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int amount) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull Material material, int amount) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, int amount) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int amount) {
        throw new NotImplementedYet();
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull Material material, int amount) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, int amount) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int newValue) {
        throw new NotImplementedYet();
    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, @NotNull Material material, int newValue) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, int newValue) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public int getStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public int getStatistic(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public int getStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        throw new NotImplementedYet();
    }

    @Override
    public void sendPluginMessage(@NotNull Plugin source, @NotNull String channel, byte @NotNull [] message) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Set<String> getListeningPluginChannels() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OfflinePlayer other)) {
            return false;
        }

        if (!Objects.equals(getUniqueId(), other.getUniqueId())) {
            return false;
        }

        return !(other instanceof FabricPlayer player) || getEntityId() == player.getEntityId();
    }

    @Override
    public int hashCode() {
        if (hash == 0 || hash == 485) {
            hash = 97 * 5 + Objects.hashCode(getUniqueId());
        }
        return hash;
    }

    @Override
    public String toString() {
        return "FabricPlayer{" + "name=" + getName() + '}';
    }
}
