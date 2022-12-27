package io.github.gaming32.fabricspigot.util;

import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import org.bukkit.HeightMap;
import org.bukkit.NamespacedKey;

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
}
