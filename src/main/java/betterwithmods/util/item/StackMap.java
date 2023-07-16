package betterwithmods.util.item;

import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ReportedException;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Purpose:
 *
 * @author primetoxinz
 * @version 2/25/17
 */
public class StackMap<T> implements Map<Stack, T> {

    private final HashMap<Stack, T> map = new HashMap<>();
    private T defaultValue;

    public StackMap(T defaultValue) {
        this.defaultValue = defaultValue;
    }


    public T put(ItemStack stack, T t) {
        return this.put(new Stack(stack), t);
    }

    public T put(Item item, int meta, T t) {
        return this.put(new Stack(item, meta), t);
    }

    public T put(Item item, T value) {
        return put(item, 0, value);
    }

    public T put(Block block, T value) {
        return put(block, 0, value);
    }

    public T put(Block block, int meta, T value) {
        try {
            Stack stack = new Stack(block, meta);
            return put(stack, value);
        } catch (NullPointerException e) {
            CrashReport report = new CrashReport("The block " + block.getRegistryName() + " cannot be converted into an item.", e);
            throw new ReportedException(report);
        }
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return map.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return map.containsValue(o);
    }

    @Override
    public T get(Object o) {
        if (containsKey(o)) {
            return map.get(o);
        }
        return defaultValue;
    }

    public T get(Item item, int meta) {
        return get(new Stack(item, meta));
    }

    public T get(ItemStack stack) {
        return get(new Stack(stack));
    }

    public T put(Stack stack, T t) {
        return this.map.put(stack, t);
    }

    public T put(String oreDictName, T t) {
        OreDictionary.getOres(oreDictName).forEach(s -> put(new Stack(s), t));
        return t;
    }

    @Override
    public T remove(Object o) {
        return this.map.remove(o);
    }

    @Override
    public void putAll(Map<? extends Stack, ? extends T> map) {
        this.map.putAll(map);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<Stack> keySet() {
        return this.map.keySet();
    }

    @Override
    public Collection<T> values() {
        return this.map.values();
    }

    @Override
    public Set<Entry<Stack, T>> entrySet() {
        return this.map.entrySet();
    }


    @Override
    public String toString() {
        return map.toString();
    }
}
