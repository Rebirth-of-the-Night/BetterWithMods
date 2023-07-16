package betterwithmods.api.tile;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IHeatRecipe {
    int getHeat();

    default boolean canCraft(IHeated tile, World world, BlockPos pos) {
        return (ignore() && tile.getHeat(world, pos) > 0) || getHeat() == tile.getHeat(world, pos);
    }

    boolean ignore();
}
