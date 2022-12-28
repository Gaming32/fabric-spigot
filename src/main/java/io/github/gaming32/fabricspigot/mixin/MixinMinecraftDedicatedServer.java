package io.github.gaming32.fabricspigot.mixin;

import com.mojang.datafixers.DataFixer;
import io.github.gaming32.fabricspigot.FabricSpigot;
import io.github.gaming32.fabricspigot.vanillaimpl.MinecraftServerExt;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.dedicated.DedicatedPlayerManager;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.util.ApiServices;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.Proxy;

@Mixin(MinecraftDedicatedServer.class)
public abstract class MixinMinecraftDedicatedServer extends MinecraftServer implements MinecraftServerExt {
    public MixinMinecraftDedicatedServer(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory) {
        super(serverThread, session, dataPackManager, saveLoader, proxy, dataFixer, apiServices, worldGenerationProgressListenerFactory);
    }

    @Inject(method = "getSpawnProtectionRadius", at = @At("HEAD"), cancellable = true)
    private void bukkitSpawnProtection(CallbackInfoReturnable<Integer> cir) {
        if (getBukkitServer().getOverrideSpawnProtection() != null) {
            cir.setReturnValue(getBukkitServer().getOverrideSpawnProtection());
        }
    }

    @Inject(
        method = "setupServer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/dedicated/MinecraftDedicatedServer;isOnlineMode()Z",
            ordinal = 0
        )
    )
    private void loadPlugins(CallbackInfoReturnable<Boolean> cir) {
        setPlayerManager(new DedicatedPlayerManager((MinecraftDedicatedServer)(Object)this, getCombinedDynamicRegistries(), saveHandler));
        FabricSpigot.setupServer(this);
    }

    @Redirect(
        method = "setupServer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/dedicated/MinecraftDedicatedServer;setPlayerManager(Lnet/minecraft/server/PlayerManager;)V"
        )
    )
    private void playerManagerWasSetEarlier(MinecraftDedicatedServer instance, PlayerManager playerManager) {
    }
}
