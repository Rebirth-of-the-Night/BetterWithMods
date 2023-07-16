package betterwithmods.common.penalties.attribute;

public interface IAttributeInstance<V> {
    V getValue();
    IAttribute<V> getParent();
}
