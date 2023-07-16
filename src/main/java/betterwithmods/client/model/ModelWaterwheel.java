package betterwithmods.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class ModelWaterwheel extends ModelBase {
    public final ModelRenderer axle;
    public final ModelRenderer[] components = new ModelRenderer[16];

    public ModelWaterwheel() {
        for (int i = 0; i < 8; i++) {
            this.components[i] = new ModelRenderer(this, 0, 0);
            this.components[i].setTextureSize(16, 16);
            this.components[i].addBox(2.5F, -1.0F, -7.0F, 36, 2, 14);
            this.components[i].setRotationPoint(0F, 0F, 0F);
            this.setRotateAngle(this.components[i], 0, 0, 3.141593F * i / 4.0F);
        }
        for (int i = 0; i < 8; i++) {
            this.components[i + 8] = new ModelRenderer(this, 0, 0);
            this.components[i + 8].setTextureSize(16, 16);
            this.components[i + 8].addBox(0.0F, -1.0F, -6.0F, 22, 2, 12);
            float angle = 0.79F * i;
            this.components[i + 8].setRotationPoint(30.0F * MathHelper.cos(angle), 30.0F * MathHelper.sin(angle), 0.0F);
            this.setRotateAngle(this.components[i + 8], 0, 0, 1.96F + 0.79F * i);
        }
        this.axle = new ModelRenderer(this, 0, 0);
        this.axle.setTextureSize(16, 16);
        this.axle.addBox(-3.0F, -3.0F, -6.5F, 6, 6, 13);
        this.axle.setRotationPoint(0, 0, 0);
        for (int i = 0; i < 16; i++)
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
