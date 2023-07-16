package betterwithmods.common.registry.crafting;

import betterwithmods.client.container.anvil.ContainerSteelAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

import java.util.Random;
import java.util.function.Predicate;

/**
 * Created by primetoxinz on 6/27/17.
 */
public abstract class ToolDamageRecipe extends ToolBaseRecipe {

    public ToolDamageRecipe(ResourceLocation group, ItemStack result, Ingredient input, Predicate<ItemStack> isTool) {
        super(group, result, input, isTool);
    }

    public boolean shouldDamage(ItemStack stack, EntityPlayer player, IBlockState state) {
        return true;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> stacks = super.getRemainingItems(inv);
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && isTool.test(stack)) {
                ItemStack copy = stack.copy();
                if (!shouldDamage(copy, null, null) || !copy.attemptDamageItem(1, new Random(), null)) {
                    stacks.set(i, copy.copy());
                }
            }
        }
        return stacks;
    }

    public void playSound(InventoryCrafting inv) {
        Container container = inv.eventHandler;
        EntityPlayer player = null;
        if (container instanceof ContainerWorkbench)
            player = ((ContainerWorkbench) container).player;
        if (container instanceof ContainerPlayer)
            player = ((ContainerPlayer) container).player;
        if (container instanceof ContainerSteelAnvil)
            player = ((ContainerSteelAnvil) container).player;

        if (player != null) {
            player.world.playSound(null, player.getPosition(), getSound(), SoundCategory.BLOCKS, getSoundValues().getLeft(), getSoundValues().getRight());
        }
    }


}
