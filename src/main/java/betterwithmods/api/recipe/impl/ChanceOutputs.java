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

public class ChanceOutputs implements IRecipeOutputs {
    private static final Random RANDOM = new Random();

    protected List<ChanceOutput> weightedItemStacks;
    private List<ItemStack> itemStacksList;

    public ChanceOutputs(ItemStack stack, double weight) {
        this(new ChanceOutput(stack, weight));
    }

    public ChanceOutputs(ChanceOutput... weightedItemStacks) {
        this(Lists.newArrayList(weightedItemStacks));
    }

    public ChanceOutputs(List<ChanceOutput> weightedItemStacks) {
        this.weightedItemStacks = weightedItemStacks;
        this.itemStacksList = weightedItemStacks.stream().map(ChanceOutput::getOutput).collect(Collectors.toList());
    }

    @Override
    public List<IOutput> getDisplayOutputs() {
        return cast(weightedItemStacks);
    }

    @Override
    public NonNullList<ItemStack> getOutputs() {
        NonNullList<ItemStack> outputs = NonNullList.create();
        for (ChanceOutput output : weightedItemStacks) {
            if (RANDOM.nextDouble() < output.getWeight()) {
                outputs.add(output.getOutput());
            }
        }
        return outputs;
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

}
