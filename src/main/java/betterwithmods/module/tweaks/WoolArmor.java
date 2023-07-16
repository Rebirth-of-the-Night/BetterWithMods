package betterwithmods.module.tweaks;

import betterwithmods.common.BWMItems;
import betterwithmods.common.BWMRecipes;
import betterwithmods.common.registry.crafting.RecipeArmorDye;
import betterwithmods.module.Feature;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class WoolArmor extends Feature {


    @Override
    public void preInit(FMLPreInitializationEvent event) {
        BWMRecipes.addRecipe(new RecipeArmorDye(Ingredient.fromItems(BWMItems.WOOL_HELMET, BWMItems.WOOL_CHEST, BWMItems.WOOL_PANTS, BWMItems.WOOL_BOOTS)));
    }

    @Override
    public String getFeatureDescription() {
        return "Add Wool Armor";
    }
}
