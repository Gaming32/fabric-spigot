package io.github.gaming32.fabricspigot.mixin;

import io.github.gaming32.fabricspigot.api.FabricChunk;
import io.github.gaming32.fabricspigot.ext.WorldChunkExt;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.tick.ChunkTickScheduler;
import org.bukkit.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldChunk.class)
public class MixinWorldChunk implements WorldChunkExt {
    private Chunk fabricSpigot$bukkitChunk;

    @Override
    public Chunk getBukkitChunk() {
        return fabricSpigot$bukkitChunk;
    }

    @Inject(
        method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/chunk/UpgradeData;Lnet/minecraft/world/tick/ChunkTickScheduler;Lnet/minecraft/world/tick/ChunkTickScheduler;J[Lnet/minecraft/world/chunk/ChunkSection;Lnet/minecraft/world/chunk/WorldChunk$EntityLoader;Lnet/minecraft/world/gen/chunk/BlendingData;)V",
        at = @At("TAIL")
    )
    private void init(
        World world,
        ChunkPos pos,
        UpgradeData upgradeData,
        ChunkTickScheduler<Block> blockTickScheduler,
        ChunkTickScheduler<Fluid> fluidTickScheduler,
        long inhabitedTime,
        ChunkSection[] sectionArrayInitializer,
        WorldChunk.EntityLoader entityLoader,
        BlendingData blendingData,
        CallbackInfo ci
    ) {
        fabricSpigot$bukkitChunk = new FabricChunk((WorldChunk)(Object)this);
    }
}
