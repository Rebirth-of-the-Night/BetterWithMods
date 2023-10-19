package betterwithaddons.interaction.jei.wrapper;

import betterwithaddons.crafting.recipes.CherryBoxRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.List;

public class CherryBoxRecipeWrapper implements IRecipeWrapper {
    CherryBoxRecipe recipe;

    public CherryBoxRecipeWrapper(CherryBoxRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM,getInputs());
        ingredients.setOutputs(VanillaTypes.ITEM,getOutputs());
    }

    public List<ItemStack> getInputs() {
        return recipe.getRecipeInputs();
    }

    public List<ItemStack> getOutputs() { return recipe.getRecipeOutputs(); }
}
