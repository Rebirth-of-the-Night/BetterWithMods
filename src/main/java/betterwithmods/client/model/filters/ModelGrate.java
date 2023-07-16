package betterwithmods.client.model.filters;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

public class ModelGrate extends ModelWithResource {
    private final ModelRenderer grate;

    public ModelGrate(ResourceLocation location) {
        super(location);
        grate = new ModelRenderer(this, 0, 0);
        grate.setTextureSize(16, 16);
        grate.addBox(-5F, 5.8F, -8F, 1, 1, 16);
        grate.addBox(-0.5F, 5.8F, -8F, 1, 1, 16);
        grate.addBox(4F, 5.8F, -8F, 1, 1, 16);
        grate.addBox(-8F, 5.75F, -5F, 16, 1, 1);
        grate.addBox(-8F, 5.75F, -0.5F, 16, 1, 1);
        grate.addBox(-8F, 5.75F, 4F, 16, 1, 1);
    }

    @Override
    public void renderModels(float scale) {
        grate.render(scale);
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
