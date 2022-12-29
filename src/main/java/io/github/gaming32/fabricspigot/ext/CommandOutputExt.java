package io.github.gaming32.fabricspigot.ext;

import net.minecraft.server.command.ServerCommandSource;
import org.bukkit.command.CommandSender;

public interface CommandOutputExt {
    CommandSender getBukkitSender(ServerCommandSource commandSource);
}
