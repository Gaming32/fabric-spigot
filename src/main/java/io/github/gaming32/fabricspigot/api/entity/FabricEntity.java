package io.github.gaming32.fabricspigot.api.entity;

import com.google.common.base.Preconditions;
import io.github.gaming32.fabricspigot.api.FabricServer;
import io.github.gaming32.fabricspigot.api.block.FabricBlock;
import io.github.gaming32.fabricspigot.util.ChatMessageConversion;
import io.github.gaming32.fabricspigot.util.Conversion;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import io.github.gaming32.fabricspigot.vanillaimpl.ServerWorldExt;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.decoration.*;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.entity.projectile.thrown.*;
import net.minecraft.entity.vehicle.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pose;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class FabricEntity implements Entity {
    private static PermissibleBase perm;

    private final Spigot spigot = new Spigot() {
    };
    protected final FabricServer server;
    protected net.minecraft.entity.Entity entity;
    private EntityDamageEvent lastDamageEvent;

    public FabricEntity(FabricServer server, net.minecraft.entity.Entity entity) {
        this.server = server;
        this.entity = entity;
    }

    public static FabricEntity getEntity(FabricServer server, net.minecraft.entity.Entity entity) {
        /*
         * Order is *EXTREMELY* important -- keep it right! =D
         */
        // CHECKSTYLE:OFF
        if (entity instanceof LivingEntity) {
            // Players
            if (entity instanceof PlayerEntity) {
                if (entity instanceof ServerPlayerEntity) { return new FabricPlayer(server, (ServerPlayerEntity)entity); }
                else { return new FabricHumanEntity(server, (PlayerEntity) entity); }
            }
            // Water Animals
            else if (entity instanceof WaterCreatureEntity) {
                if (entity instanceof SquidEntity) {
                    if (entity instanceof GlowSquidEntity) { /*return new CraftGlowSquid(server, (GlowSquid) entity);*/ throw new NotImplementedYet("FabricGlowSquid"); }
                    else { /*return new CraftSquid(server, (EntitySquid) entity);*/ throw new NotImplementedYet("FabricSquid"); }
                }
                else if (entity instanceof FishEntity) {
                    if (entity instanceof CodEntity) { /*return new CraftCod(server, (EntityCod) entity);*/ throw new NotImplementedYet("FabricCod"); }
                    else if (entity instanceof PufferfishEntity) { /*return new CraftPufferFish(server, (EntityPufferFish) entity);*/ throw new NotImplementedYet("FabricPufferFish"); }
                    else if (entity instanceof SalmonEntity) { /*return new CraftSalmon(server, (EntitySalmon) entity);*/ throw new NotImplementedYet("FabricSalmon"); }
                    else if (entity instanceof TropicalFishEntity) { /*return new CraftTropicalFish(server, (EntityTropicalFish) entity);*/ throw new NotImplementedYet("FabricTropicalFish"); }
                    else if (entity instanceof TadpoleEntity) { /*return new CraftTadpole(server, (Tadpole) entity);*/ throw new NotImplementedYet("FabricTadpole"); }
                    else { /*return new CraftFish(server, (EntityFish) entity);*/ throw new NotImplementedYet("FabricFish"); }
                }
                else if (entity instanceof DolphinEntity) { /*return new CraftDolphin(server, (EntityDolphin) entity);*/ throw new NotImplementedYet("FabricDolphin"); }
                else { /*return new CraftWaterMob(server, (EntityWaterAnimal) entity);*/ throw new NotImplementedYet("FabricWaterMob"); }
            }
            else if (entity instanceof PathAwareEntity) {
                // Animals
                if (entity instanceof AnimalEntity) {
                    if (entity instanceof ChickenEntity) { /*return new CraftChicken(server, (EntityChicken) entity);*/ throw new NotImplementedYet("FabricChicken"); }
                    else if (entity instanceof CowEntity) {
                        if (entity instanceof MooshroomEntity) { /*return new CraftMushroomCow(server, (EntityMushroomCow) entity);*/ throw new NotImplementedYet("FabricMushroomCow"); }
                        else { /*return new CraftCow(server, (EntityCow) entity);*/ throw new NotImplementedYet("FabricCow"); }
                    }
                    else if (entity instanceof PigEntity) { /*return new CraftPig(server, (EntityPig) entity);*/ throw new NotImplementedYet("FabricPig"); }
                    else if (entity instanceof TameableEntity) {
                        if (entity instanceof WolfEntity) { /*return new CraftWolf(server, (EntityWolf) entity);*/ throw new NotImplementedYet("FabricWolf"); }
                        else if (entity instanceof CatEntity) { /*return new CraftCat(server, (EntityCat) entity);*/ throw new NotImplementedYet("FabricCat"); }
                        else if (entity instanceof ParrotEntity) { /*return new CraftParrot(server, (EntityParrot) entity);*/ throw new NotImplementedYet("FabricParrot"); }
                    }
                    else if (entity instanceof SheepEntity) { /*return new CraftSheep(server, (EntitySheep) entity);*/ throw new NotImplementedYet("FabricSheep"); }
                    else if (entity instanceof AbstractHorseEntity) {
                        if (entity instanceof AbstractDonkeyEntity){
                            if (entity instanceof DonkeyEntity) { /*return new CraftDonkey(server, (EntityHorseDonkey) entity);*/ throw new NotImplementedYet("FabricDonkey"); }
                            else if (entity instanceof MuleEntity) { /*return new CraftMule(server, (EntityHorseMule) entity);*/ throw new NotImplementedYet("FabricMule"); }
                            else if (entity instanceof TraderLlamaEntity) { /*return new CraftTraderLlama(server, (EntityLlamaTrader) entity);*/ throw new NotImplementedYet("FabricTraderLlama"); }
                            else if (entity instanceof LlamaEntity) { /*return new CraftLlama(server, (EntityLlama) entity);*/ throw new NotImplementedYet("FabricLlama"); }
                        } else if (entity instanceof HorseEntity) { /*return new CraftHorse(server, (EntityHorse) entity);*/ throw new NotImplementedYet("FabricHorse"); }
                        else if (entity instanceof SkeletonHorseEntity) { /*return new CraftSkeletonHorse(server, (EntityHorseSkeleton) entity);*/ throw new NotImplementedYet("FabricSkeletonHorse"); }
                        else if (entity instanceof ZombieHorseEntity) { /*return new CraftZombieHorse(server, (EntityHorseZombie) entity);*/ throw new NotImplementedYet("FabricZombieHorse"); }
                        else if (entity instanceof CamelEntity) { /*return new CraftCamel(server, (Camel) entity);*/ throw new NotImplementedYet("FabricCamel"); }
                    }
                    else if (entity instanceof RabbitEntity) { /*return new CraftRabbit(server, (EntityRabbit) entity);*/ throw new NotImplementedYet("FabricRabbit"); }
                    else if (entity instanceof PolarBearEntity) { /*return new CraftPolarBear(server, (EntityPolarBear) entity);*/ throw new NotImplementedYet("FabricPolarBear"); }
                    else if (entity instanceof TurtleEntity) { /*return new CraftTurtle(server, (EntityTurtle) entity);*/ throw new NotImplementedYet("FabricTurtle"); }
                    else if (entity instanceof OcelotEntity) { /*return new CraftOcelot(server, (EntityOcelot) entity);*/ throw new NotImplementedYet("FabricOcelot"); }
                    else if (entity instanceof PandaEntity) { /*return new CraftPanda(server, (EntityPanda) entity);*/ throw new NotImplementedYet("FabricPanda"); }
                    else if (entity instanceof FoxEntity) { /*return new CraftFox(server, (EntityFox) entity);*/ throw new NotImplementedYet("FabricFox"); }
                    else if (entity instanceof BeeEntity) { /*return new CraftBee(server, (EntityBee) entity);*/ throw new NotImplementedYet("FabricBox"); }
                    else if (entity instanceof HoglinEntity) { /*return new CraftHoglin(server, (EntityHoglin) entity);*/ throw new NotImplementedYet("FabricHoglin"); }
                    else if (entity instanceof StriderEntity) { /*return new CraftStrider(server, (EntityStrider) entity);*/ throw new NotImplementedYet("FabricStrider"); }
                    else if (entity instanceof AxolotlEntity) { /*return new CraftAxolotl(server, (Axolotl) entity);*/ throw new NotImplementedYet("FabricAxolotl"); }
                    else if (entity instanceof GoatEntity) { /*return new CraftGoat(server, (Goat) entity);*/ throw new NotImplementedYet("FabricGoat"); }
                    else if (entity instanceof FrogEntity) { /*return new CraftFrog(server, (Frog) entity);*/ throw new NotImplementedYet("FabricFrog"); }
                    else  { /*return new CraftAnimals(server, (EntityAnimal) entity);*/ throw new NotImplementedYet("FabricAnimals"); }
                }
                // Monsters
                else if (entity instanceof HostileEntity) {
                    if (entity instanceof ZombieEntity) {
                        if (entity instanceof ZombifiedPiglinEntity) { /*return new CraftPigZombie(server, (EntityPigZombie) entity);*/ throw new NotImplementedYet("FabricPigZombie"); }
                        else if (entity instanceof HuskEntity) { /*return new CraftHusk(server, (EntityZombieHusk) entity);*/ throw new NotImplementedYet("FabricHusk"); }
                        else if (entity instanceof ZombieVillagerEntity) { /*return new CraftVillagerZombie(server, (EntityZombieVillager) entity);*/ throw new NotImplementedYet("FabricVillagerZombie"); }
                        else if (entity instanceof DrownedEntity) { /*return new CraftDrowned(server, (EntityDrowned) entity);*/ throw new NotImplementedYet("FabricDrowned"); }
                        else { /*return new CraftZombie(server, (EntityZombie) entity);*/ throw new NotImplementedYet("FabricZombie"); }
                    }
                    else if (entity instanceof CreeperEntity) { /*return new CraftCreeper(server, (EntityCreeper) entity);*/ throw new NotImplementedYet("FabricCreeper"); }
                    else if (entity instanceof EndermanEntity) { /*return new CraftEnderman(server, (EntityEnderman) entity);*/ throw new NotImplementedYet("FabricEnderman"); }
                    else if (entity instanceof SilverfishEntity) { /*return new CraftSilverfish(server, (EntitySilverfish) entity);*/ throw new NotImplementedYet("FabricSilverfish"); }
                    else if (entity instanceof GiantEntity) { /*return new CraftGiant(server, (EntityGiantZombie) entity);*/ throw new NotImplementedYet("FabricGiantZombie"); }
                    else if (entity instanceof AbstractSkeletonEntity) {
                        if (entity instanceof StrayEntity) { /*return new CraftStray(server, (EntitySkeletonStray) entity);*/ throw new NotImplementedYet("FabricStray"); }
                        else if (entity instanceof WitherSkeletonEntity) { /*return new CraftWitherSkeleton(server, (EntitySkeletonWither) entity);*/ throw new NotImplementedYet("FabricWitherSkeleton"); }
                        else if (entity instanceof SkeletonEntity){ /*return new CraftSkeleton(server, (EntitySkeleton) entity);*/ throw new NotImplementedYet("FabricSkeleton"); }
                    }
                    else if (entity instanceof BlazeEntity) { /*return new CraftBlaze(server, (EntityBlaze) entity);*/ throw new NotImplementedYet("FabricBlaze"); }
                    else if (entity instanceof WitchEntity) { /*return new CraftWitch(server, (EntityWitch) entity);*/ throw new NotImplementedYet("FabricWitch"); }
                    else if (entity instanceof WitherEntity) { /*return new CraftWither(server, (EntityWither) entity);*/ throw new NotImplementedYet("FabricWither"); }
                    else if (entity instanceof SpiderEntity) {
                        if (entity instanceof CaveSpiderEntity) { /*return new CraftCaveSpider(server, (EntityCaveSpider) entity);*/ throw new NotImplementedYet("FabricCaveSpider"); }
                        else { /*return new CraftSpider(server, (EntitySpider) entity);*/ throw new NotImplementedYet("FabricSpider"); }
                    }
                    else if (entity instanceof EndermiteEntity) { /*return new CraftEndermite(server, (EntityEndermite) entity);*/ throw new NotImplementedYet("FabricEndermite"); }
                    else if (entity instanceof GuardianEntity) {
                        if (entity instanceof ElderGuardianEntity) { /*return new CraftElderGuardian(server, (EntityGuardianElder) entity);*/ throw new NotImplementedYet("FabricElderGuardian"); }
                        else { /*return new CraftGuardian(server, (EntityGuardian) entity);*/ throw new NotImplementedYet("FabricGuardian"); }
                    }
                    else if (entity instanceof VexEntity) { /*return new CraftVex(server, (EntityVex) entity);*/ throw new NotImplementedYet("FabricVex"); }
                    else if (entity instanceof IllagerEntity) {
                        if (entity instanceof SpellcastingIllagerEntity) {
                            if (entity instanceof EvokerEntity) { /*return new CraftEvoker(server, (EntityEvoker) entity);*/ throw new NotImplementedYet("FabricEvoker"); }
                            else if (entity instanceof IllusionerEntity) { /*return new CraftIllusioner(server, (EntityIllagerIllusioner) entity);*/ throw new NotImplementedYet("FabricIllusioner"); }
                            else {  /*return new CraftSpellcaster(server, (EntityIllagerWizard) entity);*/ throw new NotImplementedYet("FabricSpellcaster"); }
                        }
                        else if (entity instanceof VindicatorEntity) { /*return new CraftVindicator(server, (EntityVindicator) entity);*/ throw new NotImplementedYet("FabricVindicator"); }
                        else if (entity instanceof PillagerEntity) { /*return new CraftPillager(server, (EntityPillager) entity);*/ throw new NotImplementedYet("FabricPillager"); }
                        else { /*return new CraftIllager(server, (EntityIllagerAbstract) entity);*/ throw new NotImplementedYet("FabricIllager"); }
                    }
                    else if (entity instanceof RavagerEntity) { /*return new CraftRavager(server, (EntityRavager) entity);*/ throw new NotImplementedYet("FabricRavager"); }
                    else if (entity instanceof AbstractPiglinEntity) {
                        if (entity instanceof PiglinEntity) /*return new CraftPiglin(server, (EntityPiglin) entity);*/ throw new NotImplementedYet("FabricPiglin");
                        else if (entity instanceof PiglinBruteEntity) { /*return new CraftPiglinBrute(server, (EntityPiglinBrute) entity);*/ throw new NotImplementedYet("FabricPiglinBrute"); }
                        else { /*return new CraftPiglinAbstract(server, (EntityPiglinAbstract) entity);*/ throw new NotImplementedYet("FabricPiglinAbstract"); }
                    }
                    else if (entity instanceof ZoglinEntity) { /*return new CraftZoglin(server, (EntityZoglin) entity);*/ throw new NotImplementedYet("FabricZoglin"); }
                    else if (entity instanceof WardenEntity) { /*return new CraftWarden(server, (Warden) entity);*/ throw new NotImplementedYet("FabricWarden"); }

                    else  { /*return new CraftMonster(server, (EntityMonster) entity);*/ throw new NotImplementedYet("FabricMonster"); }
                }
                else if (entity instanceof GolemEntity) {
                    if (entity instanceof SnowGolemEntity) { /*return new CraftSnowman(server, (EntitySnowman) entity);*/ throw new NotImplementedYet("FabricSnowman"); }
                    else if (entity instanceof IronGolemEntity) { /*return new CraftIronGolem(server, (EntityIronGolem) entity);*/ throw new NotImplementedYet("FabricIronGolem"); }
                    else if (entity instanceof ShulkerEntity) { /*return new CraftShulker(server, (EntityShulker) entity);*/ throw new NotImplementedYet("FabricShulker"); }
                    else { /*return new CraftGolem(server, (EntityGolem) entity);*/ throw new NotImplementedYet("FabricGolem"); }
                }
                else if (entity instanceof MerchantEntity) {
                    if (entity instanceof VillagerEntity) { /*return new CraftVillager(server, (EntityVillager) entity);*/ throw new NotImplementedYet("FabricVillager"); }
                    else if (entity instanceof WanderingTraderEntity) { /*return new CraftWanderingTrader(server, (EntityVillagerTrader) entity);*/ throw new NotImplementedYet("FabricWanderingTrader"); }
                    else { /*return new CraftAbstractVillager(server, (EntityVillagerAbstract) entity);*/ throw new NotImplementedYet("FabricAbstractVillager"); }
                }
                else if (entity instanceof AllayEntity) { /*return new CraftAllay(server, (Allay) entity);*/ throw new NotImplementedYet("FabricAllay"); }
                else { /*return new CraftCreature(server, (EntityCreature) entity);*/ throw new NotImplementedYet("FabricCreature"); }
            }
            // Slimes are a special (and broken) case
            else if (entity instanceof SlimeEntity) {
                if (entity instanceof MagmaCubeEntity) { /*return new CraftMagmaCube(server, (EntityMagmaCube) entity);*/ throw new NotImplementedYet("FabricMagmaCube"); }
                else { /*return new CraftSlime(server, (EntitySlime) entity);*/ throw new NotImplementedYet("FabricSlime"); }
            }
            // Flying
            else if (entity instanceof FlyingEntity) {
                if (entity instanceof GhastEntity) { /*return new CraftGhast(server, (EntityGhast) entity);*/ throw new NotImplementedYet("FabricGhast"); }
                else if (entity instanceof PhantomEntity) { /*return new CraftPhantom(server, (EntityPhantom) entity);*/ throw new NotImplementedYet("FabricPhantom"); }
                else { /*return new CraftFlying(server, (EntityFlying) entity);*/ throw new NotImplementedYet("FabricFlying"); }
            }
            else if (entity instanceof EnderDragonEntity) {
                /*return new CraftEnderDragon(server, (EntityEnderDragon) entity);*/ throw new NotImplementedYet("FabricEnderDragon");
            }
            // Ambient
            else if (entity instanceof AmbientEntity) {
                if (entity instanceof BatEntity) { /*return new CraftBat(server, (EntityBat) entity);*/ throw new NotImplementedYet("FabricBat"); }
                else { /*return new CraftAmbient(server, (EntityAmbient) entity);*/ throw new NotImplementedYet("FabricAmbient"); }
            }
            else if (entity instanceof ArmorStandEntity) { /*return new CraftArmorStand(server, (EntityArmorStand) entity);*/ throw new NotImplementedYet("FabricArmorStand"); }
            else  { /*return new CraftLivingEntity(server, (EntityLiving) entity);*/ throw new NotImplementedYet("FabricLivingEntity"); }
        }
        else if (entity instanceof EnderDragonPart) {
            /*return new CraftEnderDragonPart(server, (EntityComplexPart) entity);*/ throw new NotImplementedYet("FabricEnderDragonPart");
        }
        else if (entity instanceof ExperienceOrbEntity) { /*return new CraftExperienceOrb(server, (EntityExperienceOrb) entity);*/ throw new NotImplementedYet("FabricExperienceOrb"); }
        else if (entity instanceof ArrowEntity) { /*return new CraftTippedArrow(server, (EntityTippedArrow) entity);*/ throw new NotImplementedYet("FabricTippedArrow"); }
        else if (entity instanceof SpectralArrowEntity) { /*return new CraftSpectralArrow(server, (EntitySpectralArrow) entity);*/ throw new NotImplementedYet("FabricSpectralArrow"); }
        else if (entity instanceof PersistentProjectileEntity) {
            if (entity instanceof TridentEntity) { /*return new CraftTrident(server, (EntityThrownTrident) entity);*/ throw new NotImplementedYet("FabricTrident"); }
            else { /*return new CraftArrow(server, (EntityArrow) entity);*/ throw new NotImplementedYet("FabricArrow"); }
        }
        else if (entity instanceof BoatEntity) {
            if (entity instanceof ChestBoatEntity) { /*return new CraftChestBoat(server, (ChestBoat) entity);*/ throw new NotImplementedYet("FabricChestBoat"); }
            else { /*return new CraftBoat(server, (EntityBoat) entity);*/ throw new NotImplementedYet("FabricBoat"); }
        }
        else if (entity instanceof ThrownEntity) {
            if (entity instanceof EggEntity) { /*return new CraftEgg(server, (EntityEgg) entity);*/ throw new NotImplementedYet("FabricEgg"); }
            else if (entity instanceof SnowballEntity) { /*return new CraftSnowball(server, (EntitySnowball) entity);*/ throw new NotImplementedYet("FabricSnowball"); }
            else if (entity instanceof PotionEntity) { /*return new CraftThrownPotion(server, (EntityPotion) entity);*/ throw new NotImplementedYet("FabricThrownPotion"); }
            else if (entity instanceof EnderPearlEntity) { /*return new CraftEnderPearl(server, (EntityEnderPearl) entity);*/ throw new NotImplementedYet("FabricEnderPearl"); }
            else if (entity instanceof ExperienceBottleEntity) { /*return new CraftThrownExpBottle(server, (EntityThrownExpBottle) entity);*/ throw new NotImplementedYet("FabricThrownExpBottle"); }
        }
        else if (entity instanceof FallingBlockEntity) { /*return new CraftFallingBlock(server, (EntityFallingBlock) entity);*/ throw new NotImplementedYet("FabricFallingBlock"); }
        else if (entity instanceof ExplosiveProjectileEntity) {
            if (entity instanceof SmallFireballEntity) { /*return new CraftSmallFireball(server, (EntitySmallFireball) entity);*/ throw new NotImplementedYet("FabricSmallFireball"); }
            else if (entity instanceof FireballEntity) { /*return new CraftLargeFireball(server, (EntityLargeFireball) entity);*/ throw new NotImplementedYet("FabricLargeFireball"); }
            else if (entity instanceof WitherSkullEntity) { /*return new CraftWitherSkull(server, (EntityWitherSkull) entity);*/ throw new NotImplementedYet("FabricWitherSkull"); }
            else if (entity instanceof DragonFireballEntity) { /*return new CraftDragonFireball(server, (EntityDragonFireball) entity);*/ throw new NotImplementedYet("FabricDragonFireball"); }
            else { /*return new CraftFireball(server, (EntityFireball) entity);*/ throw new NotImplementedYet("FabricFireball"); }
        }
        else if (entity instanceof EyeOfEnderEntity) { /*return new CraftEnderSignal(server, (EntityEnderSignal) entity);*/ throw new NotImplementedYet("FabricEnderSignal"); }
        else if (entity instanceof EndCrystalEntity) { /*return new CraftEnderCrystal(server, (EntityEnderCrystal) entity);*/ throw new NotImplementedYet("FabricEnderCrystal"); }
        else if (entity instanceof FishingBobberEntity) { /*return new CraftFishHook(server, (EntityFishingHook) entity);*/ throw new NotImplementedYet("FabricFishHook"); }
        else if (entity instanceof ItemEntity) { /*return new CraftItem(server, (EntityItem) entity);*/ throw new NotImplementedYet("FabricItem"); }
        else if (entity instanceof LightningEntity) { /*return new CraftLightningStrike(server, (EntityLightning) entity);*/ throw new NotImplementedYet("FabricLightningStrike"); }
        else if (entity instanceof AbstractMinecartEntity) {
            if (entity instanceof FurnaceMinecartEntity) { /*return new CraftMinecartFurnace(server, (EntityMinecartFurnace) entity);*/ throw new NotImplementedYet("FabricMinecartFurnace"); }
            else if (entity instanceof ChestMinecartEntity) { /*return new CraftMinecartChest(server, (EntityMinecartChest) entity);*/ throw new NotImplementedYet("FabricMinecartChest"); }
            else if (entity instanceof TntMinecartEntity) { /*return new CraftMinecartTNT(server, (EntityMinecartTNT) entity);*/ throw new NotImplementedYet("FabricMinecartTNT"); }
            else if (entity instanceof HopperMinecartEntity) { /*return new CraftMinecartHopper(server, (EntityMinecartHopper) entity);*/ throw new NotImplementedYet("FabricMinecartHopper"); }
            else if (entity instanceof SpawnerMinecartEntity) { /*return new CraftMinecartMobSpawner(server, (EntityMinecartMobSpawner) entity);*/ throw new NotImplementedYet("FabricMinecartMobSpawner"); }
            else if (entity instanceof MinecartEntity) { /*return new CraftMinecartRideable(server, (EntityMinecartRideable) entity);*/ throw new NotImplementedYet("FabricMinecartRideable"); }
            else if (entity instanceof CommandBlockMinecartEntity) { /*return new CraftMinecartCommand(server, (EntityMinecartCommandBlock) entity);*/ throw new NotImplementedYet("FabricMinecartCommand"); }
        } else if (entity instanceof AbstractDecorationEntity) {
            if (entity instanceof PaintingEntity) { /*return new CraftPainting(server, (EntityPainting) entity);*/ throw new NotImplementedYet("FabricPainting"); }
            else if (entity instanceof ItemFrameEntity) {
                if (entity instanceof GlowItemFrameEntity) { /*return new CraftGlowItemFrame(server, (GlowItemFrame) entity);*/ throw new NotImplementedYet("FabricGlowItemFrame"); }
                else { /*return new CraftItemFrame(server, (EntityItemFrame) entity);*/ throw new NotImplementedYet("FabricItemFrame"); }
            }
            else if (entity instanceof LeashKnotEntity) { /*return new CraftLeash(server, (EntityLeash) entity);*/ throw new NotImplementedYet("FabricLeash"); }
            else { /*return new CraftHanging(server, (EntityHanging) entity);*/ throw new NotImplementedYet("FabricHanging"); }
        }
        else if (entity instanceof TntEntity) { /*return new CraftTNTPrimed(server, (EntityTNTPrimed) entity);*/ throw new NotImplementedYet("FabricTNTPrimed"); }
        else if (entity instanceof FireworkRocketEntity) { /*return new CraftFirework(server, (EntityFireworks) entity);*/ throw new NotImplementedYet("FabricFireworks"); }
        else if (entity instanceof ShulkerBulletEntity) { /*return new CraftShulkerBullet(server, (EntityShulkerBullet) entity);*/ throw new NotImplementedYet("FabricShulkerBullet"); }
        else if (entity instanceof AreaEffectCloudEntity) { /*return new CraftAreaEffectCloud(server, (EntityAreaEffectCloud) entity);*/ throw new NotImplementedYet("FabricAreaEffectCloud"); }
        else if (entity instanceof EvokerFangsEntity) { /*return new CraftEvokerFangs(server, (EntityEvokerFangs) entity);*/ throw new NotImplementedYet("FabricEvokerFangs"); }
        else if (entity instanceof LlamaSpitEntity) { /*return new CraftLlamaSpit(server, (EntityLlamaSpit) entity);*/ throw new NotImplementedYet("FabricLlamaSpit"); }
        else if (entity instanceof MarkerEntity) { /*return new CraftMarker(server, (Marker) entity);*/ throw new NotImplementedYet("FabricMarker"); }
        // CHECKSTYLE:ON

        return new FabricUnknownEntity(server, entity);
    }

    public net.minecraft.entity.Entity getHandle() {
        return entity;
    }

    @NotNull
    @Override
    public Location getLocation() {
        throw new NotImplementedYet();
//        return new Location(getWorld(), entity.getX(), entity.getY(), entity.getZ(), entity.get);
    }

    @Nullable
    @Override
    public Location getLocation(@Nullable Location loc) {
        if (loc != null) {
            loc.setWorld(getWorld());
            loc.setX(entity.getX());
            loc.setY(entity.getY());
            loc.setZ(entity.getZ());
            throw new NotImplementedYet();
        }
        return loc;
    }

    @Override
    public void setVelocity(@NotNull Vector velocity) {
        //noinspection ConstantValue
        Preconditions.checkArgument(velocity != null, "velocity");
        velocity.checkFinite();
        entity.setVelocity(Conversion.toVec3d(velocity));
        entity.velocityModified = true;
    }

    @NotNull
    @Override
    public Vector getVelocity() {
        return Conversion.toVector(entity.getVelocity());
    }

    @Override
    public double getHeight() {
        return getHandle().getHeight();
    }

    @Override
    public double getWidth() {
        return getHandle().getWidth();
    }

    @NotNull
    @Override
    public BoundingBox getBoundingBox() {
        final Box bb = getHandle().getBoundingBox();
        return new BoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
    }

    @Override
    public boolean isOnGround() {
        if (entity instanceof PersistentProjectileEntity projectile) {
            return projectile.inGround;
        }
        return entity.isOnGround();
    }

    @Override
    public boolean isInWater() {
        return entity.isTouchingWater();
    }

    @NotNull
    @Override
    public World getWorld() {
        return ((ServerWorldExt)entity.world).getBukkitWorld();
    }

    @Override
    public void setRotation(float yaw, float pitch) {
        NumberConversions.checkFinite(pitch, "pitch not finite");
        NumberConversions.checkFinite(yaw, "yaw not finite");

        yaw = Location.normalizeYaw(yaw);
        pitch = Location.normalizePitch(pitch);

        entity.setYaw(yaw);
        entity.setPitch(pitch);
        entity.prevYaw = yaw;
        entity.prevPitch = pitch;
        entity.setHeadYaw(yaw);
    }

    @Override
    public boolean teleport(@NotNull Location location) {
        return teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @Override
    public boolean teleport(@NotNull Location location, @NotNull PlayerTeleportEvent.TeleportCause cause) {
        //noinspection ConstantValue
        Preconditions.checkArgument(location != null, "location cannot be null");
        location.checkFinite();

        if (entity.hasPassengers() || entity.isRemoved()) {
            return false;
        }

        entity.stopRiding();

        if (location.getWorld() != null && !location.getWorld().equals(getWorld())) {
            throw new NotImplementedYet("Cross-dimension teleport");
        }

        entity.updatePositionAndAngles(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        entity.setHeadYaw(location.getYaw());
        return true;
    }

    @Override
    public boolean teleport(@NotNull Entity destination) {
        return teleport(destination.getLocation());
    }

    @Override
    public boolean teleport(@NotNull Entity destination, @NotNull PlayerTeleportEvent.TeleportCause cause) {
        return teleport(destination.getLocation(), cause);
    }

    @NotNull
    @Override
    public List<Entity> getNearbyEntities(double x, double y, double z) {
        // TODO: worldgen check

        final List<net.minecraft.entity.Entity> vanillaEntityList = entity.world.getOtherEntities(entity, entity.getBoundingBox().expand(x, y, z), entity1 -> true);
        final List<Entity> bukkitEntityList = new ArrayList<>(vanillaEntityList.size());
        for (final net.minecraft.entity.Entity e : vanillaEntityList) {
            bukkitEntityList.add(e.getBukkitEntity());
        }
        return bukkitEntityList;
    }

    @Override
    public int getEntityId() {
        return entity.getId();
    }

    @Override
    public int getFireTicks() {
        return entity.getFireTicks();
    }

    @Override
    public int getMaxFireTicks() {
        // Well... getMaxFireTicks() doesn't do what it says. However, we still need to implement it according to CB,
        // not according to the docs, since CB is what users will be developing against. If their code doesn't do what
        // they thought it would according to the docs, they'll fix it to work with CB, so we *need* to do the same
        // thing as CB here.
        return entity.getBurningDuration();
    }

    @Override
    public void setFireTicks(int ticks) {
        getHandle().setFireTicks(ticks);
    }

    @Override
    public void setVisualFire(boolean fire) {
        getHandle().hasVisualFire = fire;
    }

    @Override
    public boolean isVisualFire() {
        return getHandle().hasVisualFire;
    }

    @Override
    public int getFreezeTicks() {
        return getHandle().getFrozenTicks();
    }

    @Override
    public int getMaxFreezeTicks() {
        // See comment on #getMaxFireTicks
        return getHandle().getMinFreezeDamageTicks();
    }

    @Override
    public void setFreezeTicks(int ticks) {
        Preconditions.checkArgument(0 <= ticks, "Ticks cannot be less than 0");
        getHandle().setFrozenTicks(ticks);
    }

    @Override
    public boolean isFrozen() {
        return getHandle().isFrozen();
    }

    @Override
    public void remove() {
        entity.discard();
    }

    @Override
    public boolean isDead() {
        return !entity.isAlive();
    }

    @Override
    public boolean isValid() {
        return entity.isAlive() /* && entity.valid && entity.isChunkLoaded() */;
    }

    @NotNull
    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public boolean isPersistent() {
        throw new NotImplementedYet();
    }

    @Override
    public void setPersistent(boolean persistent) {
        throw new NotImplementedYet();
    }

    @Nullable
    @Override
    public Entity getPassenger() {
        final net.minecraft.entity.Entity result = getHandle().getFirstPassenger();
        return result != null ? result.getBukkitEntity() : null;
    }

    @Override
    public boolean setPassenger(@NotNull Entity passenger) {
        Preconditions.checkArgument(!equals(passenger), "Entity cannot ride itself.");
        if (!(passenger instanceof FabricEntity other)) {
            return false;
        }
        eject();
        return other.getHandle().startRiding(getHandle());
    }

    @NotNull
    @Override
    public List<Entity> getPassengers() {
        return getHandle().getPassengerList().stream().map(e -> (Entity)e.getBukkitEntity()).toList();
    }

    @Override
    public boolean addPassenger(@NotNull Entity passenger) {
        //noinspection ConstantValue
        Preconditions.checkArgument(passenger != null, "passenger == null");
        Preconditions.checkArgument(!equals(passenger), "Entity cannot ride itself.");
        return ((FabricEntity)passenger).getHandle().startRiding(getHandle(), true);
    }

    @Override
    public boolean removePassenger(@NotNull Entity passenger) {
        //noinspection ConstantValue
        Preconditions.checkArgument(passenger != null, "passenger == null");
        ((FabricEntity)passenger).getHandle().stopRiding();
        return true;
    }

    @Override
    public boolean isEmpty() {
        return !getHandle().hasPassengers();
    }

    @Override
    public boolean eject() {
        if (isEmpty()) {
            return false;
        }
        getHandle().removeAllPassengers();
        return true;
    }

    @Override
    public float getFallDistance() {
        return getHandle().fallDistance;
    }

    @Override
    public void setFallDistance(float distance) {
        getHandle().fallDistance = distance;
    }

    @Override
    public void setLastDamageCause(@Nullable EntityDamageEvent event) {
        lastDamageEvent = event;
    }

    @Nullable
    @Override
    public EntityDamageEvent getLastDamageCause() {
        return lastDamageEvent;
    }

    @NotNull
    @Override
    public UUID getUniqueId() {
        return getHandle().getUuid();
    }

    @Override
    public int getTicksLived() {
        return getHandle().age;
    }

    @Override
    public void setTicksLived(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Age must be at least 1 tick");
        }
        getHandle().age = value;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void playEffect(@NotNull EntityEffect type) {
        //noinspection ConstantValue
        Preconditions.checkArgument(type != null, "type");
        // TODO: worldgen check

        if (type.getApplicable().isInstance(this)) {
            getHandle().world.sendEntityStatus(getHandle(), type.getData());
        }
    }

    @NotNull
    @Override
    public Sound getSwimSound() {
        return Conversion.toSound(getHandle().getSwimSound());
    }

    @NotNull
    @Override
    public Sound getSwimSplashSound() {
        return Conversion.toSound(getHandle().getSplashSound());
    }

    @NotNull
    @Override
    public Sound getSwimHighSpeedSplashSound() {
        return Conversion.toSound(getHandle().getHighSpeedSplashSound());
    }

    @Override
    public boolean isInsideVehicle() {
        return getHandle().hasVehicle();
    }

    @Override
    public boolean leaveVehicle() {
        if (!isInsideVehicle()) {
            return false;
        }
        getHandle().stopRiding();
        return true;
    }

    @Nullable
    @Override
    public Entity getVehicle() {
        if (!isInsideVehicle()) {
            return null;
        }
        //noinspection DataFlowIssue
        return getHandle().getVehicle().getBukkitEntity();
    }

    @Override
    public void setCustomNameVisible(boolean flag) {
        getHandle().setCustomNameVisible(flag);
    }

    @Override
    public boolean isCustomNameVisible() {
        return getHandle().isCustomNameVisible();
    }

    @Override
    public void setGlowing(boolean flag) {
        getHandle().setGlowing(flag);
    }

    @Override
    public boolean isGlowing() {
        return getHandle().isGlowing();
    }

    @Override
    public void setInvulnerable(boolean flag) {
        getHandle().setInvulnerable(flag);
    }

    @Override
    public boolean isInvulnerable() {
        return getHandle().isInvulnerable();
    }

    @Override
    public boolean isSilent() {
        return getHandle().isSilent();
    }

    @Override
    public void setSilent(boolean flag) {
        getHandle().setSilent(flag);
    }

    @Override
    public boolean hasGravity() {
        return !getHandle().hasNoGravity();
    }

    @Override
    public void setGravity(boolean gravity) {
        getHandle().setNoGravity(!gravity);
    }

    @Override
    public int getPortalCooldown() {
        return getHandle().portalCooldown;
    }

    @Override
    public void setPortalCooldown(int cooldown) {
        getHandle().portalCooldown = cooldown;
    }

    @NotNull
    @Override
    public Set<String> getScoreboardTags() {
        return getHandle().getScoreboardTags();
    }

    @Override
    public boolean addScoreboardTag(@NotNull String tag) {
        return getHandle().addScoreboardTag(tag);
    }

    @Override
    public boolean removeScoreboardTag(@NotNull String tag) {
        return getHandle().removeScoreboardTag(tag);
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public PistonMoveReaction getPistonMoveReaction() {
        //noinspection DataFlowIssue
        return PistonMoveReaction.getById(getHandle().getPistonBehavior().ordinal());
    }

    @NotNull
    @Override
    public BlockFace getFacing() {
        return FabricBlock.notchToBlockFace(getHandle().getMovementDirection());
    }

    @NotNull
    @Override
    public Pose getPose() {
        return Pose.values()[getHandle().getPose().ordinal()];
    }

    @NotNull
    @Override
    public SpawnCategory getSpawnCategory() {
        return Conversion.toSpawnCategory(getHandle().getType().getSpawnGroup());
    }

    @Override
    public void setMetadata(@NotNull String metadataKey, @NotNull MetadataValue newMetadataValue) {
        throw new NotImplementedYet();
    }

    @NotNull
    @Override
    public List<MetadataValue> getMetadata(@NotNull String metadataKey) {
        throw new NotImplementedYet();
    }

    @Override
    public boolean hasMetadata(@NotNull String metadataKey) {
        throw new NotImplementedYet();
    }

    @Override
    public void removeMetadata(@NotNull String metadataKey, @NotNull Plugin owningPlugin) {
        throw new NotImplementedYet();
    }

    @Override
    public void sendMessage(@NotNull String message) {
    }

    @Override
    public void sendMessage(@NotNull String... messages) {
    }

    @Override
    public void sendMessage(@Nullable UUID sender, @NotNull String message) {
        sendMessage(message);
    }

    @Override
    public void sendMessage(@Nullable UUID sender, @NotNull String... messages) {
        sendMessage(messages);
    }

    @NotNull
    @Override
    public String getName() {
        return ChatMessageConversion.fromComponent(getHandle().getName());
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return getPermissibleBase().isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        return getPermissibleBase().isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        return getPermissibleBase().hasPermission(name);
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return getPermissibleBase().hasPermission(perm);
    }

    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
        return getPermissibleBase().addAttachment(plugin, name, value);
    }

    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return getPermissibleBase().addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
        return getPermissibleBase().addAttachment(plugin, name, value, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
        return getPermissibleBase().addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment attachment) {
        getPermissibleBase().removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        getPermissibleBase().recalculatePermissions();
    }

    @NotNull
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return getPermissibleBase().getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return getPermissibleBase().isOp();
    }

    @Override
    public void setOp(boolean value) {
        getPermissibleBase().setOp(value);
    }

    @Nullable
    @Override
    public String getCustomName() {
        final Text name = getHandle().getCustomName();
        return name != null ? ChatMessageConversion.fromComponent(name) : null;
    }

    @Override
    public void setCustomName(@Nullable String name) {
        if (name != null && name.length() > 256) {
            name = name.substring(0, 256);
        }
        getHandle().setCustomName(ChatMessageConversion.fromStringOrNull(name));
    }

    @NotNull
    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        throw new NotImplementedYet();
    }

    private static PermissibleBase getPermissibleBase() {
        if (perm == null) {
            perm = new PermissibleBase(new ServerOperator() {
                @Override
                public boolean isOp() {
                    return false;
                }

                @Override
                public void setOp(boolean value) {
                }
            });
        }
        return perm;
    }

    @NotNull
    @Override
    public Spigot spigot() {
        return spigot;
    }
}
