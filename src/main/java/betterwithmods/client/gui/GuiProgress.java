package betterwithmods.client.gui;

import betterwithmods.client.container.ContainerProgress;
import net.minecraft.util.ResourceLocation;

public abstract class GuiProgress extends GuiBase {

    private ContainerProgress container;

    public GuiProgress(ContainerProgress container, ResourceLocation background) {
        super(container, background);
        this.container = container;
    }

    @Override
    protected void drawExtras(float partialTicks, int mouseX, int mouseY, int centerX, int centerY) {
        if (container.showProgress()) {
            int progress = toPixels();
            drawTexturedModalRect(
                    centerX + getX(),
                    centerY + getY() + getHeight() - progress,
                    getTextureX(),
                    getTextureY() - progress,
                    getWidth(),
                    getHeight());
        }
    }


    protected double getPercentage() {
        return (double) container.getProgress() / (double) container.getMax();
    }

    protected abstract int toPixels();

    public abstract int getX();

    public abstract int getY();

    public abstract int getTextureX();

    public abstract int getTextureY();

    public abstract int getHeight();

    public abstract int getWidth();



}
