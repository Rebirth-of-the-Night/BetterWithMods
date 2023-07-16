package betterwithmods.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelTurntableKnob extends ModelBase {
    public final ModelRenderer knobs;

    public ModelTurntableKnob(int position) {
        float off = -4 + (position * 2);
        knobs = new ModelRenderer(this, 0, 0);
        knobs.setTextureSize(16, 16);
        knobs.addBox(off, -3F, 8F, 2, 2, 1);
        knobs.addBox(-2F + -off, -3F, -9F, 2, 2, 1);
        knobs.addBox(-9F, -3F, off, 1, 2, 2);
        knobs.addBox(8F, -3F, -2F - off, 1, 2, 2);
    }

    public void render(float scale) {
        knobs.render(scale);
    }
}
