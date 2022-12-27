package io.github.gaming32.fabricspigot.vanillaimpl;

import io.github.gaming32.fabricspigot.api.FabricWorld;
import net.minecraft.world.level.storage.LevelStorage;

public interface HasBukkitWorld {
    FabricWorld getBukkitWorld();

    LevelStorage.Session getSession();
}
