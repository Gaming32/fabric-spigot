package io.github.gaming32.fabricspigot.api.command;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class FabricCommandMap extends SimpleCommandMap {
    public FabricCommandMap(@NotNull Server server) {
        super(server);
    }

    public Map<String, Command> getKnownCommands() {
        return knownCommands;
    }
}
