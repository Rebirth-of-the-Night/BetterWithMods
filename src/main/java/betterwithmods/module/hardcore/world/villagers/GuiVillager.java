package betterwithmods.module.hardcore.world.villagers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import org.lwjgl.opengl.GL11;

public class GuiVillager {

    public static void draw(GuiMerchant merchant) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        MerchantRecipeList list = merchant.getMerchant().getRecipes(Minecraft.getMinecraft().player);
        if (list != null) {
            MerchantRecipe recipe = list.get(merchant.selectedMerchantRecipe);
            if (recipe instanceof LevelingTrade) {
                LevelingTrade trade = (LevelingTrade) recipe;
                String s = "+";
                if (trade.shouldLevel()) {
                    s += "+";
                }
                int x = merchant.getGuiLeft() + merchant.getXSize() - getFont().getStringWidth(s) - 20, y = merchant.getGuiTop() + 57;
                getFont().drawStringWithShadow(s, x, y, 0x80FF20);
            }
        }
    }

    private static FontRenderer getFont() {
        return Minecraft.getMinecraft().fontRenderer;
    }
}
