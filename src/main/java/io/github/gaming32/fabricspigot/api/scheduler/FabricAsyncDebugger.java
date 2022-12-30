package io.github.gaming32.fabricspigot.api.scheduler;

import org.bukkit.plugin.Plugin;

class FabricAsyncDebugger {
    private FabricAsyncDebugger next = null;
    private final int expiry;
    private final Plugin plugin;
    private final Class<?> clazz;

    FabricAsyncDebugger(final int expiry, final Plugin plugin, final Class<?> clazz) {
        this.expiry = expiry;
        this.plugin = plugin;
        this.clazz = clazz;

    }

    final FabricAsyncDebugger getNextHead(final int time) {
        FabricAsyncDebugger next, current = this;
        while (time > current.expiry && (next = current.next) != null) {
            current = next;
        }
        return current;
    }

    final FabricAsyncDebugger setNext(final FabricAsyncDebugger next) {
        return this.next = next;
    }

    StringBuilder debugTo(final StringBuilder string) {
        for (FabricAsyncDebugger next = this; next != null; next = next.next) {
            string.append(next.plugin.getDescription().getName()).append(':').append(next.clazz.getName()).append('@').append(next.expiry).append(',');
        }
        return string;
    }
}
