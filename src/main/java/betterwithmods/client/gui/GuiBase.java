package betterwithmods.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public abstract class GuiBase extends GuiContainer {
    protected ResourceLocation background;

    public GuiBase(Container inventorySlotsIn, ResourceLocation background) {
        super(inventorySlotsIn);
        this.background = background;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(background);

        int centerX = (this.width - this.xSize) / 2;
        int centerY = (this.height - this.ySize) / 2;
        drawTexturedModalRect(centerX, centerY, 0, 0, this.xSize, this.ySize);
        drawExtras(partialTicks,mouseX,mouseY,centerX,centerY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        String s = I18n.format(getTitle());
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, getTitleY(), getTitleColor());
    }

    public abstract String getTitle();
    public abstract int getTitleY();
    public int getTitleColor() {
        return 4210752;
    }


    protected void drawExtras(float partialTicks, int mouseX, int mouseY, int centerX, int centerY) {}
}
