package io.github.gaming32.fabricspigot.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.EmptyTickSchedulers;
import net.minecraft.world.tick.QueryableTickScheduler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class DummyStructureAccess implements StructureWorldAccess {
    public static final StructureWorldAccess INSTANCE = new DummyStructureAccess();

    protected DummyStructureAccess() {
    }

    @Override
    public long getSeed() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ServerWorld toServerWorld() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getTickOrder() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public QueryableTickScheduler<Block> getBlockTickScheduler() {
        return EmptyTickSchedulers.getClientTickScheduler();
    }

    @Override
    public void scheduleBlockTick(BlockPos pos, Block block, int delay) {
    }

    @Override
    public QueryableTickScheduler<Fluid> getFluidTickScheduler() {
        return EmptyTickSchedulers.getClientTickScheduler();
    }

    @Override
    public WorldProperties getLevelProperties() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public LocalDifficulty getLocalDifficulty(BlockPos pos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Nullable
    @Override
    public MinecraftServer getServer() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ChunkManager getChunkManager() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Random getRandom() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playSound(@Nullable PlayerEntity except, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void syncWorldEvent(@Nullable PlayerEntity player, int eventId, BlockPos pos, int data) {
    }

    @Override
    public void emitGameEvent(GameEvent event, Vec3d emitterPos, GameEvent.Emitter emitter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Entity> getOtherEntities(@Nullable Entity except, Box box, Predicate<? super Entity> predicate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends Entity> List<T> getEntitiesByType(TypeFilter<Entity, T> filter, Box box, Predicate<? super T> predicate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<? extends PlayerEntity> getPlayers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Nullable
    @Override
    public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getTopY(Heightmap.Type heightmap, int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAmbientDarkness() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BiomeAccess getBiomeAccess() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RegistryEntry<Biome> getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public int getSeaLevel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DimensionType getDimension() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DynamicRegistryManager getRegistryManager() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FeatureSet getEnabledFeatures() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public float getBrightness(Direction direction, boolean shaded) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public LightingProvider getLightingProvider() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return Fluids.EMPTY.getDefaultState();
    }

    @Override
    public WorldBorder getWorldBorder() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean testBlockState(BlockPos pos, Predicate<BlockState> state) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean testFluidState(BlockPos pos, Predicate<FluidState> state) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
        return false;
    }

    @Override
    public boolean removeBlock(BlockPos pos, boolean move) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean breakBlock(BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth) {
        return false;
    }

    @Override
    public ServerWorld getMinecraftWorld() {
        return toServerWorld();
    }
}
