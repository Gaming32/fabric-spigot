package io.github.gaming32.fabricspigot.util;

import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.util.Unit;
import org.bukkit.plugin.Plugin;

import java.util.Comparator;

public class BukkitTicket {
    public static final ChunkTicketType<Unit> PLUGIN = ChunkTicketType.create("plugin", (a, b) -> 0);
    public static final ChunkTicketType<Plugin> PLUGIN_TICKET = ChunkTicketType.create("plugin_ticket", Comparator.comparing(plugin -> plugin.getClass().getName()));
}
