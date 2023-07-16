package betterwithmods.client.render;

import betterwithmods.module.hardcore.creatures.EntityTentacle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTentacle extends Render<EntityTentacle> {
    private static final ResourceLocation FISH_PARTICLES = new ResourceLocation("textures/particle/particles.png");

    public RenderTentacle(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityTentacle entity, double x, double y, double z, float entityYaw, float partialTicks) {
        EntityLivingBase angler = entity.getAngler();

        if (angler != null && !this.renderOutlines) {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) x + 0.5, (float) y + 0.5, (float) z + 0.5);
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            this.bindEntityTexture(entity);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();

            GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

            if (this.renderOutlines) {
                GlStateManager.enableColorMaterial();
                GlStateManager.enableOutlineMode(this.getTeamColor(entity));
            }

            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
            bufferbuilder.pos(-0.5D, -0.5D, 0.0D).tex(0.0625D, 0.1875D).normal(0.0F, 1.0F, 0.0F).endVertex();
            bufferbuilder.pos(0.5D, -0.5D, 0.0D).tex(0.125D, 0.1875D).normal(0.0F, 1.0F, 0.0F).endVertex();
            bufferbuilder.pos(0.5D, 0.5D, 0.0D).tex(0.125D, 0.125D).normal(0.0F, 1.0F, 0.0F).endVertex();
            bufferbuilder.pos(-0.5D, 0.5D, 0.0D).tex(0.0625D, 0.125D).normal(0.0F, 1.0F, 0.0F).endVertex();
            tessellator.draw();

            if (this.renderOutlines) {
                GlStateManager.disableOutlineMode();
                GlStateManager.disableColorMaterial();
            }

            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();

            // float f7 = angler.getSwingProgress(partialTicks);
            // float f8 = MathHelper.sin(MathHelper.sqrt(f7) * (float) Math.PI);
            // float f9 = (angler.prevRenderYawOffset + (angler.renderYawOffset - angler.prevRenderYawOffset) * partialTicks) * 0.017453292F;
            // double d0 = (double) MathHelper.sin(f9);
            // double d1 = (double) MathHelper.cos(f9);
            double d4;
            double d5;
            double d6;
            double d7;


            d4 = angler.prevPosX + (angler.posX - angler.prevPosX);// * (double) partialTicks - d0 * 0.8D;
            d5 = angler.prevPosY + (double) angler.getEyeHeight() + (angler.posY - angler.prevPosY) * (double) partialTicks - 0.15D;
            d6 = angler.prevPosZ + (angler.posZ - angler.prevPosZ);// * (double) partialTicks + d1 * 0.8D;
            d7 = angler.isSneaking() ? -0.1875D : 0.0D;


            double d13 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks;
            double d8 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTicks + 0.25D;
            double d9 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks;
            double d10 = (double) ((float) (d4 - d13));
            double d11 = (double) ((float) (d5 - d8)) + d7;
            double d12 = (double) ((float) (d6 - d9));
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);

            for (int i1 = 0; i1 <= 16; ++i1) {
                float f11 = (float) i1 / 16.0F;
                bufferbuilder.pos(x + d10 * (double) f11, y + d11 * (double) (f11 * f11 + f11) * 0.5D + 0.25D, z + d12 * (double) f11).color(0, 0, 0, 255).endVertex();
            }

            tessellator.draw();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityTentacle entity) {
        return FISH_PARTICLES;
    }
}