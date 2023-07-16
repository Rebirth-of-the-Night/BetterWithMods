package betterwithmods.common.blocks;

import betterwithmods.client.BWCreativeTabs;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public abstract class BlockFurniture extends BWMBlock {
    public static final PropertyBool SUPPORTED = PropertyBool.create("supported");
    protected static final AxisAlignedBB BENCH_AABB = new AxisAlignedBB(0.0D, 0.375D, 0.0D, 1.0D, 0.5D, 1.0D);
    protected static final AxisAlignedBB BENCH_STAND_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.375D, 0.625D);
    protected static final AxisAlignedBB TABLE_AABB = new AxisAlignedBB(0.0D, 0.875D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB TABLE_STAND_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.875D, 0.625D);
    protected static final AxisAlignedBB HALF_BLOCK_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);

    public BlockFurniture(Material material) {
        super(material);
        this.setHardness(3.5F);
        this.setCreativeTab(BWCreativeTabs.BWTAB);
    }


    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (int i = 0; i < 6; i++) {
            ItemStack stack = new ItemStack(this, 1, i);
            list.add(stack);
        }
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        boolean connected = isConnected(state, world, pos, EnumFacing.NORTH) || isConnected(state, world, pos, EnumFacing.WEST);
        return state.withProperty(SUPPORTED, connected);
    }

    private boolean isConnected(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing facing) {
        EnumFacing opp = facing.getOpposite();
        IBlockState state1 = world.getBlockState(pos.offset(facing));
        IBlockState state2 = world.getBlockState(pos.offset(opp));
        return state1.getBlock().canBeConnectedTo(world,pos.offset(facing),opp) && state2.getBlock().canBeConnectedTo(world,pos.offset(opp),facing);
    }
}
