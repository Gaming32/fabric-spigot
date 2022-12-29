package io.github.gaming32.fabricspigot.api.entity;

import com.google.common.base.Preconditions;
import io.github.gaming32.fabricspigot.FabricSpigot;
import io.github.gaming32.fabricspigot.api.FabricServer;
import io.github.gaming32.fabricspigot.api.inventory.FabricItemStack;
import io.github.gaming32.fabricspigot.util.Conversion;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import org.apache.commons.lang3.Validate;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FabricLivingEntity extends FabricEntity implements LivingEntity {
    public FabricLivingEntity(FabricServer server, net.minecraft.entity.LivingEntity entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.entity.LivingEntity getHandle() {
        return (net.minecraft.entity.LivingEntity)entity;
    }

    @Override
    public boolean teleport(@NotNull Location location, @NotNull PlayerTeleportEvent.TeleportCause cause) {
        if (getHealth() == 0) {
            return false;
        }
        return super.teleport(location, cause);
    }

    @NotNull
    @Override
    public EntityType getType() {
        return EntityType.UNKNOWN;
    }

    @Override
    public double getEyeHeight() {
        return getHandle().getStandingEyeHeight();
    }

    @NotNull
    @Override
    public Location getEyeLocation() {
        final Location loc = getLocation();
        loc.setY(loc.getY() + getEyeHeight());
        return loc;
    }

    private List<Block> getLineOfSight(Set<Material> transparent, int maxDistance, int maxLength) {
        // TODO: worldgen check

        if (transparent == null) {
            transparent = EnumSet.of(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR);
        }
        if (maxDistance > 120) {
            maxDistance = 120;
        }
        final ArrayList<Block> blocks = new ArrayList<>();
        final Iterator<Block> it = new BlockIterator(this, maxDistance);
        while (it.hasNext()) {
            final Block block = it.next();
            blocks.add(block);
            if (maxLength != 0 && blocks.size() > maxLength) {
                blocks.remove(0);
            }
            final Material material = block.getType();
            if (!transparent.contains(material)) break;
        }
        return blocks;
    }

    @NotNull
    @Override
    public List<Block> getLineOfSight(@Nullable Set<Material> transparent, int maxDistance) {
        return getLineOfSight(transparent, maxDistance, 0);
    }

    @NotNull
    @Override
    public Block getTargetBlock(@Nullable Set<Material> transparent, int maxDistance) {
        return getLineOfSight(transparent, maxDistance, 1).get(0);
    }

    @NotNull
    @Override
    public List<Block> getLastTwoTargetBlocks(@Nullable Set<Material> transparent, int maxDistance) {
        return getLineOfSight(transparent, maxDistance, 2);
    }

    @Nullable
    @Override
    public Block getTargetBlockExact(int maxDistance) {
        return getTargetBlockExact(maxDistance, FluidCollisionMode.NEVER);
    }

    @Nullable
    @Override
    public Block getTargetBlockExact(int maxDistance, @NotNull FluidCollisionMode fluidCollisionMode) {
        final RayTraceResult result = rayTraceBlocks(maxDistance, fluidCollisionMode);
        return result != null ? result.getHitBlock() : null;
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceBlocks(double maxDistance) {
        return rayTraceBlocks(maxDistance, FluidCollisionMode.NEVER);
    }

    @Nullable
    @Override
    public RayTraceResult rayTraceBlocks(double maxDistance, @NotNull FluidCollisionMode fluidCollisionMode) {
        // TODO: worldgen check

        final Location eyeLocation = getEyeLocation();
        final Vector direction = eyeLocation.getDirection();
        return getWorld().rayTraceBlocks(eyeLocation, direction, maxDistance, fluidCollisionMode, false);
    }

    @Override
    public int getRemainingAir() {
        return getHandle().getAir();
    }

    @Override
    public void setRemainingAir(int ticks) {
        getHandle().setAir(ticks);
    }

    @Override
    public int getMaximumAir() {
        return getHandle().getMaxAir();
    }

    @Override
    public void setMaximumAir(int ticks) {
        throw new NotImplementedYet();
    }

    @Override
    public int getArrowCooldown() {
        return getHandle().stuckArrowTimer;
    }

    @Override
    public void setArrowCooldown(int ticks) {
        getHandle().stuckArrowTimer = ticks;
    }

    @Override
    public int getArrowsInBody() {
        return getHandle().getStuckArrowCount();
    }

    @Override
    public void setArrowsInBody(int count) {
        Preconditions.checkArgument(count >= 0, "New arrow count must be >= 0");
        getHandle().setStuckArrowCount(count);
    }

    @Override
    public int getMaximumNoDamageTicks() {
        return getHandle().defaultMaxHealth; // Did Matcher have a moment?
    }

    @Override
    public void setMaximumNoDamageTicks(int ticks) {
        getHandle().defaultMaxHealth = ticks; // TODO: Actually make used
    }

    @Override
    public double getLastDamage() {
        return getHandle().lastDamageTaken;
    }

    @Override
    public void setLastDamage(double damage) {
        getHandle().lastDamageTaken = (float)damage;
    }

    @Override
    public int getNoDamageTicks() {
        return getHandle().timeUntilRegen;
    }

    @Override
    public void setNoDamageTicks(int ticks) {
        getHandle().timeUntilRegen = ticks;
    }

    @Nullable
    @Override
    public Player getKiller() {
        return getHandle().attackingPlayer != null ? (Player)getHandle().attackingPlayer.getBukkitEntity() : null;
    }

    @Override
    public boolean addPotionEffect(@NotNull PotionEffect effect) {
        return addPotionEffect(effect, false);
    }

    @Override
    public boolean addPotionEffect(@NotNull PotionEffect effect, boolean force) {
        throw new NotImplementedYet();
//        return true;
    }

    @Override
    public boolean addPotionEffects(@NotNull Collection<PotionEffect> effects) {
        boolean success = true;
        for (final PotionEffect effect : effects) {
            success &= addPotionEffect(effect);
        }
        return success;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean hasPotionEffect(@NotNull PotionEffectType type) {
        return getHandle().hasStatusEffect(StatusEffect.byRawId(type.getId()));
    }

    @Nullable
    @Override
    @SuppressWarnings("deprecation")
    public PotionEffect getPotionEffect(@NotNull PotionEffectType type) {
        final StatusEffectInstance handle = getHandle().getStatusEffect(StatusEffect.byRawId(type.getId()));
        //noinspection DataFlowIssue
        return handle != null ? new PotionEffect(
            PotionEffectType.getById(StatusEffect.getRawId(handle.getEffectType())),
            handle.getDuration(), handle.getAmplifier(),
            handle.isAmbient(), handle.shouldShowParticles()
        ) : null;
    }

    @Override
    public void removePotionEffect(@NotNull PotionEffectType type) {
        throw new NotImplementedYet();
//        getHandle().removeStatusEffect(StatusEffect.byRawId(type.getId()), EntityPotionEffectEvent.Cause.PLUGIN);
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public Collection<PotionEffect> getActivePotionEffects() {
        final List<PotionEffect> effects = new ArrayList<>();
        for (final StatusEffectInstance handle : getHandle().getActiveStatusEffects().values()) {
            final PotionEffectType bukkitType = PotionEffectType.getById(StatusEffect.getRawId(handle.getEffectType()));
            if (bukkitType == null) {
                // This is a modded potion effect
                continue;
            }
            effects.add(new PotionEffect(
                bukkitType,
                handle.getDuration(), handle.getAmplifier(),
                handle.isAmbient(), handle.shouldShowParticles()
            ));
        }
        return effects;
    }

    @Override
    public boolean hasLineOfSight(@NotNull Entity other) {
        // TODO: worldgen check

        return getHandle().canSee(((FabricEntity)other).getHandle());
    }

    @Override
    public boolean getRemoveWhenFarAway() {
        return getHandle() instanceof MobEntity mob && !mob.isPersistent();
    }

    @Override
    public void setRemoveWhenFarAway(boolean remove) {
        if (getHandle() instanceof MobEntity mob) {
            throw new NotImplementedYet("Ability to set it to false");
        }
    }

    @Nullable
    @Override
    public EntityEquipment getEquipment() {
        throw new NotImplementedYet();
    }

    @Override
    public void setCanPickupItems(boolean pickup) {
        if (getHandle() instanceof MobEntity mob) {
            mob.setCanPickUpLoot(pickup);
        } else {
            throw new NotImplementedYet("bukkitPickUpLoot");
        }
    }

    @Override
    public boolean getCanPickupItems() {
        if (getHandle() instanceof MobEntity mob) {
            return mob.canPickUpLoot();
        } else {
            throw new NotImplementedYet("bukkitPickUpLoot");
        }
    }

    @Override
    public boolean isLeashed() {
        if (!(getHandle() instanceof MobEntity mob)) {
            return false;
        }
        return mob.getHoldingEntity() != null;
    }

    @NotNull
    @Override
    public Entity getLeashHolder() throws IllegalStateException {
        if (!isLeashed()) {
            throw new IllegalStateException("Entity not leashed");
        }
        //noinspection DataFlowIssue
        return ((MobEntity)getHandle()).getHoldingEntity().getBukkitEntity();
    }

    @Override
    public boolean setLeashHolder(@Nullable Entity holder) {
        if (/* getHandle().generation || */ getHandle() instanceof WitherEntity || !(getHandle() instanceof MobEntity mob)) {
            return false;
        }

        if (holder == null) {
            return unleash();
        }

        if (holder.isDead()) {
            return false;
        }

        unleash();
        mob.attachLeash(((FabricEntity)holder).getHandle(), true);
        return true;
    }

    private boolean unleash() {
        if (!isLeashed()) {
            return false;
        }
        ((MobEntity)getHandle()).detachLeash(true, false);
        return true;
    }

    @Override
    public boolean isGliding() {
        return getHandle().isFallFlying();
    }

    @Override
    public void setGliding(boolean gliding) {
        getHandle().setFlag(net.minecraft.entity.Entity.FALL_FLYING_FLAG_INDEX, gliding);
    }

    @Override
    public boolean isSwimming() {
        return getHandle().isSwimming();
    }

    @Override
    public void setSwimming(boolean swimming) {
        getHandle().setSwimming(swimming);
    }

    @Override
    public boolean isRiptiding() {
        return getHandle().isUsingRiptide();
    }

    @Override
    public boolean isSleeping() {
        return getHandle().isSleeping();
    }

    @Override
    public boolean isClimbing() {
        // TODO: worldgen check

        return getHandle().isClimbing();
    }

    @Override
    public void setAI(boolean ai) {
        if (getHandle() instanceof MobEntity mob) {
            mob.setAiDisabled(!ai);
        }
    }

    @Override
    public boolean hasAI() {
        return getHandle() instanceof MobEntity mob && !mob.isAiDisabled();
    }

    @Override
    public void attack(@NotNull Entity target) {
        //noinspection ConstantValue
        Preconditions.checkArgument(target != null, "target == null");
        // TODO: worldgen check

        if (getHandle() instanceof PlayerEntity player) {
            player.attack(((FabricEntity)target).getHandle());
        } else {
            getHandle().tryAttack(((FabricEntity)target).getHandle());
        }
    }

    @Override
    public void swingMainHand() {
        // TODO: worldgen check

        getHandle().swingHand(Hand.MAIN_HAND, true);
    }

    @Override
    public void swingOffHand() {
        // TODO: worldgen check

        getHandle().swingHand(Hand.OFF_HAND, true);
    }

    @Override
    public void setCollidable(boolean collidable) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isCollidable() {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Set<UUID> getCollidableExemptions() {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getMemory(@NotNull MemoryKey<T> memoryKey) {
        //noinspection ConstantValue,DataFlowIssue
        return (T)getHandle()
            .getBrain()
            .getOptionalMemory(Conversion.toMemoryModuleType(memoryKey))
            .map(v -> {throw new NotImplementedYet();})
            .orElse(null);
    }

    @Override
    public <T> void setMemory(@NotNull MemoryKey<T> memoryKey, @Nullable T memoryValue) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public Sound getHurtSound() {
        final SoundEvent sound = getHandle().getHurtSound(DamageSource.GENERIC);
        return sound != null ? Conversion.toSound(sound) : null;
    }

    @Nullable
    @Override
    public Sound getDeathSound() {
        final SoundEvent sound = getHandle().getDeathSound();
        return sound != null ? Conversion.toSound(sound) : null;
    }

    @NotNull
    @Override
    public Sound getFallDamageSound(int fallHeight) {
        return Conversion.toSound(getHandle().getFallSound(fallHeight));
    }

    @NotNull
    @Override
    public Sound getFallDamageSoundSmall() {
        return Conversion.toSound(getHandle().getFallSounds().small());
    }

    @NotNull
    @Override
    public Sound getFallDamageSoundBig() {
        return Conversion.toSound(getHandle().getFallSounds().big());
    }

    @NotNull
    @Override
    public Sound getDrinkingSound(@NotNull ItemStack itemStack) {
        //noinspection ConstantValue
        Preconditions.checkArgument(itemStack != null, "itemStack must not be null");
        return Conversion.toSound(getHandle().getDrinkSound(FabricItemStack.toVanilla(itemStack)));
    }

    @NotNull
    @Override
    public Sound getEatingSound(@NotNull ItemStack itemStack) {
        //noinspection ConstantValue
        Preconditions.checkArgument(itemStack != null, "itemStack must not be null");
        return Conversion.toSound(getHandle().getEatSound(FabricItemStack.toVanilla(itemStack)));
    }

    @Override
    public boolean canBreatheUnderwater() {
        return getHandle().canBreatheInWater();
    }

    @NotNull
    @Override
    public EntityCategory getCategory() {
        final EntityGroup group = getHandle().getGroup();

        if (group == EntityGroup.DEFAULT) {
            return EntityCategory.NONE;
        } else if (group == EntityGroup.UNDEAD) {
            return EntityCategory.UNDEAD;
        } else if (group == EntityGroup.ARTHROPOD) {
            return EntityCategory.ARTHROPOD;
        } else if (group == EntityGroup.ILLAGER) {
            return EntityCategory.ILLAGER;
        } else if (group == EntityGroup.AQUATIC) {
            return EntityCategory.WATER;
        }

        FabricSpigot.LOGGER.warn("LivingEntity.getGroup() returned a custom entity group in a Spigot LivingEntity.getCategory() call. Falling back to DEFAULT/NONE/UNDEFINED.");
        return EntityCategory.NONE;
    }

    @Override
    public void setInvisible(boolean invisible) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean isInvisible() {
        return getHandle().isInvisible();
    }

    @Nullable
    @Override
    public AttributeInstance getAttribute(@NotNull Attribute attribute) {
        throw new NotImplementedYet();
    }

    @Override
    public void damage(double amount) {
        damage(amount, null);
    }

    @Override
    public void damage(double amount, @Nullable Entity source) {
        // TODO: worldgen check

        DamageSource reason = DamageSource.GENERIC;

        if (source instanceof HumanEntity) {
            reason = DamageSource.player(((FabricHumanEntity)source).getHandle());
        } else if (source instanceof LivingEntity) {
            reason = DamageSource.mob(((FabricLivingEntity)source).getHandle());
        }

        entity.damage(reason, (float)amount);
    }

    @Override
    public double getHealth() {
        return Math.min(Math.max(0, getHandle().getHealth()), getMaxHealth());
    }

    @Override
    public void setHealth(double health) {
        if (health < 0 || health > getMaxHealth()) {
            throw new IllegalArgumentException("Health must be between 0 and " + getMaxHealth() + " (was " + health + ")");
        }

//        if (getHandle().generation && health == 0) {
//            getHandle().discard();
//            return;
//        }

        getHandle().setHealth((float)health);

        if (health == 0) {
            getHandle().onDeath(DamageSource.GENERIC);
        }
    }

    @Override
    public double getAbsorptionAmount() {
        return getHandle().getAbsorptionAmount();
    }

    @Override
    public void setAbsorptionAmount(double amount) {
        Preconditions.checkArgument(amount >= 0 && Double.isFinite(amount), "amount < 0 or non-finite");

        getHandle().setAbsorptionAmount((float)amount);
    }

    @Override
    public double getMaxHealth() {
        return getHandle().getMaxHealth();
    }

    @Override
    public void setMaxHealth(double health) {
        Validate.isTrue(health > 0, "Max health must be greater than 0");

        //noinspection DataFlowIssue
        getHandle().getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(health);

        if (getHealth() > health) {
            setHealth(health);
        }
    }

    @Override
    public void resetMaxHealth() {
        //noinspection DataFlowIssue
        setMaxHealth(getHandle().getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).getAttribute().getDefaultValue());
    }

    @NotNull
    @Override
    public <T extends Projectile> T launchProjectile(@NotNull Class<? extends T> projectile) {
        return launchProjectile(projectile, null);
    }

    @NotNull
    @Override
    public <T extends Projectile> T launchProjectile(@NotNull Class<? extends T> projectile, @Nullable Vector velocity) {
        throw new NotImplementedYet();
    }

    @Override
    public double getEyeHeight(boolean ignorePose) {
        throw new NotImplementedYet();
    }
}
