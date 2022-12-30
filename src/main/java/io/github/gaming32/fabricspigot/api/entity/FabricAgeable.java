package io.github.gaming32.fabricspigot.api.entity;

import io.github.gaming32.fabricspigot.api.FabricServer;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.entity.passive.PassiveEntity;
import org.bukkit.entity.Ageable;

public class FabricAgeable extends FabricCreature implements Ageable {
    public FabricAgeable(FabricServer server, PassiveEntity entity) {
        super(server, entity);
    }

    @Override
    public int getAge() {
        throw new NotImplementedYet();
    }

    @Override
    public void setAge(int age) {
        throw new NotImplementedYet();
    }

    @Override
    public void setAgeLock(boolean lock) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean getAgeLock() {
        throw new NotImplementedYet();
    }

    @Override
    public void setBaby() {
        throw new NotImplementedYet();
    }

    @Override
    public void setAdult() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isAdult() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean canBreed() {
        throw new NotImplementedYet();
    }

    @Override
    public void setBreed(boolean breed) {
        throw new NotImplementedYet();
    }

    @Override
    public PassiveEntity getHandle() {
        return (PassiveEntity)entity;
    }

    @Override
    public String toString() {
        return "FabricAgeable";
    }
}
