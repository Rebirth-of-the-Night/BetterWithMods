package betterwithmods.common.blocks;

import betterwithmods.BWMod;
import betterwithmods.client.BWCreativeTabs;
import betterwithmods.common.blocks.tile.TileEntitySteelAnvil;
import betterwithmods.util.DirUtils;
import betterwithmods.util.InvUtils;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by blueyu2 on 11/21/16.
 */
public class BlockSteelAnvil extends BlockContainer {

    public BlockSteelAnvil() {
        super(Material.IRON);
        this.setCreativeTab(BWCreativeTabs.BWTAB);
        this.setHardness(5.0F);
        this.setResistance(2000.0F);
        this.setHarvestLevel("pickaxe", 1);
        this.setSoundType(SoundType.ANVIL);
        this.setDefaultState(this.blockState.getBaseState().withProperty(DirUtils.HORIZONTAL, EnumFacing.NORTH));
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
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
        if (facing.ordinal() < 2)
            facing = DirUtils.convertEntityOrientationToFlatFacing(placer, facing);
        return state.withProperty(DirUtils.HORIZONTAL, facing.rotateY());
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        switch (state.getValue(DirUtils.HORIZONTAL)) {
            case NORTH:
            case SOUTH:
                return new AxisAlignedBB(0.25F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F);
            case EAST:
            case WEST:
                return new AxisAlignedBB(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 0.75F);
            default:
                return new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(DirUtils.HORIZONTAL).getHorizontalIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(DirUtils.HORIZONTAL, EnumFacing.byHorizontalIndex(meta));
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DirUtils.HORIZONTAL);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote)
            return true;
        else
            playerIn.openGui(BWMod.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntitySteelAnvil();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntitySteelAnvil();
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntitySteelAnvil anvil = (TileEntitySteelAnvil) worldIn.getTileEntity(pos);
        InvUtils.ejectInventoryContents(worldIn,pos,anvil.inventory);
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face.getAxis() == EnumFacing.Axis.Y ? BlockFaceShape.CENTER_BIG : BlockFaceShape.UNDEFINED;
    }
}
