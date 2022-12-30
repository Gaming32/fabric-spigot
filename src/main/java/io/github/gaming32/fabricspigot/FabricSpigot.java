package io.github.gaming32.fabricspigot;

import io.github.gaming32.fabricspigot.api.FabricServer;
import io.github.gaming32.fabricspigot.api.scoreboard.FabricScoreboardManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginLoadOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.File;
import java.util.Optional;

public class FabricSpigot implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("fabric-spigot");
    public static final FabricServer SERVER = new FabricServer();
    public static final File PLUGINS_DIR = FabricLoader.getInstance().getGameDir().resolve("plugins").toFile();

    private static String modVersion = "Unknown";

    @Override
    public void onInitialize() {
        getModContainer()
            .map(ModContainer::getMetadata)
            .map(ModMetadata::getVersion)
            .ifPresent(v -> modVersion = v.getFriendlyString());

        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        Bukkit.setServer(SERVER);

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> SERVER.setServer(null));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> SERVER.reloadVanillaCommands(dispatcher));

        ServerWorldEvents.LOAD.register((server, world) -> {
            if (world.getRegistryKey() == World.OVERWORLD) {
                SERVER.setScoreboardManager(new FabricScoreboardManager(server, world.getScoreboard()));
            }
        });
    }

    public static Optional<ModContainer> getModContainer() {
        return FabricLoader.getInstance().getModContainer("fabric-spigot");
    }

    public static String getModVersion() {
        return modVersion;
    }

    public static String toShortString(Identifier id) {
        return id.getNamespace().equals(Identifier.DEFAULT_NAMESPACE) ? id.getPath() : id.toString();
    }

    public static void setupServer(MinecraftServer server) {
        SERVER.setServer(server);
        SERVER.loadPlugins();
        SERVER.enablePlugins(PluginLoadOrder.STARTUP);
    }
}
