package betterwithmods.client.model.filters;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

public class ModelOpaque extends ModelWithResource {
    public final ModelRenderer model;

    public ModelOpaque(ResourceLocation location) {
        super(location);
        model = new ModelRenderer(this, 0, 0);
        model.setTextureSize(16, 16);
        model.addBox(-8F, 6F, -8F, 16, 1, 16);
    }

    @Override
    public void renderModels(float scale) {
        model.render(scale);
    }

    @Override
    public boolean isSolid() {
        return true;
    }
}
