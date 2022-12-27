package io.github.gaming32.fabricspigot.features;

import net.minecraft.util.Identifier;

public interface CraftingManagerExtras {
    void clearRecipes();

    boolean removeRecipe(Identifier recipe);
}
