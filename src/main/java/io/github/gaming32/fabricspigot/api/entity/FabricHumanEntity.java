package io.github.gaming32.fabricspigot.api.entity;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import io.github.gaming32.fabricspigot.api.FabricServer;
import io.github.gaming32.fabricspigot.api.FabricUnsafeValues;
import io.github.gaming32.fabricspigot.api.inventory.FabricItemStack;
import io.github.gaming32.fabricspigot.util.Conversion;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import io.github.gaming32.fabricspigot.vanillaimpl.EntityExt;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.*;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FabricHumanEntity extends FabricLivingEntity implements HumanEntity {
    protected final PermissibleBase perm = new PermissibleBase(this);
    private boolean op;

    public FabricHumanEntity(FabricServer server, PlayerEntity entity) {
        super(server, entity);
    }

    @Override
    public PlayerEntity getHandle() {
        return (PlayerEntity)entity;
    }

    @NotNull
    @Override
    public String getName() {
        return getHandle().getEntityName();
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return perm.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        return this.perm.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        return perm.hasPermission(name);
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return this.perm.hasPermission(perm);
    }

    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
        return perm.addAttachment(plugin, name, value);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return perm.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return perm.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return perm.addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        perm.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        perm.recalculatePermissions();
    }

    @Override
    public boolean isOp() {
        return op;
    }

    @Override
    public void setOp(boolean value) {
        this.op = value;
        perm.recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return perm.getEffectivePermissions();
    }

    @Nullable
    @Override
    public EntityEquipment getEquipment() {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public PlayerInventory getInventory() {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public Inventory getEnderChest() {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public MainHand getMainHand() {
        return getHandle().getMainArm() == Arm.LEFT ? MainHand.LEFT : MainHand.RIGHT;
    }

    @Override
    public boolean setWindowProperty(@NotNull InventoryView.Property prop, int value) {
        return false;
    }

    @NotNull
    @Override
    public InventoryView getOpenInventory() {
        throw new NotImplementedYet();
//        return getHandle().currentScreenHandler.getBukkitView();
    }

    @Nullable
    @Override
    public InventoryView openInventory(@NotNull Inventory inventory) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public InventoryView openWorkbench(@Nullable Location location, boolean force) {
        if (location == null) {
            location = getLocation();
        }
        if (!force) {
            final Block block = location.getBlock();
            if (block.getType() != Material.CRAFTING_TABLE) {
                return null;
            }
        }
        getHandle().openHandledScreen(((CraftingTableBlock)Blocks.CRAFTING_TABLE).createScreenHandlerFactory(
            null, getHandle().world,
            new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ())
        ));
        if (force) {
            throw new NotImplementedYet("checkReachable");
        }
        throw new NotImplementedYet("getBukkitView");
    }

    @Nullable
    @Override
    public InventoryView openEnchanting(@Nullable Location location, boolean force) {
        if (location == null) {
            location = getLocation();
        }
        if (!force) {
            final Block block = location.getBlock();
            if (block.getType() != Material.ENCHANTING_TABLE) {
                return null;
            }
        }

        final BlockPos pos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        getHandle().openHandledScreen(((EnchantingTableBlock)Blocks.ENCHANTING_TABLE).createScreenHandlerFactory(
            null, getHandle().world, pos
        ));

        if (force) {
            throw new NotImplementedYet("checkReachable");
        }
        throw new NotImplementedYet("getBukkitView");
    }

    @Override
    public void openInventory(@NotNull InventoryView inventory) {
        if (!(getHandle() instanceof ServerPlayerEntity serverPlayer)) return;
        if (serverPlayer.networkHandler == null) return;
        if (getHandle().currentScreenHandler != getHandle().playerScreenHandler) {
            serverPlayer.networkHandler.onCloseHandledScreen(new CloseHandledScreenC2SPacket(getHandle().currentScreenHandler.syncId));
        }
        final ScreenHandler container;
        throw new NotImplementedYet("FabricInventoryView");
    }

    @Nullable
    @Override
    public InventoryView openMerchant(@NotNull Villager trader, boolean force) {
        Preconditions.checkNotNull(trader, "villager cannot be null");

        return openMerchant((Merchant)trader, force);
    }

    @Nullable
    @Override
    public InventoryView openMerchant(@NotNull Merchant merchant, boolean force) {
        Preconditions.checkNotNull(merchant, "merchant cannot be null");

        if (!force && merchant.isTrading()) {
            return null;
        } else if (merchant.isTrading()) {
            //noinspection DataFlowIssue
            merchant.getTrader().closeInventory();
        }

        final net.minecraft.village.Merchant mcMerchant;
        final Text name;
        final int level = 1;
        throw new NotImplementedYet("FabricAbstractVillager");
    }

    @Override
    public void closeInventory() {
        getHandle().closeHandledScreen();
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public ItemStack getItemInHand() {
        return getInventory().getItemInHand();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setItemInHand(@Nullable ItemStack item) {
        getInventory().setItemInHand(item);
    }

    @NotNull
    @Override
    public ItemStack getItemOnCursor() {
        throw new NotImplementedYet("asFabricMirror");
    }

    @Override
    public void setItemOnCursor(@Nullable ItemStack item) {
        final net.minecraft.item.ItemStack stack = FabricItemStack.toVanilla(item);
        getHandle().currentScreenHandler.setCursorStack(stack);
        if (this instanceof FabricPlayer) {
            throw new NotImplementedYet("broadcastCarriedItem");
        }
    }

    @Override
    public boolean hasCooldown(@NotNull Material material) {
        //noinspection ConstantValue
        Preconditions.checkArgument(material != null, "Material cannot be null");
        Preconditions.checkArgument(material.isItem(), "Material %s is not an item", material);

        return getHandle().getItemCooldownManager().isCoolingDown(FabricUnsafeValues.getItem(material));
    }

    @Override
    public int getCooldown(@NotNull Material material) {
        //noinspection ConstantValue
        Preconditions.checkArgument(material != null, "Material cannot be null");
        Preconditions.checkArgument(material.isItem(), "Material %s is not an item", material);

        final ItemCooldownManager.Entry cooldown = getHandle().getItemCooldownManager().entries.get(FabricUnsafeValues.getItem(material));
        return cooldown != null ? Math.max(0, cooldown.endTick - getHandle().getItemCooldownManager().tick) : 0;
    }

    @Override
    public void setCooldown(@NotNull Material material, int ticks) {
        //noinspection ConstantValue
        Preconditions.checkArgument(material != null, "Material cannot be null");
        Preconditions.checkArgument(material.isItem(), "Material %s is not an item", material);
        Preconditions.checkArgument(ticks >= 0, "Cannot have negative cooldown");

        getHandle().getItemCooldownManager().set(FabricUnsafeValues.getItem(material), ticks);
    }

    @Override
    public int getSleepTicks() {
        return getHandle().getSleepTimer();
    }

    @Override
    public boolean sleep(@NotNull Location location, boolean force) {
        //noinspection ConstantValue
        Preconditions.checkArgument(location != null, "Location cannot be null");
        Preconditions.checkArgument(location.getWorld() != null, "Location needs to be in a world");
        Preconditions.checkArgument(location.getWorld().equals(getWorld()), "Cannot sleep across worlds");

        final BlockPos pos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        final BlockState state = getHandle().world.getBlockState(pos);
        if (!(state.getBlock() instanceof BedBlock)) {
            return false;
        }

        throw new NotImplementedYet("startSleepInBed");
    }

    @Override
    public void wakeup(boolean setSpawnLocation) {
        Preconditions.checkState(isSleeping(), "Cannot wakeup if not sleeping");

        getHandle().wakeUp(true, setSpawnLocation);
    }

    @NotNull
    @Override
    public Location getBedLocation() {
        Preconditions.checkState(isSleeping(), "Not sleeping");

        //noinspection OptionalGetWithoutIsPresent
        final BlockPos bed = getHandle().getSleepingPosition().get();
        return new Location(getWorld(), bed.getX(), bed.getY(), bed.getZ());
    }

    @NotNull
    @Override
    public GameMode getGameMode() {
        throw new NotImplementedYet("mode");
    }

    @Override
    public void setGameMode(@NotNull GameMode mode) {
        //noinspection ConstantValue
        if (mode == null) {
            throw new IllegalArgumentException("Mode cannot be null");
        }
        throw new NotImplementedYet("mode");
    }

    @Override
    public boolean isBlocking() {
        return getHandle().isBlocking();
    }

    @Override
    public boolean isHandRaised() {
        return getHandle().isUsingItem();
    }

    @Nullable
    @Override
    public ItemStack getItemInUse() {
        final net.minecraft.item.ItemStack item = getHandle().getActiveItem();
        throw new NotImplementedYet("asFabricMirror");
//        return item.isEmpty() ? null :
    }

    @Override
    public int getExpToLevel() {
        return getHandle().getNextLevelExperience();
    }

    @Override
    public float getAttackCooldown() {
        return getHandle().getAttackCooldownProgress(0.5f);
    }

    @Override
    public boolean discoverRecipe(@NotNull NamespacedKey recipe) {
        return discoverRecipes(List.of(recipe)) != 0;
    }

    @Override
    public int discoverRecipes(@NotNull Collection<NamespacedKey> recipes) {
        return getHandle().unlockRecipes(bukkitKeysToMinecraftRecipes(recipes));
    }

    @Override
    public boolean undiscoverRecipe(@NotNull NamespacedKey recipe) {
        return undiscoverRecipes(List.of(recipe)) != 0;
    }

    @Override
    public int undiscoverRecipes(@NotNull Collection<NamespacedKey> recipes) {
        return getHandle().lockRecipes(bukkitKeysToMinecraftRecipes(recipes));
    }

    @Override
    public boolean hasDiscoveredRecipe(@NotNull NamespacedKey recipe) {
        return false;
    }

    @NotNull
    @Override
    public Set<NamespacedKey> getDiscoveredRecipes() {
        return ImmutableSet.of();
    }

    private Collection<Recipe<?>> bukkitKeysToMinecraftRecipes(Collection<NamespacedKey> recipeKeys) {
        final Collection<Recipe<?>> recipes = new ArrayList<>();
        //noinspection DataFlowIssue
        final RecipeManager manager = getHandle().world.getServer().getRecipeManager();

        for (final NamespacedKey recipeKey : recipeKeys) {
            manager.get(Conversion.toIdentifier(recipeKey)).ifPresent(recipes::add);
        }

        return recipes;
    }

    @Nullable
    @Override
    public Entity getShoulderEntityLeft() {
        if (!getHandle().getShoulderEntityLeft().isEmpty()) {
            final Optional<net.minecraft.entity.Entity> shoulder = EntityType.getEntityFromNbt(getHandle().getShoulderEntityLeft(), getHandle().world);

            return shoulder.map(EntityExt::getBukkitEntity).orElse(null);
        }

        return null;
    }

    @Override
    public void setShoulderEntityLeft(@Nullable Entity entity) {
        throw new NotImplementedYet("save");
//        getHandle().setShoulderEntityLeft(entity == null ? new NbtCompound() : ((FabricEntity)entity).save());
    }

    @Nullable
    @Override
    public Entity getShoulderEntityRight() {
        if (!getHandle().getShoulderEntityRight().isEmpty()) {
            final Optional<net.minecraft.entity.Entity> shoulder = EntityType.getEntityFromNbt(getHandle().getShoulderEntityRight(), getHandle().world);

            return shoulder.map(EntityExt::getBukkitEntity).orElse(null);
        }

        return null;
    }

    @Override
    public void setShoulderEntityRight(@Nullable Entity entity) {
        throw new NotImplementedYet("save");
//        getHandle().setShoulderEntityRight(entity == null ? new NbtCompound() : ((FabricEntity)entity).save());
    }

    @Override
    public boolean dropItem(boolean dropAll) {
        if (!(getHandle() instanceof ServerPlayerEntity serverPlayer)) return false;
        return serverPlayer.dropSelectedItem(dropAll);
    }

    @Override
    public float getExhaustion() {
        return getHandle().getHungerManager().getExhaustion();
    }

    @Override
    public void setExhaustion(float value) {
        getHandle().getHungerManager().setExhaustion(value);
    }

    @Override
    public float getSaturation() {
        return getHandle().getHungerManager().getSaturationLevel();
    }

    @Override
    public void setSaturation(float value) {
        getHandle().getHungerManager().setSaturationLevel(value);
    }

    @Override
    public int getFoodLevel() {
        return getHandle().getHungerManager().getFoodLevel();
    }

    @Override
    public void setFoodLevel(int value) {
        getHandle().getHungerManager().setFoodLevel(value);
    }

    @Override
    public int getSaturatedRegenRate() {
        throw new NotImplementedYet("saturatedRegenRate");
    }

    @Override
    public void setSaturatedRegenRate(int ticks) {
        throw new NotImplementedYet("saturatedRegenRate");
    }

    @Override
    public int getUnsaturatedRegenRate() {
        throw new NotImplementedYet("unsaturatedRegenRate");
    }

    @Override
    public void setUnsaturatedRegenRate(int ticks) {
        throw new NotImplementedYet("unsaturatedRegenRate");
    }

    @Override
    public int getStarvationRate() {
        throw new NotImplementedYet("starvationRate");
    }

    @Override
    public void setStarvationRate(int ticks) {
        throw new NotImplementedYet("starvationRate");
    }

    @Nullable
    @Override
    public Location getLastDeathLocation() {
        throw new NotImplementedYet("fromNms");
//        return getHandle().getLastDeathPos().map();
    }

    @Override
    public void setLastDeathLocation(@Nullable Location location) {
        throw new NotImplementedYet("toNms");
//        getHandle().setLastDeathPos(Optional.ofNullable(location).map(FabricMemoryMapper::toNms));
    }

    @Nullable
    @Override
    public Firework fireworkBoost(@NotNull ItemStack fireworkItemStack) {
        //noinspection ConstantValue
        Preconditions.checkArgument(fireworkItemStack != null, "fireworkItemStack must not be null");
        Preconditions.checkArgument(fireworkItemStack.getType() == Material.FIREWORK_ROCKET, "fireworkItemStack must be of type %s", Material.FIREWORK_ROCKET);

        final FireworkRocketEntity fireworks = new FireworkRocketEntity(getHandle().world, FabricItemStack.toVanilla(fireworkItemStack), getHandle());
        throw new NotImplementedYet("addFreshEntity");
    }
}
