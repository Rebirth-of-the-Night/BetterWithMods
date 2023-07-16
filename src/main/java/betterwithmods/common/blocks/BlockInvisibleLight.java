package betterwithmods.common.blocks;

import betterwithmods.common.BWMBlocks;
import betterwithmods.util.DirUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockInvisibleLight extends BWMBlock {
    public static final PropertyBool SUNLIGHT = PropertyBool.create("sunlight");

    public BlockInvisibleLight() {
        super(Material.AIR);
        this.setLightLevel(1.0F);
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(SUNLIGHT, false).withProperty(DirUtils.FACING, EnumFacing.UP));
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public int tickRate(World world) {
        return 6;
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
    public EnumPushReaction getPushReaction(IBlockState state) {
        return EnumPushReaction.DESTROY;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        world.scheduleUpdate(pos, this, 5);
    }

    @Override
    public int quantityDropped(Random rand) {
        return 0;
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public boolean canCollideCheck(IBlockState state, boolean playerActivated) {
        return false;
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float f, int fortune) {
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos other) {
        if (world.getBlockState(pos).getBlock() == BWMBlocks.LIGHT_SOURCE)
            world.setBlockToAir(pos);
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (!world.isRemote) {
            if (world.getBlockState(pos).getValue(SUNLIGHT)) {
                if (entity instanceof EntityLiving) {
                    EntityLiving living = (EntityLiving) entity;
                    if (living.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
                        living.setFire(4);
                    }
                }
            }
        }
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!hasClearViewToLens(world, pos)) {
            world.setBlockToAir(pos);
        } else
            world.scheduleUpdate(pos, this, 5);
    }

    public EnumFacing getFacing(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos).getValue(DirUtils.FACING);
    }

    public EnumFacing getFacing(IBlockState state) {
        return state.getValue(DirUtils.FACING);
    }

    public IBlockState setFacingInBlock(IBlockState state, EnumFacing facing) {
        return state.withProperty(DirUtils.FACING, facing);
    }

    private boolean hasClearViewToLens(World world, BlockPos pos) {
        boolean clear = true;
        EnumFacing face = getFacing(world.getBlockState(pos));
        for (int i = 1; i < 32; i++)//64 default, try to get some lag solved
        {
            BlockPos bPos = pos.offset(face, i);
            int x = bPos.getX();
            int y = bPos.getY();
            int z = bPos.getZ();
            List<Entity> entity = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1));
            if (entity.size() > 0)
                clear = false;
            else if (world.getBlockState(bPos).getBlock() == BWMBlocks.LIGHT_SOURCE)
                clear = false;
            else if (world.getBlockState(bPos).getBlock() == BWMBlocks.LENS) {
                BlockLens lens = (BlockLens) world.getBlockState(bPos).getBlock();
                return lens.getFacing(world, bPos) == DirUtils.getOpposite(getFacing(world, bPos));
            } else if (!world.isAirBlock(bPos))
                clear = false;
            if (!clear)
                break;
        }
        return clear;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        boolean sun = false;
        if (meta > 7) {
            sun = true;
            meta -= 8;
        }
        return this.getDefaultState().withProperty(SUNLIGHT, sun).withProperty(DirUtils.FACING, EnumFacing.byIndex(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(SUNLIGHT) ? 8 : 0;
        return meta + state.getValue(DirUtils.FACING).getIndex();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DirUtils.FACING, SUNLIGHT);
    }
}
