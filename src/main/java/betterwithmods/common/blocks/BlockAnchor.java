package betterwithmods.common.blocks;

import betterwithmods.api.tile.IRopeConnector;
import betterwithmods.client.BWCreativeTabs;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.mechanical.BlockMechMachines;
import betterwithmods.common.blocks.mechanical.BlockMechMachines.EnumType;
import betterwithmods.util.InvUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import static betterwithmods.util.DirUtils.FACING;

public class BlockAnchor extends BWMBlock implements IRopeConnector {
    public static final PropertyBool LINKED = PropertyBool.create("linked");
    private static final float HEIGHT = 0.375F;

    private static final AxisAlignedBB D_AABB = new AxisAlignedBB(0.0F, 1.0F - HEIGHT, 0.0F, 1.0F, 1.0F, 1.0F);
    private static final AxisAlignedBB U_AABB = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, HEIGHT, 1.0F);
    private static final AxisAlignedBB N_AABB = new AxisAlignedBB(0.0F, 0.0F, 1.0F - HEIGHT, 1.0F, 1.0F, 1.0F);
    private static final AxisAlignedBB S_AABB = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, HEIGHT);
    private static final AxisAlignedBB W_AABB = new AxisAlignedBB(1.0F - HEIGHT, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    private static final AxisAlignedBB E_AABB = new AxisAlignedBB(0.0F, 0.0F, 0.0F, HEIGHT, 1.0F, 1.0F);
    private static final AxisAlignedBB[] BOXES = new AxisAlignedBB[]{D_AABB, U_AABB, N_AABB, S_AABB, W_AABB, E_AABB};

    public BlockAnchor() {
        super(Material.ROCK);
        setCreativeTab(BWCreativeTabs.BWTAB);
        this.setHardness(2.0F);
        this.setHarvestLevel("pickaxe", 0);

    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        EnumFacing facing = state.getValue(FACING);
        return BOXES[facing.getIndex()];
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
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float flX, float flY, float flZ, int meta, EntityLivingBase entity, EnumHand hand) {
        IBlockState state = super.getStateForPlacement(world, pos, side, flX, flY, flZ, meta, entity, hand);
        return this.setFacingInBlock(state, entity.isSneaking() ? side.getOpposite() : side);
    }

    public IBlockState setFacingInBlock(IBlockState state, EnumFacing facing) {
        return state.withProperty(FACING, facing);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);
        BlockPos down = pos.down();

        if (!heldItem.isEmpty()) {
            if (heldItem.getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock) heldItem.getItem()).getBlock();
                if (block == BWMBlocks.ROPE) {
                    if (!world.isRemote) {
                        if (world.getBlockState(down).getBlock() == BWMBlocks.ROPE) {
                            BlockRope.placeRopeUnder(heldItem, world, down, player);
                        } else if (world.getBlockState(down).getBlock().isReplaceable(world, down) || world.isAirBlock(down)) {
                            world.setBlockState(down, BWMBlocks.ROPE.getDefaultState());
                            world.playSound(null, down, BWMBlocks.ROPE.getSoundType(BWMBlocks.ROPE.getDefaultState(), world, null, null).getPlaceSound(), SoundCategory.BLOCKS, 1, 1);
                            if (!player.capabilities.isCreativeMode)
                                heldItem.shrink(1);
                        } else
                            return false;
                    }
                    return true;
                }
            }
            return false;
        } else if (!world.isRemote) {
            if (retractRope(world, pos, player))
                world.playSound(null, pos, BWMBlocks.ROPE.getSoundType(BWMBlocks.ROPE.getDefaultState(), world, null, null).getBreakSound(), SoundCategory.BLOCKS, 1, 1);
        }
        return true;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        EnumFacing facing = getFacing(world.getBlockState(pos));
        return side == facing.getOpposite();
    }

    public EnumFacing getFacing(IBlockState state) {
        return state.getValue(FACING);
    }

    private boolean retractRope(World world, BlockPos pos, EntityPlayer player) {
        for (int i = pos.getY() - 1; i >= 0; i--) {
            BlockPos pos2 = new BlockPos(pos.getX(), i, pos.getZ());
            if (world.getBlockState(pos2).getBlock() != BWMBlocks.ROPE && world.getBlockState(pos2.up()).getBlock() == BWMBlocks.ROPE) {
                if (!player.capabilities.isCreativeMode)
                    addRopeToInv(world, pos, player);
                return world.setBlockToAir(pos2.up());
            } else if (world.getBlockState(pos2).getBlock() != BWMBlocks.ROPE)
                return false;
        }
        return false;
    }

    private void addRopeToInv(World world, BlockPos pos, EntityPlayer player) {
        ItemStack rope = new ItemStack(BWMBlocks.ROPE);

        if (player.inventory.addItemStackToInventory(rope))
            world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        else
            InvUtils.ejectStackWithOffset(world, pos, rope);
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        return entity instanceof EntityPlayer && world.getBlockState(pos).getBlock() == this && world.getBlockState(pos).getValue(FACING) != EnumFacing.DOWN;
    }

    private boolean isRope(IBlockAccess world, BlockPos origin, EnumFacing facing) {
        BlockPos pos = origin.offset(facing);
        return world.getBlockState(pos).getBlock() == BWMBlocks.ROPE;
    }

    private boolean isAnchor(IBlockAccess world, BlockPos origin, EnumFacing facing) {
        BlockPos pos = origin.offset(facing);
        return world.getBlockState(pos).getBlock() == this && world.getBlockState(pos).getValue(FACING) != facing;
    }

    private boolean isPulley(IBlockAccess world, BlockPos origin, EnumFacing facing) {
        BlockPos pos = origin.offset(facing);
        return world.getBlockState(pos).getBlock() == BWMBlocks.SINGLE_MACHINES && world.getBlockState(pos).getValue(BlockMechMachines.TYPE) == EnumType.PULLEY;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        EnumFacing facing = state.getValue(FACING);
        boolean isConnected = facing == EnumFacing.UP ? isRope(world, pos, EnumFacing.UP) || isAnchor(world, pos, EnumFacing.UP) || isPulley(world, pos, EnumFacing.UP) : isRope(world, pos, EnumFacing.DOWN) || isAnchor(world, pos, EnumFacing.DOWN);
        return state.withProperty(LINKED, isConnected);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, LINKED);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == getFacing(state).getOpposite() ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }
}
