package betterwithmods.api.recipe.impl;

import betterwithmods.api.recipe.IOutput;
import betterwithmods.util.InvUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import java.util.Random;

public class RandomOutput extends StackOutput {
    private static final Random RANDOM = new Random();

    private int min, max;

    public RandomOutput(ItemStack stack, int min, int max) {
        super(stack);
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    private int rand() {
        return RANDOM.nextInt((max - min) + 1) + min;
    }

    public ItemStack getRandomStack() {
        return InvUtils.setCount(getOutput(), rand());
    }


    public RandomOutput copy() {
        return new RandomOutput(getOutput(), min, max);
    }

    public String getTooltip() {
        return I18n.format("bwm.random_output.tooltip", min, max);
    }

    @Override
    public boolean equals(IOutput output) {
        if (output instanceof RandomOutput) {
            RandomOutput other = (RandomOutput) output;
            return this.getMax() == other.getMax() && this.getMin() == other.getMin() && InvUtils.matches(other.getOutput(), this.getOutput());
        }
        return false;
    }
}
