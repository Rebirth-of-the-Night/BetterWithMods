package betterwithmods.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreIngredient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class StackIngredient extends Ingredient {
    private final Map<Ingredient,Integer> internal;
    private ItemStack[] cachedStacks;

    protected StackIngredient(Map<Ingredient,Integer> ingredients) {
        super(0);
        internal = ingredients;
    }

    protected StackIngredient(Ingredient ingredient, int amount) {
        super(0);
        internal = new HashMap<>();
        internal.put(ingredient,amount);
    }

    public static StackIngredient fromIngredient(int count, Ingredient ingredient) {
        return new StackIngredient(ingredient,count);
    }

    public static StackIngredient fromStacks(ItemStack... stacks) {
        return new StackIngredient(Arrays.stream(stacks).collect(Collectors.toMap(Ingredient::fromStacks, ItemStack::getCount)));
    }

    public static StackIngredient fromStacks(Collection<ItemStack> stacks) {
        return new StackIngredient(stacks.stream().collect(Collectors.toMap(Ingredient::fromStacks, ItemStack::getCount)));
    }

    public static StackIngredient fromOre(int count, String ore) {
        return fromIngredient(count, new OreIngredient(ore));
    }

    public static StackIngredient fromOre(String ore) {
        return fromOre(1, ore);
    }

    public static StackIngredient mergeStacked(List<StackIngredient> ingredients) {
        HashMap<Ingredient,Integer> map = new HashMap<>();
        ingredients.forEach(stackIngredient -> map.putAll(stackIngredient.internal));
        return new StackIngredient(map);
    }

    public static StackIngredient merge(Map<Ingredient,Integer> ingredients) {
        return new StackIngredient(ingredients);
    }

    @Override
    public boolean apply(@Nullable ItemStack stack) {
        return stack != null && internal.entrySet().stream().anyMatch(entry -> stack.getCount() >= entry.getValue() && entry.getKey().apply(stack));
    }

    @Override
    @Nonnull
    public ItemStack[] getMatchingStacks() {
        if(cachedStacks == null) {
            ArrayList<ItemStack> stacks = new ArrayList<>();
            for (Map.Entry<Ingredient,Integer> entry : internal.entrySet())
                Arrays.stream(entry.getKey().getMatchingStacks()).map(stack -> withCount(stack, entry.getValue())).forEach(stacks::add);
            cachedStacks = stacks.toArray(new ItemStack[stacks.size()]);
        }
        return cachedStacks;
    }

    private ItemStack withCount(ItemStack stack, int count) {
        ItemStack newStack = stack.copy();
        newStack.setCount(count);
        return newStack;
    }

    @Override
    protected void invalidate() {
        super.invalidate();
        cachedStacks = null;
    }

    public int getCount(ItemStack stack) {
        return internal.entrySet().stream().filter(entry -> entry.getKey().apply(stack)).findFirst().get().getValue();
    }
}
