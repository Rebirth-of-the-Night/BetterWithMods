package betterwithmods.common.penalties.attribute;

import net.minecraft.util.ResourceLocation;

public class StringAttribute extends Attribute<String> {

    public StringAttribute(ResourceLocation registryName, String value) {
        super(registryName, value);
    }

    @Override
    public AttributeInstance<String> fromConfig(String category, String name, String defaultValue) {
        return BWMAttributes.getStringAttribute(this, category, name, getDescription(), defaultValue);
    }


}
