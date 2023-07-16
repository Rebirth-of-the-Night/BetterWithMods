package betterwithmods.module.hardcore.needs.hunger;

import betterwithmods.util.InvUtils;
import com.google.common.collect.Maps;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import squeek.applecore.api.food.FoodValues;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by primetoxinz on 6/20/17.
 */
public class FoodHelper {
    private static final HashMap<ItemStack, FoodValues> FOOD_VALUES = Maps.newHashMap();

    public static HashMap<ItemStack, FoodValues> getFoodValues() {
        return FOOD_VALUES;
    }

    protected static Optional<FoodValues> getFoodValue(ItemStack stack) {
        return FOOD_VALUES.entrySet().stream().filter(entry -> InvUtils.matches(entry.getKey(), stack)).map(Map.Entry::getValue).findFirst();
    }

    public static void registerFood(ItemStack item, int hunger) {
        registerFood(item, hunger, 0, false);
    }

    public static void registerFood(ItemStack stack, int hunger, float fat, boolean alwaysEdible) {
        registerFood(stack, new FoodValues(hunger, fat), alwaysEdible);
    }

    public static void registerFood(ItemStack stack, FoodValues values, boolean alwaysEdible) {
        if (alwaysEdible) {
            setAlwaysEdible(stack);
        }
        FOOD_VALUES.put(stack, values);
    }


    public static void setAlwaysEdible(ItemStack stack) {
        if (stack.getItem() instanceof ItemFood) {
            ItemFood food = (ItemFood) stack.getItem();
            food.setAlwaysEdible();
        }
    }


}
