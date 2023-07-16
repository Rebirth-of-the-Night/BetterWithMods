package betterwithmods.common.penalties.attribute;

import net.minecraft.util.ResourceLocation;

public abstract class Attribute<V> implements IAttribute<V> {
    private ResourceLocation registryName;
    private V value;
    private String description;

    public Attribute(ResourceLocation registryName, V value) {
        this.registryName = registryName;
        this.value = value;
    }

    @Override
    public V getDefaultValue() {
        return value;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public Attribute<V> setRegistryName(ResourceLocation registryName) {
        this.registryName = registryName;
        return this;
    }

    public abstract AttributeInstance<V> fromConfig(String category, String name, V defaultValue);

    public String getDescription() {
        return description;
    }

    public Attribute<V> setDescription(String description) {
        this.description = description;
        return this;
    }
}
