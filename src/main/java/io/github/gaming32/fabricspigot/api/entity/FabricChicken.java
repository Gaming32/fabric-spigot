package io.github.gaming32.fabricspigot.api.entity;

import io.github.gaming32.fabricspigot.api.FabricServer;
import net.minecraft.entity.passive.ChickenEntity;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class FabricChicken extends FabricAnimals implements Chicken {
    public FabricChicken(FabricServer server, ChickenEntity entity) {
        super(server, entity);
    }

    @Override
    public ChickenEntity getHandle() {
        return (ChickenEntity)entity;
    }

    @Override
    public String toString() {
        return "FabricChicken";
    }

    @Override
    public @NotNull EntityType getType() {
        return EntityType.CHICKEN;
    }
}
