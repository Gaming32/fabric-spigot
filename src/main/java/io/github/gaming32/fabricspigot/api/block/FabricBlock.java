package io.github.gaming32.fabricspigot.api.block;

import com.google.common.base.Preconditions;
import io.github.gaming32.fabricspigot.api.FabricUnsafeValues;
import io.github.gaming32.fabricspigot.api.FabricWorld;
import io.github.gaming32.fabricspigot.api.entity.FabricPlayer;
import io.github.gaming32.fabricspigot.api.inventory.FabricItemStack;
import io.github.gaming32.fabricspigot.ext.ServerWorldExt;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import net.minecraft.world.WorldAccess;
import org.apache.commons.lang3.Validate;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FabricBlock implements Block {
    private final WorldAccess world;
    private final BlockPos position;

    public FabricBlock(WorldAccess world, BlockPos position) {
        this.world = world;
        this.position = position.toImmutable();
    }

    public static FabricBlock at(WorldAccess world, BlockPos pos) {
        return new FabricBlock(world, pos);
    }

    public BlockState getNMS() {
        return world.getBlockState(position);
    }

    public BlockPos getPosition() {
        return position;
    }

    public WorldAccess getHandle() {
        return world;
    }

    @NotNull
    @Override
    public World getWorld() {
        return ((ServerWorldExt)world.getMinecraftWorld()).getBukkitWorld();
    }

    public FabricWorld getFabricWorld() {
        return (FabricWorld)getWorld();
    }

    @NotNull
    @Override
    public Location getLocation() {
        return new Location(getWorld(), position.getX(), position.getY(), position.getZ());
    }

    @Nullable
    @Override
    public Location getLocation(@Nullable Location loc) {
        if (loc != null) {
            loc.setWorld(getWorld());
            loc.setX(position.getX());
            loc.setY(position.getY());
            loc.setZ(position.getZ());
            loc.setYaw(0);
            loc.setPitch(0);
        }
        return loc;
    }

    public BlockVector getVector() {
        return new BlockVector(getX(), getY(), getZ());
    }

    @Override
    public int getX() {
        return position.getX();
    }

    @Override
    public int getY() {
        return position.getY();
    }

    @Override
    public int getZ() {
        return position.getZ();
    }

    @NotNull
    @Override
    public Chunk getChunk() {
        return getWorld().getChunkAt(this);
    }

    public void setData(byte data) {
        setData(data, 3);
    }

    public void setData(byte data, boolean applyPhysics) {
        if (applyPhysics) {
            setData(data, 3);
        } else {
            setData(data, 2);
        }
    }

    private void setData(byte data, int flag) {
        throw new NotImplementedYet("FabricUnsafeValues.getBlock");
//        world.setBlockState(position, FabricUnsafeValues.getBlock(getType(), data), flag);
    }

    @Override
    public byte getData() {
        final BlockState state = world.getBlockState(position);
        throw new NotImplementedYet("FabricUnsafeValues.toLegacyData");
//        return FabricUnsafeValues.toLegacyData(state);
    }

    @NotNull
    @Override
    public BlockData getBlockData() {
        throw new NotImplementedYet("FabricBlockData.fromData");
//        return FabricBlockData.fromData(getNMS());
    }

    @Override
    public void setType(@NotNull Material type) {
        setType(type, true);
    }

    @Override
    public void setType(@NotNull Material type, boolean applyPhysics) {
        //noinspection ConstantValue
        Preconditions.checkArgument(type != null, "Material cannot be null");
        setBlockData(type.createBlockData(), applyPhysics);
    }

    @Override
    public void setBlockData(@NotNull BlockData data) {
        setBlockData(data, true);
    }

    @Override
    public void setBlockData(@NotNull BlockData data, boolean applyPhysics) {
        //noinspection ConstantValue
        Preconditions.checkArgument(data != null, "BlockData cannot be null");
        throw new NotImplementedYet("FabricBlockData");
//        setTypeAndData(((FabricBlockData)data).getState(), applyPhysics);
    }

    boolean setTypeAndData(BlockState state, boolean applyPhysics) {
        return setTypeAndData(world, position, getNMS(), state, applyPhysics);
    }

    public static boolean setTypeAndData(WorldAccess world, BlockPos position, BlockState old, BlockState state, boolean applyPhysics) {
        if (old.hasBlockEntity() && state.getBlock() != old.getBlock()) {
            if (world instanceof net.minecraft.world.World impl) {
                impl.removeBlockEntity(position);
            } else {
                world.setBlockState(position, Blocks.AIR.getDefaultState(), 0);
            }
        }

        if (applyPhysics) {
            return world.setBlockState(position, state, 3);
        } else {
            final boolean success = world.setBlockState(position, state, 2 | 16 | 1024);
            if (success && world instanceof net.minecraft.world.World) {
                world.getMinecraftWorld().updateListeners(position, old, state, 3);
            }
            return success;
        }
    }

    @NotNull
    @Override
    public Material getType() {
        return FabricUnsafeValues.getMaterial(world.getBlockState(position).getBlock());
    }

    @Override
    public byte getLightLevel() {
        return (byte)world.getMinecraftWorld().getLightLevel(position);
    }

    @Override
    public byte getLightFromSky() {
        return (byte)world.getLightLevel(LightType.SKY, position);
    }

    @Override
    public byte getLightFromBlocks() {
        return (byte)world.getLightLevel(LightType.BLOCK, position);
    }

    public Block getFace(BlockFace face) {
        return getRelative(face, 1);
    }

    public Block getFace(BlockFace face, int distance) {
        return getRelative(face, distance);
    }

    @NotNull
    @Override
    public Block getRelative(int modX, int modY, int modZ) {
        return getWorld().getBlockAt(getX() + modX, getY() + modY, getZ() + modZ);
    }

    @NotNull
    @Override
    public Block getRelative(@NotNull BlockFace face) {
        return getRelative(face, 1);
    }

    @NotNull
    @Override
    public Block getRelative(@NotNull BlockFace face, int distance) {
        return getRelative(face.getModX() * distance, face.getModY() * distance, face.getModZ() * distance);
    }

    @Nullable
    @Override
    public BlockFace getFace(@NotNull Block block) {
        final BlockFace[] values = BlockFace.values();

        for (final BlockFace face : values) {
            if (getX() + face.getModX() == block.getX() && getY() + face.getModY() == block.getY() && getZ() + face.getModZ() == block.getZ()) {
                return face;
            }
        }

        return null;
    }

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

    @NotNull
    @Override
    public org.bukkit.block.BlockState getState() {
        throw new NotImplementedYet("FabricBlockStates.getBlockState");
    }

    @NotNull
    @Override
    public Biome getBiome() {
        return getWorld().getBiome(getX(), getY(), getZ());
    }

    @Override
    public void setBiome(@NotNull Biome bio) {
        getWorld().setBiome(getX(), getY(), getZ(), bio);
    }

    @Override
    @SuppressWarnings("deprecation")
    public double getTemperature() {
        return world.getBiome(position).value().getTemperature(position);
    }

    @Override
    public double getHumidity() {
        return getWorld().getHumidity(getX(), getY(), getZ());
    }

    @Override
    public boolean isBlockPowered() {
        return world.getMinecraftWorld().getReceivedRedstonePower(position) > 0;
    }

    @Override
    public boolean isBlockIndirectlyPowered() {
        return world.getMinecraftWorld().isReceivingRedstonePower(position);
    }

    @Override
    public boolean isBlockFacePowered(@NotNull BlockFace face) {
        throw new NotImplementedYet("blockFaceToNotch");
//        return world.getMinecraftWorld().isEmittingRedstonePower(position, blockFaceToNotch(face));
    }

    @Override
    public boolean isBlockFaceIndirectlyPowered(@NotNull BlockFace face) {
        throw new NotImplementedYet("blockFaceToNotch");
//        final int power = world.getMinecraftWorld().getSignal(position, blockFaceToNotch(face));
    }

    @Override
    public int getBlockPower(@NotNull BlockFace face) {
        int power = 0;
        final net.minecraft.world.World world = this.world.getMinecraftWorld();
        final int x = getX();
        final int y = getY();
        final int z = getZ();
        if ((face == BlockFace.DOWN || face == BlockFace.SELF) && world.isEmittingRedstonePower(new BlockPos(x, y - 1, z), Direction.DOWN)) power = getPower(power, world.getBlockState(new BlockPos(x, y - 1, z)));
        if ((face == BlockFace.UP || face == BlockFace.SELF) && world.isEmittingRedstonePower(new BlockPos(x, y + 1, z), Direction.UP)) power = getPower(power, world.getBlockState(new BlockPos(x, y + 1, z)));
        if ((face == BlockFace.EAST || face == BlockFace.SELF) && world.isEmittingRedstonePower(new BlockPos(x + 1, y, z), Direction.EAST)) power = getPower(power, world.getBlockState(new BlockPos(x + 1, y, z)));
        if ((face == BlockFace.WEST || face == BlockFace.SELF) && world.isEmittingRedstonePower(new BlockPos(x - 1, y, z), Direction.WEST)) power = getPower(power, world.getBlockState(new BlockPos(x - 1, y, z)));
        if ((face == BlockFace.NORTH || face == BlockFace.SELF) && world.isEmittingRedstonePower(new BlockPos(x, y, z - 1), Direction.NORTH)) power = getPower(power, world.getBlockState(new BlockPos(x, y, z - 1)));
        if ((face == BlockFace.SOUTH || face == BlockFace.SELF) && world.isEmittingRedstonePower(new BlockPos(x, y, z + 1), Direction.SOUTH)) power = getPower(power, world.getBlockState(new BlockPos(x, y, z + 1)));
        return power > 0 ? power : (face == BlockFace.SELF ? isBlockIndirectlyPowered() : isBlockFaceIndirectlyPowered(face)) ? 15 : 0;
    }

    private static int getPower(int i, BlockState state) {
        if (!state.isOf(Blocks.REDSTONE_WIRE)) {
            return i;
        }
        final int j = state.get(RedstoneWireBlock.POWER);
        return Math.max(j, i);
    }

    @Override
    public int getBlockPower() {
        return getBlockPower(BlockFace.SELF);
    }

    @Override
    public boolean isEmpty() {
        return getNMS().isAir();
    }

    @Override
    public boolean isLiquid() {
        return getNMS().getMaterial().isLiquid();
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public PistonMoveReaction getPistonMoveReaction() {
        //noinspection DataFlowIssue
        return PistonMoveReaction.getById(getNMS().getPistonBehavior().ordinal());
    }

    @Override
    public boolean breakNaturally() {
        return breakNaturally(null);
    }

    @Override
    public boolean breakNaturally(@Nullable ItemStack tool) {
        final BlockState state = getNMS();
        final net.minecraft.block.Block block = state.getBlock();
        final net.minecraft.item.ItemStack nmsItem = FabricItemStack.toVanilla(tool);
        boolean result = false;

        if (block != Blocks.AIR && (tool == null || !state.isToolRequired() || nmsItem.isSuitableFor(state))) {
            net.minecraft.block.Block.dropStacks(state, world.getMinecraftWorld(), position, world.getBlockEntity(position), null, nmsItem);
            result = true;
        }

        return world.setBlockState(position, Blocks.AIR.getDefaultState(), 3) && result;
    }

    @Override
    public boolean applyBoneMeal(@NotNull BlockFace face) {
        throw new NotImplementedYet("blockFaceToNotch");
//        final Direction direction = blockFaceToNotch(face);
    }

    @NotNull
    @Override
    public Collection<ItemStack> getDrops() {
        return getDrops(null);
    }

    @NotNull
    @Override
    public Collection<ItemStack> getDrops(@Nullable ItemStack tool) {
        //noinspection DataFlowIssue // The two-argument method should probably have its first argumented annotated Nullable.
        return getDrops(tool, null);
    }

    @NotNull
    @Override
    public Collection<ItemStack> getDrops(@NotNull ItemStack tool, @Nullable Entity entity) {
        final BlockState state = getNMS();
        final net.minecraft.item.ItemStack nms = FabricItemStack.toVanilla(tool);

        //noinspection ConstantValue
        if (tool == null || isPreferredTool(state, nms)) {
            throw new NotImplementedYet("FabricItemStack.asFabricCopy");
//            return net.minecraft.block.Block.getDroppedStacks(
//                state, (ServerWorld)world.getMinecraftWorld(), position, world.getBlockEntity(position),
//                entity != null ? ((FabricEntity)entity).getHandle() : null, nms
//            ).stream()
//                .map(FabricItemStack::as)
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean isPreferredTool(@NotNull ItemStack tool) {
        final BlockState state = getNMS();
        final net.minecraft.item.ItemStack nms = FabricItemStack.toVanilla(tool);
        return isPreferredTool(state, nms);
    }

    @Override
    public float getBreakSpeed(@NotNull Player player) {
        //noinspection ConstantValue
        Preconditions.checkArgument(player != null, "player cannot be null");
        return getNMS().calcBlockBreakingDelta(((FabricPlayer)player).getHandle(), world, position);
    }

    private boolean isPreferredTool(BlockState state, net.minecraft.item.ItemStack nmsItem) {
        return !state.isToolRequired() || nmsItem.isSuitableFor(state);
    }

    @Override
    public void setMetadata(@NotNull String metadataKey, @NotNull MetadataValue newMetadataValue) {
        throw new NotImplementedYet("FabricWorld.getBlockMetadata");
//        getFabricWorld().getBlockMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    @NotNull
    @Override
    public List<MetadataValue> getMetadata(@NotNull String metadataKey) {
        throw new NotImplementedYet("FabricWorld.getBlockMetadata");
//        return getFabricWorld().getBlockMetadata().getMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(@NotNull String metadataKey) {
        throw new NotImplementedYet("FabricWorld.getBlockMetadata");
//        return getFabricWorld().getBlockMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(@NotNull String metadataKey, @NotNull Plugin owningPlugin) {
        throw new NotImplementedYet("FabricWorld.getBlockMetadata()");
//        getFabricWorld().getBlockMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    @Override
    public boolean isPassable() {
        return getNMS().getCollisionShape(world, position).isEmpty();
    }

    @Nullable
    @Override
    public RayTraceResult rayTrace(@NotNull Location start, @NotNull Vector direction, double maxDistance, @NotNull FluidCollisionMode fluidCollisionMode) {
        Validate.notNull(start, "Start location is null!");
        Validate.isTrue(getWorld().equals(start.getWorld()), "Start location is from different world!");
        start.checkFinite();

        Validate.notNull(direction, "Direction is null!");
        direction.checkFinite();
        Validate.isTrue(direction.lengthSquared() > 0, "Direction's magnitude is 0!");

        Validate.notNull(fluidCollisionMode, "Fluid collision mode is null!");
        if (maxDistance < 0) return null;

        final Vector dir = direction.clone().normalize().multiply(maxDistance);
        final Vec3d startPos = new Vec3d(start.getX(), start.getY(), start.getZ());
        final Vec3d endPos = new Vec3d(start.getX() + dir.getX(), start.getY() + dir.getY(), start.getZ() + dir.getZ());

        throw new NotImplementedYet("FabricFluidCollisionMode.toNMS");
//        final HitResult nmsHitResult = world.raycast(new RaycastContext(
//            startPos, endPos, RaycastContext.ShapeType.OUTLINE,
//        ))
    }

    @NotNull
    @Override
    public BoundingBox getBoundingBox() {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public VoxelShape getCollisionShape() {
        throw new NotImplementedYet();
    }

    @Override
    public boolean canPlace(@NotNull BlockData data) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FabricBlock other)) {
            return false;
        }

        return position.equals(other.position) && getWorld().equals(other.getWorld());
    }
}
