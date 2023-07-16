package betterwithmods.module.compat.jei.ingredient;

import betterwithmods.api.recipe.IOutput;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.plugins.vanilla.ingredients.item.ItemStackRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;

import javax.annotation.Nullable;
import java.util.List;

public class OutputRenderer<V extends IOutput> implements IIngredientRenderer<V> {

    private ItemStackRenderer itemStackRenderer;

    public OutputRenderer() {
        itemStackRenderer = new ItemStackRenderer();
    }

    @Override
    public void render(Minecraft minecraft, int xPosition, int yPosition, @Nullable V ingredient) {
        if (ingredient != null) {
            itemStackRenderer.render(minecraft, xPosition, yPosition, ingredient.getOutput());
        }
    }

    @Override
    public List<String> getTooltip(Minecraft minecraft, V ingredient, ITooltipFlag tooltipFlag) {
        List<String> tooltip = itemStackRenderer.getTooltip(minecraft, ingredient.getOutput(), tooltipFlag);
        tooltip.add(ingredient.getTooltip());
        return tooltip;
    }

    @Override
    public FontRenderer getFontRenderer(Minecraft minecraft, V ingredient) {
        return itemStackRenderer.getFontRenderer(minecraft, ingredient.getOutput());
    }
}
