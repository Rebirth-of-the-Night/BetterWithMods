package betterwithmods.common.penalties.attribute;

import net.minecraft.util.ResourceLocation;

public class PotionAttribute extends Attribute<PotionTemplate[]> {

    public PotionAttribute(ResourceLocation registryName, PotionTemplate[] value) {
        super(registryName, value);
    }

    @Override
    public AttributeInstance<PotionTemplate[]> fromConfig(String category, String name, PotionTemplate[] defaultValue) {
        return BWMAttributes.getPotionAttribute(this, category, name, getDescription(), defaultValue);
    }
}
