package betterwithmods.common;

import betterwithmods.BWMod;
import betterwithmods.api.IMultiLocations;
import betterwithmods.api.block.IMultiVariants;
import betterwithmods.client.BWCreativeTabs;
import betterwithmods.client.BWStateMapper;
import betterwithmods.common.items.*;
import betterwithmods.common.items.tools.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSoup;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class BWMItems {
    public static final ToolMaterial SOULFORGED_STEEL = EnumHelper.addToolMaterial("SOULFORGED_STEEL", 4, 2250, 10f, 3, 22);
    public static final Item MATERIAL = new ItemMaterial().setRegistryName("material");
    public static final Item AXLE_GENERATOR = new ItemAxleGenerator().setRegistryName("axle_generator");
    public static final Item BARK = new ItemBark().setRegistryName("bark");
    public static final Item DONUT = new ItemFood(2, 0.25f, false).setCreativeTab(CreativeTabs.FOOD).setRegistryName("donut");
    public static final Item DYNAMITE = new ItemDynamite().setRegistryName("dynamite");
    public static final Item DYNAMITE_BUNDLE = new ItemDynamiteBundle().setRegistryName("dynamite_bundle");
    public static final Item FERTILIZER = new ItemFertilizer().setRegistryName("fertilizer");
    public static final Item STEEL_AXE = new ItemSoulforgedAxe().setRegistryName("steel_axe");
    public static final Item STEEL_HOE = new ItemSoulforgedHoe().setRegistryName("steel_hoe");
    public static final Item STEEL_PICKAXE = new ItemSoulforgedPickaxe().setRegistryName("steel_pickaxe");
    public static final Item STEEL_SHOVEL = new ItemSoulforgedShovel().setRegistryName("steel_shovel");
    public static final Item STEEL_SWORD = new ItemSoulforgedSword().setRegistryName("steel_sword");
    public static final Item STEEL_MATTOCK = new ItemSoulforgedMattock().setRegistryName("steel_mattock");
    public static final Item STEEL_BATTLEAXE = new ItemSoulforgedBattleAxe().setRegistryName("steel_battleaxe");
    public static final Item CREEPER_OYSTER = (new ItemAltNameFood(2, false).setPotionEffect(new PotionEffect(MobEffects.POISON, 100, 0), 1)).setCreativeTab(CreativeTabs.FOOD).setRegistryName("creeper_oyster");
    public static final Item ENDER_SPECTACLES = new ItemEnderSpectacles().setRegistryName("ender_spectacles");
    public static final Item STEEL_HELMET = new ItemSoulforgeArmor(EntityEquipmentSlot.HEAD).setRegistryName("steel_helmet");
    public static final Item STEEL_CHEST = new ItemSoulforgeArmor(EntityEquipmentSlot.CHEST).setRegistryName("steel_chest");
    public static final Item STEEL_PANTS = new ItemSoulforgeArmor(EntityEquipmentSlot.LEGS).setRegistryName("steel_pants");
    public static final Item STEEL_BOOTS = new ItemSoulforgeArmor(EntityEquipmentSlot.FEET).setRegistryName("steel_boots");
    public static final Item BREEDING_HARNESS = new ItemBreedingHarness().setRegistryName("breeding_harness");
    public static final Item RAW_EGG = new ItemFood(2, 0.2f, false).setPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 0), 0.3F).setCreativeTab(CreativeTabs.FOOD).setRegistryName("raw_egg");
    public static final Item COOKED_EGG = new ItemFood(3, 0.5f, false).setCreativeTab(CreativeTabs.FOOD).setRegistryName("cooked_egg");
    public static final Item RAW_SCRAMBLED_EGG = new ItemFood(4, 0.3f, false).setPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 0), 0.3F).setCreativeTab(CreativeTabs.FOOD).setRegistryName("raw_scrambled_egg");
    public static final Item COOKED_SCRAMBLED_EGG = new ItemFood(5, 0.5f, false).setCreativeTab(CreativeTabs.FOOD).setRegistryName("cooked_scrambled_egg");
    public static final Item RAW_OMELET = new ItemFood(3, 0.5f, false).setPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 0), 0.3F).setCreativeTab(CreativeTabs.FOOD).setRegistryName("raw_omelet");
    public static final Item COOKED_OMELET = new ItemFood(4, 1, false).setCreativeTab(CreativeTabs.FOOD).setRegistryName("cooked_omelet");
    public static final Item HAM_AND_EGGS = new ItemFood(6, 1, false).setCreativeTab(CreativeTabs.FOOD).setRegistryName("ham_and_eggs");
    public static final Item TASTY_SANDWICH = new ItemFood(7, 0.7f, false).setCreativeTab(CreativeTabs.FOOD).setRegistryName("tasty_sandwich");
    public static final Item COMPOSITE_BOW = new ItemCompositeBow().setCreativeTab(BWCreativeTabs.BWTAB).setRegistryName("composite_bow");
    public static final Item BROADHEAD_ARROW = new ItemBroadheadArrow().setCreativeTab(BWCreativeTabs.BWTAB).setRegistryName("broadhead_arrow");
    public static final Item BEEF_DINNER = new ItemFood(8, 0.6f, false).setCreativeTab(CreativeTabs.FOOD).setRegistryName("beef_dinner");
    public static final Item BEEF_POTATOES = new ItemFood(7, 0.5f, false).setCreativeTab(CreativeTabs.FOOD).setRegistryName("beef_potatoes");
    public static final Item CHICKEN_SOUP = new ItemSoup(8).setMaxStackSize(64).setCreativeTab(CreativeTabs.FOOD).setRegistryName("chicken_soup");
    public static final Item CHOCOLATE = new ItemFood(2, 0.2f, false).setCreativeTab(CreativeTabs.FOOD).setRegistryName("chocolate");
    public static final Item CHOWDER = new ItemSoup(5).setMaxStackSize(64).setCreativeTab(CreativeTabs.FOOD).setRegistryName("chowder");
    public static final Item COOKED_KEBAB = new ItemFood(8, 0.4f, false).setCreativeTab(CreativeTabs.FOOD).setRegistryName("cooked_kebab");
    public static final Item HEARTY_STEW = new ItemSoup(10).setMaxStackSize(64).setCreativeTab(CreativeTabs.FOOD).setRegistryName("hearty_stew");
    public static final Item RAW_KEBAB = new ItemFood(4, 0.3f, false).setPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 0), 0.3F).setCreativeTab(CreativeTabs.FOOD).setRegistryName("raw_kebab");
    public static final Item PORK_DINNER = new ItemFood(8, 0.6f, false).setCreativeTab(CreativeTabs.FOOD).setRegistryName("pork_dinner");
    public static final Item STUMP_REMOVER = new ItemStumpRemover().setRegistryName("stump_remover");
    public static final Item DIRT_PILE = new Item().setCreativeTab(BWCreativeTabs.BWTAB).setRegistryName("dirt_pile");
    public static final Item GRAVEL_PILE = new Item().setCreativeTab(BWCreativeTabs.BWTAB).setRegistryName("gravel_pile");
    public static final Item SAND_PILE = new Item().setCreativeTab(BWCreativeTabs.BWTAB).setRegistryName("sand_pile");
    public static final Item RED_SAND_PILE = new Item().setCreativeTab(BWCreativeTabs.BWTAB).setRegistryName("red_sand_pile");
    public static final Item WOLF_CHOP = new ItemFood(3, 0.3F, true).setCreativeTab(CreativeTabs.FOOD).setRegistryName("wolf_chop");
    public static final Item COOKED_WOLF_CHOP = new ItemFood(8, 0.3F, true).setCreativeTab(CreativeTabs.FOOD).setRegistryName("cooked_wolf_chop");
    public static final Item KIBBLE = new ItemFood(3, 0, true).setPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 0), 0.3F).setCreativeTab(CreativeTabs.FOOD).setRegistryName("kibble");
    public static final Item APPLE_PIE = new ItemFood(8, 0.3F, false).setRegistryName("apple_pie").setCreativeTab(CreativeTabs.FOOD);
    public static final Item MANUAL = new ItemBookManual().setRegistryName("manual").setCreativeTab(BWCreativeTabs.BWTAB);
    public static final Item MYSTERY_MEAT = new ItemFood(2, 0.3f, true).setRegistryName("mystery_meat");
    public static final Item COOKED_MYSTERY_MEAT = new ItemFood(6, 0.8F, true).setRegistryName("cooked_mystery_meat");
    public static final Item STEEL_HACKSAW = new ItemHacksaw().setRegistryName("steel_hacksaw");
    public static final Item BAT_WING = new ItemFood(2, 0.3F, false).setRegistryName("bat_wing").setCreativeTab(CreativeTabs.FOOD);
    public static final Item COOKED_BAT_WING = new ItemFood(4, 0.6F, false).setRegistryName("cooked_bat_wing").setCreativeTab(CreativeTabs.FOOD);
    public static final Item ARCANE_SCROLL = new ItemArcaneScroll().setRegistryName("arcane_scroll").setCreativeTab(BWCreativeTabs.BWTAB);
    public static final Item LEATHER_TANNED_HELMET = new ItemLeatherTannedArmor(EntityEquipmentSlot.HEAD).setRegistryName("leather_tanned_helmet");
    public static final Item LEATHER_TANNED_CHEST = new ItemLeatherTannedArmor(EntityEquipmentSlot.CHEST).setRegistryName("leather_tanned_chest");
    public static final Item LEATHER_TANNED_PANTS = new ItemLeatherTannedArmor(EntityEquipmentSlot.LEGS).setRegistryName("leather_tanned_pants");
    public static final Item LEATHER_TANNED_BOOTS = new ItemLeatherTannedArmor(EntityEquipmentSlot.FEET).setRegistryName("leather_tanned_boots");
    public static final Item WOOL_HELMET = new ItemWoolArmor(EntityEquipmentSlot.HEAD).setRegistryName("wool_helmet");
    public static final Item WOOL_CHEST = new ItemWoolArmor(EntityEquipmentSlot.CHEST).setRegistryName("wool_chest");
    public static final Item WOOL_PANTS = new ItemWoolArmor(EntityEquipmentSlot.LEGS).setRegistryName("wool_pants");
    public static final Item WOOL_BOOTS = new ItemWoolArmor(EntityEquipmentSlot.FEET).setRegistryName("wool_boots");


    private static final List<Item> ITEMS = new ArrayList<>();

    public static List<Item> getItems() {
        return Collections.unmodifiableList(ITEMS);
    }

    public static void registerItems() {
        registerItem(MATERIAL);
        registerItem(AXLE_GENERATOR);
        registerItem(BARK);
        registerItem(DONUT);
        registerItem(DYNAMITE);
        registerItem(DYNAMITE_BUNDLE);
        registerItem(FERTILIZER);
        registerItem(STEEL_AXE);
        registerItem(STEEL_HOE);
        registerItem(STEEL_PICKAXE);
        registerItem(STEEL_SHOVEL);
        registerItem(STEEL_SWORD);
        registerItem(STEEL_MATTOCK);
        registerItem(STEEL_BATTLEAXE);
        registerItem(STEEL_HACKSAW);
        registerItem(STEEL_HELMET);
        registerItem(STEEL_CHEST);
        registerItem(STEEL_PANTS);
        registerItem(STEEL_BOOTS);
        registerItem(CREEPER_OYSTER);
        registerItem(ENDER_SPECTACLES);
        registerItem(RAW_EGG);
        registerItem(COOKED_EGG);
        registerItem(RAW_SCRAMBLED_EGG);
        registerItem(COOKED_SCRAMBLED_EGG);
        registerItem(RAW_OMELET);
        registerItem(COOKED_OMELET);
        registerItem(HAM_AND_EGGS);
        registerItem(TASTY_SANDWICH);
        registerItem(COMPOSITE_BOW);
        registerItem(BROADHEAD_ARROW);
        registerItem(BEEF_DINNER);
        registerItem(BEEF_POTATOES);
        registerItem(CHICKEN_SOUP);
        registerItem(CHOCOLATE);
        registerItem(CHOWDER);
        registerItem(COOKED_KEBAB);
        registerItem(HEARTY_STEW);
        registerItem(RAW_KEBAB);
        registerItem(PORK_DINNER);
        registerItem(STUMP_REMOVER);
        registerItem(DIRT_PILE);
        registerItem(GRAVEL_PILE);
        registerItem(SAND_PILE);
        registerItem(RED_SAND_PILE);
        registerItem(WOLF_CHOP);
        registerItem(COOKED_WOLF_CHOP);
        registerItem(KIBBLE);
        registerItem(APPLE_PIE);
        registerItem(MANUAL);
        registerItem(MYSTERY_MEAT);
        registerItem(COOKED_MYSTERY_MEAT);
        registerItem(BAT_WING);
        registerItem(COOKED_BAT_WING);
        registerItem(ARCANE_SCROLL);
        registerItem(LEATHER_TANNED_BOOTS);
        registerItem(LEATHER_TANNED_CHEST);
        registerItem(LEATHER_TANNED_HELMET);
        registerItem(LEATHER_TANNED_PANTS);
        registerItem(WOOL_BOOTS);
        registerItem(WOOL_CHEST);
        registerItem(WOOL_HELMET);
        registerItem(WOOL_PANTS);
    }

    /**
     * Register an Item.
     *
     * @param item Item instance to register.
     * @return Registered item.
     */
    public static Item registerItem(Item item) {
        if (Objects.equals(item.getTranslationKey(), "item.null")) {
            //betterwithmods:name => bwm:name
            item.setTranslationKey("bwm" + item.getRegistryName().toString().substring(BWMod.MODID.length()));
        }
        ITEMS.add(item);
        return item;
    }

    @SideOnly(Side.CLIENT)
    private static void setModelLocation(Item item, int meta, String variantSettings) {
        setModelLocation(item, meta, item.getRegistryName().toString(), variantSettings);
    }

    @SideOnly(Side.CLIENT)
    private static void setModelLocation(Item item, int meta, String location, String variantSettings) {
        if (meta == OreDictionary.WILDCARD_VALUE) {
            ModelLoader.setCustomMeshDefinition(item,
                    stack -> new ModelResourceLocation(location, variantSettings));
        } else {
            ModelLoader.setCustomModelResourceLocation(item, meta,
                    new ModelResourceLocation(location, variantSettings));
        }
    }

    @SideOnly(Side.CLIENT)
    private static void setInventoryModel(ItemBlock item) {
        Block block = item.getBlock();
        if (block instanceof IMultiVariants) {
            ModelLoader.setCustomStateMapper(block, new BWStateMapper(block.getRegistryName().toString()));
            String[] variants = ((IMultiVariants) block).getVariants();
            for (int meta = 0; meta < variants.length; meta++) {
                if (!Objects.equals(variants[meta], "")) setModelLocation(item, meta, variants[meta]);
            }
        } else if (block instanceof IMultiLocations) {
            String[] locations = ((IMultiLocations) block).getLocations();
            for (int meta = 0; meta < locations.length; meta++) {
                String location = locations[meta];
                setModelLocation(item, meta, BWMod.MODID + ":" + location, "inventory");
            }
        } else {
            setModelLocation(item, OreDictionary.WILDCARD_VALUE, "inventory");
        }
    }



    @SideOnly(Side.CLIENT)
    public static void setInventoryModel(Item item) {
        if (item.isDamageable()) {
            setModelLocation(item, OreDictionary.WILDCARD_VALUE, "inventory");
        } else if (item instanceof IMultiLocations) {
            IMultiLocations bwmItem = (IMultiLocations) item;
            String[] locations = bwmItem.getLocations();
            for (int meta = 0; meta < locations.length; meta++) {
                String location = locations[meta];
                setModelLocation(item, meta, BWMod.MODID + ":" + location, "inventory");
            }
        } else if (item instanceof ItemBlock) {
            ItemBlock itemBlock = (ItemBlock) item;
            setInventoryModel(itemBlock);
        } else {
            setModelLocation(item, OreDictionary.WILDCARD_VALUE, "inventory");
        }
    }

    ///CLIENT END
}
