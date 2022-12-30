package io.github.gaming32.fabricspigot.mixin;

import io.github.gaming32.fabricspigot.ext.EntityExt;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ArmorStandEntity.class)
public abstract class MixinArmorStandEntity extends LivingEntity implements EntityExt {
    protected MixinArmorStandEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public float getBukkitYaw() {
        return getYaw();
    }
}
