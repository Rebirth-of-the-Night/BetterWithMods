package betterwithmods.client.tesr;

import betterwithmods.BWMod;
import betterwithmods.client.model.ModelWaterwheel;
import betterwithmods.client.model.render.RenderUtils;
import betterwithmods.common.blocks.mechanical.tile.TileEntityWaterwheel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class TESRWaterwheel extends TileEntitySpecialRenderer<TileEntityWaterwheel> {

    public static final ResourceLocation WATERWHEEL = new ResourceLocation(BWMod.MODID,"textures/blocks/waterwheel.png");

    private final ModelWaterwheel waterwheel;

    public TESRWaterwheel() {
        this.waterwheel = new ModelWaterwheel();
    }

    @Override
    public void render(TileEntityWaterwheel te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        BlockPos pos = te.getBlockPos();
        RenderUtils.renderDebugBoundingBox(x,y,z,te.getRenderBoundingBox().offset(-pos.getX(),-pos.getY(),-pos.getZ()));

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        this.bindTexture(WATERWHEEL);
        EnumFacing dir = te.getOrientation();
        float rotation = (te.getCurrentRotation() + (te.getMaximumInput(dir) == 0 ? 0 : partialTicks * te.getPrevRotation()));

        if (dir == EnumFacing.EAST) {
            waterwheel.setRotateAngle(waterwheel.axle, 0, 0, (float) Math.toRadians(rotation));
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        } else if (dir == EnumFacing.SOUTH) {
            waterwheel.setRotateAngle(waterwheel.axle, 0, 0, -(float) Math.toRadians(rotation));
        } else {
            waterwheel.setRotateAngle(waterwheel.axle, 0, (float) Math.toRadians(rotation), 0);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        }

        this.waterwheel.render(0.0625F);
        GlStateManager.popMatrix();
    }

}
