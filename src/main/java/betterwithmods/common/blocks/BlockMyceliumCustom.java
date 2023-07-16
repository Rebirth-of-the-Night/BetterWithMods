package betterwithmods.common.blocks;

import betterwithmods.common.BWMBlocks;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockMycelium;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * @author Koward
 */
public class BlockMyceliumCustom extends BlockMycelium {
    public BlockMyceliumCustom() {
        super();
        this.setHardness(0.6F);
        this.setSoundType(SoundType.PLANT);
        this.setRegistryName("minecraft:mycelium");
        this.setTranslationKey("mycelium");
    }
    /**
     * Spread mycelium or turn mycelium back to dirt.
     * Based on {@link BlockMycelium#updateTick(World, BlockPos, IBlockState, Random)} build 2185
     */
    public static void handleMyceliumSpreading(World worldIn, BlockPos pos, Random rand, IBlockState dirtState) {
        if (!worldIn.isRemote) {
            if (worldIn.getLightFromNeighbors(pos.up()) < 4 && worldIn.getBlockState(pos.up()).getLightOpacity(worldIn, pos.up()) > 2) {
                worldIn.setBlockState(pos, dirtState);
            } else {
                if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
                    for (int i = 0; i < 4; ++i) {
                        BlockPos blockpos = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);
                        IBlockState iblockstate = worldIn.getBlockState(blockpos);
                        IBlockState iblockstate1 = worldIn.getBlockState(blockpos.up());

                        if (iblockstate.getBlock() == Blocks.DIRT && iblockstate.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.DIRT && worldIn.getLightFromNeighbors(blockpos.up()) >= 4 && iblockstate1.getLightOpacity(worldIn, blockpos.up()) <= 2) {
                            worldIn.setBlockState(blockpos, Blocks.MYCELIUM.getDefaultState());
                        } else if (iblockstate.getBlock() == BWMBlocks.DIRT_SLAB && iblockstate.getValue(BlockDirtSlab.VARIANT) == BlockDirtSlab.DirtSlabType.DIRT && worldIn.getLightFromNeighbors(blockpos.up()) >= 4 && iblockstate1.getLightOpacity(worldIn, blockpos.up()) <= 2) {
                            worldIn.setBlockState(blockpos, BWMBlocks.DIRT_SLAB.getDefaultState().withProperty(BlockDirtSlab.VARIANT, BlockDirtSlab.DirtSlabType.MYCELIUM));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        handleMyceliumSpreading(worldIn, pos, rand, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT));
    }
}
