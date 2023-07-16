package betterwithmods.common.blocks.mechanical;

import betterwithmods.util.DirUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockGearBoostedRail extends BlockRailPowered {

    public static final double MOTION_CART = 0.02;
    public static final double MOTION_SCALE = 0.06;

    public BlockGearBoostedRail() {
        super();
        this.setHardness(0.7F);
        this.setSoundType(SoundType.METAL);
    }

    private int getMaxPropogate() {
        return 8;
    }

    @Override
    protected boolean findPoweredRailSignal(World worldIn, BlockPos pos, IBlockState state, boolean direction, int distance) {
        if (distance >= getMaxPropogate())
            return false;

        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        boolean flag = true;
        BlockRailBase.EnumRailDirection shape = state.getValue(SHAPE);

        switch (shape) {
            case NORTH_SOUTH:

                if (direction) {
                    ++k;
                } else {
                    --k;
                }

                break;
            case EAST_WEST:

                if (direction) {
                    --i;
                } else {
                    ++i;
                }

                break;
            case ASCENDING_EAST:

                if (direction) {
                    --i;
                } else {
                    ++i;
                    ++j;
                    flag = false;
                }

                shape = BlockRailBase.EnumRailDirection.EAST_WEST;
                break;
            case ASCENDING_WEST:

                if (direction) {
                    --i;
                    ++j;
                    flag = false;
                } else {
                    ++i;
                }

                shape = BlockRailBase.EnumRailDirection.EAST_WEST;
                break;
            case ASCENDING_NORTH:

                if (direction) {
                    ++k;
                } else {
                    --k;
                    ++j;
                    flag = false;
                }

                shape = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
                break;
            case ASCENDING_SOUTH:

                if (direction) {
                    ++k;
                    ++j;
                    flag = false;
                } else {
                    --k;
                }

                shape = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
                break;
            default:
                break;
        }

        if (this.isSameRailWithPower(worldIn, new BlockPos(i, j, k), direction, distance, shape)) {
            return true;
        } else {
            return flag && this.isSameRailWithPower(worldIn, new BlockPos(i, j - 1, k), direction, distance, shape);
        }
    }


    @Override
    protected boolean isSameRailWithPower(World worldIn, BlockPos pos, boolean direction, int distance, EnumRailDirection shape) {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        if (iblockstate.getBlock() instanceof BlockGearBoostedRail) {
            BlockRailBase.EnumRailDirection enumRailDirection = iblockstate.getValue(SHAPE);
            if (shape != BlockRailBase.EnumRailDirection.EAST_WEST || enumRailDirection != BlockRailBase.EnumRailDirection.NORTH_SOUTH && enumRailDirection != BlockRailBase.EnumRailDirection.ASCENDING_NORTH && enumRailDirection != BlockRailBase.EnumRailDirection.ASCENDING_SOUTH) {
                if (shape != BlockRailBase.EnumRailDirection.NORTH_SOUTH || enumRailDirection != BlockRailBase.EnumRailDirection.EAST_WEST && enumRailDirection != BlockRailBase.EnumRailDirection.ASCENDING_EAST && enumRailDirection != BlockRailBase.EnumRailDirection.ASCENDING_WEST) {
                    if (iblockstate.getValue(POWERED)) {
                        return shouldBeActive(iblockstate, worldIn, pos) || this.findPoweredRailSignal(worldIn, pos, iblockstate, direction, distance + 1);
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void updateState(IBlockState state, World world, BlockPos pos, Block block) {
        boolean poweredProperty = state.getValue(POWERED);
        boolean isPowered = shouldBeActive(state, world, pos) || this.findPoweredRailSignal(world, pos, state, true, 0) || this.findPoweredRailSignal(world, pos, state, false, 0);
        if (poweredProperty != isPowered) {
            world.setBlockState(pos, state.withProperty(POWERED, isPowered), 3);
            world.notifyNeighborsOfStateChange(pos.down(), this, false);
            if (state.getValue(SHAPE).isAscending())
                world.notifyNeighborsOfStateChange(pos.up(), this, false);
        }
    }

    private boolean shouldBeActive(IBlockState state, World world, BlockPos pos) {
        if (!(world.getBlockState(pos.down()).getBlock() instanceof BlockGearbox)) return false;
        EnumRailDirection dir = state.getValue(SHAPE);
        IBlockState below = world.getBlockState(pos.down());
        EnumFacing face = below.getValue(DirUtils.FACING);
        boolean correctFace = false;
        if (dir == EnumRailDirection.ASCENDING_EAST || dir == EnumRailDirection.ASCENDING_WEST || dir == EnumRailDirection.EAST_WEST) {
            correctFace = face == EnumFacing.DOWN || face == EnumFacing.NORTH || face == EnumFacing.SOUTH;
        } else if (dir == EnumRailDirection.ASCENDING_NORTH || dir == EnumRailDirection.ASCENDING_SOUTH || dir == EnumRailDirection.NORTH_SOUTH) {
            correctFace = face == EnumFacing.DOWN || face == EnumFacing.EAST || face == EnumFacing.WEST;
        }
        return correctFace && ((BlockGearbox) below.getBlock()).isActive(below);
    }

    @Override
    public boolean isFlexibleRail(IBlockAccess world, BlockPos pos) {
        return false;
    }

    public void deccelerateCart(EntityMinecart cart, EnumRailDirection shape, BlockPos pos) {
        double d17 = Math.sqrt(cart.motionX * cart.motionX + cart.motionZ * cart.motionZ);

        if (d17 < 0.03D) {
            cart.motionX *= 0.0D;
            cart.motionY *= 0.0D;
            cart.motionZ *= 0.0D;
        } else {
            cart.motionX *= 0.5D;
            cart.motionY *= 0.0D;
            cart.motionZ *= 0.5D;
        }
    }

    public void accelerateCart(EntityMinecart cart, EnumRailDirection shape, BlockPos pos) {
        double d15 = Math.sqrt(cart.motionX * cart.motionX + cart.motionZ * cart.motionZ);
        if (d15 > 0.01D) {
            cart.motionX += cart.motionX / d15 * MOTION_SCALE;
            cart.motionZ += cart.motionZ / d15 * MOTION_SCALE;
        } else if (shape == BlockRailBase.EnumRailDirection.EAST_WEST) {
            if (cart.world.getBlockState(pos.west()).isNormalCube()) {
                cart.motionX = MOTION_CART;
            } else if (cart.world.getBlockState(pos.east()).isNormalCube()) {
                cart.motionX = -MOTION_CART;
            }
        } else if (shape == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
            if (cart.world.getBlockState(pos.north()).isNormalCube()) {
                cart.motionZ = MOTION_CART;
            } else if (cart.world.getBlockState(pos.south()).isNormalCube()) {
                cart.motionZ = -MOTION_CART;
            }
        }
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        if (!world.isBlockPowered(pos.down())) {
            EnumRailDirection shape = state.getValue(SHAPE);
            if (state.getValue(POWERED)) {
                accelerateCart(cart, shape, pos);
            } else {
                deccelerateCart(cart, shape, pos);
            }
        }
    }
}
