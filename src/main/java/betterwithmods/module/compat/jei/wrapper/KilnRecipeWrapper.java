package betterwithmods.module.compat.jei.wrapper;

import betterwithmods.common.registry.block.recipe.KilnRecipe;
import mezz.jei.api.IJeiHelpers;

public class KilnRecipeWrapper extends BlockRecipeWrapper<KilnRecipe> {

    public KilnRecipeWrapper(IJeiHelpers helpers, KilnRecipe recipe) {
        super(helpers, recipe, 3);
    }
}
