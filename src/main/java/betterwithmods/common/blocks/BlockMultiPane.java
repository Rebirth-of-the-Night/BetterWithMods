package betterwithmods.common.blocks;

import betterwithmods.api.block.IMultiVariants;
import betterwithmods.client.BWCreativeTabs;
import betterwithmods.util.DirUtils;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;

public class BlockMultiPane extends BlockPane implements IMultiVariants {
    public static final PropertyEnum<EnumPaneType> TYPES = PropertyEnum.create("type", EnumPaneType.class);

    public BlockMultiPane() {
        super(Material.WOOD);
        this.setCreativeTab(BWCreativeTabs.BWTAB);
        this.setHardness(2.0F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPES, EnumPaneType.OAK).withProperty(DirUtils.NORTH, false).withProperty(DirUtils.EAST, false).withProperty(DirUtils.SOUTH, false).withProperty(DirUtils.WEST, false));
        this.setSoundType(SoundType.WOOD);
    }

    @Override
    public String[] getVariants() {
        String[] var = new String[BlockPlanks.EnumType.values().length];
        for (int i = 0; i < var.length; i++) {
            var[i] = "east=false,north=true,south=true,type=" + BlockPlanks.EnumType.byMetadata(i) + ",west=false";
        }
        return var;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(TYPES).getMeta();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPES).getMeta();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (EnumPaneType type : EnumPaneType.values()) {
            items.add(new ItemStack(this, 1, type.getMeta()));
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPES, EnumPaneType.byMeta(meta));
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPES, DirUtils.SOUTH, DirUtils.EAST, DirUtils.NORTH, DirUtils.WEST);
    }

    public enum EnumPaneType implements IStringSerializable {
        OAK(0, "oak"),
        SPRUCE(1, "spruce"),
        BIRCH(2, "birch"),
        JUNGLE(3, "jungle"),
        ACACIA(4, "acacia"),
        DARK_OAK(5, "dark_oak");

        private static final EnumPaneType[] META_LOOKUP = new EnumPaneType[values().length];

        static {
            for (EnumPaneType type : values()) {
                META_LOOKUP[type.getMeta()] = type;
            }
        }

        private String name;
        private int meta;

        EnumPaneType(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        public static EnumPaneType byMeta(int meta) {
            return META_LOOKUP[meta];
        }

        @Override
        public String getName() {
            return name;
        }

        public int getMeta() {
            return meta;
        }
    }
}
