package betterwithmods.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelVerticalFrame extends ModelBase {
    public final ModelRenderer axle;
    public final ModelRenderer[] components = new ModelRenderer[32];

    public ModelVerticalFrame() {
        for (int i = 0; i < 8; i++) {
            this.components[i] = new ModelRenderer(this, 0, 0);
            this.components[i].setTextureSize(16, 16);
            this.components[i].addBox(2.0F, -52.9F, -1.0F, 67, 2, 2);
            this.components[i].setRotationPoint(0.0F, 0.0F, 0.0F);
            this.setRotateAngle(this.components[i], 0, 6.3F * i / 8.0F, 0);
            this.components[i + 8] = new ModelRenderer(this, 0, 0);
            this.components[i + 8].setTextureSize(16, 16);
            this.components[i + 8].addBox(2.0F, 50.9F, -1.0F, 67, 2, 2);
            this.components[i + 8].setRotationPoint(0.0F, 0.0F, 0.0F);
            this.setRotateAngle(this.components[i + 8], 0, 6.3F * i / 8.0F, 0);
            this.components[i + 16] = new ModelRenderer(this, 0, 0);
            this.components[i + 16].setTextureSize(16, 16);
            this.components[i + 16].addBox(64.900002F, -52.9F, -26.0F, 2, 2, 52);
            this.components[i + 16].setRotationPoint(0.0F, 0.0F, 0.0F);
            this.setRotateAngle(this.components[i + 16], 0, 6.3F * i / 8.0F + 0.39F, 0);
            this.components[i + 24] = new ModelRenderer(this, 0, 0);
            this.components[i + 24].setTextureSize(16, 16);
            this.components[i + 24].addBox(64.900002F, 50.900002F, -26.0F, 2, 2, 52);
            this.components[i + 24].setRotationPoint(0.0F, 0.0F, 0.0F);
            this.setRotateAngle(this.components[i + 24], 0.0F, 6.3F * i / 8.0F + 0.39F, 0.0F);
        }
        this.axle = new ModelRenderer(this, 0, 0);
        this.axle.setRotationPoint(0, 0, 0);
        for (int i = 0; i < 32; i++)
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
