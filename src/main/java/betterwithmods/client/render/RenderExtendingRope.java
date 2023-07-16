package betterwithmods.client.render;

import betterwithmods.client.model.render.RenderUtils;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.entity.EntityExtendingRope;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class RenderExtendingRope extends Render<EntityExtendingRope> {

    public RenderExtendingRope(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityExtendingRope entity) {
        return null;
    }

    @Override
    public void doRender(EntityExtendingRope entity, double x, double y, double z, float entityYaw,
                         float partialTicks) {
        World world = entity.getEntityWorld();
        IBlockState iblockstate = BWMBlocks.ROPE.getDefaultState();
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(0, (entity.posY - entity.prevPosY) * partialTicks, 0);

            RenderHelper.disableStandardItemLighting();

            if (Minecraft.isAmbientOcclusionEnabled()) {
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
            } else {
                GlStateManager.shadeModel(GL11.GL_FLAT);
            }

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder vertexbuffer = tessellator.getBuffer();

            vertexbuffer.begin(7, DefaultVertexFormats.BLOCK);
            BlockPos blockpos = new BlockPos(entity.posX, entity.getEntityBoundingBox().maxY, entity.posZ);
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(x - blockpos.getX() - 0.5, (float) (y - (double) blockpos.getY()), z - blockpos.getZ() - 0.5);
                BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

                int i = 0;
                while (entity.getPulleyPosition().getY() - entity.posY > i && i < 2) {
                    blockrendererdispatcher.getBlockModelRenderer().renderModel(world, blockrendererdispatcher.getModelForState(iblockstate), iblockstate, blockpos.up(i), vertexbuffer,
                            false, 0);
                    i++;
                }

                for (Vec3i vec : entity.getBlocks().keySet()) {
                    IBlockState state = entity.getBlocks().get(vec);
                    blockrendererdispatcher.getBlockModelRenderer().renderModel(world,
                            blockrendererdispatcher.getModelForState(state), state, blockpos.add(vec), vertexbuffer, false, 0);
                }

                tessellator.draw();

                RenderHelper.enableStandardItemLighting();

            }
            GlStateManager.popMatrix();

            if (entity.blockBB != null) {
                RenderUtils.renderDebugBoundingBox(x, y, z, new Vec3d(1, 0, 0), entity.blockBB);
                RenderUtils.renderDebugBoundingBox(x, y, z, entity.blockBB.grow(0.0625).getBoundingBoxes());

            }

        }
        GlStateManager.popMatrix();

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

}
