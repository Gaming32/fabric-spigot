package io.github.gaming32.fabricspigot.api.scoreboard;

import io.github.gaming32.fabricspigot.util.ChatMessageConversion;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.apache.commons.lang3.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class FabricObjective extends FabricScoreboardComponent implements Objective {
    private final ScoreboardObjective objective;
    private final FabricCriteria criteria;

    FabricObjective(FabricScoreboard scoreboard, ScoreboardObjective objective) {
        super(scoreboard);
        this.objective = objective;
        criteria = FabricCriteria.getFromNMS(objective);
    }

    ScoreboardObjective getHandle() {
        return objective;
    }

    @NotNull
    @Override
    public String getName() throws IllegalStateException {
        checkState();
        return objective.getName();
    }

    @NotNull
    @Override
    public String getDisplayName() throws IllegalStateException {
        checkState();
        return ChatMessageConversion.fromComponent(objective.getDisplayName());
    }

    @Override
    public void setDisplayName(@NotNull String displayName) throws IllegalStateException, IllegalArgumentException {
        Validate.notNull(displayName, "Display name cannot be null");
        Validate.isTrue(displayName.length() <= 128, "Display name '" + displayName + "' is longer than the limit of 128 characters");
        checkState();

        objective.setDisplayName(ChatMessageConversion.fromString(displayName)[0]);
    }

    @NotNull
    @Override
    public String getCriteria() {
        checkState();
        return criteria.getName();
    }

    @NotNull
    @Override
    public Criteria getTrackedCriteria() throws IllegalStateException {
        checkState();
        return criteria;
    }

    @Override
    public boolean isModifiable() throws IllegalStateException {
        checkState();
        return !criteria.isReadOnly();
    }

    @Override
    public void setDisplaySlot(@Nullable DisplaySlot slot) throws IllegalStateException {
        final FabricScoreboard scoreboard = checkState();
        final Scoreboard board = scoreboard.board;
        final ScoreboardObjective objective = this.objective;

        for (int i = 0; i < FabricScoreboardTranslations.MAX_DISPLAY_SLOT; i++) {
            if (board.getObjectiveForSlot(i) == objective) {
                board.setObjectiveSlot(i, null);
            }
        }
        if (slot != null) {
            final int slotNumber = FabricScoreboardTranslations.fromBukkitSlot(slot);
            board.setObjectiveSlot(slotNumber, getHandle());
        }
    }

    @Nullable
    @Override
    public DisplaySlot getDisplaySlot() throws IllegalStateException {
        final FabricScoreboard scoreboard = checkState();
        final Scoreboard board = scoreboard.board;
        final ScoreboardObjective objective = this.objective;

        for (int i = 0; i < FabricScoreboardTranslations.MAX_DISPLAY_SLOT; i++) {
            if (board.getObjectiveForSlot(i) == objective) {
                return FabricScoreboardTranslations.toBukkitSlot(i);
            }
        }
        return null;
    }

    @Override
    public void setRenderType(@NotNull RenderType renderType) throws IllegalStateException {
        Validate.notNull(renderType, "RenderType cannot be null");
        checkState();

        objective.setRenderType(FabricScoreboardTranslations.fromBukkitRender(renderType));
    }

    @NotNull
    @Override
    public RenderType getRenderType() throws IllegalStateException {
        checkState();
        return FabricScoreboardTranslations.toBukkitRender(objective.getRenderType());
    }

    @NotNull
    @Override
    public Score getScore(@NotNull OfflinePlayer player) throws IllegalArgumentException, IllegalStateException {
        Validate.notNull(player, "Player cannot be null");
        checkState();

        return new FabricScore(this, player.getName());
    }

    @NotNull
    @Override
    public Score getScore(@NotNull String entry) throws IllegalArgumentException, IllegalStateException {
        Validate.notNull(entry, "Entry cannot be null");
        Validate.isTrue(entry.length() <= Short.MAX_VALUE, "Score '" + entry + "' is longer than the limit of 32767 characters");
        checkState();

        return new FabricScore(this, entry);
    }

    @Override
    public void unregister() throws IllegalStateException {
        final FabricScoreboard scoreboard = checkState();
        scoreboard.board.removeObjective(objective);
    }

    @Override
    FabricScoreboard checkState() throws IllegalStateException {
        if (getScoreboard().board.getObjective(objective.getName()) == null) {
            throw new IllegalStateException("Unregistered scoreboard component");
        }
        return getScoreboard();
    }
}
