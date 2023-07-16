package betterwithmods.common.blocks;

import betterwithmods.client.BWCreativeTabs;
import betterwithmods.common.BWMItems;
import betterwithmods.common.items.ItemMaterial;
import net.minecraft.block.BlockClay;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Random;

public class BlockNetherClay extends BlockClay {
    public BlockNetherClay() {
        this.setCreativeTab(BWCreativeTabs.BWTAB);
        this.setHardness(0.6F);
        this.setSoundType(SoundType.GROUND);
        this.setTranslationKey("bwm:nether_clay");
    }

    @Override
    public int damageDropped(IBlockState state) {
        return ItemMaterial.EnumMaterial.NETHER_SLUDGE.ordinal();
    }

    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return MapColor.NETHERRACK;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return BWMItems.MATERIAL;
    }
}
