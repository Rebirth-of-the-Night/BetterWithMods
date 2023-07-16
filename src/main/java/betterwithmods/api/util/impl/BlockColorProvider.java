package betterwithmods.api.util.impl;

import betterwithmods.api.util.IColorProvider;
import betterwithmods.common.BWMRecipes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class BlockColorProvider implements IColorProvider {
    public static final BlockColorProvider INSTANCE = new BlockColorProvider();

    @Override
    public int getColor(ItemStack stack) {
        if (stack.getItem() instanceof ItemBlock) {
            Block block = ((ItemBlock) stack.getItem()).getBlock();
            if (block instanceof BlockColored || block instanceof BlockStainedGlass) {
                return EnumDyeColor.byMetadata(stack.getMetadata()).colorValue;
            }
            IBlockState state = BWMRecipes.getStateFromStack(stack);
            try {
                MapColor color = state.getMapColor(null, null);
                return color.colorValue;
            } catch (Throwable ignore) {
            }
            if (block instanceof IColorProvider) {
                return ((IColorProvider) block).getColor(stack);
            }
        }
        return 0;
    }

    @Override
    public float[] getColorComponents(ItemStack stack) {
        if (stack.getItem() instanceof ItemBlock) {
            Block block = ((ItemBlock) stack.getItem()).getBlock();
            if (block instanceof BlockColored || block instanceof BlockStainedGlass) {
                return EnumDyeColor.byMetadata(stack.getMetadata()).getColorComponentValues();
            }
        }

        return IColorProvider.super.getColorComponents(stack);
    }
}
