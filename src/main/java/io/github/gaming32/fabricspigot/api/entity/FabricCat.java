package io.github.gaming32.fabricspigot.api.entity;

import io.github.gaming32.fabricspigot.api.FabricServer;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.entity.passive.CatEntity;
import org.bukkit.DyeColor;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public class FabricCat extends FabricTameableAnimal implements Cat {
    public FabricCat(FabricServer server, CatEntity entity) {
        super(server, entity);
    }

    @Override
    public CatEntity getHandle() {
        return (CatEntity)super.getHandle();
    }

    @Override
    public @NotNull EntityType getType() {
        return EntityType.CAT;
    }

    @Override
    public String toString() {
        return "FabricCat";
    }

    @NotNull
    @Override
    public Type getCatType() {
        throw new NotImplementedYet();
    }

    @Override
    public void setCatType(@NotNull Cat.Type type) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public DyeColor getCollarColor() {
        throw new NotImplementedYet();
    }

    @Override
    public void setCollarColor(@NotNull DyeColor color) {
        throw new NotImplementedYet();
    }
}
