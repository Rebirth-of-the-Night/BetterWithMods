package betterwithmods.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public abstract class BlockStickBase extends BWMBlock {
    public static final PropertyEnum<Connection> CONNECTION = PropertyEnum.create("connection", Connection.class);
    public static final PropertyBool GROUND = PropertyBool.create("ground");

    public BlockStickBase(Material material) {
        super(material);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CONNECTION, GROUND);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        boolean ground = (meta & 1) == 1;
        int connection = meta >> 1;
        return getDefaultState().withProperty(GROUND, ground).withProperty(CONNECTION, Connection.VALUES[connection]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int connection = state.getValue(CONNECTION).ordinal() << 1;
        int ground = state.getValue(GROUND) ? 1 : 0;
        return ground | connection;
    }

    public abstract IBlockState getConnections(IBlockState state, IBlockAccess world, BlockPos pos);

    public abstract double getHeight(IBlockState state);

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        IBlockState newState = state;
        if (worldIn.getBlockState(pos.down()).isBlockNormalCube()) {
            newState = newState.withProperty(GROUND, true);
        }
        return getConnections(newState, worldIn, pos);
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing face) {
        return face.getAxis() == EnumFacing.Axis.Y ? BlockFaceShape.CENTER_SMALL : BlockFaceShape.UNDEFINED;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        double y = getHeight(state);
        return new AxisAlignedBB(7d / 16d, 0, 7d / 16d, 9d / 16d, y, 9d / 16d);
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        this.checkAndDropBlock(worldIn, pos, state);
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        this.checkAndDropBlock(worldIn, pos, state);
    }

    private void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!this.canPlaceBlockAt(worldIn, pos)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    public enum Connection implements IStringSerializable {
        DISCONNECTED,
        CONNECTED,
        CANDLE,
        SKULL;

        public static Connection[] VALUES = values();

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }
}
