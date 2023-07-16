package betterwithmods.common.blocks;

import betterwithmods.api.tile.IRopeConnector;
import betterwithmods.common.blocks.mechanical.tile.TileEntityPulley;
import betterwithmods.common.blocks.tile.TileEntityBucket;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockBucket extends BWMBlock implements IRopeConnector {

    public static final PropertyBool CONNECTED = PropertyBool.create("connected");
    public static final PropertyBool IN_WATER = PropertyBool.create("in_water");
    public static final PropertyBool HAS_WATER = PropertyBool.create("has_water");

    public BlockBucket() {
        super(Material.IRON);
        setDefaultState(getDefaultState().withProperty(CONNECTED, true).withProperty(IN_WATER, false).withProperty(HAS_WATER, false));
        setHardness(5.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, IN_WATER, HAS_WATER, CONNECTED);
    }

    @Override
    public EnumFacing getFacing(IBlockState state) {
        return EnumFacing.UP;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, facing);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityBucket();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        IBlockState above = worldIn.getBlockState(pos.up());
        if (above.getBlock() instanceof BlockRope || worldIn.getTileEntity(pos.up()) instanceof TileEntityPulley) {
            return getDefaultState().withProperty(CONNECTED, true);
        }
        return super.getActualState(state, worldIn, pos).withProperty(CONNECTED, false);
    }

    @Override
    public boolean canMovePlatforms(World world, BlockPos pos) {
        return false;
    }

    @Override
    public void onLand(World world, BlockPos pos, IBlockState previousState) {
        TileEntityBucket bucket = (TileEntityBucket) world.getTileEntity(pos);
        if(bucket != null) {
            if(bucket.isWater(previousState))
                bucket.fill(new FluidStack(FluidRegistry.WATER, 1000), true);
            bucket.fillFromSurrounding();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
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
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
}
