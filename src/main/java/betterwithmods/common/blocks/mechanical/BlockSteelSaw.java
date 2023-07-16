package betterwithmods.common.blocks.mechanical;

import betterwithmods.common.blocks.EnumTier;
import betterwithmods.common.blocks.mechanical.tile.TileAxle;
import betterwithmods.common.blocks.mechanical.tile.TileSteelSaw;
import betterwithmods.util.DirUtils;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Optional;

import static betterwithmods.util.DirUtils.AXIS;

public class BlockSteelSaw extends BlockAxle {

    public BlockSteelSaw() {
        super(EnumTier.STEEL, 1, 3, 5);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileSteelSaw();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(AXIS, facing.getAxis());
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DirUtils.AXIS, ACTIVE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ACTIVE, (meta & 1) == 1).withProperty(DirUtils.AXIS, DirUtils.getAxis(meta >> 1));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int active = state.getValue(ACTIVE) ? 1 : 0;
        int axis = state.getValue(DirUtils.AXIS).ordinal();
        return active | axis << 1;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    public Optional<TileAxle> withTile(World world, BlockPos pos) {
        return Optional.of(getTile(world, pos));
    }

    public TileAxle getTile(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileAxle)
            return (TileAxle) tile;
        return null;
    }

    @Override
    public void overpower(World world, BlockPos pos) {

    }
}
