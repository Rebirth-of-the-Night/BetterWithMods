package betterwithmods.common.blocks.mechanical;

import betterwithmods.api.block.IMultiVariants;
import betterwithmods.api.block.IOverpower;
import betterwithmods.client.BWCreativeTabs;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.BlockRotate;
import betterwithmods.common.blocks.mechanical.tile.TileEntityWaterwheel;
import betterwithmods.common.blocks.mechanical.tile.TilePump;
import betterwithmods.util.DirUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

/**
 * @author mrebhan
 */

public class BlockPump extends BlockRotate implements IBlockActive, IMultiVariants, IOverpower {

    public BlockPump() {
        super(Material.WOOD);
        this.setCreativeTab(BWCreativeTabs.BWTAB);
        this.setTickRandomly(true);
        this.setHardness(3.5F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(DirUtils.HORIZONTAL, EnumFacing.NORTH).withProperty(ACTIVE, false));
        setSoundType(SoundType.WOOD);
    }

    public static boolean hasWaterToPump(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return TileEntityWaterwheel.isWater(world, pos.offset(state.getValue(DirUtils.HORIZONTAL)));
    }

    @Override
    public String[] getVariants() {
        return new String[]{"active=false,facing=north"};
    }

    @Override
    public int tickRate(World world) {
        return 5;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float flX, float flY, float flZ,
                                            int meta, EntityLivingBase entity, EnumHand hand) {
        IBlockState state = super.getStateForPlacement(world, pos, side, flX, flY, flZ, meta, entity, hand);
        return setFacingInBlock(state, entity.getHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity,
                                ItemStack stack) {
        EnumFacing facing = DirUtils.convertEntityOrientationToFlatFacing(entity, EnumFacing.NORTH);
        setFacingInBlock(state, facing);
    }

    public IBlockState setFacingInBlock(IBlockState state, EnumFacing facing) {
        return state.withProperty(DirUtils.HORIZONTAL, facing);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        boolean isActive = false;
        if (meta > 7) {
            isActive = true;
            meta -= 8;
        }
        return this.getDefaultState().withProperty(ACTIVE, isActive).withProperty(DirUtils.HORIZONTAL,
                EnumFacing.byHorizontalIndex(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(ACTIVE) ? 8 : 0;
        return meta + state.getValue(DirUtils.HORIZONTAL).getIndex();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DirUtils.HORIZONTAL, ACTIVE);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        world.scheduleBlockUpdate(pos, this, tickRate(world), 5);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        withTile(worldIn, pos).ifPresent(TilePump::onChanged);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        withTile(world, pos).ifPresent(TilePump::onChanged);
        if (isActive(state)) {
            if (world.isAirBlock(pos.up()) && hasWaterToPump(world, pos)) {
                world.setBlockState(pos.up(), BWMBlocks.TEMP_LIQUID_SOURCE.getDefaultState());
            }
        }
        world.scheduleBlockUpdate(pos, this, tickRate(world), 5);
    }

    @Override
    public void nextState(World world, BlockPos pos, IBlockState state) {
        world.setBlockState(pos, state.withProperty(ACTIVE, false).cycleProperty(DirUtils.HORIZONTAL));
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TilePump();
    }

    @Override
    public void overpower(World world, BlockPos pos) {

    }

    public Optional<TilePump> withTile(World world, BlockPos pos) {
        return Optional.ofNullable(getTile(world, pos));
    }

    public TilePump getTile(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TilePump)
            return (TilePump) tile;
        return null;
    }
}
