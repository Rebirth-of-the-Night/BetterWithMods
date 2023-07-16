package betterwithmods.client.tesr;

import betterwithmods.client.model.ModelTurntableKnob;
import betterwithmods.common.blocks.mechanical.tile.TileEntityTurntable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

public class TESRTurntable extends TileEntitySpecialRenderer<TileEntityTurntable> {
    private ModelTurntableKnob knob;
    private int position = 0;

    @Override
    public void render(TileEntityTurntable te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (position != te.getTimerPos()) {
            position = te.getTimerPos();
            knob = new ModelTurntableKnob(position);
        } else if (knob == null)
            knob = new ModelTurntableKnob(position);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        this.bindTexture(new ResourceLocation("minecraft", "textures/blocks/planks_oak.png"));
        knob.render(0.0625F);
        GlStateManager.popMatrix();
    }

}
