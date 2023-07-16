package betterwithmods.module.gameplay.miniblocks.blocks;

import betterwithmods.api.block.IRenderRotationPlacement;
import betterwithmods.client.ClientEventHandler;
import betterwithmods.client.baking.UnlistedPropertyGeneric;
import betterwithmods.common.blocks.BlockRotate;
import betterwithmods.module.gameplay.miniblocks.MiniBlocks;
import betterwithmods.module.gameplay.miniblocks.client.MiniCacheInfo;
import betterwithmods.module.gameplay.miniblocks.orientations.BaseOrientation;
import betterwithmods.module.gameplay.miniblocks.tiles.TileMini;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class BlockMini extends BlockRotate implements IRenderRotationPlacement {

    public static final IUnlistedProperty<MiniCacheInfo> MINI_INFO = new UnlistedPropertyGeneric<>("mini", MiniCacheInfo.class);


    public BlockMini(Material material) {
        super(material);
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        return getTile(worldIn, pos).map(t -> t.getState().getBlockHardness(worldIn, pos)).orElse(super.getBlockHardness(blockState, worldIn, pos));
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        return getTile(world, pos).map(t -> t.getState().getBlock().getExplosionResistance(world, pos, exploder, explosion)).orElse(super.getExplosionResistance(world, pos, exploder, explosion));
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.addAll(MiniBlocks.MATERIALS.get(material).stream().sorted(this::compareBlockStates).map(state -> MiniBlocks.fromParent(this, state)).collect(Collectors.toList()));
    }

    private int compareBlockStates(IBlockState a, IBlockState b) {
        Block blockA = a.getBlock();
        Block blockB = b.getBlock();
        int compare = Integer.compare(Block.getIdFromBlock(blockA), Block.getIdFromBlock(blockB));
        if (compare == 0)
            return Integer.compare(blockA.getMetaFromState(a), blockB.getMetaFromState(b));
        else
            return compare;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{MINI_INFO});
    }

    @Nullable
    @Override
    public abstract TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state);

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nonnull
    @Override
    public IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState extendedBS = (IExtendedBlockState) super.getExtendedState(state, world, pos);
        return getTile(world, pos).map(t -> extendedBS.withProperty(MINI_INFO, MiniCacheInfo.from(t))).orElse(extendedBS);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public Optional<TileMini> getTile(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMini)
            return Optional.of((TileMini) tile);
        return Optional.empty();
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return getTile(source, pos).map(t -> t.getOrientation().getBounds()).orElse(Block.FULL_BLOCK_AABB);
    }


    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    }

    @Override
    public void nextState(World world, BlockPos pos, IBlockState state) {
        rotateBlock(world, pos, EnumFacing.UP);
    }

    @Override
    public boolean rotateBlock(World world, @Nonnull BlockPos pos, @Nonnull EnumFacing axis) {
        return getTile(world, pos).map(t -> t.changeOrientation(t.getOrientation().next(), false)).orElse(false);
    }

    @Override
    public RenderFunction getRenderFunction() {
        return ClientEventHandler::renderMiniBlock;
    }

    public abstract BaseOrientation getOrientationFromPlacement(EntityLivingBase placer, @Nullable EnumFacing face, ItemStack stack, float hitX, float hitY, float hitZ);

    @Override
    public AxisAlignedBB getBounds(World world, BlockPos pos, EnumFacing facing, float flX, float flY, float flZ, ItemStack stack, EntityLivingBase placer) {
        return getOrientationFromPlacement(placer, facing, stack, flX, flY, flZ).getBounds();
    }

    @Override
    public void getDrops(@Nonnull NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
        drops.add(getPickBlock(state, null, (World) world, pos, null));
    }

    @Override
    public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
        if (willHarvest) return true; //If it will harvest, delay deletion of the block until after getDrops
        return super.removedByPlayer(state, world, pos, player, false);
    }

    @Override
    public void harvestBlock(@Nonnull World world, EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity te, ItemStack tool) {
        super.harvestBlock(world, player, pos, state, te, tool);
        world.setBlockToAir(pos);
    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
        return getTile(world, pos).map(t -> t.getPickBlock(player, target, state)).orElse(new ItemStack(this));
    }

    @Override
    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return getTile(world, pos).map(t -> {
            try {
                return t.getState().getBlock().isFlammable(world, pos, face);
            } catch (IllegalArgumentException e) { // Likely BoP crash
                return Blocks.PLANKS.isFlammable(world, pos, face);
            }
        }).orElse(false);
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return getTile(world, pos).map(t -> {
            try {
                return t.getState().getBlock().getFireSpreadSpeed(world, pos, face);
            } catch (IllegalArgumentException e) { // Again, likely BoP crash
                return Blocks.PLANKS.getFireSpreadSpeed(world, pos, face);
            }
        }).orElse(5);
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return getTile(world, pos).map(t -> {
            try {
                return t.getState().getBlock().getFlammability(world, pos, face);
            } catch (IllegalArgumentException e) { // Same, BoP crash, yes, I know there's already a catch down there
                return Blocks.PLANKS.getFlammability(world, pos, face);
            } catch (Exception e) {
                return null;
            }
        }).orElse(10);
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public EnumPushReaction getPushReaction(IBlockState state) {
        if(state instanceof IExtendedBlockState) {
            IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
            MiniCacheInfo info = extendedBlockState.getValue(MINI_INFO);
            if(info != null) {
                IBlockState parent = info.getState();
                if(parent != null)
                    return parent.getPushReaction();
            }
        }
        return super.getPushReaction(state);
    }
}


