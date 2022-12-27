package io.github.gaming32.fabricspigot.api.generator.structure;

import io.github.gaming32.fabricspigot.util.Conversion;
import net.minecraft.registry.Registries;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;
import org.jetbrains.annotations.NotNull;

public class FabricStructureType extends StructureType {
    private final NamespacedKey key;
    private final net.minecraft.world.gen.structure.StructureType<?> structureType;

    public FabricStructureType(NamespacedKey key, net.minecraft.world.gen.structure.StructureType<?> structureType) {
        this.key = key;
        this.structureType = structureType;
    }

    public net.minecraft.world.gen.structure.StructureType<?> getHandle() {
        return structureType;
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return key;
    }

    public static StructureType minecraftToBukkit(net.minecraft.world.gen.structure.StructureType<?> minecraft) {
        if (minecraft == null) return null;
        //noinspection DataFlowIssue
        return Registry.STRUCTURE_TYPE.get(Conversion.toNamespacedKey(Registries.STRUCTURE_TYPE.getId(minecraft)));
    }

    public static net.minecraft.world.gen.structure.StructureType<?> bukkitToMinecraft(StructureType bukkit) {
        if (bukkit == null) return null;
        return ((FabricStructureType)bukkit).getHandle();
    }
}
