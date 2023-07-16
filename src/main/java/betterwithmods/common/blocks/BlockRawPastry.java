package betterwithmods.common.blocks;

import betterwithmods.api.IMultiLocations;
import betterwithmods.client.BWCreativeTabs;
import betterwithmods.common.BWMBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;

/**
 * Created by blueyu2 on 11/19/16.
 */
public class BlockRawPastry extends Block implements IMultiLocations {
    public static final PropertyEnum<BlockRawPastry.EnumType> VARIANT = PropertyEnum.create("variant", BlockRawPastry.EnumType.class);

    public BlockRawPastry() {
        super(Material.CAKE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumType.CAKE));
        this.setHardness(0.1F);
        this.setSoundType(SoundType.CLOTH);
        this.setCreativeTab(BWCreativeTabs.BWTAB);
    }

    public static ItemStack getStack(EnumType type) {
        return new ItemStack(BWMBlocks.RAW_PASTRY, 1, type.getMetadata());
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (EnumType type : EnumType.VALUES) {
            items.add(getStack(type));
        }
    }


    @Override
    public EnumPushReaction getPushReaction(IBlockState state) {
        return EnumPushReaction.NORMAL;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!this.canBlockStay(worldIn, pos)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(VARIANT).getAABB();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta));
    }

    private boolean canBlockStay(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos, EnumFacing.UP);
    }

    @Override
    public String[] getLocations() {
        ArrayList<String> variants = new ArrayList<>();
        for (EnumType variant : EnumType.values()) {
            variants.add(variant.getName());
        }
        return variants.toArray(new String[variants.size()]);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face != EnumFacing.DOWN ? BlockFaceShape.UNDEFINED : BlockFaceShape.CENTER;
    }

    public enum EnumType implements IStringSerializable {
        CAKE(0, "raw_cake", new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.5D, 0.9375D)),
        PUMPKIN(1, "raw_pumpkin_pie", new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.5D, 0.9375D)),
        COOKIE(2, "raw_cookie", new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.0625D, 0.8125D)),
        BREAD(3, "raw_flour", new AxisAlignedBB(0.25D, 0.0D, 0.0625D, 0.75D, 0.375D, 0.9375D)),
        APPLE(4, "raw_apple_pie", new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.5D, 0.9375D));

        private static final BlockRawPastry.EnumType[] VALUES = values();

        private final int meta;
        private final String name;
        private final AxisAlignedBB aabb;

        EnumType(int metaIn, String nameIn, AxisAlignedBB aabbIn) {
            this.meta = metaIn;
            this.name = nameIn;
            this.aabb = aabbIn;
        }

        public static BlockRawPastry.EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= VALUES.length) {
                meta = 0;
            }

            return VALUES[meta];
        }

        public int getMetadata() {
            return this.meta;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public AxisAlignedBB getAABB() {
            return this.aabb;
        }

        public String toString() {
            return this.name;
        }
    }
}
