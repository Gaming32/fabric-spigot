package io.github.gaming32.fabricspigot.api.entity;

import io.github.gaming32.fabricspigot.api.FabricServer;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.entity.passive.AnimalEntity;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class FabricAnimals extends FabricAgeable implements Animals {
    public FabricAnimals(FabricServer server, AnimalEntity entity) {
        super(server, entity);
    }

    @Override
    public AnimalEntity getHandle() {
        return (AnimalEntity)entity;
    }

    @Override
    public String toString() {
        return "FabricAnimals";
    }

    @Nullable
    @Override
    public UUID getBreedCause() {
        throw new NotImplementedYet();
    }

    @Override
    public void setBreedCause(@Nullable UUID uuid) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isLoveMode() {
        throw new NotImplementedYet();
    }

    @Override
    public void setLoveModeTicks(int ticks) {
        throw new NotImplementedYet();
    }

    @Override
    public int getLoveModeTicks() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isBreedItem(@NotNull ItemStack stack) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isBreedItem(@NotNull Material material) {
        throw new NotImplementedYet();
    }
}
