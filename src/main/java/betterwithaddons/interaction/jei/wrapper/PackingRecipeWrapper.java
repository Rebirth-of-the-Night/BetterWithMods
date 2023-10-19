package betterwithaddons.interaction.jei.wrapper;

import betterwithaddons.crafting.recipes.PackingRecipe;
import betterwithaddons.interaction.jei.BWAJEIPlugin;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.List;

public class PackingRecipeWrapper implements IRecipeWrapper {
    PackingRecipe recipe;

    public PackingRecipeWrapper(PackingRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<List<ItemStack>> inputStacks = BWAJEIPlugin.expand(recipe.inputs);
        ingredients.setInputLists(VanillaTypes.ITEM,inputStacks);
        ingredients.setOutputs(VanillaTypes.ITEM,recipe.output.getJEIItems());
    }

    public PackingRecipe getRecipe() {
        return recipe;
    }

    public List<Ingredient> getInputs() {
        return recipe.inputs;
    }
}
