package io.github.gaming32.fabricspigot.api.inventory;

import io.github.gaming32.fabricspigot.api.FabricUnsafeValues;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.item.Item;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@DelegateDeserialization(ItemStack.class)
public class FabricItemStack extends ItemStack {
    @SuppressWarnings("deprecation")
    public static net.minecraft.item.ItemStack toVanilla(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) {
            return net.minecraft.item.ItemStack.EMPTY;
        }
        final Item item = FabricUnsafeValues.getItem(stack.getType(), stack.getDurability());
        if (item == null) {
            return net.minecraft.item.ItemStack.EMPTY;
        }
        final net.minecraft.item.ItemStack result = new net.minecraft.item.ItemStack(item, stack.getAmount());
        if (stack.hasItemMeta()) {
            setItemMeta(result, stack.getItemMeta());
        }
        return result;
    }

    public static boolean setItemMeta(net.minecraft.item.ItemStack item, ItemMeta meta) {
        if (item == null) return false;
        throw new NotImplementedYet();
    }
}
