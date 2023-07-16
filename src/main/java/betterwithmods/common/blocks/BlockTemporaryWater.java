package betterwithmods.common.blocks;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.mechanical.BlockPump;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;


/**
 * @author mrebhan
 */
public class BlockTemporaryWater extends BlockLiquid {

    public BlockTemporaryWater() {
        super(Material.WATER);
        this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 4));
        this.setTickRandomly(true);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        int level = state.getValue(LEVEL);

        IBlockState stateBelow = worldIn.getBlockState(pos.down());

        if (!(stateBelow.getBlock() == BWMBlocks.PUMP && stateBelow.getValue(BlockPump.ACTIVE) && BlockPump.hasWaterToPump(worldIn, pos.down()))) {
            worldIn.setBlockToAir(pos);
            return;
        }

        if (this.canFlowInto(worldIn, pos.down(), stateBelow)) {
            if (level >= 8) {
                this.tryFlowInto(worldIn, pos.down(), stateBelow, level);
            } else {
                this.tryFlowInto(worldIn, pos.down(), stateBelow, level + 8);
            }
        } else if (level >= 0 && (level == 0 || this.isBlocked(worldIn, pos.down(), stateBelow))) {
            Set<EnumFacing> set = this.getPossibleFlowDirections(worldIn, pos);
            int nextLevel = level + 1;

            if (level >= 8) {
                nextLevel = 1;
            }

            if (nextLevel >= 8) {
                return;
            }

            for (EnumFacing facing : set) {
                this.tryFlowInto(worldIn, pos.offset(facing), worldIn.getBlockState(pos.offset(facing)), nextLevel);
            }
        }

        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }

    private boolean canFlowInto(World worldIn, BlockPos pos, IBlockState state) {
        Material material = state.getMaterial();
        return material != this.material && material != Material.LAVA && !this.isBlocked(worldIn, pos, state);
    }

    private void tryFlowInto(World worldIn, BlockPos pos, IBlockState state, int level) {
        if (this.canFlowInto(worldIn, pos, state)) {
            if (state.getMaterial() != Material.AIR) {
                state.getBlock().dropBlockAsItem(worldIn, pos, state, 0);
            }

            worldIn.setBlockState(pos,
                    Blocks.FLOWING_WATER.getDefaultState().withProperty(LEVEL, level), 3);
        }
    }

    private boolean isBlocked(World worldIn, BlockPos pos, IBlockState state) {
        Block block = worldIn.getBlockState(pos).getBlock();
        return !(!(block instanceof BlockDoor) && block != Blocks.STANDING_SIGN && block != Blocks.LADDER
                && block != Blocks.REEDS) || (!(state.getMaterial() != Material.PORTAL
                && state.getMaterial() != Material.STRUCTURE_VOID) || state.getMaterial().blocksMovement());
    }

    private Set<EnumFacing> getPossibleFlowDirections(World worldIn, BlockPos pos) {
        int i = 1000;
        Set<EnumFacing> set = EnumSet.noneOf(EnumFacing.class);

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            BlockPos blockpos = pos.offset(enumfacing);
            IBlockState iblockstate = worldIn.getBlockState(blockpos);

            if (!this.isBlocked(worldIn, blockpos, iblockstate) && (iblockstate.getMaterial() != this.material
                    || iblockstate.getValue(LEVEL) > 0)) {
                int j;

                if (this.isBlocked(worldIn, blockpos.down(), worldIn.getBlockState(blockpos.down()))) {
                    j = this.getSlopeDistance(worldIn, blockpos, 1, enumfacing.getOpposite());
                } else {
                    j = 0;
                }

                if (j < i) {
                    set.clear();
                }

                if (j <= i) {
                    set.add(enumfacing);
                    i = j;
                }
            }
        }

        return set;
    }

    private int getSlopeDistance(World worldIn, BlockPos pos, int distance, EnumFacing calculateFlowCost) {
        int i = 1000;

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            if (enumfacing != calculateFlowCost) {
                BlockPos blockpos = pos.offset(enumfacing);
                IBlockState iblockstate = worldIn.getBlockState(blockpos);

                if (!this.isBlocked(worldIn, blockpos, iblockstate) && (iblockstate.getMaterial() != this.material
                        || iblockstate.getValue(LEVEL) > 0)) {
                    if (!this.isBlocked(worldIn, blockpos.down(), iblockstate)) {
                        return distance;
                    }

                    if (distance < 4) {
                        int j = this.getSlopeDistance(worldIn, blockpos, distance + 1, enumfacing.getOpposite());

                        if (j < i) {
                            i = j;
                        }
                    }
                }
            }
        }

        return i;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (!this.checkForMixing(worldIn, pos, state)) {
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
        }
    }

}
