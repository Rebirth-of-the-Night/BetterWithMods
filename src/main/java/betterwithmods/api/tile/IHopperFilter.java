package betterwithmods.api.tile;

import betterwithmods.common.blocks.mechanical.tile.TileEntityFilteredHopper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IHopperFilter {
    boolean allow(ItemStack stack);

    String getName();

    Ingredient getFilter();

    default void onInsert(World world, BlockPos pos, TileEntityFilteredHopper tile, Entity entity) {}
}
