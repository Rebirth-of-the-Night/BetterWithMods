package betterwithmods.common.blocks.mechanical;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.BWMBlock;
import betterwithmods.common.blocks.EnumTier;
import betterwithmods.util.DirUtils;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static betterwithmods.util.DirUtils.AXIS;
import static net.minecraft.util.EnumFacing.Axis.Y;

public abstract class BlockAxleGenerator extends BWMBlock implements IBlockActive {

    private static final AxisAlignedBB X_AABB = new AxisAlignedBB(0.0F, 0.375F, 0.375F, 1.0F, 0.625F, 0.625F);
    private static final AxisAlignedBB Y_AABB = new AxisAlignedBB(0.375F, 0.0F, 0.375F, 0.625F, 1.0F, 0.625F);
    private static final AxisAlignedBB Z_AABB = new AxisAlignedBB(0.375F, 0.375F, 0.0F, 0.625F, 0.625F, 1.0F);

    public BlockAxleGenerator(Material material) {
        super(material);
        this.setSoundType(SoundType.WOOD);
        this.setHardness(2.0F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Z).withProperty(EnumTier.TIER, EnumTier.WOOD).withProperty(ACTIVE, false));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS, EnumTier.TIER, ACTIVE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int axis = state.getValue(AXIS).ordinal();
        int tier = state.getValue(EnumTier.TIER).ordinal();
        int active = state.getValue(ACTIVE) ? 1 : 0;
        return axis | tier << 2 | active << 3;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(AXIS, DirUtils.getAxis(meta & 3)).withProperty(EnumTier.TIER, EnumTier.VALUES[meta >> 2 & 1]).withProperty(ACTIVE,(meta >> 3)==1);
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
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(AXIS)) {
            case X:
                return X_AABB;
            case Y:
                return Y_AABB;
            case Z:
            default:
                return Z_AABB;
        }
    }

    @Override
    public int tickRate(World world) {
        return 20;
    }

    @Override
    public abstract ItemStack getItem(World worldIn, BlockPos pos, IBlockState state);

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        super.getDrops(drops, world, pos, state, fortune);
        if(state.getValue(EnumTier.TIER) == EnumTier.WOOD) {
            drops.add(new ItemStack(BWMBlocks.WOODEN_AXLE));
        } else {
            drops.add(new ItemStack(BWMBlocks.STEEL_AXLE));
        }
        drops.add(getItem((World) world, pos, state));

    }

    public IBlockState getAxisState(EnumFacing.Axis axis) {
        return this.getDefaultState().withProperty(AXIS, axis);
    }

    public EnumFacing.Axis getAxis(IBlockState state) {
        return state.getValue(AXIS);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        if (world.getBlockState(pos).getValue(EnumTier.TIER) == EnumTier.STEEL)
            return 4000f;
        return 0;
    }

    @Override
    public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos).getValue(EnumTier.TIER)  == EnumTier.STEEL)
            return 100f;
        return 3.5f;
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        if (world.getBlockState(pos).getValue(EnumTier.TIER)  == EnumTier.STEEL)
            return SoundType.METAL;
        return SoundType.WOOD;
    }

    @Override
    public Material getMaterial(IBlockState state) {
        if (state.getValue(EnumTier.TIER) == EnumTier.STEEL)
            return Material.IRON;
        return Material.WOOD;
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {
        return getAxis(state) == Y;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face.getAxis() == getAxis(state) ? BlockFaceShape.CENTER : BlockFaceShape.UNDEFINED;
    }
}

