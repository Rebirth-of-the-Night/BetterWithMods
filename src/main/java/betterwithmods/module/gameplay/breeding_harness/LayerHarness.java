package betterwithmods.module.gameplay.breeding_harness;

import betterwithmods.module.gameplay.breeding_harness.models.ModelSheepHarness;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.util.ResourceLocation;

public class LayerHarness<T extends EntityLiving> implements LayerRenderer<T> {

    private final RenderLiving<T> render;
    private final ResourceLocation texture;
    private ModelBase model;

    protected LayerHarness(ModelBase model, RenderLiving<T> render, ResourceLocation texture) {
        this.model = model;
        this.render = render;
        this.texture = texture;
    }

    @Override
    public void doRenderLayer(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if(entity instanceof EntitySheep)
            model = new ModelSheepHarness(0.5f);
        if (BreedingHarness.hasHarness(entity)) {
            GlStateManager.pushMatrix();
            render.bindTexture(texture);
            this.model.isChild = entity.isChild();
            this.model.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
            this.model.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);
            this.model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
