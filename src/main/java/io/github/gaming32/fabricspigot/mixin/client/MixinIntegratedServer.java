package io.github.gaming32.fabricspigot.mixin.client;

import io.github.gaming32.fabricspigot.FabricSpigot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class)
public class MixinIntegratedServer {
    @Inject(
        method = "setupServer",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/integrated/IntegratedServer;setOnlineMode(Z)V"
        )
    )
    private void loadPlugins(CallbackInfoReturnable<Boolean> cir) {
        FabricSpigot.setupServer((MinecraftServer)(Object)this);
    }
}
