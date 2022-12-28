package io.github.gaming32.fabricspigot.vanillaimpl;

import io.github.gaming32.fabricspigot.api.FabricServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.WorldSaveHandler;

public interface MinecraftServerExt {
    FabricServer getBukkitServer();

    void setBukkitServer(FabricServer server);

    void removeWorld(ServerWorld world);

    WorldSaveHandler getSaveHandler();
}
