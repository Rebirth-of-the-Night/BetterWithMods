package betterwithmods.common.blocks;

import betterwithmods.api.IMultiLocations;
import betterwithmods.common.damagesource.BWDamageSource;
import betterwithmods.common.items.tools.ItemSoulforgeArmor;
import betterwithmods.util.player.PlayerHelper;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Created by primetoxinz on 6/25/17.
 */
public class BlockNetherGrowth extends BWMBlock implements IMultiLocations {
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 7);

    public BlockNetherGrowth() {
        super(Material.GRASS);
        setTickRandomly(true);
        setDefaultState(getDefaultState().withProperty(AGE, 0));
        setHardness(0.5f);
        setResistance(0.1f);
    }


    public int getAge(IBlockState state) {
        return state.getValue(AGE);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack held = playerIn.getHeldItemMainhand();
        if (!held.isItemEqual(BlockUrn.getStack(BlockUrn.EnumType.FULL, 1)))
            return false;
        if (!world.isRemote) {

            Iterable<BlockPos> pool = BlockPos.PooledMutableBlockPos.getAllInBox(pos.add(-3, -3, -3), pos.add(3, 3, 3));
            boolean grew = false;
            for (BlockPos p : pool) {
                IBlockState s = world.getBlockState(p);
                if (s.getBlock() == this) {
                    BlockNetherGrowth b = (BlockNetherGrowth) s.getBlock();
                    for (int i = 0; i < 10; i++)
                        b.grow(world, p, s, world.rand);
                    grew = true;
                }
            }
            if (grew) {
                held.shrink(1);
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {

        Random rand = world.rand;
        for (int i = 0; i < 9; i++) {
            this.spread(world, pos, rand);
        }

        AxisAlignedBB bb = this.getBoundingBox(state, world, pos).offset(pos).grow(3);
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, bb);
        for (Entity e : entities) {
            e.attackEntityFrom(BWDamageSource.growth, 2);
        }
        world.playSound(null, pos, SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.BLOCKS, 0.4f, 0.5f);

        super.breakBlock(world, pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        checkCanStay(worldIn, pos);
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        grow(worldIn, pos, state, rand);
    }

    private int range(Random rand) {
        return rand.nextInt(3) - 1;
    }

    public void grow(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote) {
            checkCanStay(world, pos);
            int age = getAge(state);
            if (age > 3) {
                spread(world, pos, rand);
            }
            if (age < 7) {
                if (age == 6)
                    world.playSound(null, pos, SoundEvents.BLOCK_CHORUS_FLOWER_DEATH, SoundCategory.BLOCKS, 0.2f, 0.5f);
                world.setBlockState(pos, state.withProperty(AGE, age + 1));
                fixEntities(world, pos, state);
            }
        }
    }

    public void spread(World world, BlockPos pos, Random rand) {
        BlockPos spread = pos.add(range(rand), range(rand), range(rand));
        if (canPlaceBlockAt(world, spread) && !spread.equals(pos)) {
            world.setBlockState(spread, getDefaultState());
        }
    }

    private void fixEntities(World world, BlockPos pos, IBlockState state) {
        AxisAlignedBB bb = this.getBoundingBox(state, world, pos).offset(pos).grow(1 / 16d);
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, bb);
        for (Entity e : entities) {
            e.setPosition(e.posX, e.posY + 4 / 16d, e.posZ);
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AGE);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        int age = getAge(state) + 1;
        return new AxisAlignedBB(0, 0, 0, 1, age / 16d, 1);
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        return SoundType.SLIME;
    }


    @Override
    public String[] getLocations() {
        return new String[]{"nether_spore"};
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return getAge(state);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(AGE, meta);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        //NO DROPS
        return Lists.newArrayList();
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (rand.nextInt(10) == 0) {
            worldIn.spawnParticle(EnumParticleTypes.TOWN_AURA, (double) ((float) pos.getX() + rand.nextFloat()), (double) ((float) pos.getY() + 1.1F), (double) ((float) pos.getZ() + rand.nextFloat()), 0.0D, 0.0D, 0.0D, new int[0]);
        }
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (entityIn instanceof EntityItem) {
            ItemStack stack = ((EntityItem) entityIn).getItem();
            if (stack.getItem() instanceof ItemFood) {
                worldIn.playSound(null, pos, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.BLOCKS, 1.0f, 0.2f);
                grow(worldIn, pos, state, worldIn.rand);
                entityIn.setDead();
            }
        } else if (entityIn instanceof EntityLivingBase) {
            if (PlayerHelper.hasPart((EntityLivingBase) entityIn, EntityEquipmentSlot.FEET, ItemSoulforgeArmor.class))
                return;
            entityIn.attackEntityFrom(BWDamageSource.growth, 5);
            entityIn.fallDistance = 0;
            entityIn.motionY = 1;
        }
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
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.isAirBlock(pos) && canStay(worldIn, pos);
    }

    public void checkCanStay(World world, BlockPos pos) {
        if (!canStay(world, pos)) {
            breakBlock(world, pos, world.getBlockState(pos));
            world.setBlockToAir(pos);
        }
    }

    public boolean canStay(World world, BlockPos pos) {
        Block block = world.getBlockState(pos.down()).getBlock();
        return block == Blocks.NETHERRACK || block == Blocks.QUARTZ_ORE;
    }

    @Override
    public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, EntityLiving.SpawnPlacementType type) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face != EnumFacing.DOWN ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

}
