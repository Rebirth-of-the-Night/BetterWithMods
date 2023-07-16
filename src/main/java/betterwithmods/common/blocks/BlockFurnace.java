package betterwithmods.common.blocks;

import betterwithmods.common.blocks.tile.TileFurnace;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockFurnace extends net.minecraft.block.BlockFurnace {
    public BlockFurnace(boolean isBurning) {
        super(isBurning);
        if (isBurning) {
            setLightLevel(0.875F);
        }
        setTranslationKey("furnace");

        setHardness(3.5F);
        setSoundType(SoundType.STONE);
        setCreativeTab(CreativeTabs.DECORATIONS);
    }

    static Item furnace;

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return furnace == null ? furnace = Item.getItemFromBlock(Blocks.FURNACE) : furnace;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState oldState) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() != Blocks.FURNACE && state.getBlock() != Blocks.LIT_FURNACE) {
            TileEntity tileentity = world.getTileEntity(pos);
            if (tileentity instanceof TileFurnace) {
                InventoryHelper.dropInventoryItems(world, pos, (TileFurnace) tileentity);
                world.updateComparatorOutputLevel(pos, this);
            }
            world.removeTileEntity(pos);
        }
    }


    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileFurnace();
    }
}


