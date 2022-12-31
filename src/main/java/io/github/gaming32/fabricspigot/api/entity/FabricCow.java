package io.github.gaming32.fabricspigot.api.entity;

import io.github.gaming32.fabricspigot.api.FabricServer;
import net.minecraft.entity.passive.CowEntity;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class FabricCow extends FabricAnimals implements Cow {
    public FabricCow(FabricServer server, CowEntity entity) {
        super(server, entity);
    }

    @Override
    public CowEntity getHandle() {
        return (CowEntity)entity;
    }

    @Override
    public String toString() {
        return "FabricCow";
    }

    @Override
    public @NotNull EntityType getType() {
        return EntityType.COW;
    }
}
