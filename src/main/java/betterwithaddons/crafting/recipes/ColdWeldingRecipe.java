package betterwithaddons.crafting.recipes;

import betterwithmods.common.registry.anvil.ShapedAnvilRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ColdWeldingRecipe extends ShapedAnvilRecipe {
    public ColdWeldingRecipe(ResourceLocation group, @Nonnull ItemStack result, Object... recipe) {
        super(group, result, recipe);
    }

    @Override
    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world) {
        return !world.provider.hasSkyLight() && super.matches(inv, world);
    }
}
