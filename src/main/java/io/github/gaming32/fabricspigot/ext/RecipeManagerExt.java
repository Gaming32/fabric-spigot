package io.github.gaming32.fabricspigot.ext;

import net.minecraft.util.Identifier;

public interface RecipeManagerExt {
    void clearRecipes();

    boolean removeRecipe(Identifier recipe);
}
