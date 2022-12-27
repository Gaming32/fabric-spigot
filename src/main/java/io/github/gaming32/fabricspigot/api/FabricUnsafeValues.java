package io.github.gaming32.fabricspigot.api;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Dynamic;
import io.github.gaming32.fabricspigot.FabricSpigot;
import io.github.gaming32.fabricspigot.util.Conversion;
import io.github.gaming32.fabricspigot.util.NotImplementedYet;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.datafixer.Schemas;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtString;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.WorldSavePath;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.CreativeCategory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

@SuppressWarnings("deprecation")
public class FabricUnsafeValues implements UnsafeValues {
    public static final FabricUnsafeValues INSTANCE = new FabricUnsafeValues();

    private static final List<String> SUPPORTED_API = List.of("1.13", "1.14", "1.15", "1.16", "1.17", "1.18", "1.19");

    private static final Map<Block, Material> BLOCK_MATERIAL = new HashMap<>();
    private static final Map<Item, Material> ITEM_MATERIAL = new HashMap<>();
    private static final Map<net.minecraft.fluid.Fluid, Fluid> FLUID_MATERIAL = new HashMap<>();
    private static final Map<Material, Item> MATERIAL_ITEM = new HashMap<>();
    private static final Map<Material, Block> MATERIAL_BLOCK = new HashMap<>();
    private static final Map<Material, net.minecraft.fluid.Fluid> MATERIAL_FLUID = new HashMap<>();

    static {
        for (Block block : Registries.BLOCK) {
            BLOCK_MATERIAL.put(block, Material.getMaterial(Registries.BLOCK.getId(block).getPath().toUpperCase(Locale.ROOT)));
        }

        for (Item item : Registries.ITEM) {
            ITEM_MATERIAL.put(item, Material.getMaterial(Registries.ITEM.getId(item).getPath().toUpperCase(Locale.ROOT)));
        }

        for (net.minecraft.fluid.Fluid fluid : Registries.FLUID) {
            FLUID_MATERIAL.put(fluid, Registry.FLUID.get(Conversion.toNamespacedKey(Registries.FLUID.getId(fluid))));
        }

        for (Material material : Material.values()) {
            if (material.isLegacy()) {
                continue;
            }

            Identifier key = key(material);
            Registries.ITEM.getOrEmpty(key).ifPresent((item) -> {
                MATERIAL_ITEM.put(material, item);
            });
            Registries.BLOCK.getOrEmpty(key).ifPresent((block) -> {
                MATERIAL_BLOCK.put(material, block);
            });
            Registries.FLUID.getOrEmpty(key).ifPresent((fluid) -> {
                MATERIAL_FLUID.put(material, fluid);
            });
        }
    }

    @Override
    public Material toLegacy(Material material) {
        throw new NotImplementedYet();
    }

    @Override
    public Material fromLegacy(Material material) {
        throw new NotImplementedYet();
    }

    @Override
    public Material fromLegacy(MaterialData material) {
        throw new NotImplementedYet();
    }

    @Override
    public Material fromLegacy(MaterialData material, boolean itemPriority) {
        throw new NotImplementedYet();
    }

    @Override
    public BlockData fromLegacy(Material material, byte data) {
        throw new NotImplementedYet();
    }

    @Override
    public Material getMaterial(String material, int version) {
        Preconditions.checkArgument(material != null, "material == null");
        Preconditions.checkArgument(version <= getDataVersion(), "Newer version! Server downgrades are not supported!");

        if (version == getDataVersion()) {
            return Material.getMaterial(material);
        }

        final Dynamic<NbtElement> name = new Dynamic<>(NbtOps.INSTANCE, NbtString.of("minecraft:" + material.toLowerCase(Locale.ROOT)));
        Dynamic<NbtElement> converted = Schemas.getFixer().update(TypeReferences.ITEM_NAME, name, version, getDataVersion());

        if (name.equals(converted)) {
            converted = Schemas.getFixer().update(TypeReferences.BLOCK_NAME, name, version, getDataVersion());
        }

        return Material.matchMaterial(converted.asString(""));
    }

    @Override
    public int getDataVersion() {
        return SharedConstants.getGameVersion().getSaveVersion().getId();
    }

    @Override
    public ItemStack modifyItemStack(ItemStack stack, String arguments) {
        throw new NotImplementedYet();
    }

    @Override
    public void checkSupported(PluginDescriptionFile pdf) throws InvalidPluginException {
        if (pdf.getAPIVersion() != null) {
            final int pluginIndex = SUPPORTED_API.indexOf(pdf.getAPIVersion());

            if (pluginIndex == -1) {
                throw new InvalidPluginException("Unsupported API version " + pdf.getAPIVersion());
            }
        } else {
            throw new NotImplementedYet();
        }
    }

    @Override
    public byte[] processClass(PluginDescriptionFile pdf, String path, byte[] clazz) {
        // TODO: Implement Spigot Commodore to update API version. This is also where we'll implement NMS remapping.
        return clazz;
    }

    private static File getBukkitDataPackFolder() {
        return new File(FabricSpigot.SERVER.getHandle().getSavePath(WorldSavePath.DATAPACKS).toFile(), "bukkit");
    }

    @Override
    public Advancement loadAdvancement(NamespacedKey key, String advancement) {
        if (Bukkit.getAdvancement(key) != null) {
            throw new IllegalArgumentException("Advancement " + key + " already exists");
        }
        final Identifier vanillaKey = Conversion.toIdentifier(key);
        final JsonElement element = ServerAdvancementLoader.GSON.fromJson(advancement, JsonElement.class);
        final JsonObject object = JsonHelper.asObject(element, "advancement");
        final net.minecraft.advancement.Advancement.Builder nms = net.minecraft.advancement.Advancement.Builder.fromJson(object, new AdvancementEntityPredicateDeserializer(vanillaKey, FabricSpigot.SERVER.getHandle().getPredicateManager()));
        //noinspection ConstantValue
        if (nms != null) {
            FabricSpigot.SERVER.getHandle().getAdvancementLoader().manager.load(Maps.newHashMap(Map.of(vanillaKey, nms)));
            final Advancement bukkit = Bukkit.getAdvancement(key);

            if (bukkit != null) {
                final File file = new File(getBukkitDataPackFolder(), "data" + File.separator + key.getNamespace() + File.separator + "advancements" + File.separator + key.getKey() + ".json");
                //noinspection ResultOfMethodCallIgnored
                file.getParentFile().mkdirs();

                try {
                    Files.write(advancement, file, Charsets.UTF_8);
                } catch (IOException ex) {
                    Bukkit.getLogger().log(Level.SEVERE, "Error saving advancement " + key, ex);
                }

                FabricSpigot.SERVER.getHandle().getPlayerManager().onDataPacksReloaded();

                return bukkit;
            }
        }
        return null;
    }

    @Override
    public boolean removeAdvancement(NamespacedKey key) {
        final File file = new File(getBukkitDataPackFolder(), "data" + File.separator + key.getNamespace() + File.separator + "advancements" + File.separator + key.getKey() + ".json");
        return file.delete();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(Material material, EquipmentSlot slot) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> defaultAttributes = ImmutableMultimap.builder();

        throw new NotImplementedYet();
//        Multimap<EntityAttribute, EntityAttributeModifier> nmsDefaultAttributes = getItem(material).getAttributeModifiers(CraftEquipmentSlot.getNMS(slot));
//        for (Map.Entry<EntityAttribute, EntityAttributeModifier> mapEntry : nmsDefaultAttributes.entries()) {
//            Attribute attribute = CraftAttributeMap.fromMinecraft(BuiltInRegistries.ATTRIBUTE.getKey(mapEntry.getKey()).toString());
//            defaultAttributes.put(attribute, CraftAttributeInstance.convert(mapEntry.getValue(), slot));
//        }
//
//        return defaultAttributes.build();
    }

    public static Item getItem(Material material, short data) {
        if (material.isLegacy()) {
            throw new NotImplementedYet();
        }

        return getItem(material);
    }

    public static Item getItem(Material material) {
        if (material != null && material.isLegacy()) {
            throw new NotImplementedYet();
        }

        return MATERIAL_ITEM.get(material);
    }

    public static Identifier key(Material mat) {
        return Conversion.toIdentifier(mat.getKey());
    }

    @Override
    public CreativeCategory getCreativeCategory(Material material) {
        return CreativeCategory.BUILDING_BLOCKS;
    }

    public static Material getMaterial(Block block) {
        return BLOCK_MATERIAL.get(block);
    }

    public static Block getBlock(Material material) {
        if (material != null && material.isLegacy()) {
            throw new NotImplementedYet();
        }
        return MATERIAL_BLOCK.get(material);
    }
}
