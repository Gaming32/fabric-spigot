package io.github.gaming32.fabricspigot.mixin;

import io.github.gaming32.fabricspigot.ext.WorldAccessExt;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerWorldAccess.class)
public interface MixinServerWorldAccess extends WorldAccessExt {
    @Shadow ServerWorld toServerWorld();

    @Override
    default ServerWorld getMinecraftWorld() {
        return toServerWorld();
    }
}
