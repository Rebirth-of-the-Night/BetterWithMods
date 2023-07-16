package betterwithmods.common.blocks.mechanical;

import betterwithmods.BWMod;
import betterwithmods.api.block.IMultiVariants;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.BWMBlock;
import betterwithmods.common.blocks.mechanical.tile.TileEntityCauldron;
import betterwithmods.common.blocks.mechanical.tile.TileEntityCookingPot;
import betterwithmods.common.blocks.mechanical.tile.TileEntityCrucible;
import betterwithmods.common.blocks.tile.TileEntityDragonVessel;
import betterwithmods.util.DirUtils;
import betterwithmods.util.InvUtils;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

/**
 * Purpose:
 *
 * @author primetoxinz
 * @version 3/3/17
 */
public class BlockCookingPot extends BWMBlock implements IMultiVariants {
    public static final PropertyEnum<EnumType> TYPE = PropertyEnum.create("type", EnumType.class);

    public BlockCookingPot() {
        super(Material.ROCK);
        this.setHardness(3.5F);
    }

    public static ItemStack getStack(EnumType type) {
        return new ItemStack(BWMBlocks.COOKING_POTS, 1, type.getMeta());
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(TYPE).getMeta();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (EnumType type : EnumType.VALUES)
            items.add(getStack(type));
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        switch (state.getValue(TYPE)) {
            default:
                return SoundType.STONE;
            case CRUCIBLE:
                return SoundType.GLASS;
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE, DirUtils.TILTING);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).getMeta();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, EnumType.byMeta(meta));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (player.isSneaking())
            return false;
        if (world.isRemote) {
            return true;
        } else {
            if (world.getTileEntity(pos) != null && world.getTileEntity(pos).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
                player.openGui(BWMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
            }
            return true;
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        switch (state.getValue(TYPE)) {
            case CRUCIBLE:
                return new TileEntityCrucible();
            case CAULDRON:
                return new TileEntityCauldron();
            case DRAGONVESSEL:
                return new TileEntityDragonVessel();
            default:
                return super.createTileEntity(world, state);
        }
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        return true;
    }

    @Override
    public String[] getVariants() {
        return new String[]{
                "facing=up,type=crucible",
                "facing=up,type=cauldron",
                "facing=up,type=dragonvessel",
        };
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        switch (blockState.getValue(TYPE)) {
            case CRUCIBLE:
            case CAULDRON:
                TileEntityCookingPot tile = (TileEntityCookingPot) worldIn.getTileEntity(pos);
                if (tile != null)
                    return InvUtils.calculateComparatorLevel(tile.inventory);
            case DRAGONVESSEL:
                TileEntityDragonVessel vessel = (TileEntityDragonVessel) worldIn.getTileEntity(pos);
                if (vessel != null)
                    return (int) (((double) vessel.getExperience() / (double) vessel.getMaxExperience()) * 15);
        }
        return super.getComparatorInputOverride(blockState, worldIn, pos);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == state.getValue(DirUtils.TILTING) ? BlockFaceShape.BOWL : BlockFaceShape.CENTER_BIG;
    }

    public enum EnumType implements IStringSerializable {
        CRUCIBLE(0, "crucible"),
        CAULDRON(1, "cauldron"),
        DRAGONVESSEL(2, "dragonvessel");

        private static final EnumType[] VALUES = values();

        private int meta;
        private String name;

        EnumType(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        public static EnumType byMeta(int meta) {
            return VALUES[meta % VALUES.length];
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
