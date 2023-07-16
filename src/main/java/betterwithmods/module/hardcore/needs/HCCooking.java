package betterwithmods.module.hardcore.needs;

import betterwithmods.common.BWMRecipes;
import betterwithmods.module.Feature;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class HCCooking extends Feature{

    @Override
    public String getFeatureDescription() {
        return "Changes the recipes for baked goods to require the Kiln and changes soups to require the Cauldron.";
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        BWMRecipes.removeRecipe(new ItemStack(Items.MUSHROOM_STEW));
        BWMRecipes.removeRecipe(new ItemStack(Items.CAKE));
        BWMRecipes.removeRecipe(new ItemStack(Items.COOKIE));
        BWMRecipes.removeRecipe(new ItemStack(Items.PUMPKIN_PIE));
        BWMRecipes.removeRecipe(new ItemStack(Items.RABBIT_STEW));
        BWMRecipes.removeRecipe(new ItemStack(Items.BEETROOT_SOUP));
        BWMRecipes.removeRecipe(new ItemStack(Items.BREAD));
    }



}
