package betterwithmods.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelVerticalShafts extends ModelBase {
    public final ModelRenderer axle;
    public final ModelRenderer[] components = new ModelRenderer[8];

    public ModelVerticalShafts() {
        for (int i = 0; i < 8; i++) {
            this.components[i] = new ModelRenderer(this, 0, 0);
            this.components[i].setTextureSize(16, 16);
            this.components[i].addBox(68.4F, -54.0F, -2.0F, 4, 108, 4);
            this.components[i].setRotationPoint(0.0F, 0.0F, 0.0F);
            this.setRotateAngle(this.components[i], 0, 6.3F * i / 8.0F, 0);
        }
        this.axle = new ModelRenderer(this, 0, 0);
        this.axle.setRotationPoint(0, 0, 0);
        for (int i = 0; i < 8; i++)
            this.axle.addChild(this.components[i]);
    }

    public void render(float scale) {
        this.axle.render(scale);
    }

    public void setRotateAngle(ModelRenderer render, float x, float y, float z) {
        render.rotateAngleX = x;
        render.rotateAngleY = y;
        render.rotateAngleZ = z;
    }
}
