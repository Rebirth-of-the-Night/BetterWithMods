package betterwithmods.module.compat.jei.wrapper;

import betterwithmods.common.registry.anvil.ShapedAnvilRecipe;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;

public class ShapedAnvilRecipeWrapper extends ShapelessRecipeWrapper<ShapedAnvilRecipe> implements IShapedCraftingRecipeWrapper {
    public ShapedAnvilRecipeWrapper(IJeiHelpers helpers, ShapedAnvilRecipe recipe) {
        super(helpers, recipe);
    }

    @Override
    public int getWidth() {
        return recipe.getRecipeWidth();
    }

    @Override
    public int getHeight() {
        return recipe.getRecipeHeight();
    }
}
