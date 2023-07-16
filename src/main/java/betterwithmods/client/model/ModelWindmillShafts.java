package betterwithmods.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelWindmillShafts extends ModelBase {
    public final ModelRenderer axle;
    public final ModelRenderer[] components = new ModelRenderer[4];

    public ModelWindmillShafts() {
        for (int i = 0; i < 4; i++) {
            this.components[i] = new ModelRenderer(this, 0, 0);
            this.components[i].setTextureSize(16, 16);
            this.components[i].addBox(0.5F, -2.0F, -2.0F, 99, 4, 4);
            this.components[i].setRotationPoint(0F, 0F, 0F);
            setRotateAngle(this.components[i], 0, 0, 3.141593F * (i) / 2.0F);
        }
        this.axle = new ModelRenderer(this, 0, 0);
        this.axle.setTextureSize(16, 16);
        this.axle.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5);
        this.axle.setRotationPoint(0, 0, 0);
        for (int i = 0; i < 4; i++)
            this.axle.addChild(this.components[i]);
    }

    public void render(float scale) {
        axle.render(scale);
    }

    public void setRotateAngle(ModelRenderer render, float x, float y, float z) {
        render.rotateAngleX = x;
        render.rotateAngleY = y;
        render.rotateAngleZ = z;
    }
}
