package betterwithmods.common.blocks;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.mechanical.BlockMechMachines;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRope extends BWMBlock {
    private static final AxisAlignedBB ROPE_AABB = new AxisAlignedBB(0.4375F, 0.0F, 0.4375F, 0.5625F, 1.0F, 0.5625F);

    public BlockRope() {
        super(BWMBlocks.MATERIAL_ROPE);
        setSoundType(SoundType.PLANT);
        this.setHardness(0.5F);
    }

    public static boolean placeRopeUnder(ItemStack stack, World world, BlockPos pos, EntityPlayer player) {
        if (!stack.isEmpty() || player == null) {
            BlockPos bp = getLowestRopeBlock(world, pos).down();
            Block block = world.getBlockState(bp).getBlock();
            if ((world.isAirBlock(bp) || block.isReplaceable(world, bp)) && ((BlockRope) BWMBlocks.ROPE.getDefaultState().getBlock()).canBlockStay(world, bp)) {
                world.setBlockState(bp, BWMBlocks.ROPE.getDefaultState());
                world.playSound(null, bp, BWMBlocks.ROPE.getSoundType(BWMBlocks.ROPE.getDefaultState(), world, null, null).getPlaceSound(), SoundCategory.BLOCKS, 1, 1);
                if (player != null && !player.capabilities.isCreativeMode) // if this is placed by a pulley, let the pulley manage the stack size
                    stack.shrink(1);
                return true;
            }
        }
        return false;
    }

    public static BlockPos getLowestRopeBlock(World world, BlockPos pos) {
        if (world != null && pos != null) {
            BlockPos down = pos.down();
            Block below = world.getBlockState(down).getBlock();
            if (below == BWMBlocks.ROPE) {
                return getLowestRopeBlock(world, down);
            } else {
                return pos;
            }
        } else {
            return null;
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = playerIn.getHeldItem(hand);
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof ItemBlock && ((ItemBlock) heldItem.getItem()).getBlock() == this) {
            return placeRopeUnder(heldItem, worldIn, pos, playerIn);
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canBlockStay(worldIn, pos)) {
            dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    public boolean canBlockStay(World world, BlockPos pos) {
        Block blockAbove = world.getBlockState(pos.up()).getBlock();

        boolean supported = false;

        if (blockAbove == BWMBlocks.ANCHOR) {
            EnumFacing facing = ((BlockAnchor) BWMBlocks.ANCHOR).getFacing(world.getBlockState(pos.up()));
            supported = facing != EnumFacing.UP;
        }
        if (blockAbove == this) {
            supported = true;
        }
        if (blockAbove == BWMBlocks.SINGLE_MACHINES) {
            if (world.getBlockState(pos.up()).getValue(BlockMechMachines.TYPE) == BlockMechMachines.EnumType.PULLEY) {
                supported = true;
            }
        }

        return supported;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return canBlockStay(world, pos);
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
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return ROPE_AABB;
    }

    public EnumFacing getFacing(IBlockState state) {
        return null;
    }

    public IBlockState setFacingInBlock(IBlockState state, EnumFacing facing) {
        return state;
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        return entity instanceof EntityPlayer;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face.getAxis() != EnumFacing.Axis.Y ? BlockFaceShape.UNDEFINED : BlockFaceShape.CENTER_SMALL;
    }
}
