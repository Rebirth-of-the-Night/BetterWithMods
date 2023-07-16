package betterwithmods.client.gui.bulk;

import betterwithmods.BWMod;
import betterwithmods.client.container.bulk.ContainerMill;
import betterwithmods.client.gui.GuiProgress;
import betterwithmods.common.blocks.mechanical.tile.TileEntityMill;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;

public class GuiMill extends GuiProgress {

    private static final ResourceLocation TEXTURE = new ResourceLocation(BWMod.MODID, "textures/gui/mill.png");
    private static final String NAME = "inv.mill.name";
    private ContainerMill container;

    public GuiMill(EntityPlayer player, TileEntityMill mill) {
        super(new ContainerMill(player, mill), TEXTURE);
        this.container = (ContainerMill) inventorySlots;
        this.ySize = 158;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    public String getTitle() {
        return NAME;
    }

    @Override
    public int getTitleY() {
        return 6;
    }

    @Override
    protected void drawExtras(float partialTicks, int mouseX, int mouseY, int centerX, int centerY) {
        super.drawExtras(partialTicks, mouseX, mouseY, centerX, centerY);
        if (container.blocked) {
            String str = I18n.format("bwm.millstone_blocked.message");
            int width = fontRenderer.getStringWidth(str) / 2;
            drawString(fontRenderer, str, centerX + this.xSize / 2 - width, centerY + 32, EnumDyeColor.RED.getColorValue());
            drawToolTip(mouseX, mouseY, centerX + this.xSize / 2 - width, centerY + 32, 32, 32, I18n.format("bwm.millstone_blocked.tooltip"));
        }
    }

    private void drawToolTip(int mouseX, int mouseY, int x, int y, int w, int h, String text) {
        if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= (y + h)) {
            drawHoveringText(text, mouseX, mouseY);
        }
    }


    @Override
    public int getX() {
        return 80;
    }

    @Override
    public int getY() {
        return 18;
    }

    @Override
    public int getTextureX() {
        return 176;
    }

    @Override
    public int getTextureY() {
        return 14;
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public int getWidth() {
        return 14;
    }

    @Override
    protected int toPixels() {
        return (int) (getHeight() * getPercentage());
    }
}
