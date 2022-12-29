package io.github.gaming32.fabricspigot.mixin;

import com.google.common.collect.ImmutableMap;
import io.github.gaming32.fabricspigot.ext.RecipeManagerExt;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.function.Function;

@Mixin(RecipeManager.class)
public class MixinRecipeManager implements RecipeManagerExt {
    @Shadow private Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipes;

    @Shadow private Map<Identifier, Recipe<?>> recipesById;

    @Override
    public void clearRecipes() {
        recipes = Registries.RECIPE_TYPE.stream()
            .collect(ImmutableMap.toImmutableMap(
                Function.identity(),
                type -> ImmutableMap.of()
            ));
        recipesById = ImmutableMap.of();
    }

    @Override
    public boolean removeRecipe(Identifier recipe) {
        recipes = recipes.entrySet().stream()
            .collect(ImmutableMap.toImmutableMap(
                Map.Entry::getKey,
                values -> values.getValue().entrySet().stream()
                    .filter(e -> !e.getKey().equals(recipe))
                    .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue))
            ));
        final boolean[] anyRemoved = {false};
        recipesById = recipesById.entrySet().stream()
            .filter(e -> {
                if (e.getKey().equals(recipe)) {
                    anyRemoved[0] = true;
                    return false;
                }
                return true;
            })
            .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));
        return anyRemoved[0];
    }
}
