package betterwithmods.api.tile;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface IHeated {
    int getHeat(World world, BlockPos pos);
}
