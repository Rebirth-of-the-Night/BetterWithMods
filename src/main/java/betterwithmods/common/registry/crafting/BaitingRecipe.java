package betterwithmods.common.registry.crafting;


import betterwithmods.BWMod;
import betterwithmods.module.hardcore.crafting.HCFishing;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.ForgeHooks;
import org.apache.commons.lang3.tuple.Pair;

import static betterwithmods.module.hardcore.crafting.HCFishing.setBaited;

public class BaitingRecipe extends ToolBaseRecipe {

    public BaitingRecipe() {
        super(new ResourceLocation(BWMod.MODID,"baiting_recipe"), ItemStack.EMPTY, HCFishing.BAIT, stack -> HCFishing.isBaited(stack, false));
        setRegistryName(getGroup());
    }

    public ItemStack findRod(InventoryCrafting inv) {
        for (int x = 0; x < inv.getSizeInventory(); x++) {
            ItemStack slot = inv.getStackInSlot(x);
            if (isTool.test(slot)) {
                return slot;
            }
        }
        return ItemStack.EMPTY;
    }

    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < ret.size(); i++) {
            ret.set(i, ForgeHooks.getContainerItem(inv.getStackInSlot(i)));
        }
        return ret;
    }

    @Override
    public void playSound(InventoryCrafting inv) {}

    @Override
    public SoundEvent getSound() {
        return null;
    }

    @Override
    public Pair<Float, Float> getSoundValues() {
        return null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack rod = findRod(inv);
        if (!rod.isEmpty())
            return setBaited(rod.copy(), true);
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getRecipeOutput() {
        ItemStack rod = new ItemStack(Items.FISHING_ROD);
        if (!rod.isEmpty())
            return setBaited(rod.copy(), true);
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public ItemStack getExampleStack() {
        return new ItemStack(Items.FISHING_ROD);
    }
}