package betterwithmods.module.compat.jei.wrapper;

import betterwithmods.common.registry.bulk.recipes.BulkRecipe;
import betterwithmods.module.compat.jei.IngredientTypes;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;

import javax.annotation.Nonnull;

public class BulkRecipeWrapper<T extends BulkRecipe> implements IRecipeWrapper {

    protected final T recipe;
    private final IJeiHelpers helpers;
    private int boxes;

    public BulkRecipeWrapper(IJeiHelpers helpers, @Nonnull T recipe, int boxes) {
        this.recipe = recipe;
        this.helpers = helpers;
        this.boxes = boxes;
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, helpers.getStackHelper().expandRecipeItemStackInputs(recipe.getInputs()));
        ingredients.setOutputLists(IngredientTypes.OUTPUT_GENERIC, recipe.getRecipeOutput().getExpandedOutputs(boxes));
    }

    @Nonnull
    public T getRecipe() {
        return recipe;
    }


}
