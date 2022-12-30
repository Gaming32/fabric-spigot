package io.github.gaming32.fabricspigot.api;

import com.mojang.authlib.GameProfile;
import io.github.gaming32.fabricspigot.FabricSpigot;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.world.WorldSaveHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@SerializableAs("Player")
public class FabricOfflinePlayer implements OfflinePlayer, ConfigurationSerializable {
    private final GameProfile profile;
    private final FabricServer server;
    private final WorldSaveHandler storage;

    protected FabricOfflinePlayer(FabricServer server, GameProfile profile) {
        this.server = server;
        this.profile = profile;
        this.storage = server.getHandle().getSaveHandler();
    }

    @Override
    public boolean isOnline() {
        return getPlayer() != null;
    }

    @Nullable
    @Override
    public String getName() {
        final Player player = getPlayer();
        if (player != null) {
            return player.getName();
        }

        if (profile.getName() != null) {
            return profile.getName();
        }

        final NbtCompound data = getBukkitData();

        if (data != null && data.contains("lastKnownName", NbtElement.STRING_TYPE)) {
            return data.getString("lastKnownName");
        }

        return null;
    }

    @NotNull
    @Override
    public UUID getUniqueId() {
        return profile.getId();
    }

    @NotNull
    @Override
    public PlayerProfile getPlayerProfile() {
        throw new NotImplementedYet();
    }

    public FabricServer getServer() {
        return server;
    }

    @Override
    public boolean isBanned() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isWhitelisted() {
        return server.getHandle().getPlayerManager().isWhitelisted(profile);
    }

    @Override
    public void setWhitelisted(boolean value) {
        if (value) {
            server.getHandle().getPlayerManager().getWhitelist().add(new WhitelistEntry(profile));
        } else {
            server.getHandle().getPlayerManager().getWhitelist().remove(profile);
        }
    }

    @Nullable
    @Override
    public Player getPlayer() {
        return server.getPlayer(getUniqueId());
    }

    @Override
    public long getFirstPlayed() {
        final Player player = getPlayer();
        if (player != null) return player.getFirstPlayed();

        final NbtCompound data = getBukkitData();

        if (data != null) {
            if (data.contains("firstPlayed", NbtElement.LONG_TYPE)) {
                return data.getLong("firstPlayed");
            } else {
                final File file = getDataFile();
                return file.lastModified();
            }
        }
        return 0;
    }

    @Override
    public long getLastPlayed() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean hasPlayedBefore() {
        return getData() != null;
    }

    @Nullable
    @Override
    public Location getBedSpawnLocation() {
        throw new NotImplementedYet();
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, int amount) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, int amount) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, int newValue) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public int getStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public int getStatistic(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public int getStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull Material material, int amount) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int amount) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull Material material, int amount) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, @NotNull Material material, int newValue) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int amount) {
        throw new NotImplementedYet();
    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int newValue) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public Location getLastDeathLocation() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isOp() {
        return server.getHandle().getPlayerManager().isOperator(profile);
    }

    @Override
    public void setOp(boolean value) {
        if (value == isOp()) return;
        if (value) {
            server.getHandle().getPlayerManager().addToOperators(profile);
        } else {
            server.getHandle().getPlayerManager().removeFromOperators(profile);
        }
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> result = new LinkedHashMap<>();
        result.put("UUID", profile.getId().toString());
        return result;
    }

    private NbtCompound getBukkitData() {
        NbtCompound result = getData();

        if (result != null) {
            if (!result.contains("bukkit", NbtElement.COMPOUND_TYPE)) {
                result.put("bukkit", new NbtCompound());
            }
            result = result.getCompound("bukkit");
        }

        return result;
    }

    private File getDataFile() {
        return new File(storage.playerDataDir, getUniqueId() + ".dat");
    }

    private NbtCompound getData() {
        return getPlayerData(storage, getUniqueId().toString());
    }

    private static NbtCompound getPlayerData(WorldSaveHandler saveHandler, String s) {
        try {
            final File file1 = new File(saveHandler.playerDataDir, s + ".dat");
            if (file1.exists()) {
                return NbtIo.readCompressed(file1);
            }
        } catch (Exception e) {
            FabricSpigot.LOGGER.warn("Failed to load player data for " + s, e);
        }

        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OfflinePlayer other)) {
            return false;
        }

        return Objects.equals(getUniqueId(), other.getUniqueId());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(getUniqueId());
        return hash;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[UUID=" + profile.getId() + "]";
    }
}
