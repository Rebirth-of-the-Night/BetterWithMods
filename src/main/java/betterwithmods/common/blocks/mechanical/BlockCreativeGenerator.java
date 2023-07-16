package betterwithmods.common.blocks.mechanical;

import betterwithmods.client.BWCreativeTabs;
import betterwithmods.common.blocks.BWMBlock;
import betterwithmods.common.blocks.mechanical.tile.TileEntityCreativeGen;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by primetoxinz on 8/5/16.
 */
public class BlockCreativeGenerator extends BWMBlock  {
    public BlockCreativeGenerator() {
        super(Material.WOOD);
        setCreativeTab(BWCreativeTabs.BWTAB);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityCreativeGen();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

}
