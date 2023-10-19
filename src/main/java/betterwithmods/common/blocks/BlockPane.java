package betterwithmods.common.blocks;

import betterwithmods.util.DirUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by primetoxinz on 6/27/17.
 */
public class BlockPane extends BWMBlock {
    private final Map<PropertyBool, AxisAlignedBB> bounds = new HashMap<PropertyBool, AxisAlignedBB>() {
        private static final long serialVersionUID = -8296162462257114399L;

        {
        put(DirUtils.NORTH, new AxisAlignedBB(0.4375F, 0.0F, 0.0F, 0.5625F, 1.0F, 0.5625F));
        put(DirUtils.SOUTH, new AxisAlignedBB(0.4375F, 0.0F, 0.4375F, 0.5625F, 1.0F, 1.0F));
        put(DirUtils.WEST, new AxisAlignedBB(0.0F, 0.0F, 0.4375F, 0.5625F, 1.0F, 0.5625F));
        put(DirUtils.EAST, new AxisAlignedBB(0.4375F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F));
        }
    };

    public BlockPane(Material material) {
        super(material);
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
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entity, boolean pass) {
        state = state.getActualState(world, pos);

        float minY = 0.001F;
        float maxY = 0.999F;
        float minX = 0.4375F;
        float maxX = 0.5625F;
        float minZ = 0.4375F;
        float maxZ = 0.5625F;
        if (state.getValue(DirUtils.NORTH))
            minZ = 0.0F;
        if (state.getValue(DirUtils.SOUTH))
            maxZ = 1.0F;
        if (state.getValue(DirUtils.WEST))
            minX = 0.0F;
        if (state.getValue(DirUtils.EAST))
            maxX = 1.0F;

        AxisAlignedBB stick = new AxisAlignedBB(0.5F - 0.0625F, 0.0F, 0.5F - 0.0625F, 0.5625F, 1.0F, 0.5625F);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, stick);

        if (minZ == 0.0F || maxZ == 1.0F) {
            AxisAlignedBB extZ = new AxisAlignedBB(0.4375F, minY, minZ, 0.5625F, maxY, maxZ);
            addCollisionBoxToList(pos, entityBox, collidingBoxes, extZ);
        }

        if (minX == 0.0F || maxX == 1.0F) {
            AxisAlignedBB extX = new AxisAlignedBB(minX, minY, 0.4375F, maxX, maxY, 0.5625F);
            addCollisionBoxToList(pos, entityBox, collidingBoxes, extX);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        state = state.getActualState(world, pos);
        AxisAlignedBB bound = new AxisAlignedBB(0.4375F, 0.0F, 0.4375F, 0.5625F, 1.0F, 0.5625F);
        for (PropertyBool dir : DirUtils.DIR_PROP_HORIZ)
            if (state.getValue(dir))
                bound = bound.union(bounds.get(dir));
        return bound;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    public final boolean isIncompatibleBlock(Block block) {
        return false;
    }

    public final boolean isFenceGate(IBlockAccess world, BlockPos pos, EnumFacing dir) {
        IBlockState state = world.getBlockState(pos.offset(dir));
        if (dir == EnumFacing.EAST || dir == EnumFacing.WEST)
            return state.getBlock() instanceof BlockFenceGate && (state.getValue(BlockHorizontal.FACING) == EnumFacing.NORTH || state.getValue(BlockHorizontal.FACING) == EnumFacing.SOUTH);
        else
            return state.getBlock() instanceof BlockFenceGate && (state.getValue(BlockHorizontal.FACING) == EnumFacing.EAST || state.getValue(BlockHorizontal.FACING) == EnumFacing.WEST);
    }

    public boolean isCompatiblePane(IBlockAccess world, BlockPos pos, EnumFacing dir) {
        BlockPos neighbor = pos.offset(dir);
        Block block = world.getBlockState(neighbor).getBlock();
        return block instanceof BlockPane;
    }

    public boolean canConnectTo(IBlockAccess world, BlockPos pos, EnumFacing dir) {
        return isFenceGate(world, pos, dir) || isCompatiblePane(world, pos, dir) || world.isSideSolid(pos.offset(dir),dir.getOpposite(),true);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        boolean north = canConnectTo(world, pos, EnumFacing.NORTH);
        boolean east = canConnectTo(world, pos, EnumFacing.EAST);
        boolean south = canConnectTo(world, pos, EnumFacing.SOUTH);
        boolean west = canConnectTo(world, pos, EnumFacing.WEST);
        return state.withProperty(DirUtils.NORTH, north).withProperty(DirUtils.EAST, east).withProperty(DirUtils.SOUTH, south).withProperty(DirUtils.WEST, west);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face != EnumFacing.UP && face != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE_THIN : BlockFaceShape.CENTER_SMALL;
    }
}
