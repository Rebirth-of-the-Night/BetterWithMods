package betterwithmods.common.registry;

import betterwithmods.api.tile.IHopperFilter;
import betterwithmods.module.gameplay.HopperRecipes;
import com.google.common.collect.Maps;
import net.minecraft.item.ItemStack;

import java.util.Map;

/**
 * Purpose:
 *
 * @author primetoxinz
 * @version 11/13/16
 */
public class HopperFilters {

    private Map<String, IHopperFilter> FILTERS = Maps.newHashMap();

    public void addFilter(IHopperFilter filter) {
        FILTERS.put(filter.getName(), filter);
    }

    public IHopperFilter getFilter(String name) {
        return FILTERS.getOrDefault(name, HopperFilter.NONE);
    }

    public IHopperFilter getFilter(ItemStack stack) {
        if (stack.isEmpty())
            return HopperFilter.NONE;
        return FILTERS.values().stream().filter(filter -> filter.getFilter().apply(stack)).findFirst().orElse(HopperRecipes.useSelfFiltering ? new SelfHopperFilter(stack) : HopperFilter.NONE);
    }

    public boolean isFilter(ItemStack stack) {
        return getFilter(stack) != HopperFilter.NONE;
    }

}
