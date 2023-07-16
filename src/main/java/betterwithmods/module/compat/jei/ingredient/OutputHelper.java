package betterwithmods.module.compat.jei.ingredient;

import betterwithmods.api.recipe.IOutput;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.color.ColorGetter;
import mezz.jei.startup.StackHelper;
import mezz.jei.util.ErrorUtil;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public class OutputHelper<V extends IOutput> implements IIngredientHelper<V> {

    private final StackHelper stackHelper;

    public OutputHelper(StackHelper stackHelper) {
        this.stackHelper = stackHelper;
    }

    @Override
    public List<V> expandSubtypes(List<V> contained) {
        return contained;
    }

    @Nullable
    @Override
    public V getMatch(Iterable<V> ingredients, V ingredientToMatch) {
        for (V r : ingredients) {
            if (r.equals(ingredientToMatch)) {
                return r;
            }
        }
        return null;
    }

    @Override
    public String getDisplayName(V ingredient) {
        return ErrorUtil.checkNotNull(ingredient.getOutput().getDisplayName(), "itemStack.getDisplayName()");
    }

    @Override
    public String getUniqueId(V ingredient) {
        ErrorUtil.checkNotEmpty(ingredient.getOutput());
        return stackHelper.getUniqueIdentifierForStack(ingredient.getOutput());
    }

    @Override
    public String getWildcardId(V ingredient) {
        ErrorUtil.checkNotEmpty(ingredient.getOutput());
        return stackHelper.getUniqueIdentifierForStack(ingredient.getOutput(), StackHelper.UidMode.WILDCARD);
    }

    @Override
    public String getModId(V ingredient) {
        ErrorUtil.checkNotEmpty(ingredient.getOutput());

        Item item = ingredient.getOutput().getItem();
        ResourceLocation itemName = item.getRegistryName();
        if (itemName == null) {
            String stackInfo = getErrorInfo(ingredient);
            throw new IllegalStateException("item.getRegistryName() returned null for: " + stackInfo);
        }

        return itemName.getNamespace();
    }

    @Override
    public Iterable<Color> getColors(V ingredient) {
        return ColorGetter.getColors(ingredient.getOutput(), 2);
    }

    @Override
    public String getResourceId(V ingredient) {
        ErrorUtil.checkNotEmpty(ingredient.getOutput());

        Item item = ingredient.getOutput().getItem();
        ResourceLocation itemName = item.getRegistryName();
        if (itemName == null) {
            String stackInfo = getErrorInfo(ingredient);
            throw new IllegalStateException("item.getRegistryName() returned null for: " + stackInfo);
        }

        return itemName.getPath();

    }

    @Override
    public V copyIngredient(V ingredient) {
        return (V) ingredient.copy();
    }

    @Override
    public String getErrorInfo(V ingredient) {
        return ErrorUtil.getItemStackInfo(ingredient.getOutput());
    }
}
