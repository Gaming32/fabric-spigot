package io.github.gaming32.fabricspigot.features;

import net.minecraft.util.Identifier;

// TODO: Move to vanillaimpl
public interface CraftingManagerExtras {
    void clearRecipes();

    boolean removeRecipe(Identifier recipe);
}
