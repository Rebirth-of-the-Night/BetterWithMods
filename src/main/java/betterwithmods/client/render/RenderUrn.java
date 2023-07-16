package betterwithmods.client.render;

import betterwithmods.common.blocks.BlockUrn;
import betterwithmods.common.entity.EntityUrn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

/**
 * Created by primetoxinz on 6/13/17.
 */
public class RenderUrn extends Render<EntityUrn> {
    private RenderItem render = Minecraft.getMinecraft().getRenderItem();

    public RenderUrn(RenderManager renderManager) {
        super(renderManager);
    }

    public void doRender(EntityUrn entity, double x, double y, double z, float entityYaw, float partialTicks) {

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

        GlStateManager.scale(4,4,4);
        this.bindTexture(getEntityTexture(entity));
        render.renderItem(BlockUrn.getStack(BlockUrn.EnumType.FULL, 1), ItemCameraTransforms.TransformType.GROUND);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    protected ResourceLocation getEntityTexture(EntityUrn entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

}
