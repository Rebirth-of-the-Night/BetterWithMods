package betterwithaddons.interaction.jei.wrapper;

import betterwithaddons.crafting.recipes.SmeltingRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.List;

public class SmeltingRecipeWrapper implements IRecipeWrapper {
    SmeltingRecipe recipe;

    public SmeltingRecipeWrapper(SmeltingRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM,getInputs());
        ingredients.setOutputs(VanillaTypes.ITEM,getOutputs());
    }

    public SmeltingRecipe getRecipe() {
        return recipe;
    }

    public List<ItemStack> getInputs() {
        return recipe.getRecipeInputs();
    }

    public List<ItemStack> getOutputs() { return recipe.getRecipeOutputs(); }
}
