package betterwithaddons.block;

import betterwithaddons.lib.IRedstoneSensitive;
import betterwithaddons.tileentity.TileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockContainerBase extends BlockBase
{
    protected BlockContainerBase(String name, Material materialIn)
    {
        super(name, materialIn);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        TileEntity te = worldIn.getTileEntity(pos);

        if (te instanceof IRedstoneSensitive && te instanceof TileEntityBase)
        {
            ((TileEntityBase)te).neighborChanged(state,worldIn,pos,blockIn);
        }
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public abstract TileEntity createTileEntity(World world, IBlockState state);

    @SuppressWarnings("deprecation")
    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param)
    {
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
    }
}