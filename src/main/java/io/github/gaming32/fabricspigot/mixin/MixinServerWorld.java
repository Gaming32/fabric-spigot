package io.github.gaming32.fabricspigot.mixin;

import io.github.gaming32.fabricspigot.api.FabricWorld;
import io.github.gaming32.fabricspigot.vanillaimpl.ServerWorldExt;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.Spawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerWorld.class)
public class MixinServerWorld implements ServerWorldExt {
    private FabricWorld fabricSpigot$bukkitWorld;
    private LevelStorage.Session fabricSpigot$session;

    @Override
    public FabricWorld getBukkitWorld() {
        return fabricSpigot$bukkitWorld;
    }

    @Override
    public LevelStorage.Session getSession() {
        return fabricSpigot$session;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void bukkitInit(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> worldKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List<Spawner> spawners, boolean shouldTickTime, CallbackInfo ci) {
        fabricSpigot$session = session;
        fabricSpigot$bukkitWorld = new FabricWorld((ServerWorld)(Object)this);
        server.getBukkitServer().addWorld(fabricSpigot$bukkitWorld);
    }
}
