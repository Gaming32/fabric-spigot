package io.github.gaming32.fabricspigot.api.scoreboard;

abstract class FabricScoreboardComponent {
    private FabricScoreboard scoreboard;

    FabricScoreboardComponent(FabricScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    abstract FabricScoreboard checkState() throws IllegalStateException;

    public FabricScoreboard getScoreboard() {
        return scoreboard;
    }

    abstract void unregister() throws IllegalStateException;
}
