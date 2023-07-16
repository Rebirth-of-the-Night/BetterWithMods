package betterwithmods.common.penalties.attribute;

import net.minecraft.util.ResourceLocation;

public class FloatAttribute extends Attribute<Float> {
    public FloatAttribute(ResourceLocation registryName, Float value) {
        super(registryName, value);
    }

    @Override
    public AttributeInstance<Float> fromConfig(String category, String name, Float defaultValue) {
        return BWMAttributes.getFloatAttribute(this, category, name, getDescription(), defaultValue);
    }
}
