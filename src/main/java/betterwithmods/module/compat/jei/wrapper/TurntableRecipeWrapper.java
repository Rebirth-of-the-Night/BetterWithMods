package betterwithmods.module.compat.jei.wrapper;

import betterwithmods.api.recipe.IOutput;
import betterwithmods.common.registry.block.recipe.TurntableRecipe;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;

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
        ingredients.setInput(ItemStack.class, helpers.getStackHelper().toItemStackList(recipe.getInput()));
        ingredients.setOutput(ItemStack.class, recipe.getRepresentative());
        ingredients.setOutputLists(IOutput.class, recipe.getRecipeOutput().getExpandedOutputs(2));
    }
}
