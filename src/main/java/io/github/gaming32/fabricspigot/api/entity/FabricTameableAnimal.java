package io.github.gaming32.fabricspigot.api.entity;

import io.github.gaming32.fabricspigot.api.FabricServer;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.entity.passive.TameableEntity;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Tameable;
import org.jetbrains.annotations.Nullable;

public class FabricTameableAnimal extends FabricAnimals implements Tameable, Creature {
    public FabricTameableAnimal(FabricServer server, TameableEntity entity) {
        super(server, entity);
    }

    @Override
    public TameableEntity getHandle() {
        return (TameableEntity)super.getHandle();
    }

    @Nullable
    @Override
    public AnimalTamer getOwner() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isTamed() {
        throw new NotImplementedYet();
    }

    @Override
    public void setOwner(@Nullable AnimalTamer tamer) {
        throw new NotImplementedYet();
    }

    @Override
    public void setTamed(boolean tame) {
        throw new NotImplementedYet();
    }

    public boolean isSitting() {
        return getHandle().isInSittingPose();
    }

    public void setSitting(boolean sitting) {
        getHandle().setInSittingPose(sitting);
        getHandle().setSitting(sitting);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{owner=" + getOwner() + ",tamed=" + isTamed() + "}";
    }
}
