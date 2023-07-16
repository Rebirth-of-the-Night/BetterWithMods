package betterwithmods.common.registry;

import betterwithmods.BWMod;
import betterwithmods.api.tile.IHopperFilter;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.List;

public class HopperFilter implements IHopperFilter {

    public static IHopperFilter NONE = new HopperFilter(BWMod.MODID+":none", Ingredient.EMPTY, Lists.newArrayList());

    public String name;
    public Ingredient filter;
    public List<Ingredient> filtered;

    public HopperFilter(String name, Ingredient filter, List<Ingredient> filtered) {
        this.name = name;
        this.filter = filter;
        this.filtered = filtered;
    }

    public String getName() {
        return name;
    }

    public Ingredient getFilter() {
        return filter;
    }

    public List<Ingredient> getFiltered() {
        return filtered;
    }

    public boolean allow(ItemStack stack) {
        return filtered.isEmpty() || filtered.stream().anyMatch(i -> i.apply(stack));
    }
}
