package betterwithmods.module.compat.jei.wrapper;

import betterwithmods.common.registry.block.recipe.TurntableRecipe;
import betterwithmods.module.compat.jei.IngredientTypes;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;

import java.util.Collections;

import javax.annotation.Nonnull;

/**
 * Purpose:
 *
 * @author primetoxinz
 * @version 11/11/16
 */
public class TurntableRecipeWrapper extends BlockRecipeWrapper<TurntableRecipe> {
    public TurntableRecipeWrapper(IJeiHelpers helpers, TurntableRecipe recipe) {
        super(helpers, recipe, 3);
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(helpers.getStackHelper().toItemStackList(recipe.getInput())));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRepresentative());
        ingredients.setOutputLists(IngredientTypes.OUTPUT_GENERIC, recipe.getRecipeOutput().getExpandedOutputs(2));
    }
}
