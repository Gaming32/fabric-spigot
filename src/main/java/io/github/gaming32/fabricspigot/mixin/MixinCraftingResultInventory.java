package io.github.gaming32.fabricspigot.mixin;

import io.github.gaming32.fabricspigot.ext.InventoryExt;
import net.minecraft.inventory.CraftingResultInventory;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CraftingResultInventory.class)
public class MixinCraftingResultInventory implements InventoryExt {
    @Override
    public InventoryHolder getOwner() {
        return null;
    }
}
