package betterwithmods.api.recipe;

import betterwithmods.util.InvUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.List;
import java.util.stream.Collectors;

public interface IRecipeOutputs {
    NonNullList<ItemStack> getOutputs();

    List<IOutput> getDisplayOutputs();

    boolean matches(List<ItemStack> outputs);

    boolean matchesFuzzy(List<ItemStack> outputs);

    boolean isInvalid();


    default List<List<IOutput>> getExpandedOutputs(int boxes) {
        return InvUtils.splitIntoBoxes(getDisplayOutputs(), boxes);
    }

    default List<IOutput> cast(List<? extends IOutput> list) {
        return list.stream().map(o -> (IOutput)o).collect(Collectors.toList());
    }

}
