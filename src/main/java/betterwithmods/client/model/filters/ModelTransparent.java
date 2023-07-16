package betterwithmods.client.model.filters;

import net.minecraft.util.ResourceLocation;

public class ModelTransparent extends ModelOpaque {
    public ModelTransparent(ResourceLocation location) {
        super(location);
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
