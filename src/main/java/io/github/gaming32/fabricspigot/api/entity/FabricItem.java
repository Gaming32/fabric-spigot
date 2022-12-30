package io.github.gaming32.fabricspigot.api.entity;

import io.github.gaming32.fabricspigot.api.FabricServer;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class FabricItem extends FabricEntity implements Item {
    private final ItemEntity item;

    public FabricItem(FabricServer server, Entity entity, ItemEntity item) {
        super(server, entity);
        this.item = item;
    }

    public FabricItem(FabricServer server, ItemEntity entity) {
        this(server, entity, entity);
    }

    @NotNull
    @Override
    public ItemStack getItemStack() {
        throw new NotImplementedYet();
    }

    @Override
    public void setItemStack(@NotNull ItemStack stack) {
        throw new NotImplementedYet();
    }

    @Override
    public int getPickupDelay() {
        throw new NotImplementedYet();
    }

    @Override
    public void setPickupDelay(int delay) {
        throw new NotImplementedYet();
    }

    @Override
    public void setUnlimitedLifetime(boolean unlimited) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isUnlimitedLifetime() {
        throw new NotImplementedYet();
    }

    @Override
    public void setTicksLived(int value) {
        throw new NotImplementedYet();
    }

    @Override
    public void setOwner(@Nullable UUID owner) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public UUID getOwner() {
        throw new NotImplementedYet();
    }

    @Override
    public void setThrower(@Nullable UUID uuid) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public UUID getThrower() {
        throw new NotImplementedYet();
    }

    @Override
    public String toString() {
        return "FabricItem";
    }

    @NotNull
    @Override
    public EntityType getType() {
        return EntityType.DROPPED_ITEM;
    }
}
