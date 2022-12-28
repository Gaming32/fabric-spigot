package io.github.gaming32.fabricspigot.api.entity;

import io.github.gaming32.fabricspigot.api.FabricServer;
import net.minecraft.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class FabricUnknownEntity extends FabricEntity {
    public FabricUnknownEntity(FabricServer server, Entity entity) {
        super(server, entity);
    }

    @NotNull
    @Override
    public EntityType getType() {
        return EntityType.UNKNOWN;
    }
}
