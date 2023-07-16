package betterwithmods.common.penalties.attribute;

import net.minecraft.util.ResourceLocation;

public class BooleanAttribute extends Attribute<Boolean> {

    public BooleanAttribute(ResourceLocation registryName, Boolean value) {
        super(registryName, value);
    }

    @Override
    public AttributeInstance<Boolean> fromConfig(String category, String name, Boolean defaultValue) {
        return BWMAttributes.getBooleanAttribute(this, category, name, getDescription(), defaultValue);
    }


}
