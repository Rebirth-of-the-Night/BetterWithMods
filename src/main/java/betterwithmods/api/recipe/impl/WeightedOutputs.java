package betterwithmods.api.recipe.impl;

import betterwithmods.api.recipe.IOutput;
import betterwithmods.api.recipe.IRecipeOutputs;
import betterwithmods.util.InvUtils;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class WeightedOutputs implements IRecipeOutputs {
    private static final Random RANDOM = new Random();

    protected List<ChanceOutput> weightedItemStacks;
    private List<ItemStack> itemStacksList;

    public WeightedOutputs(ItemStack stack, double weight) {
        this(new ChanceOutput(stack,weight));
    }

    public WeightedOutputs(ChanceOutput... weightedItemStacks) {
        this(Lists.newArrayList(weightedItemStacks));
    }

    public WeightedOutputs(List<ChanceOutput> weightedItemStacks) {
        this.weightedItemStacks = weightedItemStacks;
        this.itemStacksList = weightedItemStacks.stream().map(ChanceOutput::getOutput).collect(Collectors.toList());
    }

    @Override
    public List<IOutput> getDisplayOutputs() {
        return cast(weightedItemStacks);
    }

    @Override
    public NonNullList<ItemStack> getOutputs() {
        return InvUtils.asNonnullList(findResult());
    }

    @Override
    public boolean matches(List<ItemStack> outputs) {
        return InvUtils.matchesExact(outputs, itemStacksList);
    }

    @Override
    public boolean matchesFuzzy(List<ItemStack> outputs) {
        return InvUtils.matches(outputs, itemStacksList);
    }

    @Override
    public boolean isInvalid() {
        return weightedItemStacks.isEmpty();
    }

    private ItemStack findResult() {
        ItemStack result = ItemStack.EMPTY;
        double bestValue = Double.MAX_VALUE;
        for (ChanceOutput element : weightedItemStacks) {
            double value = -Math.log(RANDOM.nextDouble()) / element.getWeight();
            if (value < bestValue) {
                bestValue = value;
                result = element.getOutput();
            }
        }
        return result;
    }

}
