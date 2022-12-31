package io.github.gaming32.fabricspigot.api.entity;

import io.github.gaming32.fabricspigot.api.FabricServer;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.entity.passive.AbstractHorseEntity;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.AbstractHorseInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FabricAbstractHorse extends FabricAnimals implements AbstractHorse {
    public FabricAbstractHorse(FabricServer server, AbstractHorseEntity entity) {
        super(server, entity);
    }

    @Override
    public AbstractHorseEntity getHandle() {
        return (AbstractHorseEntity)entity;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setVariant(Horse.Variant variant) {
        throw new NotImplementedYet();
    }

    @Override
    public int getDomestication() {
        throw new NotImplementedYet();
    }

    @Override
    public void setDomestication(int level) {
        throw new NotImplementedYet();
    }

    @Override
    public int getMaxDomestication() {
        throw new NotImplementedYet();
    }

    @Override
    public void setMaxDomestication(int level) {
        throw new NotImplementedYet();
    }

    @Override
    public double getJumpStrength() {
        throw new NotImplementedYet();
    }

    @Override
    public void setJumpStrength(double strength) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isTamed() {
        throw new NotImplementedYet();
    }

    @Override
    public void setTamed(boolean tame) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public AnimalTamer getOwner() {
        throw new NotImplementedYet();
    }

    @Override
    public void setOwner(@Nullable AnimalTamer tamer) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isEatingHaystack() {
        throw new NotImplementedYet();
    }

    @Override
    public void setEatingHaystack(boolean eatingHaystack) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public AbstractHorseInventory getInventory() {
        throw new NotImplementedYet();
    }
}
