package betterwithmods.util;

import betterwithmods.common.registry.block.recipe.BlockIngredient;
import com.google.common.collect.Sets;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class SetBlockIngredient extends BlockIngredient implements Set<BlockIngredient> {

    private final Set<BlockIngredient> ingredients = Sets.newHashSet();

    public SetBlockIngredient() {
        super();
    }

    public SetBlockIngredient(BlockIngredient... ingredients) {
        super();
        this.ingredients.addAll(Arrays.asList(ingredients));
    }

    @Override
    public boolean apply(World world, BlockPos pos, @Nullable IBlockState state) {
        return ingredients.stream().anyMatch(i -> i.apply(world, pos, state));
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
    public Iterator<BlockIngredient> iterator() {
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
    public boolean add(BlockIngredient ingredient) {
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
    public boolean addAll(@Nonnull Collection<? extends BlockIngredient> collection) {
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


}
