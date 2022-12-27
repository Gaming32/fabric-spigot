package io.github.gaming32.fabricspigot.api;

import com.google.common.base.Preconditions;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import io.github.gaming32.fabricspigot.util.RandomWrapper;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChorusFlowerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.RegionAccessor;
import org.bukkit.TreeType;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public abstract class FabricRegionAccessor implements RegionAccessor {
    public abstract StructureWorldAccess getHandle();

    public abstract void setBiome(int x, int y, int z, RegistryEntry<net.minecraft.world.biome.Biome> biomeBase);

    public abstract Iterable<Entity> getNMSEntities();

    public abstract void addEntityToWorld(Entity entity, CreatureSpawnEvent.SpawnReason reason);

    @NotNull
    @Override
    public Biome getBiome(@NotNull Location location) {
        return getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @NotNull
    @Override
    public Biome getBiome(int x, int y, int z) {
        throw new NotImplementedYet();
    }

    @Override
    public void setBiome(@NotNull Location location, @NotNull Biome biome) {
        setBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ(), biome);
    }

    @Override
    public void setBiome(int x, int y, int z, @NotNull Biome biome) {
        Preconditions.checkArgument(biome != Biome.CUSTOM, "Cannot set the biome to %s", biome);
        throw new NotImplementedYet();
//        final RegistryEntry<net.minecraft.world.biome.Biome> =
    }

    @NotNull
    @Override
    public BlockState getBlockState(@NotNull Location location) {
        return getBlockState(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @NotNull
    @Override
    public BlockState getBlockState(int x, int y, int z) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public BlockData getBlockData(@NotNull Location location) {
        return getBlockData(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @NotNull
    @Override
    public BlockData getBlockData(int x, int y, int z) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Material getType(@NotNull Location location) {
        return getType(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @NotNull
    @Override
    public Material getType(int x, int y, int z) {
        return FabricUnsafeValues.getMaterial(getData(x, y, z).getBlock());
    }

    private net.minecraft.block.BlockState getData(int x, int y, int z) {
        return getHandle().getBlockState(new BlockPos(x, y, z));
    }

    @Override
    public void setBlockData(@NotNull Location location, @NotNull BlockData blockData) {
        setBlockData(location.getBlockX(), location.getBlockY(), location.getBlockZ(), blockData);
    }

    @Override
    public void setBlockData(int x, int y, int z, @NotNull BlockData blockData) {
        final StructureWorldAccess world = getHandle();
        final BlockPos pos = new BlockPos(x, y, z);
        final net.minecraft.block.BlockState old = getHandle().getBlockState(pos);
        throw new NotImplementedYet();
    }

    @Override
    public void setType(@NotNull Location location, @NotNull Material material) {
        setType(location.getBlockX(), location.getBlockY(), location.getBlockZ(), material);
    }

    @Override
    public void setType(int x, int y, int z, @NotNull Material material) {
        setBlockData(x, y, z, material.createBlockData());
    }

    @Override
    public boolean generateTree(@NotNull Location location, @NotNull Random random, @NotNull TreeType type) {
        final BlockPos pos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        return generateTree(getHandle(), getHandle().toServerWorld().getChunkManager().getChunkGenerator(), pos, new RandomWrapper(random), type);
    }

    @Override
    public boolean generateTree(@NotNull Location location, @NotNull Random random, @NotNull TreeType type, @Nullable Consumer<BlockState> stateConsumer) {
        return generateTree(location, random, type, stateConsumer == null ? null : block -> {
            stateConsumer.accept(block);
            return true;
        });
    }

    @Override
    public boolean generateTree(@NotNull Location location, @NotNull Random random, @NotNull TreeType type, @Nullable Predicate<BlockState> statePredicate) {
        final BlockPos pos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        throw new NotImplementedYet();
    }

    public boolean generateTree(StructureWorldAccess access, ChunkGenerator chunkGenerator, BlockPos pos, net.minecraft.util.math.random.Random random, TreeType treeType) {
        final RegistryKey<ConfiguredFeature<?, ?>> gen = switch (treeType) {
            case BIG_TREE -> TreeConfiguredFeatures.FANCY_OAK;
            case BIRCH -> TreeConfiguredFeatures.BIRCH;
            case REDWOOD -> TreeConfiguredFeatures.SPRUCE;
            case TALL_REDWOOD -> TreeConfiguredFeatures.PINE;
            case JUNGLE -> TreeConfiguredFeatures.MEGA_JUNGLE_TREE;
            case SMALL_JUNGLE -> TreeConfiguredFeatures.JUNGLE_TREE_NO_VINE;
            case COCOA_TREE -> TreeConfiguredFeatures.JUNGLE_TREE;
            case JUNGLE_BUSH -> TreeConfiguredFeatures.JUNGLE_BUSH;
            case RED_MUSHROOM -> TreeConfiguredFeatures.HUGE_RED_MUSHROOM;
            case BROWN_MUSHROOM -> TreeConfiguredFeatures.HUGE_BROWN_MUSHROOM;
            case SWAMP -> TreeConfiguredFeatures.SWAMP_OAK;
            case ACACIA -> TreeConfiguredFeatures.ACACIA;
            case DARK_OAK -> TreeConfiguredFeatures.DARK_OAK;
            case MEGA_REDWOOD -> TreeConfiguredFeatures.MEGA_PINE;
            case TALL_BIRCH -> TreeConfiguredFeatures.SUPER_BIRCH_BEES_0002;
            case CHORUS_PLANT -> {
                ChorusFlowerBlock.generate(access, pos, random, 8);
                yield null;
            }
            case CRIMSON_FUNGUS -> TreeConfiguredFeatures.CRIMSON_FUNGUS_PLANTED;
            case WARPED_FUNGUS -> TreeConfiguredFeatures.WARPED_FUNGUS_PLANTED;
            case AZALEA -> TreeConfiguredFeatures.AZALEA_TREE;
            case MANGROVE -> TreeConfiguredFeatures.MANGROVE;
            case TALL_MANGROVE -> TreeConfiguredFeatures.TALL_MANGROVE;
            case TREE -> TreeConfiguredFeatures.OAK;
        };
        if (gen == null) return true;
        return access.getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE).getEntry(gen).map(holder ->
            holder.value().generate(access, chunkGenerator, random, pos)
        ).orElse(false);
    }

    @NotNull
    @Override
    public org.bukkit.entity.Entity spawnEntity(@NotNull Location location, @NotNull EntityType type) {
        //noinspection DataFlowIssue
        return spawn(location, type.getEntityClass());
    }

    @NotNull
    @Override
    public org.bukkit.entity.Entity spawnEntity(@NotNull Location loc, @NotNull EntityType type, boolean randomizeData) {
        return spawn(loc, type.getEntityClass(), null, CreatureSpawnEvent.SpawnReason.CUSTOM, randomizeData);
    }

    @NotNull
    @Override
    public List<org.bukkit.entity.Entity> getEntities() {
        final List<org.bukkit.entity.Entity> result = new ArrayList<>();
        getNMSEntities().forEach(entity -> {
            throw new NotImplementedYet();
        });
        return result;
    }

    @NotNull
    @Override
    public List<LivingEntity> getLivingEntities() {
        final List<LivingEntity> result = new ArrayList<>();
        getNMSEntities().forEach(entity -> {
            throw new NotImplementedYet();
        });
        return result;
    }

    @NotNull
    @Override
    public <T extends org.bukkit.entity.Entity> Collection<T> getEntitiesByClass(@NotNull Class<T> cls) {
        final Collection<T> result = new ArrayList<>();
        getNMSEntities().forEach(entity -> {
            throw new NotImplementedYet();
        });
        return result;
    }

    @NotNull
    @Override
    public Collection<org.bukkit.entity.Entity> getEntitiesByClasses(@NotNull Class<?>... classes) {
        final Collection<org.bukkit.entity.Entity> result = new ArrayList<>();
        getNMSEntities().forEach(entity -> {
            throw new NotImplementedYet();
        });
        return result;
    }

    @NotNull
    @Override
    public <T extends org.bukkit.entity.Entity> T spawn(@NotNull Location location, @NotNull Class<T> clazz) throws IllegalArgumentException {
        return spawn(location, clazz, null, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @NotNull
    @Override
    public <T extends org.bukkit.entity.Entity> T spawn(@NotNull Location location, @NotNull Class<T> clazz, @Nullable Consumer<T> function) throws IllegalArgumentException {
        return spawn(location, clazz, function, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @NotNull
    @Override
    public <T extends org.bukkit.entity.Entity> T spawn(@NotNull Location location, @NotNull Class<T> clazz, boolean randomizeData, @Nullable Consumer<T> function) throws IllegalArgumentException {
        return spawn(location, clazz, function, CreatureSpawnEvent.SpawnReason.CUSTOM, randomizeData);
    }

    public <T extends org.bukkit.entity.Entity> T spawn(Location location, Class<T> clazz, Consumer<T> function, CreatureSpawnEvent.SpawnReason reason) throws IllegalArgumentException {
        return spawn(location, clazz, function, reason, true);
    }

    public <T extends org.bukkit.entity.Entity> T spawn(Location location, Class<T> clazz, Consumer<T> function, CreatureSpawnEvent.SpawnReason reason, boolean randomizeData) throws IllegalArgumentException {
        throw new NotImplementedYet();
    }
}
