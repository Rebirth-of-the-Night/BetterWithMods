package betterwithmods.common.blocks;

import betterwithmods.api.block.IMultiVariants;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockWoodTable extends BlockFurniture implements IMultiVariants {
    public BlockWoodTable() {
        super(Material.WOOD);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (state.getBlock() instanceof BlockWoodTable) {
            BlockWoodTable block = (BlockWoodTable) state.getBlock();
            state = block.getActualState(state, source, pos);
            if (state.getValue(SUPPORTED))
                return TABLE_AABB;
        }
        return FULL_BLOCK_AABB;
    }

    @Override
    public String[] getVariants() {
        ArrayList<String> variants = new ArrayList<>();
        for (BlockPlanks.EnumType blockplanks$enumtype : BlockPlanks.EnumType.values()) {
            variants.add("supported=false,variant=" + blockplanks$enumtype.getName());
        }
        return variants.toArray(new String[variants.size()]);
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean pass) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, TABLE_AABB);
        if (state.getBlock() instanceof BlockWoodTable) {
            BlockWoodTable block = (BlockWoodTable) state.getBlock();
            state = block.getActualState(state, world, pos);
            if (!state.getValue(SUPPORTED))
                addCollisionBoxToList(pos, entityBox, collidingBoxes, TABLE_STAND_AABB);
        }
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.UP;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(BlockPlanks.VARIANT).getMetadata();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BlockPlanks.VARIANT).getMetadata();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.byMetadata(meta));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, SUPPORTED, BlockPlanks.VARIANT);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.DOWN ? BlockFaceShape.CENTER : (face == EnumFacing.UP ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED);
    }

    @Override
    public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        return world.getBlockState(pos.offset(facing)).getBlock() instanceof BlockWoodTable;
    }
}
