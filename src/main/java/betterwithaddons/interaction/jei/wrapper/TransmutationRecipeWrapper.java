package betterwithaddons.interaction.jei.wrapper;

import betterwithaddons.crafting.recipes.infuser.TransmutationRecipe;
import betterwithaddons.interaction.InteractionEriottoMod;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import java.util.List;

public class TransmutationRecipeWrapper extends SmeltingRecipeWrapper {
    TransmutationRecipe recipe;

    public TransmutationRecipeWrapper(TransmutationRecipe recipe) {
        super(recipe);
        this.recipe = recipe;
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        int requiredSpirit = recipe.getRecipeRequiredSpirit();

        if(mouseX >= 78 && mouseY >= 30 && mouseX <= 96 && mouseY <= 44)
            return Lists.newArrayList(I18n.format("inv.infuser.cost.description",requiredSpirit));

        return super.getTooltipStrings(mouseX, mouseY);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        int foregroundcolor = InteractionEriottoMod.SPIRIT_GUI_COLOR_HIGH.getRGB();
        int backgroundcolor = InteractionEriottoMod.SPIRIT_GUI_COLOR_LOW.getRGB();

        int requiredSpirit = recipe.getRecipeRequiredSpirit();
        String costString = I18n.format("inv.infuser.cost.name",requiredSpirit);

        if(requiredSpirit > 0) {
            int drawoffsetX = 87 - minecraft.fontRenderer.getStringWidth(costString) / 2;
            int drawoffsetY = 33;

            minecraft.fontRenderer.drawString(costString, drawoffsetX, drawoffsetY+1, backgroundcolor);
            minecraft.fontRenderer.drawString(costString, drawoffsetX+1, drawoffsetY+1, backgroundcolor);
            minecraft.fontRenderer.drawString(costString, drawoffsetX+1, drawoffsetY+1, backgroundcolor);
            minecraft.fontRenderer.drawString(costString, drawoffsetX, drawoffsetY, foregroundcolor);
        }

        super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);
    }
}
