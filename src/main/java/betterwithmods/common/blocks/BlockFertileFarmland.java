package betterwithmods.common.blocks;

import net.minecraft.block.BlockFarmland;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

import java.util.Random;

public class BlockFertileFarmland extends BlockFarmland {
    public BlockFertileFarmland() {
        super();
        this.setHardness(0.6F);
        this.setSoundType(SoundType.GROUND);
        this.setHarvestLevel("shovel", 0);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(world, pos, state, rand);

        IBlockState above = world.getBlockState(pos.up());
        if (above.getBlock() instanceof IPlantable && canSustainPlant(above, world, pos, EnumFacing.UP, (IPlantable) above.getBlock())) {
            world.scheduleBlockUpdate(pos.up(), above.getBlock(), above.getBlock().tickRate(world), 5);
            if (rand.nextInt(150) == 0) {
                int meta = this.getMetaFromState(state);
                world.setBlockState(pos, Blocks.FARMLAND.getDefaultState().withProperty(MOISTURE, meta & 7));
            }
        }
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
        return plantable.getPlantType(world, pos.up()) == EnumPlantType.Crop || plantable.getPlantType(world, pos.up()) == EnumPlantType.Plains;
    }
}
