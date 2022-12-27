package io.github.gaming32.fabricspigot.api.generator.structure;

import io.github.gaming32.fabricspigot.util.Conversion;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;
import org.jetbrains.annotations.NotNull;

public class FabricStructure extends Structure {
    private final NamespacedKey key;
    private final net.minecraft.world.gen.structure.Structure structure;
    private final StructureType structureType;

    public FabricStructure(NamespacedKey key, net.minecraft.world.gen.structure.Structure structure) {
        this.key = key;
        this.structure = structure;
        throw new NotImplementedYet();
    }

    public net.minecraft.world.gen.structure.Structure getHandle() {
        return structure;
    }

    @NotNull
    @Override
    public StructureType getStructureType() {
        return structureType;
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return key;
    }

    public static Structure minecraftToBukkit(net.minecraft.world.gen.structure.Structure minecraft, DynamicRegistryManager registryHolder) {
        if (minecraft == null) return null;
        //noinspection DataFlowIssue
        return Registry.STRUCTURE.get(Conversion.toNamespacedKey(registryHolder.get(RegistryKeys.STRUCTURE).getId(minecraft)));
    }

    public static net.minecraft.world.gen.structure.Structure bukkitToMinecraft(Structure bukkit) {
        if (bukkit == null) return null;
        return ((FabricStructure)bukkit).getHandle();
    }
}
