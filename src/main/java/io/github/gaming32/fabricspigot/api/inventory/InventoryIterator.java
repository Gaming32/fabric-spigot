package io.github.gaming32.fabricspigot.api.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ListIterator;

public class InventoryIterator implements ListIterator<ItemStack> {
    private final Inventory inventory;
    private int nextIndex;
    private Boolean lastDirection;

    InventoryIterator(Inventory inventory) {
        this.inventory = inventory;
        nextIndex = 0;
    }

    InventoryIterator(Inventory inventory, int index) {
        this.inventory = inventory;
        nextIndex = index;
    }

    @Override
    public boolean hasNext() {
        return nextIndex < inventory.getSize();
    }

    @Override
    public ItemStack next() {
        lastDirection = true;
        return inventory.getItem(nextIndex++);
    }

    @Override
    public int nextIndex() {
        return nextIndex;
    }

    @Override
    public boolean hasPrevious() {
        return nextIndex > 0;
    }

    @Override
    public ItemStack previous() {
        lastDirection = false;
        return inventory.getItem(--nextIndex);
    }

    @Override
    public int previousIndex() {
        return nextIndex - 1;
    }

    @Override
    public void set(ItemStack item) {
        if (lastDirection == null) {
            throw new IllegalStateException("No current item!");
        }
        final int i = lastDirection ? nextIndex - 1 : nextIndex;
        inventory.setItem(i, item);
    }

    @Override
    public void add(ItemStack itemStack) {
        throw new UnsupportedOperationException("Can't change the size of an inventory!");
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Can't change the size of an inventory!");
    }
}
