package betterwithmods.common.blocks;

import betterwithmods.api.block.IMultiVariants;
import betterwithmods.common.BWMBlocks;
import betterwithmods.util.DirUtils;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class BlockLens extends BlockRotate implements IMultiVariants {
    public static final PropertyBool LIT = PropertyBool.create("lit");
    public static final int RANGE = 256;

    public BlockLens() {
        super(Material.IRON);
        this.setHardness(3.5F);
        this.setTickRandomly(true);
        this.setDefaultState(this.getDefaultState().withProperty(DirUtils.FACING, EnumFacing.NORTH));
        this.setHarvestLevel("pickaxe", 0);
    }


    @Override
    public int tickRate(World world) {
        return 5;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        world.scheduleBlockUpdate(pos, this, 3, 5);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float flX, float flY, float flZ, int meta, EntityLivingBase entity, EnumHand hand) {
        IBlockState state = super.getStateForPlacement(world, pos, side, flX, flY, flZ, meta, entity, hand);
        EnumFacing face = DirUtils.convertEntityOrientationToFacing(entity, side);
        return setFacingInBlock(state, face);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        cleanupLightToFacing(world, pos, state.getValue(DirUtils.FACING));
        super.breakBlock(world, pos, state);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
        EnumFacing facing = DirUtils.convertEntityOrientationToFacing(entity, EnumFacing.NORTH);
        setFacingInBlock(state, facing);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos other) {
        world.scheduleBlockUpdate(pos, this, 3, 5);
    }

    private boolean canPassLight(World world, BlockPos pos, IBlockState state) {
        return !state.getMaterial().isOpaque() || state.getBlock().isAir(state, world, pos);
    }

    private void placeLightSource(World world, BlockPos pos, EnumFacing facing, BlockPos lensPos) {
        if (world.getBlockState(pos).getBlock().isReplaceable(world, pos)) {
            boolean sunlight = isLightFromSun(world, lensPos);
            world.setBlockState(pos, BWMBlocks.LIGHT_SOURCE.getDefaultState().withProperty(BlockInvisibleLight.SUNLIGHT, sunlight).withProperty(DirUtils.FACING, facing));
        }
    }


    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        cleanupLight(world, pos);
        EnumFacing dir = getFacing(world, pos), opposite = dir.getOpposite();

        boolean lightOn = isInputLit(world, pos);
        if (isLit(world, pos) != lightOn) {
            setLit(world, pos, lightOn);
        }

        if (isLit(world, pos)) {
            int currentRange = RANGE;
            for (int i = 1; i < RANGE; i++) {
                BlockPos offset = pos.offset(dir, i);
                IBlockState offsetState = world.getBlockState(offset);

                if (!canPassLight(world, offset, offsetState)) {
                    currentRange = i + 1;
                    break;
                }
            }

            AxisAlignedBB bb = FULL_BLOCK_AABB.offset(pos);

            int mod = dir.getAxisDirection().getOffset();
            switch (dir.getAxis()) {
                case X:
                    bb = bb.expand(mod * currentRange, 0, 0);
                    break;
                case Y:
                    bb = bb.expand(0, mod * currentRange, 0);
                    break;
                case Z:
                    bb = bb.expand(0, 0, mod * currentRange);
                    break;
            }


            List<Entity> box = world.getEntitiesWithinAABB(Entity.class, bb);
            HashMap<Integer, Entity> map = Maps.newHashMap();
            for (Entity e : box) {
                int distance = 0;
                switch (dir.getAxis()) {
                    case X:
                        distance = (int) (pos.getX() - e.posX);
                        break;
                    case Y:
                        distance = (int) (pos.getY() - e.posY);
                        break;
                    case Z:
                        distance = (int) (pos.getZ() - e.posZ);
                        break;
                }
                map.put(Math.abs(distance), e);
            }

            for (int i = 1; i < currentRange; i++) {
                BlockPos offset = pos.offset(dir, i);
                IBlockState offsetState = world.getBlockState(offset);
                boolean blocked = false;
                if (canPassLight(world, pos, offsetState)) {
                    if (map.containsKey(i)) {
                        blocked = true;
                    }
                } else {
                    blocked = true;
                }

                if (blocked) {
                    placeLightSource(world, offset.offset(opposite), opposite, pos);
                    break;
                }
            }

        }

        world.scheduleBlockUpdate(pos, this, 5, 5);
    }

    @Override
    public EnumPushReaction getPushReaction(IBlockState state) {
        return EnumPushReaction.BLOCK;
    }

    public EnumFacing getFacing(IBlockAccess world, BlockPos pos) {
        return getFacing(world.getBlockState(pos));
    }

    public EnumFacing getFacing(IBlockState state) {
        return state.getValue(DirUtils.FACING);
    }

    public IBlockState setFacingInBlock(IBlockState state, EnumFacing facing) {
        return state.withProperty(DirUtils.FACING, facing);
    }

    public boolean isLit(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos).getValue(LIT);
    }

    public void setLit(World world, BlockPos pos, boolean isOn) {
        boolean oldLit = world.getBlockState(pos).getValue(LIT);

        if (isOn != oldLit) {
            world.setBlockState(pos, world.getBlockState(pos).withProperty(LIT, isOn));
            world.neighborChanged(pos, this, pos);
        }
    }

    private boolean isInputLit(World world, BlockPos pos) {
        EnumFacing facing = getFacing(world, pos);
        EnumFacing dir = DirUtils.getOpposite(facing);
        BlockPos oppOff = pos.offset(dir);
        Block block = world.getBlockState(oppOff).getBlock();

        if (world.isAirBlock(oppOff) && world.canBlockSeeSky(oppOff)) {
            return world.getLightFor(EnumSkyBlock.SKY, oppOff) > 12 && world.isDaytime() && (!world.isRaining() || !world.isThundering());
        } else {
            return block.getLightValue(world.getBlockState(oppOff), world, oppOff) > 12;
        }
    }


    private boolean isLightFromSun(World world, BlockPos pos) {
        EnumFacing facing = DirUtils.getOpposite(getFacing(world, pos));
        BlockPos offset = pos.offset(facing);
        if (world.isAirBlock(offset) && world.canBlockSeeSky(offset))
            return true;
        else {
            return world.getBlockState(offset).getBlock() == BWMBlocks.LIGHT_SOURCE && world.getBlockState(offset).getValue(BlockInvisibleLight.SUNLIGHT);
        }
    }

    private void cleanupLightToFacing(World world, BlockPos pos, EnumFacing facing) {
        EnumFacing opp = DirUtils.getOpposite(facing);
        for (int i = 1; i < RANGE; i++) {
            BlockPos offset = pos.offset(facing, i);
            if (world.getBlockState(offset).getBlock() == BWMBlocks.LIGHT_SOURCE) {
                EnumFacing lightFace = ((BlockInvisibleLight) world.getBlockState(offset).getBlock()).getFacing(world, offset);
                if (lightFace == opp) {
                    world.setBlockToAir(offset);
                    break;
                }
            } else if (!world.isAirBlock(offset))
                break;
        }
    }

    private void cleanupLight(World world, BlockPos pos) {
        EnumFacing facing = getFacing(world, pos);
        EnumFacing oppFacing = DirUtils.getOpposite(facing);

        for (int i = 1; i < RANGE; i++) {
            BlockPos offset = pos.offset(facing, i);

            if (world.getBlockState(offset).getBlock() == BWMBlocks.LIGHT_SOURCE) {
                EnumFacing lightFace = ((BlockInvisibleLight) world.getBlockState(offset).getBlock()).getFacing(world, offset);
                if (lightFace == oppFacing) {
                    world.setBlockToAir(offset);
                }
            } else if (!world.isAirBlock(offset)) {
                if (world.getBlockState(offset).getBlock() == this) {
                    BlockLens lens = (BlockLens) world.getBlockState(pos).getBlock();
                    if (lens.getFacing(world, offset) == facing)
                        break;
                }
            }
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        boolean lit = false;
        if (meta > 7) {
            lit = true;
            meta -= 8;
        }
        return this.getDefaultState().withProperty(LIT, lit).withProperty(DirUtils.FACING, EnumFacing.byIndex(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(LIT) ? 8 : 0;
        return meta + state.getValue(DirUtils.FACING).getIndex();
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DirUtils.FACING, LIT);
    }

    @Override
    public void nextState(World world, BlockPos pos, IBlockState state) {
        world.setBlockState(pos, state.withProperty(LIT, false).cycleProperty(DirUtils.FACING));
    }

    @Override
    public String[] getVariants() {
        return new String[]{"facing=north,lit=false"};
    }

}
