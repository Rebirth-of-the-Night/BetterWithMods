package betterwithmods.common.registry.block.recipe;

import betterwithmods.BWMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.function.Predicate;

public class IngredientSpecial extends Ingredient {
    private ItemStack[] matchingStacks = new ItemStack[0];
    private boolean matchingStacksCached;
    private Predicate<ItemStack> matcher;

    public IngredientSpecial(Predicate<ItemStack> matcher) {
        super(0);
        this.matcher = matcher;
    }

    @Override
    public boolean apply(@Nullable ItemStack stack) {
        if (stack == null)
            stack = ItemStack.EMPTY;

        return matcher.test(stack);
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        if (!matchingStacksCached)
            cacheMatchingStacks();
        return matchingStacks;
    }

    public void cacheMatchingStacks() {
        ArrayList<ItemStack> matches = new ArrayList<>();
        for (Item item : ForgeRegistries.ITEMS) {
            CreativeTabs[] tabs = item.getCreativeTabs();
            for (CreativeTabs tab : tabs) {
                if (tab == null)
                    continue;
                NonNullList<ItemStack> items = NonNullList.create();
                try {
                    item.getSubItems(tab, items);
                }   catch(Exception e) {
                    items.add(new ItemStack(item));
                    BWMod.getLog().catching(e);
                }
                items.stream().filter(matcher).forEach(matches::add);
            }
        }
        matchingStacks = matches.toArray(matchingStacks);
        matchingStacksCached = true;
    }

}

