package betterwithmods.api.recipe.impl;

import betterwithmods.api.recipe.IOutput;
import betterwithmods.util.InvUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class StackOutput implements IOutput {
    protected ItemStack output;

    public StackOutput(ItemStack stack) {
        this.output = stack.copy();
    }

    @Override
    public ItemStack getOutput() {
        return output.copy();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getTooltip() {
        return "";
    }

    @Override
    public boolean equals(IOutput output) {
        return InvUtils.matches(output.getOutput(),this.getOutput());
    }

    @Override
    public IOutput copy() {
        return new StackOutput(output);
    }
}
