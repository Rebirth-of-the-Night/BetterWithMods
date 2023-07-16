package betterwithmods.common.registry;

import betterwithmods.BWMod;
import betterwithmods.api.tile.IHopperFilter;
import betterwithmods.util.StackIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class SelfHopperFilter implements IHopperFilter {

    private Ingredient filter;

    public SelfHopperFilter(ItemStack stack) {
        this(StackIngredient.fromStacks(stack));
    }

    public SelfHopperFilter(Ingredient filter) {
        this.filter = filter;
    }

    @Override
    public boolean allow(ItemStack stack) {
        return filter.apply(stack);
    }

    @Override
    public String getName() {
        return BWMod.MODID + ":self";
    }

    @Override
    public Ingredient getFilter() {
        return filter;
    }
}
