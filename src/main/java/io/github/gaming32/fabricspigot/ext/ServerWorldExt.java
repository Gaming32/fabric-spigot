package io.github.gaming32.fabricspigot.ext;

import io.github.gaming32.fabricspigot.api.FabricWorld;
import net.minecraft.world.level.storage.LevelStorage;

public interface ServerWorldExt {
    FabricWorld getBukkitWorld();

    LevelStorage.Session getSession();
}
