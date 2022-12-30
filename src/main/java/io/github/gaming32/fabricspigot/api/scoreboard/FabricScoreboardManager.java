package io.github.gaming32.fabricspigot.api.scoreboard;

import io.github.gaming32.fabricspigot.api.entity.FabricPlayer;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import org.bukkit.craftbukkit.util.WeakCollection;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
}
