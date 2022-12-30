package io.github.gaming32.fabricspigot.api.entity;

import io.github.gaming32.fabricspigot.api.FabricServer;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.entity.passive.MerchantEntity;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FabricAbstractVillager extends FabricAgeable implements AbstractVillager, InventoryHolder {
    public FabricAbstractVillager(FabricServer server, MerchantEntity entity) {
        super(server, entity);
    }

    @Override
    public MerchantEntity getHandle() {
        return (MerchantEntity)entity;
    }

    @Override
    public String toString() {
        return "FabricAbstractVillager";
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public List<MerchantRecipe> getRecipes() {
        throw new NotImplementedYet();
    }

    @Override
    public void setRecipes(@NotNull List<MerchantRecipe> recipes) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public MerchantRecipe getRecipe(int i) throws IndexOutOfBoundsException {
        throw new NotImplementedYet();
    }

    @Override
    public void setRecipe(int i, @NotNull MerchantRecipe recipe) throws IndexOutOfBoundsException {
        throw new NotImplementedYet();
    }

    @Override
    public int getRecipeCount() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isTrading() {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public HumanEntity getTrader() {
        throw new NotImplementedYet();
    }
}
