package betterwithmods.common.penalties.attribute;

public class AttributeInstance<V> implements IAttributeInstance<V> {

    private V value;
    private IAttribute<V> parent;

    public AttributeInstance(IAttribute<V> parent) {
        this(parent, parent.getDefaultValue());
    }

    public AttributeInstance(IAttribute<V> parent, V value) {
        this.value = value;
        this.parent = parent;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public IAttribute<V> getParent() {
        return parent;
    }
}
