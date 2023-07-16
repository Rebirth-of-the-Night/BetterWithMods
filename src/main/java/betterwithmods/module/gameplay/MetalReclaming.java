package betterwithmods.module.gameplay;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWMItems;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.items.ItemMaterial;
import betterwithmods.module.Feature;
import betterwithmods.module.ModuleLoader;
import betterwithmods.module.tweaks.CheaperAxes;
import com.google.common.collect.Lists;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class MetalReclaming extends Feature {
    public static int reclaimCount;

    public static void addReclaimRecipe(ItemStack input, String oreSuffix, int ingotCount) {
        int totalNuggets = ingotCount * reclaimCount;
        int ingots = totalNuggets / 9;
        int nuggets = totalNuggets % 9;
        ItemStack ingotStack = ItemStack.EMPTY;
        ItemStack nuggetStack = ItemStack.EMPTY;
        if (ingots > 0 && !OreDictionary.getOres("ingot" + oreSuffix).isEmpty())
            ingotStack = OreDictionary.getOres("ingot" + oreSuffix).get(0);
        if (nuggets > 0 && !OreDictionary.getOres("nugget" + oreSuffix).isEmpty())
            nuggetStack = OreDictionary.getOres("nugget" + oreSuffix).get(0);
        List<ItemStack> outputs = Lists.newArrayList();
        if (ingotStack.isEmpty()) {
            if (!nuggetStack.isEmpty()) {
                outputs.add(new ItemStack(nuggetStack.getItem(), totalNuggets > nuggets ? totalNuggets : nuggets, nuggetStack.getMetadata()));
            }
        } else {
            outputs.add(new ItemStack(ingotStack.getItem(), ingots, ingotStack.getMetadata()));
            if (!nuggetStack.isEmpty())
                outputs.add(new ItemStack(nuggetStack.getItem(), nuggets, nuggetStack.getMetadata()));
        }
        BWRegistry.CRUCIBLE.addStokedRecipe(input, outputs);
    }

    @Override
    public String getFeatureDescription() {
        return "Adds recipes to the Crucible to melt metal items back into their component metals";
    }

    @Override
    public void setupConfig() {
        reclaimCount = loadPropInt("Reclaming Count", "Amount (in nuggets per ingot) tools and armor in the crucible reclaim. Does not affect diamond or soulforged steel ingot reclamation. (Set to 0 to disable reclamation entirely.)", "", 6, 0, 9);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        int axe_amt = ModuleLoader.isFeatureEnabled(CheaperAxes.class) ? 2 : 3;

        if (reclaimCount > 0) {
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(BWMItems.STEEL_HOE, 1, OreDictionary.WILDCARD_VALUE), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.INGOT_STEEL, 2));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(BWMItems.STEEL_SWORD, 1, OreDictionary.WILDCARD_VALUE), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.INGOT_STEEL, 3));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(BWMItems.STEEL_PICKAXE, 1, OreDictionary.WILDCARD_VALUE), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.INGOT_STEEL, 3));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(BWMItems.STEEL_AXE, 1, OreDictionary.WILDCARD_VALUE), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.INGOT_STEEL, axe_amt));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(BWMItems.STEEL_SHOVEL, 1, OreDictionary.WILDCARD_VALUE), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.INGOT_STEEL));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(BWMItems.STEEL_MATTOCK, 1, OreDictionary.WILDCARD_VALUE), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.INGOT_STEEL, 4));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(BWMItems.STEEL_BATTLEAXE, 1, OreDictionary.WILDCARD_VALUE), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.INGOT_STEEL, 5));
            addReclaimRecipe(new ItemStack(Items.IRON_CHESTPLATE, 1, OreDictionary.WILDCARD_VALUE), "Iron", 8);
            addReclaimRecipe(new ItemStack(Items.IRON_AXE, 1, OreDictionary.WILDCARD_VALUE), "Iron", axe_amt);
            addReclaimRecipe(new ItemStack(Items.IRON_BOOTS, 1, OreDictionary.WILDCARD_VALUE), "Iron", 4);
            addReclaimRecipe(new ItemStack(Items.IRON_HELMET, 1, OreDictionary.WILDCARD_VALUE), "Iron", 5);
            addReclaimRecipe(new ItemStack(Items.IRON_LEGGINGS, 1, OreDictionary.WILDCARD_VALUE), "Iron", 7);
            addReclaimRecipe(new ItemStack(Items.IRON_HOE, 1, OreDictionary.WILDCARD_VALUE), "Iron", 2);
            addReclaimRecipe(new ItemStack(Items.IRON_PICKAXE, 1, OreDictionary.WILDCARD_VALUE), "Iron", 3);
            addReclaimRecipe(new ItemStack(Items.IRON_SHOVEL, 1, OreDictionary.WILDCARD_VALUE), "Iron", 1);
            addReclaimRecipe(new ItemStack(Items.IRON_SWORD, 1, OreDictionary.WILDCARD_VALUE), "Iron", 2);
            addReclaimRecipe(new ItemStack(Items.GOLDEN_CHESTPLATE, 1, OreDictionary.WILDCARD_VALUE), "Gold", 8);
            addReclaimRecipe(new ItemStack(Items.GOLDEN_AXE, 1, OreDictionary.WILDCARD_VALUE), "Gold", axe_amt);
            addReclaimRecipe(new ItemStack(Items.GOLDEN_BOOTS, 1, OreDictionary.WILDCARD_VALUE), "Gold", 4);
            addReclaimRecipe(new ItemStack(Items.GOLDEN_HELMET, 1, OreDictionary.WILDCARD_VALUE), "Gold", 5);
            addReclaimRecipe(new ItemStack(Items.GOLDEN_LEGGINGS, 1, OreDictionary.WILDCARD_VALUE), "Gold", 7);
            addReclaimRecipe(new ItemStack(Items.GOLDEN_HOE, 1, OreDictionary.WILDCARD_VALUE), "Gold", 2);
            addReclaimRecipe(new ItemStack(Items.GOLDEN_PICKAXE, 1, OreDictionary.WILDCARD_VALUE), "Gold", 3);
            addReclaimRecipe(new ItemStack(Items.GOLDEN_SHOVEL, 1, OreDictionary.WILDCARD_VALUE), "Gold", 1);
            addReclaimRecipe(new ItemStack(Items.GOLDEN_SWORD, 1, OreDictionary.WILDCARD_VALUE), "Gold", 2);
            addReclaimRecipe(new ItemStack(Items.SHEARS, 1, OreDictionary.WILDCARD_VALUE), "Iron", 2);

            BWRegistry.CRUCIBLE.addStokedRecipe(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.ARMOR_PLATE), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.INGOT_STEEL));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(BWMItems.STEEL_HELMET, 1, OreDictionary.WILDCARD_VALUE), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.INGOT_STEEL, 10));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(BWMItems.STEEL_CHEST, 1, OreDictionary.WILDCARD_VALUE), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.INGOT_STEEL, 14));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(BWMItems.STEEL_PANTS, 1, OreDictionary.WILDCARD_VALUE), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.INGOT_STEEL, 12));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(BWMItems.STEEL_BOOTS, 1, OreDictionary.WILDCARD_VALUE), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.INGOT_STEEL, 8));

            BWRegistry.CRUCIBLE.addStokedRecipe(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.CHAIN_MAIL), new ItemStack(Items.IRON_NUGGET, 4));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.CHAINMAIL_HELMET, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.IRON_NUGGET, 20));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.CHAINMAIL_LEGGINGS, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.IRON_NUGGET, 28));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.CHAINMAIL_CHESTPLATE, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.IRON_NUGGET, 32));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.CHAINMAIL_BOOTS, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.IRON_NUGGET, 16));

            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.SHIELD, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.IRON_INGOT, 1));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.IRON_DOOR), new ItemStack(Items.IRON_INGOT, 2));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.IRON_HORSE_ARMOR, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.IRON_INGOT, 8));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.GOLDEN_HORSE_ARMOR, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.GOLD_INGOT, 8));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.MINECART), new ItemStack(Items.IRON_INGOT, 5));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.CHEST_MINECART), new ItemStack(Items.IRON_INGOT, 5));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.FURNACE_MINECART), new ItemStack(Items.IRON_INGOT, 5));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.HOPPER_MINECART), new ItemStack(Items.IRON_INGOT, 5));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.TNT_MINECART), new ItemStack(Items.IRON_INGOT, 5));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.CAULDRON), new ItemStack(Items.IRON_INGOT, 7));

            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Blocks.RAIL, 8), new ItemStack(Items.IRON_INGOT, 3));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Blocks.GOLDEN_RAIL, 6), new ItemStack(Items.GOLD_INGOT, 6));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Blocks.ACTIVATOR_RAIL, 6), new ItemStack(Items.IRON_INGOT, 6));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Blocks.DETECTOR_RAIL, 6), new ItemStack(Items.IRON_INGOT, 6));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(BWMBlocks.BOOSTER, 6), new ItemStack(Items.IRON_INGOT, 6));

            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Blocks.IRON_BARS, 8), new ItemStack(Items.IRON_INGOT, 3));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Blocks.ANVIL, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.IRON_INGOT, 31));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Blocks.HOPPER, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.IRON_INGOT, 5));

            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, 1), new ItemStack(Items.IRON_INGOT, 2));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, 1), new ItemStack(Items.GOLD_INGOT, 2));

            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(BWMBlocks.STEEL_ANVIL), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.INGOT_STEEL, 7));
        }
    }
}

