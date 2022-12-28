package io.github.gaming32.fabricspigot.api.help;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.MultipleCommandAlias;
import org.bukkit.help.HelpTopic;
import org.jetbrains.annotations.NotNull;

public class MultipleCommandAliasHelpTopic extends HelpTopic {
    private final MultipleCommandAlias alias;

    public MultipleCommandAliasHelpTopic(MultipleCommandAlias alias) {
        this.alias = alias;

        name = "/" + alias.getLabel();

        // Build short text
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < alias.getCommands().length; i++) {
            if (i != 0) {
                sb.append(ChatColor.GOLD).append(" > ").append(ChatColor.WHITE);
            }
            sb.append("/");
            sb.append(alias.getCommands()[i].getLabel());
        }
        shortText = sb.toString();

        // Build full text
        fullText = ChatColor.GOLD + "Alias for: " + ChatColor.WHITE + getShortText();
    }

    @Override
    public boolean canSee(@NotNull CommandSender sender) {
        if (amendedPermission == null) {
            if (sender instanceof ConsoleCommandSender) {
                return true;
            }

            for (Command command : alias.getCommands()) {
                if (!command.testPermissionSilent(sender)) {
                    return false;
                }
            }

            return true;
        } else {
            return sender.hasPermission(amendedPermission);
        }
    }
}
