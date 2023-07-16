package betterwithmods.api.tile;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IHeatSource {
    int getHeat();
    boolean matches(World world, BlockPos pos);
}
