package betterwithmods.client.render;

import betterwithmods.BWMod;
import betterwithmods.common.entity.EntityShearedCreeper;
import net.minecraft.client.model.ModelCreeper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;


public class RenderShearedCreeper extends RenderLiving<EntityShearedCreeper> {

    public RenderShearedCreeper(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelCreeper(), 0.5f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityShearedCreeper entity) {
        return new ResourceLocation(BWMod.MODID, "textures/entity/sheared_creeper.png");
    }
}