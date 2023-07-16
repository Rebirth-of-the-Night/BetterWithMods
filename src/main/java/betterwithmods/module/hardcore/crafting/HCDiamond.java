package betterwithmods.module.hardcore.crafting;

import betterwithmods.common.BWMRecipes;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.items.ItemMaterial;
import betterwithmods.module.Feature;
import betterwithmods.module.ModuleLoader;
import betterwithmods.module.gameplay.MetalReclaming;
import betterwithmods.module.tweaks.CheaperAxes;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class HCDiamond extends Feature {
     @Override
    public String getFeatureDescription() {
        return "Makes it so diamonds have to be made into an ingot alloy to be used in certain recipes";
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        BWMRecipes.removeRecipe(new ResourceLocation("minecraft:diamond_axe"));
        BWMRecipes.removeRecipe(new ResourceLocation("minecraft:diamond_hoe"));
        BWMRecipes.removeRecipe(new ResourceLocation("minecraft:diamond_pickaxe"));
        BWMRecipes.removeRecipe(new ResourceLocation("minecraft:diamond_sword"));
        BWMRecipes.removeRecipe(new ResourceLocation("minecraft:diamond_shovel"));
        BWMRecipes.removeRecipe(new ResourceLocation("minecraft:diamond_helmet"));
        BWMRecipes.removeRecipe(new ResourceLocation("minecraft:diamond_chestplate"));
        BWMRecipes.removeRecipe(new ResourceLocation("minecraft:diamond_leggings"));
        BWMRecipes.removeRecipe(new ResourceLocation("minecraft:diamond_boots"));
    }

    @Override
    public void init(FMLInitializationEvent event) {
        if (ModuleLoader.isFeatureEnabled(MetalReclaming.class) && MetalReclaming.reclaimCount > 0) {
            if (ModuleLoader.isFeatureEnabled(CheaperAxes.class)) {
                BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.DIAMOND_AXE, 1, OreDictionary.WILDCARD_VALUE),ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.DIAMOND_INGOT, 2));
            } else {
                BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.DIAMOND_AXE, 1, OreDictionary.WILDCARD_VALUE),ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.DIAMOND_INGOT, 3));
            }
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.DIAMOND_HOE, 1, OreDictionary.WILDCARD_VALUE),ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.DIAMOND_INGOT, 2));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.DIAMOND_PICKAXE, 1, OreDictionary.WILDCARD_VALUE),ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.DIAMOND_INGOT, 3));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.DIAMOND_SHOVEL, 1, OreDictionary.WILDCARD_VALUE),ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.DIAMOND_INGOT, 1));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.DIAMOND_SWORD, 1, OreDictionary.WILDCARD_VALUE),ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.DIAMOND_INGOT, 2));

            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.DIAMOND_HELMET, 1, OreDictionary.WILDCARD_VALUE),ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.DIAMOND_INGOT, 5));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.DIAMOND_CHESTPLATE, 1, OreDictionary.WILDCARD_VALUE),ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.DIAMOND_INGOT, 8));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.DIAMOND_LEGGINGS, 1, OreDictionary.WILDCARD_VALUE),ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.DIAMOND_INGOT, 7));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.DIAMOND_BOOTS, 1, OreDictionary.WILDCARD_VALUE),ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.DIAMOND_INGOT, 4));

        }
    }


    @Override
    public boolean requiresMinecraftRestartToEnable() {
        return true;
    }
}
