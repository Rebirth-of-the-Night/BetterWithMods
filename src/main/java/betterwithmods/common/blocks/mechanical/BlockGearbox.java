package betterwithmods.common.blocks.mechanical;

import betterwithmods.api.BWMAPI;
import betterwithmods.api.block.IAdvancedRotationPlacement;
import betterwithmods.api.block.IOverpower;
import betterwithmods.api.block.IRenderRotationPlacement;
import betterwithmods.client.ClientEventHandler;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWSounds;
import betterwithmods.common.blocks.BlockRotate;
import betterwithmods.common.blocks.EnumTier;
import betterwithmods.common.blocks.mechanical.tile.TileGearbox;
import betterwithmods.util.DirUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class BlockGearbox extends BlockRotate implements IBlockActive, IOverpower, IAdvancedRotationPlacement, IRenderRotationPlacement {
    private final int maxPower;
    private EnumTier type;

    public BlockGearbox(int maxPower, EnumTier type) {
        super(Material.WOOD);
        this.maxPower = maxPower;
        this.setHardness(2.0F);
        this.setDefaultState(getDefaultState().withProperty(DirUtils.FACING, EnumFacing.UP).withProperty(ACTIVE, false));
        this.type = type;
    }


    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(I18n.format("tooltip.gearbox.name"));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float flX, float flY, float flZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getStateForAdvancedRotationPlacement(getDefaultState(), placer.isSneaking() ? side : side.getOpposite(), flX, flY, flZ);
    }

    @Override
    public void nextState(World world, BlockPos pos, IBlockState state) {
        world.setBlockState(pos, state.cycleProperty(DirUtils.FACING).withProperty(ACTIVE, false));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        world.scheduleBlockUpdate(pos, this, 5, 5);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        onChange(world, pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos other) {
        onChange(world, pos);
    }

    public void onChange(World world, BlockPos pos) {
        if (!world.isRemote) {
            withTile(world, pos).ifPresent(TileGearbox::onChanged);
        }
    }


    public EnumFacing getFacing(IBlockAccess world, BlockPos pos) {
        return getFacingFromState(world.getBlockState(pos));
    }

    public EnumFacing getFacingFromState(IBlockState state) {
        return state.getValue(DirUtils.FACING);
    }


    private void emitGearboxParticles(World world, BlockPos pos, Random rand) {
        for (int i = 0; i < 5; i++) {
            float flX = pos.getX() + rand.nextFloat();
            float flY = pos.getY() + rand.nextFloat() * 0.5F + 1.0F;
            float flZ = pos.getZ() + rand.nextFloat();

            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, flX, flY, flZ, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (state.getValue(ACTIVE)) {
            emitGearboxParticles(world, pos, rand);

            if (rand.nextInt(10) == 0) {
                TileGearbox tile = getTile(world, pos);
                if (tile != null && tile.isOverpowered())
                    world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.ENTITY_ZOMBIE_ATTACK_DOOR_WOOD, SoundCategory.BLOCKS, 0.25F, world.rand.nextFloat() * 0.25F + 0.25F, true);
            }
            if (rand.nextInt(50) == 0) {
                world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, BWSounds.WOODCREAK, SoundCategory.BLOCKS, 0.25F, world.rand.nextFloat() * 0.25F + 0.25F, false);
            }

        }
    }


    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        boolean[] dirs = new boolean[6];
        for (int i = 0; i < 6; i++) {
            EnumFacing facing = EnumFacing.byIndex(i);
            dirs[i] = BWMAPI.IMPLEMENTATION.isAxle(world, pos.offset(facing), facing.getOpposite()) && this.getFacing(world, pos) != facing;
        }
        return state.withProperty(DirUtils.DOWN, dirs[0]).withProperty(DirUtils.UP, dirs[1]).withProperty(DirUtils.NORTH, dirs[2]).withProperty(DirUtils.SOUTH, dirs[3]).withProperty(DirUtils.WEST, dirs[4]).withProperty(DirUtils.EAST, dirs[5]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int facing = state.getValue(DirUtils.FACING).getIndex();
        int active = isActive(state) ? 1 : 0;
        return active | facing << 1;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(ACTIVE, (meta & 1) == 1).withProperty(DirUtils.FACING, EnumFacing.byIndex(meta >> 1));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DirUtils.FACING, ACTIVE, DirUtils.UP, DirUtils.DOWN, DirUtils.NORTH, DirUtils.SOUTH, DirUtils.WEST, DirUtils.EAST);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World worldIn, BlockPos pos) {
        return isActive(state) ? 15 : 0;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileGearbox(maxPower);
    }

    public Optional<TileGearbox> withTile(World world, BlockPos pos) {
        return Optional.of(getTile(world, pos));
    }

    public TileGearbox getTile(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileGearbox)
            return (TileGearbox) tile;
        return null;
    }

    @Override
    public void onChangeActive(World world, BlockPos pos, boolean newValue) {
        if (newValue) {
            world.playSound(null, pos, BWSounds.WOODCREAK, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.25F + 0.25F);
        }
    }

    @Override
    public void overpower(World world, BlockPos pos) {
        overpowerSound(world, pos);
        EnumFacing facing = world.getBlockState(pos).getValue(DirUtils.FACING);
        Block block = type == EnumTier.WOOD ? BWMBlocks.WOODEN_BROKEN_GEARBOX : BWMBlocks.STEEL_BROKEN_GEARBOX;
        world.setBlockState(pos, block.getDefaultState().withProperty(DirUtils.FACING, facing));
    }


    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        if (type == EnumTier.STEEL)
            return 4000f;
        return 0;
    }

    @Override
    public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
        if (type == EnumTier.STEEL)
            return 100f;
        return 3.5f;
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        if (type == EnumTier.STEEL)
            return SoundType.METAL;
        return SoundType.WOOD;
    }

    @Override
    public Material getMaterial(IBlockState state) {
        if (type == EnumTier.STEEL)
            return Material.IRON;
        return Material.WOOD;
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return type != EnumTier.STEEL || entity instanceof EntityPlayer;
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
        if (type != EnumTier.STEEL) {
            super.onBlockExploded(world, pos, explosion);
        }
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        EnumFacing facing = getFacingFromState(state);
        if (facing.getAxis().isHorizontal())
            return state.withProperty(DirUtils.FACING, rot.rotate(facing));
        return state;
    }

    @Override
    public IBlockState getStateForAdvancedRotationPlacement(IBlockState defaultState, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = defaultState;
        float hitXFromCenter = hitX - 0.5F;
        float hitYFromCenter = hitY - 0.5F;
        float hitZFromCenter = hitZ - 0.5F;
        EnumFacing newFacing = facing;
        switch (facing.getAxis()) {
            case Y:
                if (inCenter(hitXFromCenter, hitZFromCenter, 1 / 16f)) {
                    newFacing = facing.getOpposite();
                } else if (isMax(hitXFromCenter, hitZFromCenter)) {
                    newFacing = ((hitXFromCenter > 0) ? EnumFacing.EAST : EnumFacing.WEST);
                } else {
                    newFacing = ((hitZFromCenter > 0) ? EnumFacing.SOUTH : EnumFacing.NORTH);
                }
                break;
            case X:
                if (inCenter(hitYFromCenter, hitZFromCenter, 1 / 16f)) {
                    newFacing = facing.getOpposite();
                } else if (isMax(hitYFromCenter, hitZFromCenter)) {
                    newFacing = ((hitYFromCenter > 0) ? EnumFacing.UP : EnumFacing.DOWN);
                } else {
                    newFacing = ((hitZFromCenter > 0) ? EnumFacing.SOUTH : EnumFacing.NORTH);
                }
                break;
            case Z:
                if (inCenter(hitYFromCenter, hitXFromCenter, 1 / 16f)) {
                    newFacing = facing.getOpposite();
                } else if (isMax(hitYFromCenter, hitXFromCenter)) {
                    newFacing = ((hitYFromCenter > 0) ? EnumFacing.UP : EnumFacing.DOWN);
                } else {
                    newFacing = ((hitXFromCenter > 0) ? EnumFacing.EAST : EnumFacing.WEST);
                }
                break;
        }

        return state.withProperty(DirUtils.FACING, newFacing);

    }


    @Override
    public AxisAlignedBB getBounds(World world, BlockPos pos, EnumFacing facing, float flX, float flY, float flZ, ItemStack stack, EntityLivingBase placer) {
        return getStateForAdvancedRotationPlacement(getDefaultState(), facing, flX, flY, flZ).getBoundingBox(world, pos);
    }

    @Override
    public RenderFunction getRenderFunction() {
        return ClientEventHandler::renderBasicGrid;
    }
}
