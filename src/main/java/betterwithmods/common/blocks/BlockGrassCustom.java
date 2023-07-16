package betterwithmods.common.blocks;

import betterwithmods.common.BWMBlocks;
import betterwithmods.util.WorldUtils;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Custom grass block. Spreading now handles {@link BlockDirtSlab}, and only works in sunlight.
 * @author Koward
 */
public class BlockGrassCustom extends BlockGrass {
    public BlockGrassCustom() {
        super();
        this.setHardness(0.6F);
        this.setSoundType(SoundType.PLANT);
        this.setRegistryName("minecraft:grass");
        this.setTranslationKey("grass");
    }


    /**
     * Spread grass or turn grass back to dirt.
     * Based on {@link BlockGrass#updateTick(World, BlockPos, IBlockState, Random)} build 2185
     */
    public static void handleGrassSpreading(World worldIn, BlockPos pos, Random rand, IBlockState dirtState) {
        if (!worldIn.isRemote) {
            int shrinkLight = WorldUtils.getNaturalLightFromNeighbors(worldIn, pos.up());
            int growthLight = shrinkLight - worldIn.getSkylightSubtracted();
            if (shrinkLight < 9 && worldIn.getBlockState(pos.up()).getLightOpacity(worldIn, pos.up()) > 2) {
                worldIn.setBlockState(pos, dirtState);
            } else {
                if (growthLight >= 11) {
                    BlockPos blockpos = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);

                    if (blockpos.getY() >= 0 && blockpos.getY() < 256 && !worldIn.isBlockLoaded(blockpos)) {
                        return;
                    }

                    IBlockState iblockstate = worldIn.getBlockState(blockpos.up());
                    IBlockState iblockstate1 = worldIn.getBlockState(blockpos);

                    if (iblockstate1.getBlock() == Blocks.DIRT && iblockstate1.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.DIRT && WorldUtils.getNaturalLightFromNeighbors(worldIn, blockpos.up()) >= 11 && iblockstate.getLightOpacity(worldIn, pos.up()) <= 2) {
                        worldIn.setBlockState(blockpos, Blocks.GRASS.getDefaultState());
                    } else if (iblockstate1.getBlock() == BWMBlocks.DIRT_SLAB && iblockstate1.getValue(BlockDirtSlab.VARIANT) == BlockDirtSlab.DirtSlabType.DIRT && WorldUtils.getNaturalLightFromNeighbors(worldIn, blockpos.up()) >= 11 && iblockstate.getLightOpacity(worldIn, pos.up()) <= 2) {
                        worldIn.setBlockState(blockpos, BWMBlocks.DIRT_SLAB.getDefaultState().withProperty(BlockDirtSlab.VARIANT, BlockDirtSlab.DirtSlabType.GRASS));
                    } else if (iblockstate1.getBlock() == Blocks.FARMLAND && rand.nextInt(3) == 0 && worldIn.isAirBlock(blockpos.up())) {
                        int light = WorldUtils.getNaturalLightFromNeighbors(worldIn, blockpos.up());
                        if (light >= 11) {
                            worldIn.setBlockState(blockpos.up(), Blocks.TALLGRASS.getDefaultState());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        handleGrassSpreading(worldIn, pos, rand, Blocks.DIRT.getDefaultState());
    }
}
