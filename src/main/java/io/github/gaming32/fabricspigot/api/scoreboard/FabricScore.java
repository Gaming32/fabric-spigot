package io.github.gaming32.fabricspigot.api.scoreboard;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Score;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class FabricScore implements Score {
    private final String entry;
    private final FabricObjective objective;

    FabricScore(FabricObjective objective, String entry) {
        this.objective = objective;
        this.entry = entry;
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(entry);
    }

    @NotNull
    @Override
    public String getEntry() {
        return entry;
    }

    @NotNull
    @Override
    public FabricObjective getObjective() {
        return objective;
    }

    @Override
    public int getScore() throws IllegalStateException {
        final Scoreboard board = objective.checkState().board;

        if (board.getKnownPlayers().contains(entry)) {
            final var scores = board.getPlayerObjectives(entry);
            final ScoreboardPlayerScore score = scores.get(objective.getHandle());
            if (score != null) {
                return score.getScore();
            }
        }

        return 0;
    }

    @Override
    public void setScore(int score) throws IllegalStateException {
        objective.checkState().board.getPlayerScore(entry, objective.getHandle()).setScore(score);
    }

    @Override
    public boolean isScoreSet() throws IllegalStateException {
        final Scoreboard board = objective.checkState().board;
        return board.getKnownPlayers().contains(entry) && board.getPlayerObjectives(entry).containsKey(objective.getHandle());
    }

    @Nullable
    @Override
    public FabricScoreboard getScoreboard() {
        return objective.getScoreboard();
    }
}
