package betterwithmods.client.model.filters;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

public class ModelSlats extends ModelWithResource {
    private final ModelRenderer slats;

    public ModelSlats(ResourceLocation location) {
        super(location);
        slats = new ModelRenderer(this, 0, 0);
        slats.setTextureSize(16, 16);
        slats.addBox(-5.5F, 5.8F, -8F, 2, 2, 16);
        slats.addBox(-2.67F, 5.8F, -8F, 2, 2, 16);
        slats.addBox(0.26F, 5.8F, -8F, 2, 2, 16);
        slats.addBox(3.19F, 5.8F, -8F, 2, 2, 16);
    }

    @Override
    public void renderModels(float scale) {
        slats.render(scale);
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
