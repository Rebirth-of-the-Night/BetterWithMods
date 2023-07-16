package betterwithmods.common.blocks;

import betterwithmods.util.DirUtils;
import betterwithmods.util.InvUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStakeString extends BWMBlock {
    public BlockStakeString() {
        super(Material.CLOTH);
        setDefaultState(getDefaultState().withProperty(DirUtils.NORTH, false).withProperty(DirUtils.SOUTH, false).withProperty(DirUtils.WEST, false).withProperty(DirUtils.EAST, false).withProperty(DirUtils.UP, false).withProperty(DirUtils.DOWN, false));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        state = getActualState(state, source, pos);
        AxisAlignedBB box = new AxisAlignedBB(7 / 16d, 7 / 16d, 7 / 16d, 9 / 16d, 9 / 16d, 9 / 16d);
        if (state.getValue(DirUtils.EAST))
            box = box.union(new AxisAlignedBB(0, 7 / 16d, 7 / 16d, 0.5, 9 / 16d, 9 / 16d));
        if (state.getValue(DirUtils.WEST))
            box = box.union(new AxisAlignedBB(0.5, 7 / 16d, 7 / 16d, 1, 9 / 16d, 9 / 16d));
        if (state.getValue(DirUtils.NORTH))
            box = box.union(new AxisAlignedBB(7 / 16d, 7 / 16d, 0, 9 / 16d, 9 / 16d, 0.5));
        if (state.getValue(DirUtils.SOUTH))
            box = box.union(new AxisAlignedBB(7 / 16d, 7 / 16d, 0.5, 9 / 16d, 9 / 16d, 1));
        return box;
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }


    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DirUtils.UP, DirUtils.DOWN, DirUtils.NORTH, DirUtils.SOUTH, DirUtils.WEST, DirUtils.EAST);
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(Items.STRING);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.add(getItem((World) world, pos, state));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        IBlockState newState = state;

        for (int i = 0; i < EnumFacing.VALUES.length; i++) {
            newState = newState.withProperty(DirUtils.DIR_PROP[i], getDirection(worldIn, pos, EnumFacing.byIndex(i)));
        }
        return newState;
    }

    public static boolean getDirection(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        Block block = world.getBlockState(pos.offset(facing)).getBlock();
        return block instanceof BlockStakeString || block instanceof BlockStake;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    public void drop(World world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() instanceof BlockStakeString) {
            InvUtils.ejectStackWithOffset(world, pos, getItem(world, pos, world.getBlockState(pos)));
            world.setBlockToAir(pos);
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        state = state.getActualState(worldIn,pos);
        for (int i = 0; i < DirUtils.DIR_PROP.length; i++) {
            if (state.getValue(DirUtils.DIR_PROP[i])) {
                drop(worldIn, pos.offset(EnumFacing.byIndex(i)));
            }
        }
        super.breakBlock(worldIn, pos, state);
    }


    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

}
