package io.github.gaming32.fabricspigot.api.inventory;

import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public class FabricInventory implements Inventory {
    protected final net.minecraft.inventory.Inventory inventory;

    public FabricInventory(net.minecraft.inventory.Inventory inventory) {
        this.inventory = inventory;
    }

    public net.minecraft.inventory.Inventory getInventory() {
        return inventory;
    }

    @Override
    public int getSize() {
        return getInventory().size();
    }

    @Nullable
    @Override
    public ItemStack getItem(int index) {
        final net.minecraft.item.ItemStack item = getInventory().getStack(index);
        throw new NotImplementedYet("asFabricMirror");
//        return item.isEmpty() ? null :
    }

    protected ItemStack[] asFabricMirror(List<net.minecraft.item.ItemStack> mcItems) {
        final int size = mcItems.size();
        final ItemStack[] items = new ItemStack[size];

        for (int i = 0; i < size; i++) {
            final net.minecraft.item.ItemStack mcItem = mcItems.get(i);
            throw new NotImplementedYet("FabricItemStack.asFabricMirror");
//            items[i] = mcItem.isEmpty() ? null : FabricItemStack.as
        }

        return items;
    }

    @NotNull
    @Override
    public ItemStack[] getStorageContents() {
        return getContents();
    }

    @Override
    public void setStorageContents(@NotNull ItemStack[] items) throws IllegalArgumentException {
        setContents(items);
    }

    @NotNull
    @Override
    public ItemStack[] getContents() {
        throw new NotImplementedYet();
    }

    @Override
    public void setContents(@NotNull ItemStack[] items) throws IllegalArgumentException {
        if (getSize() < items.length) {
            throw new IllegalArgumentException("Invalid inventory size; expected " + getSize() + " or less");
        }

        for (int i = 0; i < getSize(); i++) {
            if (i >= items.length) {
                setItem(i, null);
            } else {
                setItem(i, items[i]);
            }
        }
    }

    @Override
    public void setItem(int index, @Nullable ItemStack item) {
        getInventory().setStack(index, FabricItemStack.toVanilla(item));
    }

    @Override
    public boolean contains(@NotNull Material material) throws IllegalArgumentException {
        Validate.notNull(material, "Material cannot be null");
        throw new NotImplementedYet("FabricLegacy.fromLegacy");
    }

    @Override
    public boolean contains(@Nullable ItemStack item) {
        if (item == null) {
            return false;
        }
        for (final ItemStack other : getStorageContents()) {
            if (item.equals(other)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(@NotNull Material material, int amount) throws IllegalArgumentException {
        Validate.notNull(material, "Material cannot be null");
        throw new NotImplementedYet("FabricLegacy.fromLegacy");
    }

    @Override
    public boolean contains(@Nullable ItemStack item, int amount) {
        if (item == null) {
            return false;
        }
        if (amount <= 0) {
            return true;
        }
        for (final ItemStack other : getStorageContents()) {
            if (item.equals(other) && --amount <= 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAtLeast(@Nullable ItemStack item, int amount) {
        if (item == null) {
            return false;
        }
        if (amount <= 0) {
            return true;
        }
        for (final ItemStack other : getStorageContents()) {
            if (item.isSimilar(other) && (amount -= other.getAmount()) <= 0) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    @Override
    public HashMap<Integer, ? extends ItemStack> all(@NotNull Material material) throws IllegalArgumentException {
        Validate.notNull(material, "Material cannot be null");
        throw new NotImplementedYet("FabricLegacy.fromLegacy");
    }

    @NotNull
    @Override
    public HashMap<Integer, ? extends ItemStack> all(@Nullable ItemStack item) {
        final HashMap<Integer, ItemStack> slots = new HashMap<>();
        if (item != null) {
            final ItemStack[] inventory = getStorageContents();
            for (int i = 0; i < inventory.length; i++) {
                if (item.equals(inventory[i])) {
                    slots.put(i, inventory[i]);
                }
            }
        }
        return slots;
    }

    @Override
    public int first(@NotNull Material material) throws IllegalArgumentException {
        Validate.notNull(material, "Material cannot be null");
        throw new NotImplementedYet("FabricLegacy.fromLegacy");
    }

    @Override
    public int first(@NotNull ItemStack item) {
        return first(item, true);
    }

    private int first(ItemStack item, boolean withAmount) {
        if (item == null) {
            return -1;
        }
        final ItemStack[] inventory = getStorageContents();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) continue;

            if (withAmount ? item.equals(inventory[i]) : item.isSimilar(inventory[i])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int firstEmpty() {
        final ItemStack[] inventory = getStorageContents();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    private int firstPartial(ItemStack item) {
        final ItemStack[] inventory = getStorageContents();
        throw new NotImplementedYet("FabricItemStack.asFabricCopy");
    }

    @NotNull
    @Override
    public HashMap<Integer, ItemStack> addItem(@NotNull ItemStack... items) throws IllegalArgumentException {
        Validate.noNullElements(items, "Item cannot be null");
        final HashMap<Integer, ItemStack> leftover = new HashMap<>();

        for (int i = 0; i < items.length; i++) {
            final ItemStack item = items[i];
            while (true) {
                final int firstPartial = firstPartial(item);

                if (firstPartial == -1) {
                    final int firstFree = firstEmpty();

                    if (firstFree == -1) {
                        leftover.put(i, item);
                        break;
                    } else {
                        if (item.getAmount() > getMaxItemStack()) {
                            throw new NotImplementedYet("FabricItemStack.asFabricCopy");
//                            final FabricItemStack stack = FabricItemStack.
                        } else {
                            setItem(firstFree, item);
                            break;
                        }
                    }
                } else {
                    final ItemStack partialItem = getItem(firstPartial);

                    final int amount = item.getAmount();
                    assert partialItem != null;
                    final int partialAmount = partialItem.getAmount();
                    final int maxAmount = partialItem.getMaxStackSize();

                    if (amount + partialAmount <= maxAmount) {
                        partialItem.setAmount(amount + partialAmount);
                        setItem(firstPartial, partialItem);
                        break;
                    }

                    partialItem.setAmount(maxAmount);
                    setItem(firstPartial, partialItem);
                    item.setAmount(amount + partialAmount - maxAmount);
                }
            }
        }
        return leftover;
    }

    @NotNull
    @Override
    public HashMap<Integer, ItemStack> removeItem(@NotNull ItemStack... items) throws IllegalArgumentException {
        Validate.notNull(items, "Items cannot be null");
        final HashMap<Integer, ItemStack> leftover = new HashMap<>();

        for (int i = 0; i < items.length; i++) {
            final ItemStack item = items[i];
            int toDelete = item.getAmount();

            while (true) {
                final int first = first(item, false);

                if (first == -1) {
                    item.setAmount(toDelete);
                    leftover.put(i, item);
                    break;
                } else {
                    final ItemStack itemStack = getItem(first);
                    assert itemStack != null;
                    final int amount = itemStack.getAmount();

                    if (amount <= toDelete) {
                        toDelete -= amount;
                        clear(first);
                    } else {
                        itemStack.setAmount(amount - toDelete);
                        setItem(first, itemStack);
                        toDelete = 0;
                    }
                }

                if (toDelete <= 0) break;
            }
        }
        return leftover;
    }

    private int getMaxItemStack() {
        return getInventory().getMaxCountPerStack();
    }

    @Override
    public void remove(@NotNull Material material) throws IllegalArgumentException {
        Validate.notNull(material, "Material cannot be null");
        throw new NotImplementedYet("FabricLegacy.fromLegacy");
    }

    @Override
    public void remove(@NotNull ItemStack item) {
        final ItemStack[] items = getStorageContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].equals(item)) {
                clear(i);
            }
        }
    }

    @Override
    public void clear(int index) {
        setItem(index, null);
    }

    @Override
    public void clear() {
        for (int i = 0; i < getSize(); i++) {
            clear(i);
        }
    }

    @NotNull
    @Override
    public ListIterator<ItemStack> iterator() {
        return new InventoryIterator(this);
    }

    @NotNull
    @Override
    public ListIterator<ItemStack> iterator(int index) {
        if (index < 0) {
            index += getSize() + 1;
        }
        return new InventoryIterator(this, index);
    }

    @NotNull
    @Override
    public List<HumanEntity> getViewers() {
        throw new NotImplementedYet("Inventory.getViewers");
//        return inventory.getViewers();
    }

    @NotNull
    @Override
    public InventoryType getType() {
        if (inventory instanceof CraftingInventory) {
            return inventory.size() >= 9 ? InventoryType.WORKBENCH : InventoryType.CRAFTING;
        } else if (inventory instanceof PlayerInventory) {
            return InventoryType.PLAYER;
        } else if (inventory instanceof DropperBlockEntity) {
            return InventoryType.DROPPER;
        } else if (inventory instanceof DispenserBlockEntity) {
            return InventoryType.DISPENSER;
        } else if (inventory instanceof BlastFurnaceBlockEntity) {
            return InventoryType.BLAST_FURNACE;
        } else if (inventory instanceof SmokerBlockEntity) {
            return InventoryType.SMOKER;
        } else if (inventory instanceof AbstractFurnaceBlockEntity) {
            return InventoryType.FURNACE;
        } throw new NotImplementedYet("FabricInventoryEnchanting"); /* else if (this instanceof CraftInventoryEnchanting) {
            return InventoryType.ENCHANTING;
        } else if (inventory instanceof TileEntityBrewingStand) {
            return InventoryType.BREWING;
        } else if (inventory instanceof CraftInventoryCustom.MinecraftInventory) {
            return ((CraftInventoryCustom.MinecraftInventory) inventory).getType();
        } else if (inventory instanceof InventoryEnderChest) {
            return InventoryType.ENDER_CHEST;
        } else if (inventory instanceof InventoryMerchant) {
            return InventoryType.MERCHANT;
        } else if (this instanceof CraftInventoryBeacon) {
            return InventoryType.BEACON;
        } else if (this instanceof CraftInventoryAnvil) {
            return InventoryType.ANVIL;
        } else if (this instanceof CraftInventorySmithing) {
            return InventoryType.SMITHING;
        } else if (inventory instanceof IHopper) {
            return InventoryType.HOPPER;
        } else if (inventory instanceof TileEntityShulkerBox) {
            return InventoryType.SHULKER_BOX;
        } else if (inventory instanceof TileEntityBarrel) {
            return InventoryType.BARREL;
        } else if (inventory instanceof TileEntityLectern.LecternInventory) {
            return InventoryType.LECTERN;
        } else if (inventory instanceof ChiseledBookShelfBlockEntity) {
            return InventoryType.CHISELED_BOOKSHELF;
        } else if (this instanceof CraftInventoryLoom) {
            return InventoryType.LOOM;
        } else if (this instanceof CraftInventoryCartography) {
            return InventoryType.CARTOGRAPHY;
        } else if (this instanceof CraftInventoryGrindstone) {
            return InventoryType.GRINDSTONE;
        } else if (this instanceof CraftInventoryStonecutter) {
            return InventoryType.STONECUTTER;
        } else if (inventory instanceof BlockComposter.ContainerEmpty || inventory instanceof BlockComposter.ContainerInput || inventory instanceof BlockComposter.ContainerOutput) {
            return InventoryType.COMPOSTER;
        } else {
            return InventoryType.CHEST;
        } */
    }

    @Nullable
    @Override
    public InventoryHolder getHolder() {
        return inventory.getOwner();
    }

    @Override
    public int getMaxStackSize() {
        return inventory.getMaxCountPerStack();
    }

    @Override
    public void setMaxStackSize(int size) {
        throw new NotImplementedYet("Inventory.setMaxCountPerStack");
//        inventory.setMaxCountPerStack(size);
    }

    @Nullable
    @Override
    public Location getLocation() {
        throw new NotImplementedYet("Inventory.getLocation");
//        return inventory.getLocation();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FabricInventory other && other.inventory.equals(inventory);
    }
}
