package betterwithmods.common.blocks.mechanical;

import betterwithmods.api.block.IMultiVariants;
import betterwithmods.api.block.IOverpower;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWMItems;
import betterwithmods.common.BWSounds;
import betterwithmods.common.blocks.BlockRotate;
import betterwithmods.common.blocks.EnumTier;
import betterwithmods.common.blocks.mechanical.tile.TileBellows;
import betterwithmods.common.registry.BellowsManager;
import betterwithmods.util.DirUtils;
import betterwithmods.util.InvUtils;
import betterwithmods.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class BlockBellows extends BlockRotate implements IBlockActive, IOverpower, IMultiVariants {

    public BlockBellows() {
        super(Material.WOOD);
        this.setTickRandomly(true);
        this.setHardness(2.0F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(DirUtils.HORIZONTAL, EnumFacing.SOUTH).withProperty(ACTIVE, false).withProperty(EnumTier.TIER, EnumTier.WOOD));
        this.setSoundType(SoundType.WOOD);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileBellows();
    }

    @Override
    public int tickRate(World world) {
        return 37;
    }

    @Override
    public void nextState(World world, BlockPos pos, IBlockState state) {
        world.setBlockState(pos, state.cycleProperty(DirUtils.HORIZONTAL).withProperty(ACTIVE, false));
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return state.getBlock() == this && !state.getValue(ACTIVE);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return state.getBlock() == this && !state.getValue(ACTIVE);
    }

    @Deprecated
    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float flX, float flY, float flZ,
                                            int meta, EntityLivingBase living, EnumHand hand) {
        IBlockState state = super.getStateForPlacement(world, pos, side, flX, flY, flZ, meta, living, hand);
        return setFacingInBlock(state, living.getHorizontalFacing()).withProperty(ACTIVE, false).withProperty(EnumTier.TIER, EnumTier.VALUES[meta]);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(EnumTier.TIER).ordinal();
    }

    public IBlockState setFacingInBlock(IBlockState state, EnumFacing facing) {
        return state.withProperty(DirUtils.HORIZONTAL, facing);
    }

    public EnumTier getTier(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        if (state.getPropertyKeys().contains(EnumTier.TIER)) {
            return state.getValue(EnumTier.TIER);
        }
        return null;
    }

    public EnumFacing getFacing(IBlockState state) {
        return state.getValue(DirUtils.HORIZONTAL);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity,
                                ItemStack stack) {
        if (entity == null)
            setFacingInBlock(state, EnumFacing.NORTH);
        EnumFacing facing = DirUtils.convertEntityOrientationToFlatFacing(entity);
        setFacingInBlock(state, facing);
        world.scheduleBlockUpdate(pos, this, 10, 5);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state.getValue(ACTIVE))
            return new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.6875F, 1.0F);
        return FULL_BLOCK_AABB;
    }

    @Override
    public void onChangeActive(World world, BlockPos pos, boolean active) {
        world.scheduleBlockUpdate(pos, this, tickRate(world), 5);
        if (active) {
            world.playSound(null, pos, BWSounds.BELLOW, SoundCategory.BLOCKS, 0.7F, world.rand.nextFloat() * 0.25F + 2.5F);
            blow(world, pos);
        } else {
            world.playSound(null, pos, BWSounds.BELLOW, SoundCategory.BLOCKS, 0.2F, world.rand.nextFloat() * 0.25F + 2.5F);
        }
        liftCollidingEntities(world, pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos other) {
        withTile(world, pos).ifPresent(TileBellows::onChange);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        withTile(world, pos).ifPresent(TileBellows::onChange);
    }

    @Override
    public void overpower(World world, BlockPos pos) {
        breakBellows(world, pos);
    }

    public void blow(World world, BlockPos pos) {
        stokeFlames(world, pos);
        blowItems(world, pos);
    }

    public void blowItems(World world, BlockPos pos) {
        EnumFacing facing = getFacing(world.getBlockState(pos));
        BlockPos pos2 = pos.offset(facing, 4);
        AxisAlignedBB box = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos2.getX() + 1, pos2.getY() + 1, pos2.getZ() + 1);

        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, box);
        for (EntityItem item : items) {
            blowItem(pos, facing, item);
        }
    }

    private final float scale = 4 / 16f;

    public void blowItem(BlockPos pos, EnumFacing facing, EntityItem item) {
        double x = 0, z = 0;

        if (WorldUtils.getDistance(pos, item.getPosition()) > BellowsManager.getWeight(item.getItem()))
            return;
        switch (facing.getAxis()) {
            case X:
                x += facing.getAxisDirection().getOffset();
                break;
            case Z:
                z += facing.getAxisDirection().getOffset();
                break;
            default:
                break;
        }

        item.addVelocity(x * scale, 0, z * scale);
    }

    public void playStateChangeSound(World world, BlockPos pos) {
        liftCollidingEntities(world, pos);
    }

    private void stokeFlames(World world, BlockPos pos) {
        EnumFacing dir = getFacing(world.getBlockState(pos));
        EnumFacing dirLeft = DirUtils.rotateFacingAroundY(getFacing(world.getBlockState(pos)), false);
        EnumFacing dirRight = DirUtils.rotateFacingAroundY(getFacing(world.getBlockState(pos)), true);

        for (int i = 0; i < 3; i++) {
            BlockPos dirPos = pos.offset(dir, 1 + i);
            //
            Block target = world.getBlockState(dirPos).getBlock();

            if (target == Blocks.FIRE || target == BWMBlocks.STOKED_FLAME)
                stokeFire(world, dirPos);
            else if (!world.isAirBlock(dirPos))
                break;
            //
            BlockPos posLeft = dirPos.offset(dirLeft);

            Block targetLeft = world.getBlockState(posLeft).getBlock();
            if (targetLeft == Blocks.FIRE || targetLeft == BWMBlocks.STOKED_FLAME)
                stokeFire(world, posLeft);
            //
            BlockPos posRight = dirPos.offset(dirRight);

            Block targetRight = world.getBlockState(posRight).getBlock();
            if (targetRight == Blocks.FIRE || targetRight == BWMBlocks.STOKED_FLAME)
                stokeFire(world, posRight);
        }
    }

    private void stokeFire(World world, BlockPos pos) {
        BlockPos down = pos.down();
        if (world.getBlockState(down).getBlock() == BWMBlocks.HIBACHI) {
            int flag = (world.getBlockState(pos).getBlock() == BWMBlocks.STOKED_FLAME) ? 4 : 3;
            world.setBlockState(pos, BWMBlocks.STOKED_FLAME.getDefaultState(), flag);
        } else
            world.setBlockToAir(pos);
    }

    private void liftCollidingEntities(World world, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        List<Entity> list = world.getEntitiesWithinAABB(Entity.class,
                new AxisAlignedBB(x, y + 0.6875F, z, x + 1, y + 1, z + 1));

        float extendedY = y + 1;

        if (list != null && list.size() > 0) {
            for (Entity entity : list) {
                if (!entity.isDead && (entity.canBePushed() || entity instanceof EntityItem)) {
                    double tempY = entity.getEntityBoundingBox().minY;

                    if (tempY < extendedY) {
                        double entityOffset = extendedY - tempY;
                        entity.setPosition(entity.posX, entity.posY + entityOffset, entity.posZ);
                    }
                }
            }
        }
    }

    public void breakBellows(World world, BlockPos pos) {
        InvUtils.ejectStackWithOffset(world, pos, new ItemStack(Blocks.WOODEN_SLAB, 2, 0));
        InvUtils.ejectStackWithOffset(world, pos, new ItemStack(BWMItems.MATERIAL, 1, 0));
        InvUtils.ejectStackWithOffset(world, pos, new ItemStack(BWMItems.MATERIAL, 2, 6));
        world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 0.3F,
                world.rand.nextFloat() * 0.1F + 0.45F);
        world.setBlockToAir(pos);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int facing = state.getValue(DirUtils.HORIZONTAL).getHorizontalIndex();
        int tier = state.getValue(EnumTier.TIER).ordinal();
        int active = isActive(state) ? 1 : 0;
        return active | tier << 1 | facing << 2;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(ACTIVE, (meta & 1) == 1).withProperty(EnumTier.TIER, EnumTier.VALUES[(meta >> 1 & 1)]).withProperty(DirUtils.HORIZONTAL, EnumFacing.byHorizontalIndex(meta >> 2));
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DirUtils.HORIZONTAL, ACTIVE, EnumTier.TIER);
    }

    public Optional<TileBellows> withTile(World world, BlockPos pos) {
        return Optional.ofNullable(getTile(world, pos));
    }

    public TileBellows getTile(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileBellows)
            return (TileBellows) tile;
        return null;
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, 0));
        items.add(new ItemStack(this, 1, 1));
    }

    @Override
    public String[] getVariants() {
        return new String[]{"active=true,facing=north,tier=wood", "active=true,facing=north,tier=steel"};
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face != EnumFacing.DOWN && state.getValue(ACTIVE) ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
    }
}
