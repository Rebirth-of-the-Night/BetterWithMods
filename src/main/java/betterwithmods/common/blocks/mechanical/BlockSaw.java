package betterwithmods.common.blocks.mechanical;

import betterwithmods.BWMod;
import betterwithmods.api.block.IOverpower;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWSounds;
import betterwithmods.common.blocks.BWMBlock;
import betterwithmods.common.blocks.BlockAesthetic;
import betterwithmods.common.blocks.mechanical.tile.TileSaw;
import betterwithmods.common.damagesource.BWDamageSource;
import betterwithmods.module.gameplay.MechanicalBreakage;
import betterwithmods.util.DirUtils;
import betterwithmods.util.InvUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class BlockSaw extends BWMBlock implements IBlockActive, IOverpower {
    private static final float HEIGHT = 3f/4f;
    private static final AxisAlignedBB D_AABB = new AxisAlignedBB(0.0F, 1.0F - HEIGHT, 0.0F, 1.0F, 1.0F, 1.0F);
    private static final AxisAlignedBB U_AABB = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, HEIGHT, 1.0F);
    private static final AxisAlignedBB N_AABB = new AxisAlignedBB(0.0F, 0.0F, 1.0F - HEIGHT, 1.0F, 1.0F, 1.0F);
    private static final AxisAlignedBB S_AABB = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, HEIGHT);
    private static final AxisAlignedBB W_AABB = new AxisAlignedBB(1.0F - HEIGHT, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    private static final AxisAlignedBB E_AABB = new AxisAlignedBB(0.0F, 0.0F, 0.0F, HEIGHT, 1.0F, 1.0F);



    public BlockSaw() {
        super(Material.WOOD);
        this.setHardness(2.0F);
        this.setSoundType(SoundType.WOOD);
        this.setDefaultState(this.blockState.getBaseState().withProperty(DirUtils.FACING, EnumFacing.UP));
    }


    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileSaw();
    }

    @Override
    public int tickRate(World world) {
        return 10;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float flX, float flY, float flZ, int meta, EntityLivingBase placer, EnumHand hand) {
        IBlockState state = super.getStateForPlacement(world, pos, facing, flX, flY, flZ, meta, placer, hand);
        return setFacingInBlock(state, DirUtils.getOpposite(facing));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
        EnumFacing facing = DirUtils.convertEntityOrientationToFacing(entity, EnumFacing.NORTH);
        world.setBlockState(pos, world.getBlockState(pos).withProperty(DirUtils.FACING, facing));
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        world.scheduleBlockUpdate(pos, this, tickRate(world), 5);
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
        state = state.getActualState(world, pos);
        EnumFacing facing = getFacing(state);
        switch (facing) {
            case DOWN:
                return D_AABB;
            case UP:
                return U_AABB;
            case NORTH:
                return N_AABB;
            case SOUTH:
                return S_AABB;
            case WEST:
                return W_AABB;
            case EAST:
            default:
                return E_AABB;
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos other) {
        world.scheduleBlockUpdate(pos, this, tickRate(world), 5);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        withTile(world, pos).ifPresent(TileSaw::onChanged);
        if (isActive(state)) {
            if (!world.isRemote) {
                TileSaw tile = getTile(world,pos);
                if(tile != null) {
                    tile.cut(world, pos, rand);
                    world.scheduleBlockUpdate(pos, this, tickRate(world) + rand.nextInt(6), 5);
                }
            }

        }
    }

    @Override
    public void onChangeActive(World world, BlockPos pos, boolean newValue) {
        Random rand = world.rand;
        emitSawParticles(world, pos, rand, EnumParticleTypes.SMOKE_NORMAL, 5);
        if (newValue) {
            world.scheduleBlockUpdate(pos, this, tickRate(world) + rand.nextInt(6), 5);
            world.playSound(null, pos, BWSounds.SAW_CUT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat() * 0.1F, 1.5F + rand.nextFloat() * 0.1F);
        } else {
            world.playSound(null, pos, BWSounds.SAW_CUT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat() * 0.1F, 0.75F + rand.nextFloat() * 0.1F);
        }
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (isActive(state) && entity instanceof EntityLivingBase) {
            EnumFacing dir = getFacing(world, pos);

            DamageSource source = BWDamageSource.getSawDamage();

            int damage = 4;
            boolean unobstructed = true;
            for (int i = 0; i < 3; i++) {
                BlockPos pos2 = new BlockPos(pos.getX(), pos.getY() - i, pos.getZ()).offset(dir);
                Block block = world.getBlockState(pos2).getBlock();
                IBlockState blockState = world.getBlockState(pos2);
                if (isChoppingBlock(blockState)) {
                    source = BWDamageSource.getChoppingBlockDamage();
                    damage *= 3;
                    if (blockState.getValue(BlockAesthetic.TYPE).getMeta() == 0 && unobstructed)
                        world.setBlockState(pos2, BWMBlocks.AESTHETIC.getDefaultState().withProperty(BlockAesthetic.TYPE, BlockAesthetic.EnumType.CHOPBLOCKBLOOD));
                    break;
                } else if (!world.isAirBlock(pos2) && !(block instanceof BlockLiquid) && !(block instanceof IFluidBlock))
                    break;
                else if (!world.isAirBlock(pos2))
                    unobstructed = false;
            }
            if (source != null && entity.attackEntityFrom(source, damage)) {
                ((EntityLivingBase) entity).recentlyHit = 60;
                world.playSound(null, pos, BWSounds.SAW_CUT, SoundCategory.BLOCKS, 1.0F + world.rand.nextFloat() * 0.1F, 1.5F + world.rand.nextFloat() * 0.1F);
            }
        }
    }

    public boolean isChoppingBlock(IBlockState state) {
        return state.getBlock() == BWMBlocks.AESTHETIC && state.getValue(BlockAesthetic.TYPE).getMeta() < 2;
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side != getFacing(state);
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

    @Override
    public void overpower(World world, BlockPos pos) {
        if (MechanicalBreakage.saw)
            InvUtils.ejectBrokenItems(world, pos, new ResourceLocation(BWMod.MODID, "block/saw"));
        world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 0.3F, world.rand.nextFloat() * 0.1F + 0.45F);
        world.setBlockToAir(pos);
    }


    public void emitSawParticles(World world, BlockPos pos, Random rand, EnumParticleTypes type, int amount) {
        EnumFacing facing = getFacing(world, pos);
        float xPos = pos.getX();
        float yPos = pos.getY();
        float zPos = pos.getZ();
        float xExtent = 0.0F;
        float zExtent = 0.0F;

        switch (facing) {
            case DOWN:
                xPos += 0.5F;
                zPos += 0.5F;
                xExtent = 1.0F;
                break;
            case UP:
                xPos += 0.5F;
                zPos += 0.5F;
                yPos += 1.0F;
                xExtent = 1.0F;
                break;
            case NORTH:
                xPos += 0.5F;
                yPos += 0.5F;
                xExtent = 1.0F;
                break;
            case SOUTH:
                xPos += 0.5F;
                yPos += 0.5F;
                zPos += 1.0F;
                xExtent = 1.0F;
                break;
            case WEST:
                yPos += 0.5F;
                zPos += 0.5F;
                zExtent = 1.0F;
                break;
            default:
                yPos += 0.5F;
                zPos += 0.5F;
                xPos += 1.0F;
                zExtent = 1.0F;
        }
        for (int i = 0; i < amount; i++) {
            float smokeX = xPos + (rand.nextFloat() - 0.5F) * xExtent;
            float smokeY = yPos + rand.nextFloat() * 0.1F;
            float smokeZ = zPos + (rand.nextFloat() - 0.5F) * zExtent;
            world.spawnParticle(type, smokeX, smokeY, smokeZ, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (isActive(state)) {
            emitSawParticles(world, pos, rand, EnumParticleTypes.SMOKE_NORMAL, 5);

            List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)));
            if (!entities.isEmpty())
                emitSawParticles(world, pos, rand, EnumParticleTypes.REDSTONE, 20);
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        int active = meta & 1;
        int facing = meta >> 1;
        return this.getDefaultState().withProperty(ACTIVE, active == 1).withProperty(DirUtils.FACING, EnumFacing.byIndex(facing));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int active = state.getValue(ACTIVE) ? 1 : 0;
        int facing = state.getValue(DirUtils.FACING).getIndex() << 1;
        return active | facing;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVE, DirUtils.FACING);
    }

    public Optional<TileSaw> withTile(World world, BlockPos pos) {
        return Optional.ofNullable(getTile(world, pos));
    }

    public TileSaw getTile(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileSaw)
            return (TileSaw) tile;
        return null;
    }

    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {
        return getFacing(state) != EnumFacing.UP;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face != getFacing(state).getOpposite() ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        if (super.rotateBlock(world, pos, axis)) {
            setActive(world, pos, false);
            return true;
        }
        return false;
    }
}
