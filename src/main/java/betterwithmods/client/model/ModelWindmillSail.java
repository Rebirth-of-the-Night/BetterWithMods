package betterwithmods.client.model;

import betterwithmods.common.blocks.mechanical.tile.TileEntityWindmillHorizontal;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelWindmillSail extends ModelBase {
    public final ModelRenderer axle;
    public final ModelRendererChild[] components = new ModelRendererChild[4];

    public ModelWindmillSail() {
        for (int i = 0; i < 4; i++) {
            this.components[i] = new ModelRendererChild(this, 0, 0);
            this.components[i].setTextureSize(16, 16);
            this.components[i].addBox(15.0F, 1.75F, 1.0F, 84, 16, 1);
            this.components[i].setRotationPoint(0.0F, 0.0F, 0.0F);
            this.setRotateAngle(this.components[i], -0.26F, 0, 3.141593F * i / 2.0F);
        }
        this.axle = new ModelRenderer(this, 0, 0);
        this.axle.setRotationPoint(0, 0, 0);
        for (int i = 0; i < 4; i++)
            this.axle.addChild(this.components[i]);
    }

    public void render(float scale, TileEntityWindmillHorizontal tile) {
        for (int i = 0; i < 4; i++) {
            int color = tile.getBladeColor(i);
            this.components[i].renderWithColor(scale, color);
        }
    }

    public void setRotateAngle(ModelRenderer render, float x, float y, float z) {
        if (x != 0) render.rotateAngleX = x;
        if (y != 0) render.rotateAngleY = y;
        if (z != 0) render.rotateAngleZ = z;
    }

    public void setRotateAngleForSails(float x, float y, float z) {
        for (int i = 0; i < 4; i++) {
            this.setRotateAngle(this.components[i], x, y, (3.141593F * i / 2.0F) + z);
        }
    }
}
