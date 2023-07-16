package betterwithmods.common.registry.anvil;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import javax.annotation.Nonnull;

/**
 * Created by primetoxinz on 6/28/17.
 */
public class ShapelessAnvilRecipe extends ShapelessOreRecipe {
    public ShapelessAnvilRecipe(ResourceLocation group, Block result, Object... recipe) {
        super(group, result, recipe);
    }

    public ShapelessAnvilRecipe(ResourceLocation group, Item result, Object... recipe) {
        super(group, result, recipe);
    }

    public ShapelessAnvilRecipe(ResourceLocation group, NonNullList<Ingredient> input, @Nonnull ItemStack result) {
        super(group, input, result);
    }

    public ShapelessAnvilRecipe(ResourceLocation group, @Nonnull ItemStack result, Object... recipe) {
        super(group, result, recipe);
    }
}
