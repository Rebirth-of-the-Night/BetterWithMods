package betterwithmods.api.recipe.impl;

import betterwithmods.api.recipe.IOutput;
import betterwithmods.api.recipe.IRecipeOutputs;
import betterwithmods.util.InvUtils;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.List;
import java.util.stream.Collectors;

public class ListOutputs implements IRecipeOutputs {
    protected List<StackOutput> outputs;

    public ListOutputs(ItemStack... outputs) {
        this(Lists.newArrayList(outputs));
    }

    public ListOutputs(List<ItemStack> outputs) {
        this.outputs = outputs.stream().filter(s -> !s.isEmpty()).map(StackOutput::new).collect(Collectors.toList());
    }

    @Override
    public NonNullList<ItemStack> getOutputs() {
        return InvUtils.asNonnullList(this.outputs.stream().map(StackOutput::getOutput).map(ItemStack::copy).collect(Collectors.toList()));
    }

    @Override
    public List<IOutput> getDisplayOutputs() {
        return cast(outputs);
    }

    @Override
    public boolean matches(List<ItemStack> outputs) {
        return InvUtils.matchesExact(getOutputs(), outputs);
    }

    @Override
    public boolean matchesFuzzy(List<ItemStack> outputs) {
        return InvUtils.matches(getOutputs(), outputs);
    }

    @Override
    public boolean isInvalid() {
        return outputs.isEmpty();
    }


}
