package betterwithaddons.client.render;

import betterwithaddons.entity.EntityYa;
import betterwithaddons.lib.Reference;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderYa extends RenderArrow<EntityYa> {
    public static final IRenderFactory<EntityYa> YA_RENDER = renderManager1 -> new RenderYa(renderManager1);
    ResourceLocation res;

    public RenderYa(RenderManager renderManagerIn) {
        super(renderManagerIn);
        this.res = new ResourceLocation(Reference.MOD_ID,"textures/entity/ya.png");
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityYa entity) {
        return res;
    }

    @Override
    public void doRender(EntityYa t, double d, double d2, double d3, float f, float f2) {
        this.bindEntityTexture(t);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.translate((float)d, (float)d2, (float)d3);
        GlStateManager.rotate(t.prevRotationYaw + (t.rotationYaw - t.prevRotationYaw) * f2 - 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(t.prevRotationPitch + (t.rotationPitch - t.prevRotationPitch) * f2, 0.0f, 0.0f, 1.0f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexBuffer = tessellator.getBuffer();
        // boolean bl = false;
        // float f3 = 0.0f;
        // float f4 = 0.5f;
        // float f5 = 0.0f;
        // float f6 = 0.15625f;
        // float f7 = 0.0f;
        // float f8 = 0.15625f;
        // float f9 = 0.15625f;
        // float f10 = 0.3125f;
        // float f11 = 0.05625f;
        GlStateManager.enableRescaleNormal();
        float f12 = (float)t.arrowShake - f2;
        if (f12 > 0.0f) {
            float f13 = (- MathHelper.sin(f12 * 3.0f)) * f12;
            GlStateManager.rotate(f13, 0.0f, 0.0f, 1.0f);
        }
        GlStateManager.rotate(45.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(0.05625f, 0.05625f, 0.05625f);
        GlStateManager.translate(-4.0f, 0.0f, 0.0f);
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(t));
        }
        GlStateManager.glNormal3f(0.05625f, 0.0f, 0.0f);
        vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexBuffer.pos(-7.0, -2.0, -2.0).tex(0.0, 0.15625).endVertex();
        vertexBuffer.pos(-7.0, -2.0, 2.0).tex(0.15625, 0.15625).endVertex();
        vertexBuffer.pos(-7.0, 2.0, 2.0).tex(0.15625, 0.3125).endVertex();
        vertexBuffer.pos(-7.0, 2.0, -2.0).tex(0.0, 0.3125).endVertex();
        tessellator.draw();
        GlStateManager.glNormal3f(-0.05625f, 0.0f, 0.0f);
        vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexBuffer.pos(-7.0, 2.0, -2.0).tex(0.0, 0.15625).endVertex();
        vertexBuffer.pos(-7.0, 2.0, 2.0).tex(0.15625, 0.15625).endVertex();
        vertexBuffer.pos(-7.0, -2.0, 2.0).tex(0.15625, 0.3125).endVertex();
        vertexBuffer.pos(-7.0, -2.0, -2.0).tex(0.0, 0.3125).endVertex();
        tessellator.draw();
        for (int i = 0; i < 4; ++i) {
            GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.glNormal3f(0.0f, 0.0f, 0.05625f);
            vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            vertexBuffer.pos(-8.0, -2.0, 0.0).tex(0.0, 0.0).endVertex();
            vertexBuffer.pos(8.0, -2.0, 0.0).tex(0.5, 0.0).endVertex();
            vertexBuffer.pos(8.0, 2.0, 0.0).tex(0.5, 0.15625).endVertex();
            vertexBuffer.pos(-8.0, 2.0, 0.0).tex(0.0, 0.15625).endVertex();
            tessellator.draw();
        }
        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.disableRescaleNormal();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}