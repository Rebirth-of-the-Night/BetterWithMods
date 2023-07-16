package betterwithmods.client.tesr;

import betterwithmods.BWMod;
import betterwithmods.client.model.ModelWindmillSail;
import betterwithmods.client.model.ModelWindmillShafts;
import betterwithmods.client.model.render.RenderUtils;
import betterwithmods.common.blocks.mechanical.tile.TileEntityWindmillHorizontal;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class TESRWindmill extends TileEntitySpecialRenderer<TileEntityWindmillHorizontal> {
    public static final ResourceLocation WINDMILL = new ResourceLocation(BWMod.MODID, "textures/blocks/horizontal_windmill.png");
    public static final ResourceLocation WINDMILL_SAIL = new ResourceLocation(BWMod.MODID, "textures/blocks/horizontal_windmill_sail.png");
    private final ModelWindmillShafts shafts = new ModelWindmillShafts();
    private final ModelWindmillSail sail = new ModelWindmillSail();

    @Override
    public void render(TileEntityWindmillHorizontal te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        BlockPos pos = te.getBlockPos();
        RenderUtils.renderDebugBoundingBox(x, y, z, te.getRenderBoundingBox().offset(-pos.getX(), -pos.getY(), -pos.getZ()));

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        EnumFacing dir = te.getOrientation();
        float rotation = (te.getCurrentRotation() + (te.getMechanicalOutput(dir) == 0 ? 0 : partialTicks * te.getPrevRotation()));
        if (dir == EnumFacing.EAST) {
            shafts.setRotateAngle(shafts.axle, 0, 0, -(float) Math.toRadians(rotation));
            sail.setRotateAngleForSails(0, 0, -(float) Math.toRadians(rotation));
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        } else if (dir == EnumFacing.SOUTH) {
            shafts.setRotateAngle(shafts.axle, 0, 0, -(float) Math.toRadians(rotation));
            sail.setRotateAngleForSails(0, 0, -(float) Math.toRadians(rotation));
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        } else {
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        }

        this.bindTexture(WINDMILL);
        this.shafts.render(0.0625F);
        this.bindTexture(WINDMILL_SAIL);
        this.sail.render(0.0625F, te);
        GlStateManager.popMatrix();
    }


}
