package io.github.gaming32.fabricspigot.api;

import io.github.gaming32.fabricspigot.api.generator.structure.FabricStructure;
import io.github.gaming32.fabricspigot.api.generator.structure.FabricStructureType;
import io.github.gaming32.fabricspigot.util.Conversion;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class FabricRegistry<B extends Keyed, M> implements Registry<B> {
    private final Map<NamespacedKey, B> cache = new HashMap<>();
    private final net.minecraft.registry.Registry<M> minecraftRegistry;
    private final BiFunction<NamespacedKey, M, B> minecraftToBukkit;

    public FabricRegistry(net.minecraft.registry.Registry<M> minecraftRegistry, BiFunction<NamespacedKey, M, B> minecraftToBukkit) {
        this.minecraftRegistry = minecraftRegistry;
        this.minecraftToBukkit = minecraftToBukkit;
    }

    @Nullable
    @Override
    public B get(@NotNull NamespacedKey key) {
        final B cached = cache.get(key);
        if (cached != null) return cached;

        final B bukkit = createBukkit(key, minecraftRegistry.getOrEmpty(Conversion.toIdentifier(key)).orElse(null));
        if (bukkit == null) return null;

        cache.put(key, bukkit);
        return bukkit;
    }

    @NotNull
    @Override
    public Iterator<B> iterator() {
        return values().iterator();
    }

    public B createBukkit(NamespacedKey key, M minecraft) {
        if (minecraft == null) return null;
        return minecraftToBukkit.apply(key, minecraft);
    }

    public Stream<B> values() {
        return minecraftRegistry.getIds().stream().map(id -> get(Conversion.toNamespacedKey(id)));
    }

    public static <B extends Keyed> Registry<?> createRegistry(Class<B> bukkitClass, DynamicRegistryManager registryHolder) {
        if (bukkitClass == Structure.class) {
            return new FabricRegistry<>(registryHolder.get(RegistryKeys.STRUCTURE), FabricStructure::new);
        }
        if (bukkitClass == StructureType.class) {
            return new FabricRegistry<>(Registries.STRUCTURE_TYPE, FabricStructureType::new);
        }
        return null;
    }
}
