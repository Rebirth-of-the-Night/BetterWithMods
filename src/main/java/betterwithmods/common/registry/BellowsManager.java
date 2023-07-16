package betterwithmods.common.registry;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWOreDictionary;
import betterwithmods.common.items.ItemMaterial;
import betterwithmods.util.item.Stack;
import betterwithmods.util.item.StackMap;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by primetoxinz on 6/29/17.
 */
public class BellowsManager {

    public static final StackMap<Float> bellowing = new StackMap<>(1.0F);

    public static float getWeight(ItemStack stack) {
        return bellowing.get(stack);
    }

    public static void put(ItemStack stack, float value) {
        bellowing.put(new Stack(stack), value);
    }

    public static void put(Block stack, float value) {
        bellowing.put(new Stack(stack, 0), value);
    }

    public static void put(Item item, float value) {
        bellowing.put(item, value);
    }

    public static void put(String ore, float value) {
        bellowing.put(ore, value);
    }

    public static void postInit() {
        put(Items.GUNPOWDER, 3f);
        put(Items.PAPER, 3f);
        put(Items.MAP, 3f);
        put(Items.FILLED_MAP, 3f);
        put(Items.FEATHER, 3f);
        put(Items.SUGAR, 3f);
        put("foodFlour", 3f);
        BWOreDictionary.dustNames.forEach(stack -> put(stack.getOre(), 3f));
        put(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.GROUND_NETHERRACK), 2f);
        put(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.HEMP), 2f);
        put(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.HEMP_FIBERS), 2f);
        put(BWMBlocks.HEMP, 2f);
        put(Items.BEETROOT_SEEDS, 2f);
        put(Items.MELON_SEEDS, 2f);
        put(Items.PUMPKIN_SEEDS, 2f);
        put(Items.WHEAT_SEEDS, 2f);
        put(Items.STRING, 2f);
        put(Items.DYE, 2f);

        put(Items.WHEAT, 1f);
        put(Items.NETHER_WART, 1f);
        put(Items.ARROW, 1f);
        put(Items.TIPPED_ARROW, 1f);
        put(Items.SPECTRAL_ARROW, 1f);
        put(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.ELEMENT), 2f);
        put(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.FILAMENT), 2f);
        put(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.LEATHER_STRAP), 2f);

    }


}
