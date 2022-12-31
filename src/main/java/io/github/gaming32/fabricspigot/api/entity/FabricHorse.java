package io.github.gaming32.fabricspigot.api.entity;

import io.github.gaming32.fabricspigot.api.FabricServer;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.entity.passive.HorseEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.HorseInventory;
import org.jetbrains.annotations.NotNull;

public class FabricHorse extends FabricAbstractHorse implements Horse {
    public FabricHorse(FabricServer server, HorseEntity entity) {
        super(server, entity);
    }

    @Override
    public HorseEntity getHandle() {
        return (HorseEntity)super.getHandle();
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public Variant getVariant() {
        return Variant.HORSE;
    }

    @NotNull
    @Override
    public Color getColor() {
        throw new NotImplementedYet();
    }

    @Override
    public void setColor(@NotNull Horse.Color color) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Style getStyle() {
        throw new NotImplementedYet();
    }

    @Override
    public void setStyle(@NotNull Horse.Style style) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isCarryingChest() {
        return false;
    }

    @Override
    public void setCarryingChest(boolean chest) {
        throw new NotImplementedYet();
    }

    @Override
    public @NotNull HorseInventory getInventory() {
        throw new NotImplementedYet();
    }

    @Override
    public String toString() {
        return "FabricHorse{variant=" + getVariant() + ", owner=" + getOwner() + '}';
    }

    @Override
    public @NotNull EntityType getType() {
        return EntityType.HORSE;
    }
}
