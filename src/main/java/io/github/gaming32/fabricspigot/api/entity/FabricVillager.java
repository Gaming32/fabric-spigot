package io.github.gaming32.fabricspigot.api.entity;

import io.github.gaming32.fabricspigot.api.FabricServer;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FabricVillager extends FabricAbstractVillager implements Villager {
    public FabricVillager(FabricServer server, VillagerEntity entity) {
        super(server, entity);
    }

    @Override
    public MerchantEntity getHandle() {
        return (VillagerEntity)entity;
    }

    @Override
    public String toString() {
        return "FabricVillager";
    }

    @NotNull
    @Override
    public EntityType getType() {
        return EntityType.VILLAGER;
    }

    @Override
    public void remove() {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Profession getProfession() {
        throw new NotImplementedYet();
    }

    @Override
    public void setProfession(@NotNull Villager.Profession profession) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Type getVillagerType() {
        throw new NotImplementedYet();
    }

    @Override
    public void setVillagerType(@NotNull Villager.Type type) {
        throw new NotImplementedYet();
    }

    @Override
    public int getVillagerLevel() {
        throw new NotImplementedYet();
    }

    @Override
    public void setVillagerLevel(int level) {
        throw new NotImplementedYet();
    }

    @Override
    public int getVillagerExperience() {
        throw new NotImplementedYet();
    }

    @Override
    public void setVillagerExperience(int experience) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean sleep(@NotNull Location location) {
        throw new NotImplementedYet();
    }

    @Override
    public void wakeup() {
        throw new NotImplementedYet();
    }

    @Override
    public void shakeHead() {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public ZombieVillager zombify() {
        throw new NotImplementedYet();
    }
}
