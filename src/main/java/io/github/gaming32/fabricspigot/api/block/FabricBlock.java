package io.github.gaming32.fabricspigot.api.block;

import net.minecraft.util.math.Direction;
import org.bukkit.block.BlockFace;

public class FabricBlock {
    public static BlockFace notchToBlockFace(Direction notch) {
        if (notch == null) {
            return BlockFace.SELF;
        }
        return switch (notch) {
            case DOWN -> BlockFace.DOWN;
            case UP -> BlockFace.UP;
            case NORTH -> BlockFace.NORTH;
            case SOUTH -> BlockFace.SOUTH;
            case WEST -> BlockFace.WEST;
            case EAST -> BlockFace.EAST;
        };
    }
}
