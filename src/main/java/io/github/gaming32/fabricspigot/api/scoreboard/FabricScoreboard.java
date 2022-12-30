package io.github.gaming32.fabricspigot.api.scoreboard;

import io.github.gaming32.fabricspigot.util.ChatMessageConversion;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.apache.commons.lang3.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public final class FabricScoreboard implements Scoreboard {
    final net.minecraft.scoreboard.Scoreboard board;

    FabricScoreboard(net.minecraft.scoreboard.Scoreboard board) {
        this.board = board;
    }

    public net.minecraft.scoreboard.Scoreboard getHandle() {
        return board;
    }

    @NotNull
    @Override
    public FabricObjective registerNewObjective(@NotNull String name, @NotNull String criteria) throws IllegalArgumentException {
        return registerNewObjective(name, criteria, name);
    }

    @NotNull
    @Override
    public FabricObjective registerNewObjective(@NotNull String name, @NotNull String criteria, @NotNull String displayName) throws IllegalArgumentException {
        return registerNewObjective(name, FabricCriteria.getFromBukkit(criteria), displayName, RenderType.INTEGER);
    }

    @NotNull
    @Override
    public FabricObjective registerNewObjective(@NotNull String name, @NotNull String criteria, @NotNull String displayName, @NotNull RenderType renderType) throws IllegalArgumentException {
        return registerNewObjective(name, FabricCriteria.getFromBukkit(criteria), displayName, renderType);
    }

    @NotNull
    @Override
    public FabricObjective registerNewObjective(@NotNull String name, @NotNull Criteria criteria, @NotNull String displayName) throws IllegalArgumentException {
        return registerNewObjective(name, criteria, displayName, RenderType.INTEGER);
    }

    @NotNull
    @Override
    public FabricObjective registerNewObjective(@NotNull String name, @NotNull Criteria criteria, @NotNull String displayName, @NotNull RenderType renderType) throws IllegalArgumentException {
        Validate.notNull(name, "Objective name cannot be null");
        Validate.notNull(criteria, "Criteria cannot be null");
        Validate.notNull(displayName, "Display name cannot be null");
        Validate.notNull(renderType, "RenderType cannot be null");
        Validate.isTrue(name.length() <= Short.MAX_VALUE, "The name '" + name + "' is longer than the limit of 32767 characters");
        Validate.isTrue(displayName.length() <= 128, "The display name '" + displayName + "' is longer than the limit of 128 characters");
        Validate.isTrue(board.getObjective(name) == null, "An objective of name '" + name + "' already exists");

        final ScoreboardObjective objective = board.addObjective(
            name,
            ((FabricCriteria)criteria).criteria,
            ChatMessageConversion.fromStringOrNull(displayName),
            FabricScoreboardTranslations.fromBukkitRender(renderType)
        );
        return new FabricObjective(this, objective);
    }

    @Nullable
    @Override
    public Objective getObjective(@NotNull String name) throws IllegalArgumentException {
        Validate.notNull(name, "Name cannot be null");
        final ScoreboardObjective nms = board.getObjective(name);
        return nms != null ? new FabricObjective(this, nms) : null;
    }

    @NotNull
    @Override
    public Set<Objective> getObjectivesByCriteria(@NotNull String criteria) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Set<Objective> getObjectivesByCriteria(@NotNull Criteria criteria) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Set<Objective> getObjectives() {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public Objective getObjective(@NotNull DisplaySlot slot) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Set<Score> getScores(@NotNull OfflinePlayer player) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Set<Score> getScores(@NotNull String entry) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void resetScores(@NotNull String entry) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Override
    public void resetScores(@NotNull OfflinePlayer player) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public Team getPlayerTeam(@NotNull OfflinePlayer player) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public Team getEntryTeam(@NotNull String entry) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public Team getTeam(@NotNull String teamName) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Set<Team> getTeams() {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Team registerNewTeam(@NotNull String name) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Set<OfflinePlayer> getPlayers() {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Set<String> getEntries() {
        throw new NotImplementedYet();
    }

    @Override
    public void clearSlot(@NotNull DisplaySlot slot) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }
}
