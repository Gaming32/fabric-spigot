package io.github.gaming32.fabricspigot.mixin;

import io.github.gaming32.fabricspigot.api.FabricServer;
import io.github.gaming32.fabricspigot.api.command.FabricConsoleCommandSender;
import io.github.gaming32.fabricspigot.vanillaimpl.CommandOutputExt;
import io.github.gaming32.fabricspigot.vanillaimpl.MinecraftServerExt;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldSaveHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginLoadOrder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer implements MinecraftServerExt, CommandOutputExt {
    @Shadow
    @Final
    private Map<RegistryKey<World>, ServerWorld> worlds;
    @Shadow @Final protected WorldSaveHandler saveHandler;
    private FabricServer fabricSpigot$bukkitServer;

    @Override
    public FabricServer getBukkitServer() {
        return fabricSpigot$bukkitServer;
    }

    @Override
    public void setBukkitServer(FabricServer server) {
        fabricSpigot$bukkitServer = server;
    }

    @Override
    public void removeWorld(ServerWorld world) {
        worlds.remove(world.getRegistryKey());
    }

    @Inject(method = "getSpawnProtectionRadius", at = @At("HEAD"), cancellable = true)
    private void bukkitSpawnProtection(CallbackInfoReturnable<Integer> cir) {
        if (getBukkitServer().getOverrideSpawnProtection() != null) {
            cir.setReturnValue(getBukkitServer().getOverrideSpawnProtection());
        }
    }

    @Inject(method = "loadWorld", at = @At("TAIL"))
    private void enablePlugins(CallbackInfo ci) {
        if (getBukkitServer() != null) {
            getBukkitServer().enablePlugins(PluginLoadOrder.POSTWORLD);
        }
    }

    @Inject(
        method = "shutdown",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/MinecraftServer;getNetworkIo()Lnet/minecraft/server/ServerNetworkIo;",
            ordinal = 0
        )
    )
    private void disablePlugins(CallbackInfo ci) {
        if (getBukkitServer() != null) {
            getBukkitServer().disablePlugins();
        }
    }

    @Override
    public CommandSender getBukkitSender(ServerCommandSource commandSource) {
        return FabricConsoleCommandSender.getInstance();
    }

    @Override
    public WorldSaveHandler getSaveHandler() {
        return saveHandler;
    }
}
