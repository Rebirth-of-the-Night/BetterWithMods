package betterwithmods.common.blocks;

import betterwithmods.api.block.IMultiVariants;
import betterwithmods.client.BWCreativeTabs;
import betterwithmods.common.BWMBlocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static betterwithmods.common.blocks.BlockAesthetic.EnumType.HELLFIRE;

public class BlockAesthetic extends BWMBlock implements IMultiVariants {
    public static final PropertyEnum<BlockAesthetic.EnumType> TYPE = PropertyEnum.create("blocktype", BlockAesthetic.EnumType.class);

    //Chopping Block, Chopping Block(dirty), Steel block, Hellfire block, Rope block, Flint block, Barrel (tile entity?) (6 - 11 subtypes)
    public BlockAesthetic() {
        super(Material.ROCK);
        this.setCreativeTab(BWCreativeTabs.BWTAB);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BlockAesthetic.EnumType.CHOPBLOCK));
    }

    public static IBlockState getVariant(EnumType type) {
        return BWMBlocks.AESTHETIC.getDefaultState().withProperty(TYPE, type);
    }

    public static ItemStack getStack(EnumType type) {
        return new ItemStack(BWMBlocks.AESTHETIC, 1, type.getMeta());
    }

    public static ItemStack getStack(EnumType type, int amount) {
        return new ItemStack(BWMBlocks.AESTHETIC, amount, type.getMeta());
    }

    @Override
    public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
        return state.getValue(TYPE).getHardness();
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        return state.getValue(TYPE).getSoundType();
    }

    @Override
    public Material getMaterial(IBlockState state) {
        return state.getValue(TYPE).getMaterial();
    }

    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state.getValue(TYPE).getMapColor();
    }

    @Override
    public int getHarvestLevel(IBlockState state) {
        return 1;
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        return world.getBlockState(pos).getValue(TYPE).getResistance();
    }

    @Override
    public boolean canDropFromExplosion(Explosion explosionIn) {
        return false;
    }

    @Override
    public String[] getVariants() {
        return new String[]{"blocktype=chopping", "blocktype=chopping_blood", "blocktype=nether_clay", "blocktype=hellfire", "blocktype=rope", "blocktype=flint", "blocktype=whitestone", "blocktype=whitecobble", "blocktype=enderblock", "blocktype=padding", "blocktype=soap", "blocktype=dung", "blocktype=wicker", "blocktype=nethercoal"};
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (EnumType type : EnumType.VALUES) {
            if(!type.name.equalsIgnoreCase("unused"))
                items.add(getStack(type));
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return (state.getValue(TYPE)).getMeta();
    }

    @Override
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
        return (world.getBlockState(pos).getValue(TYPE)) == HELLFIRE;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, BlockAesthetic.EnumType.byMeta(meta));
    }


    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).getMeta();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    public enum EnumType implements IStringSerializable {
        CHOPBLOCK(0, "chopping", MapColor.STONE),
        CHOPBLOCKBLOOD(1, "chopping_blood", MapColor.NETHERRACK),
        NETHERCLAY(2, "nether_clay", MapColor.NETHERRACK),
        HELLFIRE(3, "hellfire", MapColor.ADOBE),
        ROPE(4, "rope", MapColor.DIRT, Material.CLOTH, SoundType.CLOTH, 1F, 5F),
        FLINT(5, "flint", MapColor.STONE),
        WHITESTONE(6, "whitestone", MapColor.CLOTH),
        WHITECOBBLE(7, "whitecobble", MapColor.CLOTH),
        ENDERBLOCK(8, "enderblock", MapColor.CYAN),
        PADDING(9, "padding", MapColor.CLOTH, Material.CLOTH, SoundType.CLOTH, 1F, 5F),
        SOAP(10, "soap", MapColor.PINK, Material.GROUND, SoundType.GROUND, 1F, 5F),
        DUNG(11, "dung", MapColor.BROWN, Material.GROUND, SoundType.GROUND, 1F, 2F),
        WICKER(12, "wicker", MapColor.BROWN, Material.WOOD, SoundType.WOOD, 1F, 5F),
        NETHERCOAL(13, "nethercoal", MapColor.RED, Material.REDSTONE_LIGHT, SoundType.STONE, 5.0F,10.0F);

        private static final BlockAesthetic.EnumType[] VALUES = values();

        private final int meta;
        private final String name;
        private final MapColor color;
        private final Material material;
        private final SoundType soundType;
        private final float hardness;
        private final float resistance;

        EnumType(int meta, String name, MapColor color) {
            this.meta = meta;
            this.name = name;
            this.color = color;
            this.material = Material.ROCK;
            this.soundType = SoundType.STONE;
            this.hardness = 1.5F;
            this.resistance = 10F;
        }

        EnumType(int meta, String name, MapColor color, Material material, SoundType soundType, float hardness, float resistance) {
            this.meta = meta;
            this.name = name;
            this.color = color;
            this.material = material;
            this.soundType = soundType;
            this.hardness = hardness;
            this.resistance = resistance;
        }


        public static BlockAesthetic.EnumType byMeta(int meta) {
            return VALUES[meta];
        }

        @Override
        public String getName() {
            return name;
        }

        public int getMeta() {
            return meta;
        }

        public MapColor getMapColor() {
            return color;
        }

        public Material getMaterial() {
            return material;
        }

        public SoundType getSoundType() {
            return soundType;
        }

        public float getHardness() {
            return hardness;
        }

        public float getResistance() {
            return resistance;
        }
    }
}
