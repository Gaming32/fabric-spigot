package io.github.gaming32.fabricspigot.util;

import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import org.bukkit.HeightMap;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.util.Vector;

public class Conversion {
    public static Identifier toIdentifier(NamespacedKey key) {
        return new Identifier(key.getNamespace(), key.getKey());
    }

    @SuppressWarnings("deprecation")
    public static NamespacedKey toNamespacedKey(Identifier id) {
        return new NamespacedKey(id.getNamespace(), id.getPath());
    }

    public static Heightmap.Type toHeightmapType(HeightMap heightMap) {
        return switch (heightMap) {
            case MOTION_BLOCKING_NO_LEAVES -> Heightmap.Type.MOTION_BLOCKING_NO_LEAVES;
            case OCEAN_FLOOR -> Heightmap.Type.OCEAN_FLOOR;
            case OCEAN_FLOOR_WG -> Heightmap.Type.OCEAN_FLOOR_WG;
            case WORLD_SURFACE -> Heightmap.Type.WORLD_SURFACE;
            case WORLD_SURFACE_WG -> Heightmap.Type.WORLD_SURFACE_WG;
            case MOTION_BLOCKING -> Heightmap.Type.MOTION_BLOCKING;
        };
    }

    public static Vec3d toVec3d(Vector vector) {
        return new Vec3d(vector.getX(), vector.getY(), vector.getZ());
    }

    public static Vector toVector(Vec3d vec3d) {
        return new Vector(vec3d.x, vec3d.y, vec3d.z);
    }

    public static Sound toSound(SoundEvent soundEvent) {
        //noinspection DataFlowIssue
        return Registry.SOUNDS.get(toNamespacedKey(Registries.SOUND_EVENT.getId(soundEvent)));
    }

    public static SpawnCategory toSpawnCategory(SpawnGroup spawnGroup) {
        return switch (spawnGroup) {
            case MONSTER -> SpawnCategory.MONSTER;
            case CREATURE -> SpawnCategory.ANIMAL;
            case AMBIENT -> SpawnCategory.AMBIENT;
            case AXOLOTLS -> SpawnCategory.AXOLOTL;
            case WATER_CREATURE -> SpawnCategory.WATER_ANIMAL;
            case WATER_AMBIENT -> SpawnCategory.WATER_AMBIENT;
            case UNDERGROUND_WATER_CREATURE -> SpawnCategory.WATER_UNDERGROUND_CREATURE;
            case MISC -> SpawnCategory.MISC;
        };
    }

    @SuppressWarnings("unchecked")
    public static <T> MemoryModuleType<T> toMemoryModuleType(MemoryKey<T> memoryKey) {
        return (MemoryModuleType<T>)Registries.MEMORY_MODULE_TYPE.get(toIdentifier(memoryKey.getKey()));
    }
}
