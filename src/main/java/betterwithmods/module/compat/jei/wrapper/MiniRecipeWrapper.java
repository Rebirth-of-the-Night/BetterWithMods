package betterwithmods.module.compat.jei.wrapper;

import betterwithmods.module.gameplay.miniblocks.MiniRecipe;
import com.google.common.collect.Lists;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.List;

public class MiniRecipeWrapper extends ShapelessRecipeWrapper<MiniRecipe> {
    protected final MiniRecipe recipe;
    private final IJeiHelpers jeiHelpers;

    public MiniRecipeWrapper(IJeiHelpers jeiHelpers, MiniRecipe recipe) {
        super(jeiHelpers, recipe);
        this.jeiHelpers = jeiHelpers;
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        IStackHelper h = jeiHelpers.getStackHelper();
        List<ItemStack> inputs = h.getSubtypes(recipe.getStart()), outputs = Lists.newArrayList();
        for (ItemStack input : inputs) {
            outputs.add(recipe.getOutput(input));
        }
        ingredients.setInputLists(ItemStack.class, h.expandRecipeItemStackInputs(inputs));
        ingredients.setOutputLists(ItemStack.class, h.expandRecipeItemStackInputs(outputs));
        super.getIngredients(ingredients);
    }
}
