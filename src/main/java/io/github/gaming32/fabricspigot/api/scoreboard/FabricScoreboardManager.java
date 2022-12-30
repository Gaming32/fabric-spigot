package io.github.gaming32.fabricspigot.api.scoreboard;

import io.github.gaming32.fabricspigot.api.entity.FabricPlayer;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.commons.lang3.Validate;
import org.bukkit.craftbukkit.util.WeakCollection;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class FabricScoreboardManager implements ScoreboardManager {
    private final FabricScoreboard mainScoreboard;
    private final MinecraftServer server;
    private final Collection<FabricScoreboard> scoreboards = new WeakCollection<>();
    private final Map<FabricPlayer, FabricScoreboard> playerBoards = new HashMap<>();

    public FabricScoreboardManager(MinecraftServer server, Scoreboard serverScoreboard) {
        mainScoreboard = new FabricScoreboard(serverScoreboard);
        this.server = server;
        scoreboards.add(mainScoreboard);
    }

    @NotNull
    @Override
    public FabricScoreboard getMainScoreboard() {
        return mainScoreboard;
    }

    @NotNull
    @Override
    public FabricScoreboard getNewScoreboard() {
        final FabricScoreboard scoreboard = new FabricScoreboard(new ServerScoreboard(server));
        scoreboards.add(scoreboard);
        return scoreboard;
    }

    public FabricScoreboard getPlayerBoard(FabricPlayer player) {
        final FabricScoreboard board = playerBoards.get(player);
        return board != null ? board : getMainScoreboard();
    }

    public void setPlayerBoard(FabricPlayer player, org.bukkit.scoreboard.Scoreboard bukkitScoreboard) throws IllegalArgumentException {
        Validate.isTrue(bukkitScoreboard instanceof FabricScoreboard, "Cannot set player scoreboard to an unregistered Scoreboard");

        final FabricScoreboard scoreboard = (FabricScoreboard)bukkitScoreboard;
        final Scoreboard oldBoard = getPlayerBoard(player).getHandle();
        final Scoreboard newBoard = scoreboard.getHandle();
        final ServerPlayerEntity playerEntity = player.getHandle();

        if (oldBoard == newBoard) return;

        if (scoreboard == mainScoreboard) {
            playerBoards.remove(player);
        } else {
            playerBoards.put(player, scoreboard);
        }

        final Set<ScoreboardObjective> removed = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            final ScoreboardObjective objective = oldBoard.getObjectiveForSlot(i);
            if (objective != null && removed.add(objective)) {
                playerEntity.networkHandler.sendPacket(new ScoreboardObjectiveUpdateS2CPacket(objective, ScoreboardObjectiveUpdateS2CPacket.REMOVE_MODE));
            }
        }

        for (final Team team : oldBoard.getTeams()) {
            playerEntity.networkHandler.sendPacket(TeamS2CPacket.updateRemovedTeam(team));
        }

        server.getPlayerManager().sendScoreboard((ServerScoreboard)newBoard, player.getHandle());
    }
}
