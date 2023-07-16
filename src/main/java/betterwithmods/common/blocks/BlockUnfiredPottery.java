package betterwithmods.common.blocks;

import betterwithmods.api.block.IMultiVariants;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.items.ItemMaterial;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class BlockUnfiredPottery extends BWMBlock implements IMultiVariants {
    public static final PropertyEnum<EnumType> TYPE = PropertyEnum.create("potterytype",
            EnumType.class);
    private static final AxisAlignedBB BLOCK_AABB = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    private static final AxisAlignedBB URN_AABB = new AxisAlignedBB(0.3125D, 0.0F, 0.3125D, 0.6875D, 0.625D, 0.6875D);
    private static final AxisAlignedBB VASE_AABB = new AxisAlignedBB(0.125D, 0, 0.125D, 0.875D, 1.0D, 0.875D);
    private static final AxisAlignedBB BRICK_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.0625D, 0.75D, 0.375D, 0.9375D);

    public BlockUnfiredPottery() {
        super(Material.CLAY);
        this.setSoundType(SoundType.GROUND);
        this.setHardness(0.5F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, EnumType.CRUCIBLE));
    }

    public static ItemStack getStack(EnumType type) {
        return new ItemStack(BWMBlocks.UNFIRED_POTTERY, 1, type.getMeta());
    }

    @Override
    public String[] getVariants() {
        return new String[]{"potterytype=crucible", "potterytype=planter", "potterytype=urn", "potterytype=vase", "potterytype=brick", "potterytype=nether_brick"};
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float flX, float flY, float flZ,
                                            int meta, EntityLivingBase entity, EnumHand hand) {
        IBlockState state = super.getStateForPlacement(world, pos, side, flX, flY, flZ, meta, entity, hand);
        return state.withProperty(TYPE, EnumType.byMeta(meta));
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

    @SuppressWarnings("deprecation")
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        EnumType type = state.getValue(TYPE);
        switch (type) {
            case BRICK:
                return Lists.newArrayList(new ItemStack(Items.CLAY_BALL));
            case NETHER_BRICK:
                return Lists.newArrayList(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.NETHER_SLUDGE));
            default:
                return super.getDrops(world,pos,state,fortune);
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(TYPE).getMeta();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        EnumType type = state.getValue(TYPE);
        switch (type) {
            case CRUCIBLE:
            case PLANTER:
                return BLOCK_AABB;
            case URN:
                return URN_AABB;
            case VASE:
                return VASE_AABB;
            case BRICK:
            case NETHER_BRICK:
                return BRICK_AABB;
            default:
                return NULL_AABB;
        }
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face != EnumFacing.DOWN ? BlockFaceShape.UNDEFINED : BlockFaceShape.CENTER;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return world.isSideSolid(pos.down(), EnumFacing.UP);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos other) {
        if (!world.isSideSolid(pos.down(), EnumFacing.UP)) {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, EnumType.byMeta(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).getMeta();
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    public enum EnumType implements IStringSerializable {
        CRUCIBLE(0, "crucible"),
        PLANTER(1, "planter"),
        URN(2, "urn"),
        VASE(3, "vase"),
        BRICK(4, "brick"),
        NETHER_BRICK(5, "nether_brick");
        private static final EnumType[] VALUES = values();

        private String name;
        private int meta;

        EnumType(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        public static EnumType byMeta(int meta) {
            return VALUES[meta];
        }

        public int getMeta() {
            return meta;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
