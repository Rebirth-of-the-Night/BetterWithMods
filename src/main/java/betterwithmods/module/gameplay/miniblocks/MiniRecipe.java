package betterwithmods.module.gameplay.miniblocks;

import betterwithmods.common.BWMRecipes;
import betterwithmods.module.gameplay.miniblocks.blocks.BlockMini;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class MiniRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    public Block start, end;

    public MiniRecipe(Block start, Block end) {
        this.start = start;
        this.end = end;
        setRegistryName(start.getRegistryName());
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        ItemStack[] stacks = new ItemStack[2];
        int matches = 0;
        for (int x = 0; x < inv.getSizeInventory(); x++) {
            boolean inRecipe = false;
            ItemStack stack = inv.getStackInSlot(x);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof ItemMini && matches <= 1) {
                    Block block = ((ItemMini) stack.getItem()).getBlock();
                    if (block == start) {
                        stacks[matches] = stack;
                        inRecipe = true;
                        matches++;
                    }
                }
                if (!inRecipe)
                    return false;
            }

        }
        return ItemMini.matches(stacks[0], stacks[1]);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack first = ItemStack.EMPTY;
        for (int x = 0; x < inv.getSizeInventory(); x++) {
            ItemStack stack = inv.getStackInSlot(x);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof ItemMini) {
                    first = stack;
                    break;
                }
            }
        }
        return getOutput(first);
    }

    @Override
    public boolean canFit(int width, int height) {
        return (width * height) >= 2;
    }

    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    public ItemStack getStart() {
        return new ItemStack(start);
    }

    public ItemStack getEnd() {
        return new ItemStack(end);
    }

    public ItemStack getOutput(ItemStack input) {
        ItemStack result = ItemStack.EMPTY;
        if (!input.isEmpty()) {
            if (end instanceof BlockMini) {
                result = new ItemStack(end);
                NBTTagCompound tag = input.getTagCompound();
                result.setTagCompound(tag);
            } else {
                IBlockState state = ItemMini.getState(input);
                if (state != null)
                    result = BWMRecipes.getStackFromState(state);
            }
        }
        return result;

    }
}
