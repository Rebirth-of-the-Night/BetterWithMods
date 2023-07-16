package betterwithmods.client.gui;


import betterwithmods.common.penalties.BasicPenalty;
import betterwithmods.module.hardcore.needs.hunger.HCHunger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.FoodStats;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

/**
 * GUI for hunger levels and player penalties.
 *
 * @author Koward
 */
public class GuiHunger {

    public static GuiHunger INSTANCE = new GuiHunger();
    private final Random rand = new Random();
    private final Minecraft mc = Minecraft.getMinecraft();
    private int shakeCounter = 0;
    private boolean shakeTriggered;

    @SideOnly(Side.CLIENT)
    public void draw() {
        ScaledResolution scale = ((GuiIngameForge) mc.ingameGUI).getResolution();

        int left = scale.getScaledWidth() / 2 + 91;
        int top = scale.getScaledHeight() - GuiIngameForge.right_height;

        drawFoodOverlay(left, top);
    }

    @SuppressWarnings("Duplicates")
    @SideOnly(Side.CLIENT)
    private void drawFoodOverlay(int left, int top) {
        EntityPlayer player = this.mc.player;
        FoodStats foodStats = player.getFoodStats();
        int food = foodStats.getFoodLevel();
        int fat = (int) foodStats.getSaturationLevel();

        int roll = fat / 6;
        int shank = food / 6;

        if (shakeTriggered) {
            this.shakeCounter = 20;
            shakeTriggered = false;
        } else if (shakeCounter > 0) {
            --this.shakeCounter;
        }

        GlStateManager.enableBlend();
        for (int i = 0; i < 10; ++i) {
            int y = top;
            int icon = 16;
            byte background = 0;
            if (player.isPotionActive(MobEffects.HUNGER)) {
                icon += 36;
                background = 13;
            } else if (i < roll) {
                background = 1;
            }

            BasicPenalty<Integer> p = HCHunger.hungerPenalties.getPenalty(player);
            if (p != null && p.getSeverity() > 0 && mc.ingameGUI.getUpdateCounter() % (food * 5 + 1) == 0) {
                y = top + (rand.nextInt(3) - 1);
            } else if (shakeCounter > 0) {
                y = top + (rand.nextInt(3) - 1);
            }

            int x = left - i * 8 - 9;

            mc.ingameGUI.drawTexturedModalRect(x, y, 16 + background * 9, 27, 9, 9);

            int remainder;
            if (i == roll && !player.isPotionActive(MobEffects.HUNGER)) {
                remainder = fat % 6;
                if (remainder != 0) {
                    mc.ingameGUI.drawTexturedModalRect(x + 8 - remainder, y, 33 - remainder,
                            27, 1 + remainder, 9);
                }
            }

            if (i < shank) {
                mc.ingameGUI.drawTexturedModalRect(x, y, icon + 36, 27, 9, 9);
            } else if (i == shank) {
                remainder = food % 6;
                if (remainder != 0) {
                    mc.ingameGUI.drawTexturedModalRect(x + 7 - remainder, y, icon + 36 + 7
                            - remainder, 27, 3 + remainder, 9);
                }
            }
        }

        GlStateManager.disableBlend();
        GuiIngameForge.right_height += 10;
    }

    public void shake() {
        this.shakeTriggered = true;
    }
}
