package io.github.gaming32.fabricspigot.mixin;

import io.github.gaming32.fabricspigot.ext.EntityExt;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity implements EntityExt {
    @Shadow public abstract float getHeadYaw();

    @Override
    public float getBukkitYaw() {
        return getHeadYaw();
    }
}
