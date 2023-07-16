package betterwithmods.module.tweaks;

import betterwithmods.common.BWMRecipes;
import betterwithmods.module.Feature;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CheaperAxes extends Feature {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        BWMRecipes.removeRecipe(new ResourceLocation("minecraft:stone_axe"));
        BWMRecipes.removeRecipe(new ResourceLocation("minecraft:iron_axe"));
        BWMRecipes.removeRecipe(new ResourceLocation("minecraft:golden_axe"));
        BWMRecipes.removeRecipe(new ResourceLocation("minecraft:diamond_axe"));
    }

    @Override
    public String getFeatureDescription() {
        return "Change vanilla axe recipes to only require 2 material rather than 3.";
    }
}
