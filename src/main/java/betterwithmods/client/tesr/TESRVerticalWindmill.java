package betterwithmods.client.tesr;

import betterwithmods.BWMod;
import betterwithmods.client.model.ModelVerticalFrame;
import betterwithmods.client.model.ModelVerticalSails;
import betterwithmods.client.model.ModelVerticalShafts;
import betterwithmods.client.model.render.RenderUtils;
import betterwithmods.common.blocks.mechanical.tile.TileEntityWindmillVertical;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class TESRVerticalWindmill extends TileEntitySpecialRenderer<TileEntityWindmillVertical> {
    private final ModelVerticalShafts modelShafts = new ModelVerticalShafts();
    private final ModelVerticalSails modelSails = new ModelVerticalSails();
    private final ModelVerticalFrame modelFrame = new ModelVerticalFrame();


    public static final ResourceLocation WINDMILL_SHAFTS = new ResourceLocation(BWMod.MODID,"textures/blocks/vertical_windmill_shaft.png");
    public static final ResourceLocation WINDMILL = new ResourceLocation(BWMod.MODID,"textures/blocks/vertical_windmill.png");
    public static final ResourceLocation WINDMILL_SAIL = new ResourceLocation(BWMod.MODID,"textures/blocks/vertical_windmill_sail.png");

    @Override
    public void render(TileEntityWindmillVertical te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {


        float rotation = (te.getCurrentRotation() + (te.getMechanicalOutput(EnumFacing.UP) == 0 ? 0 : partialTicks * te.getPrevRotation()));
        rotation = -rotation;

        BlockPos pos = te.getBlockPos();
        RenderUtils.renderDebugBoundingBox(x,y,z,te.getRenderBoundingBox().offset(-pos.getX(),-pos.getY(),-pos.getZ()));

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        modelShafts.setRotateAngle(modelShafts.axle, 0, (float) Math.toRadians(rotation), 0);
        modelSails.setRotateAngleForSails(0, (float) Math.toRadians(rotation), 0);
        modelFrame.setRotateAngle(modelFrame.axle, 0, (float) Math.toRadians(rotation), 0);
        this.bindTexture(WINDMILL_SHAFTS);
        this.modelShafts.render(0.0625F);
        this.bindTexture(WINDMILL);
        this.modelFrame.render(0.0625F);
        this.bindTexture(WINDMILL_SAIL);
        this.modelSails.render(0.0625F, te);
        GlStateManager.popMatrix();



    }

}
