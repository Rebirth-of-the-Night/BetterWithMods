package betterwithmods.client.render;

import betterwithmods.BWMod;
import betterwithmods.common.entity.EntityBroadheadArrow;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

/**
 * Purpose:
 *
 * @author primetoxinz
 * @version 11/18/16
 */
public class RenderBroadheadArrow extends RenderArrow<EntityBroadheadArrow> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(BWMod.MODID, "textures/entity/broadhead_arrow.png");

    public RenderBroadheadArrow(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityBroadheadArrow entity) {
        return TEXTURE;
    }
}
