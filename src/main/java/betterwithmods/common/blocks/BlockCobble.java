package betterwithmods.common.blocks;

import betterwithmods.api.block.IMultiVariants;
import betterwithmods.client.BWCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockCobble extends Block implements IMultiVariants {
    public static final PropertyEnum<EnumCobbleType> TYPE = PropertyEnum.create("type", EnumCobbleType.class);

    public BlockCobble() {
        super(Material.ROCK);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, EnumCobbleType.GRANITE));
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        this.setCreativeTab(BWCreativeTabs.BWTAB);
        this.setTranslationKey("bwm:cobble");
    }

    @Override
    public String[] getVariants() {
        return new String[] {"type=granite", "type=diorite", "type=andesite"};
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(TYPE).ordinal();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (EnumCobbleType type : EnumCobbleType.values())
            items.add(new ItemStack(this, 1, type.ordinal()));
    }

    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state.getValue(TYPE).getColor();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, EnumCobbleType.byType(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    public static enum EnumCobbleType implements IStringSerializable {
        GRANITE("granite", MapColor.DIRT),
        DIORITE("diorite", MapColor.QUARTZ),
        ANDESITE("andesite", MapColor.STONE);

        private String name;
        private MapColor color;

        private static final EnumCobbleType[] TYPES = values();

        private EnumCobbleType(String name, MapColor color) {
            this.name = name;
            this.color = color;
        }

        @Override
        public String getName() {
            return name;
        }

        public MapColor getColor() {
            return color;
        }

        public static EnumCobbleType byType(int meta) {
            if (meta < 0 || meta > 2)
                meta = 0;
            return TYPES[meta];
        }
    }
}
