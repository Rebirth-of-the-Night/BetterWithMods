package betterwithmods.client.model;

import betterwithmods.common.blocks.mechanical.tile.TileEntityWindmillVertical;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelVerticalSails extends ModelBase {
    public final ModelRenderer axle;
    public final ModelRendererChild[] components = new ModelRendererChild[8];

    public ModelVerticalSails() {
        for (int i = 0; i < 8; i++) {
            this.components[i] = new ModelRendererChild(this, 0, 0);
            this.components[i].setTextureSize(16, 16);
            this.components[i].addBox(70.4F, -50.0F, -20.0F, 1, 100, 20);
            this.components[i].setRotationPoint(0.0F, 0.0F, 0.0F);
            this.setRotateAngle(this.components[i], 0, 6.3F * i / 8.0F, 0);
        }
        this.axle = new ModelRenderer(this, 0, 0);
        this.axle.setRotationPoint(0, 0, 0);
        for (int i = 0; i < 8; i++)
            this.axle.addChild(this.components[i]);
    }

    public void render(float scale, TileEntityWindmillVertical tile) {
        for (int i = 0; i < 8; i++) {
            int meta = tile.getBladeColor(i);
            this.components[i].renderWithColor(scale, meta);
        }
    }

    public void setRotateAngle(ModelRenderer render, float x, float y, float z) {
        if (x != 0) render.rotateAngleX = x;
        if (y != 0) render.rotateAngleY = y;
        if (z != 0) render.rotateAngleZ = z;
    }

    public void setRotateAngleForSails(float x, float y, float z) {
        for (int i = 0; i < 8; i++) {
            this.setRotateAngle(this.components[i], x, (6.283186F * i / 8.0F) + y, z);
        }
    }
}
