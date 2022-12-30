package io.github.gaming32.fabricspigot.api.entity;

import io.github.gaming32.fabricspigot.api.FabricServer;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.entity.mob.MobEntity;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.Nullable;

public class FabricMob extends FabricLivingEntity implements Mob {
    public FabricMob(FabricServer server, MobEntity entity) {
        super(server, entity);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public LivingEntity getTarget() {
        throw new NotImplementedYet();
    }

    @Override
    public void setAware(boolean aware) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isAware() {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public Sound getAmbientSound() {
        throw new NotImplementedYet();
    }

    @Override
    public MobEntity getHandle() {
        return (MobEntity)entity;
    }

    @Override
    public String toString() {
        return "FabricMob";
    }

    @Override
    public void setLootTable(@Nullable LootTable table) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public LootTable getLootTable() {
        throw new NotImplementedYet();
    }

    @Override
    public void setSeed(long seed) {
        throw new NotImplementedYet();
    }

    @Override
    public long getSeed() {
        throw new NotImplementedYet();
    }
}
