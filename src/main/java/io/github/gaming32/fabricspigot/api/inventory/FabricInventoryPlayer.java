package io.github.gaming32.fabricspigot.api.inventory;

import com.google.common.base.Preconditions;
import io.github.gaming32.fabricspigot.api.entity.FabricPlayer;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FabricInventoryPlayer extends FabricInventory implements PlayerInventory, EntityEquipment {
    public FabricInventoryPlayer(net.minecraft.entity.player.PlayerInventory inventory) {
        super(inventory);
    }

    @Override
    public net.minecraft.entity.player.PlayerInventory getInventory() {
        return (net.minecraft.entity.player.PlayerInventory)inventory;
    }

    @NotNull
    @Override
    public ItemStack[] getStorageContents() {
        return asFabricMirror(getInventory().main);
    }

    @NotNull
    @Override
    public ItemStack getItemInMainHand() {
        throw new NotImplementedYet("FabricItemStack.asFabricMirror");
//        return FabricItemStack.asFabricMirror(getInventory().getMainHandStack());
    }

    @Override
    public void setItemInMainHand(@Nullable ItemStack item) {
        setItem(getHeldItemSlot(), item);
    }

    @Override
    public void setItemInMainHand(@Nullable ItemStack item, boolean silent) {
        setItemInMainHand(item);
    }

    @NotNull
    @Override
    public ItemStack getItemInOffHand() {
        throw new NotImplementedYet("FabricItemStack.asFabricMirror");
//        return FabricItemStack.asFabricMirror(getInventory().offHand.get(0));
    }

    @Override
    public void setItemInOffHand(@Nullable ItemStack item) {
        final ItemStack[] extra = getExtraContents();
        extra[0] = item;
        setExtraContents(extra);
    }

    @Override
    public void setItemInOffHand(@Nullable ItemStack item, boolean silent) {
        setItemInOffHand(item);
    }

    @NotNull
    @Override
    public ItemStack getItemInHand() {
        return getItemInMainHand();
    }

    @Override
    public void setItemInHand(@Nullable ItemStack stack) {
        setItemInMainHand(stack);
    }

    @Override
    public void setItem(int index, @Nullable ItemStack item) {
        super.setItem(index, item);
        if (getHolder() == null) return;
        final ServerPlayerEntity player = ((FabricPlayer)getHolder()).getHandle();
        if (player.networkHandler == null) return;

        if (index < net.minecraft.entity.player.PlayerInventory.getHotbarSize()) {
            index += 36;
        } else if (index > 39) {
            index += 5;
        } else if (index > 35) {
            index = 8 - (index - 36);
        }
        player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(
            player.playerScreenHandler.syncId,
            player.playerScreenHandler.nextRevision(),
            index,
            FabricItemStack.toVanilla(item)
        ));
    }

    @Override
    public void setItem(@NotNull EquipmentSlot slot, @Nullable ItemStack item) {
        //noinspection ConstantValue
        Preconditions.checkArgument(slot != null, "slot must not be null");

        switch (slot) {
            case HAND -> setItemInMainHand(item);
            case OFF_HAND -> setItemInOffHand(item);
            case FEET -> setBoots(item);
            case LEGS -> setLeggings(item);
            case CHEST -> setChestplate(item);
            case HEAD -> setHelmet(item);
            default -> throw new IllegalArgumentException("Not implemented. This is a bug");
        }
    }

    @Override
    public void setItem(@NotNull EquipmentSlot slot, @Nullable ItemStack item, boolean silent) {
        setItem(slot, item);
    }

    @Nullable
    @Override
    @SuppressWarnings("NullableProblems") // EntityEquipment defines this method as being NotNull, which is incorrect (and also not what PlayerInventory says).
    public ItemStack getItem(@NotNull EquipmentSlot slot) {
        //noinspection ConstantValue
        Preconditions.checkArgument(slot != null, "slot must not be null");

        return switch (slot) {
            case HAND -> getItemInMainHand();
            case OFF_HAND -> getItemInOffHand();
            case FEET -> getBoots();
            case LEGS -> getLeggings();
            case CHEST -> getChestplate();
            case HEAD -> getHelmet();
        };
    }

    @Override
    public int getHeldItemSlot() {
        return getInventory().selectedSlot;
    }

    @Override
    public void setHeldItemSlot(int slot) {
        Validate.isTrue(slot >= 0 && slot < net.minecraft.entity.player.PlayerInventory.getHotbarSize(), "Slot is not between 0 and 8 inclusive");
        getInventory().selectedSlot = slot;
        //noinspection DataFlowIssue
        ((FabricPlayer)getHolder()).getHandle().networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(slot));
    }

    @Nullable
    @Override
    public ItemStack getHelmet() {
        return getItem(getSize() - 2);
    }

    @Nullable
    @Override
    public ItemStack getChestplate() {
        return getItem(getSize() - 3);
    }

    @Nullable
    @Override
    public ItemStack getLeggings() {
        return getItem(getSize() - 4);
    }

    @Nullable
    @Override
    public ItemStack getBoots() {
        return getItem(getSize() - 5);
    }

    @Override
    public void setHelmet(@Nullable ItemStack helmet) {
        setItem(getSize() - 2, helmet);
    }

    @Override
    public void setHelmet(@Nullable ItemStack helmet, boolean silent) {
        setHelmet(helmet);
    }

    @Override
    public void setChestplate(@Nullable ItemStack chestplate) {
        setItem(getSize() - 3, chestplate);
    }

    @Override
    public void setChestplate(@Nullable ItemStack chestplate, boolean silent) {
        setChestplate(chestplate);
    }

    @Override
    public void setLeggings(@Nullable ItemStack leggings) {
        setItem(getSize() - 4, leggings);
    }

    @Override
    public void setLeggings(@Nullable ItemStack leggings, boolean silent) {
        setLeggings(leggings);
    }

    @Override
    public void setBoots(@Nullable ItemStack boots) {
        setItem(getSize() - 5, boots);
    }

    @Override
    public void setBoots(@Nullable ItemStack boots, boolean silent) {
        setBoots(boots);
    }

    @NotNull
    @Override
    public ItemStack[] getArmorContents() {
        return asFabricMirror(getInventory().armor);
    }

    private void setSlots(ItemStack[] items, int baseSlot, int length) {
        if (items == null) {
            items = new ItemStack[length];
        }
        Preconditions.checkArgument(items.length <= length, "items.length must be < %s", length);

        for (int i = 0; i < length; i++) {
            if (i >= items.length) {
                setItem(baseSlot + i, null);
            } else {
                setItem(baseSlot + i, items[i]);
            }
        }
    }

    @Override
    public void setStorageContents(@NotNull ItemStack[] items) throws IllegalArgumentException {
        setSlots(items, 0, getInventory().main.size());
    }

    @Override
    public void setArmorContents(@Nullable ItemStack[] items) {
        setSlots(items, getInventory().main.size(), getInventory().armor.size());
    }

    @NotNull
    @Override
    public ItemStack[] getExtraContents() {
        return asFabricMirror(getInventory().offHand);
    }

    @Override
    public void setExtraContents(@Nullable ItemStack[] items) {
        setSlots(items, getInventory().main.size() + getInventory().armor.size(), getInventory().offHand.size());
    }

    @Override
    public HumanEntity getHolder() {
        return (HumanEntity)inventory.getOwner();
    }

    @Override
    public float getItemInHandDropChance() {
        return getItemInMainHandDropChance();
    }

    @Override
    public void setItemInHandDropChance(float chance) {
        setItemInMainHandDropChance(chance);
    }

    @Override
    public float getItemInMainHandDropChance() {
        return 1;
    }

    @Override
    public void setItemInMainHandDropChance(float chance) {
        throw new UnsupportedOperationException("Cannot set drop chance for PlayerInventory");
    }

    @Override
    public float getItemInOffHandDropChance() {
        return 1;
    }

    @Override
    public void setItemInOffHandDropChance(float chance) {
        throw new UnsupportedOperationException("Cannot set drop chance for PlayerInventory");
    }

    @Override
    public float getHelmetDropChance() {
        return 1;
    }

    @Override
    public void setHelmetDropChance(float chance) {
        throw new UnsupportedOperationException("Cannot set drop chance for PlayerInventory");
    }

    @Override
    public float getChestplateDropChance() {
        return 1;
    }

    @Override
    public void setChestplateDropChance(float chance) {
        throw new UnsupportedOperationException("Cannot set drop chance for PlayerInventory");
    }

    @Override
    public float getLeggingsDropChance() {
        return 1;
    }

    @Override
    public void setLeggingsDropChance(float chance) {
        throw new UnsupportedOperationException("Cannot set drop chance for PlayerInventory");
    }

    @Override
    public float getBootsDropChance() {
        return 1;
    }

    @Override
    public void setBootsDropChance(float chance) {
        throw new UnsupportedOperationException("Cannot set drop chance for PlayerInventory");
    }
}
