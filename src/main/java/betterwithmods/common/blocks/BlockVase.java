package betterwithmods.common.blocks;

import betterwithmods.api.block.IMultiVariants;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.tile.TileEntityVase;
import betterwithmods.util.ColorUtils;
import betterwithmods.util.InvUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.Random;

/**
 * Created by Christian on 24.09.2016.
 */
public class BlockVase extends BWMBlock implements IMultiVariants {

    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.125D, 0, 0.125D, 0.875D, 1.0D, 0.875D);

    public BlockVase() {
        super(BWMBlocks.POTTERY);
        this.setHardness(2.0F);
        this.setHarvestLevel("pickaxe", -1);
        this.setDefaultState(this.blockState.getBaseState().withProperty(ColorUtils.COLOR, EnumDyeColor.WHITE));
        this.setSoundType(SoundType.GLASS);
    }

    public static ItemStack getStack(EnumDyeColor type) {
        return new ItemStack(BWMBlocks.VASE, 1, type.getMetadata());
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityVase();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return AABB;
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
    public String[] getVariants() {
        EnumDyeColor[] dyes = EnumDyeColor.values();
        String[] variants = new String[dyes.length];

        for (int i = 0; i < dyes.length; ++i) {
            EnumDyeColor dye = dyes[i];
            variants[i] = "color=" + dye.getName();
        }

        return variants;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            InvUtils.readFromStack(tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), stack);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = playerIn.getHeldItem(hand);
        if (playerIn.isSneaking())
            return false;
        InvUtils.getItemHandler(worldIn, pos, null).ifPresent(inv -> {
            if (InvUtils.insertSingle(inv, heldItem, false).isEmpty()) {
                if (!playerIn.isCreative())
                    heldItem.shrink(1);
                worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                        SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F,
                        ((worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }
        });
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ);
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (!world.isRemote && entity != null && entity instanceof EntityArrow) {
            world.playEvent(2001, pos, Block.getStateId(state));
            world.updateComparatorOutputLevel(pos, this);
            world.setBlockToAir(pos);
        }
        super.onEntityCollision(world, pos, state, entity);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        ItemStack stack = new ItemStack(Item.getItemFromBlock(this), 1, state.getValue(ColorUtils.COLOR).getMetadata());
        TileEntity tile = world.getTileEntity(pos);
        if (!world.isRemote && tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            InvUtils.writeToStack(tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), stack);
        }
        return stack;
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return true;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return (state.getValue(ColorUtils.COLOR)).getMetadata();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (EnumDyeColor color : EnumDyeColor.values())
            items.add(getStack(color));
        super.getSubBlocks(itemIn, items);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(ColorUtils.COLOR, EnumDyeColor.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ColorUtils.COLOR).getMetadata();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ColorUtils.COLOR);
    }

    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            if (tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
                return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(0) != null ? 15 : 0;
            }
        }
        return 0;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.DOWN ? BlockFaceShape.CENTER_BIG : (face == EnumFacing.UP ? BlockFaceShape.CENTER : BlockFaceShape.UNDEFINED); //Top is center instead of bowl to facilitate placing candles on these.
    }
}
