package betterwithmods.common.blocks;

import betterwithmods.common.blocks.tile.TileEntityBeacon;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by primetoxinz on 7/17/17.
 */
public class BlockBeacon extends net.minecraft.block.BlockBeacon {

    public BlockBeacon() {
        super();
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        setTranslationKey("beacon");
        setLightLevel(1.0f);
    }

    public TileEntityBeacon getTile(IBlockAccess world, BlockPos pos) {
        return ((TileEntityBeacon) world.getTileEntity(pos));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        getTile(worldIn, pos).processInteraction(worldIn, playerIn, playerIn.getHeldItemMainhand());
        return true;
    }

    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        return;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        getTile(worldIn, pos).onRemoved();
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityBeacon();
    }
}
