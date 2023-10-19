package betterwithaddons.interaction.jei.wrapper;

import betterwithaddons.crafting.recipes.SpindleRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by primetoxinz on 6/29/17.
 */
public class SpindleRecipeWrapper implements IRecipeWrapper {
    private SpindleRecipe recipe;

    public SpindleRecipeWrapper(SpindleRecipe recipe) {
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

    public List<ItemStack> getOutputs() {
        return recipe.getOutput();
    }
}
