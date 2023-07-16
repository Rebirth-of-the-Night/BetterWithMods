package betterwithmods.common.blocks.mechanical;

import betterwithmods.common.BWMItems;
import betterwithmods.common.blocks.mechanical.tile.TileEntityWaterwheel;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockWaterwheel extends BlockAxleGenerator {

    public BlockWaterwheel() {
        super(Material.WOOD);
    }

    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        return new ItemStack(BWMItems.AXLE_GENERATOR, 1, 1);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityWaterwheel();
    }

}
