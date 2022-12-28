package io.github.gaming32.fabricspigot.api.command;

import com.google.common.base.Joiner;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.gaming32.fabricspigot.api.FabricServer;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FabricCommandWrapper extends BukkitCommand {
    private final CommandManager manager;
    public final LiteralCommandNode<ServerCommandSource> fabricCommand;

    public FabricCommandWrapper(CommandManager manager, LiteralCommandNode<ServerCommandSource> fabricCommand) {
        super(fabricCommand.getName(), "A command provided by Vanilla or a Fabric mod", fabricCommand.getUsageText(), Collections.emptyList());
        this.manager = manager;
        this.fabricCommand = fabricCommand;
        setPermission(getPermission(fabricCommand));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!testPermission(sender)) return true;
        final ServerCommandSource source = getSource(sender);
        manager.executeWithPrefix(source, toDispatcher(args, getName()));
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location location) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        final ServerCommandSource source = getSource(sender);
        final ParseResults<ServerCommandSource> parsed = manager.getDispatcher().parse(toDispatcher(args, getName()), source);

        final List<String> results = new ArrayList<>();
        manager.getDispatcher().getCompletionSuggestions(parsed).thenAccept(suggestions ->
            suggestions.getList().forEach(s -> results.add(s.getText()))
        );
        return results;
    }

    public static ServerCommandSource getSource(CommandSender sender) {
        if (sender instanceof Player) {
            throw new NotImplementedYet("Player");
        }
        if (sender instanceof BlockCommandSender) {
            return ((FabricBlockCommandSender)sender).getWrapper();
        }
        if (sender instanceof CommandMinecart) {
            throw new NotImplementedYet("CommandMinecart");
        }
        if (sender instanceof RemoteConsoleCommandSender) {
            throw new NotImplementedYet("RemoteConsoleCommandSender");
        }
        if (sender instanceof ConsoleCommandSender) {
            return ((FabricServer)sender.getServer()).getHandle().getCommandSource();
        }
        if (sender instanceof ProxiedCommandSender) {
            throw new NotImplementedYet("ProxiedCommandSender");
        }
        throw new IllegalArgumentException("Cannot make " + sender + " a fabric command listener");
    }

    public static String getPermission(CommandNode<ServerCommandSource> fabricCommand) {
        return "fabric.command." + (fabricCommand.getRedirect() == null ? fabricCommand.getName() : fabricCommand.getRedirect().getName());
    }

    private String toDispatcher(String[] args, String name) {
        return name + (args.length > 0 ? " " + Joiner.on(' ').join(args) : "");
    }
}
