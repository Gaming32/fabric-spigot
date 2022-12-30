package io.github.gaming32.fabricspigot.mixin;

import io.github.gaming32.fabricspigot.ext.InventoryExt;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerInventory.class)
public class MixinPlayerInventory implements InventoryExt {
    @Shadow @Final public PlayerEntity player;

    @Override
    public InventoryHolder getOwner() {
        return (HumanEntity)player.getBukkitEntity();
    }
}
