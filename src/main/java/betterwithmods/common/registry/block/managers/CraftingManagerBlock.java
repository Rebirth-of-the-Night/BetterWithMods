package betterwithmods.common.registry.block.managers;

import betterwithmods.common.registry.block.recipe.BlockRecipe;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Purpose:
 *
 * @author primetoxinz
 * @version 11/11/16
 */
public abstract class CraftingManagerBlock<T extends BlockRecipe> {

    protected final ArrayList<T> recipes = Lists.newArrayList();

    protected HashMap<IBlockState, T> recipeCache = Maps.newHashMap();

    public T addRecipe(T recipe) {
        if (!recipe.isInvalid())
            recipes.add(recipe);
        return recipe;
    }

    protected List<T> findRecipe(List<ItemStack> outputs) {
        List<T> recipes = findRecipeExact(outputs);
        if (recipes.isEmpty())
            recipes = findRecipeFuzzy(outputs);
        return recipes;
    }

    protected List<T> findRecipeFuzzy(List<ItemStack> outputs) {
        return recipes.stream().filter(r -> r.getRecipeOutput().matchesFuzzy(outputs)).collect(Collectors.toList());
    }

    protected List<T> findRecipeExact(List<ItemStack> outputs) {
        return recipes.stream().filter(r -> r.getRecipeOutput().matches(outputs)).collect(Collectors.toList());
    }

    protected List<T> findRecipeByInput(ItemStack input) {
        return recipes.stream().filter(r -> r.getInput().apply(input)).collect(Collectors.toList());
    }

    public List<T> findRecipes(World world, BlockPos pos, IBlockState state) {
        return recipes.stream().filter(r -> r.matches(world, pos, state)).collect(Collectors.toList());
    }

    public Optional<T> findRecipe(World world, BlockPos pos, IBlockState state) {
        //Don't do caching for input states that have TEs, can't properly put extended states into hashmaps anyways.
        boolean hasTile = state.getBlock().hasTileEntity(state);
        if (!hasTile && recipeCache.containsKey(state)) {
            T t = recipeCache.get(state);
            if(t != null && t.matches(world,pos,state)) {
                return Optional.of(t);
            }
        }

        Optional<T> recipe = findRecipes(world, pos, state).stream().findFirst();
        if (!hasTile) {
            recipe.ifPresent(t -> recipeCache.put(state, t));
        }
        return recipe;
    }


    public boolean remove(T t) {
        return t != null && recipes.remove(t);
    }

    public boolean remove(List<ItemStack> outputs) {
        return recipes.removeAll(findRecipe(outputs));
    }

    public boolean removeFuzzy(List<ItemStack> outputs) {
        return recipes.removeAll(findRecipeFuzzy(outputs));
    }

    public boolean removeExact(List<ItemStack> outputs) {
        return recipes.removeAll(findRecipeExact(outputs));
    }

    public boolean removeByInput(ItemStack input) {
        return recipes.removeAll(findRecipeByInput(input));
    }

    public List<T> getRecipes() {
        return recipes;
    }

    public List<T> getDisplayRecipes() {
        return getRecipes().stream().filter(r -> !r.isHidden()).collect(Collectors.toList());
    }

}
