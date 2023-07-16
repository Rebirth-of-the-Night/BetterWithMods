package betterwithmods.common.blocks.mechanical;

import betterwithmods.common.blocks.EnumTier;
import betterwithmods.common.blocks.mechanical.tile.TileMerger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockMerger extends BlockGearbox {

    public BlockMerger() {
        super(50, EnumTier.STEEL);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileMerger(50);
    }
}