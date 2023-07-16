package betterwithmods.api.recipe;

import net.minecraft.item.ItemStack;

public interface IOutput {

    ItemStack getOutput();

    String getTooltip();

    boolean equals(IOutput output);

    IOutput copy();
}
