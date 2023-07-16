package betterwithmods.module.hardcore.crafting;

import betterwithmods.common.BWMBlocks;
import betterwithmods.module.Feature;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nullable;

public class HCHopper extends Feature {

    private static Block HOPPER = new BlockHopper().setRegistryName("minecraft:hopper").setTranslationKey("hopper");


    public HCHopper() {
    }


    @Override
    public String getFeatureDescription() {
        return "Completely disable the abilities of the vanilla Hopper to transfer items, as to make BWM tech the primary form of item transport";
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        BWMBlocks.registerBlock(HOPPER);
    }

    public static class BlockHopper extends net.minecraft.block.BlockHopper {

        @Override
        public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
            return false;
        }

        @Nullable
        @Override
        public TileEntity createTileEntity(World world, IBlockState state) {
            return null;
        }

        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta) {
            return null;
        }
    }


}
