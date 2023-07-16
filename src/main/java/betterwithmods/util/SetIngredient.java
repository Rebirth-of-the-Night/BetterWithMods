package betterwithmods.util;

import com.google.common.collect.Sets;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class SetIngredient extends Ingredient implements Set<Ingredient> {

    private final Set<Ingredient> ingredients = Sets.newHashSet();

    public SetIngredient() {
        super();
    }

    public SetIngredient(Ingredient... ingredients) {
        super();
        this.ingredients.addAll(Arrays.asList(ingredients));
    }

    @Override
    public boolean apply(@Nullable ItemStack stack) {
        return ingredients.stream().anyMatch(i -> i.apply(stack));
    }

    @Override
    public int size() {
        return ingredients.size();
    }

    @Override
    public boolean isEmpty() {
        return ingredients.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return ingredients.contains(o);
    }

    @Nonnull
    @Override
    public Iterator<Ingredient> iterator() {
        return ingredients.iterator();
    }

    @Nonnull
    @Override
    public Object[] toArray() {
        return ingredients.toArray();
    }

    @Nonnull
    @Override
    public <T> T[] toArray(@Nonnull T[] ts) {
        return ingredients.toArray(ts);
    }

    @Override
    public boolean add(Ingredient ingredient) {
        return ingredients.add(ingredient);
    }

    @Override
    public boolean remove(Object o) {
        return ingredients.remove(o);
    }

    @Override
    public boolean containsAll(@Nonnull Collection<?> collection) {
        return ingredients.containsAll(collection);
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends Ingredient> collection) {
        return ingredients.addAll(collection);
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> collection) {
        return ingredients.retainAll(collection);
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> collection) {
        return ingredients.removeAll(collection);
    }

    @Override
    public void clear() {
        ingredients.clear();
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        return ingredients.stream().map(Ingredient::getMatchingStacks).flatMap(Arrays::stream).toArray(ItemStack[]::new);
    }
}
