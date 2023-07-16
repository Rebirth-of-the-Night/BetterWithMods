package betterwithmods.common.blocks;

import betterwithmods.api.block.PropertyObject;
import betterwithmods.common.BWMRecipes;
import betterwithmods.common.blocks.tile.TileKiln;
import betterwithmods.common.registry.KilnStructureManager;
import betterwithmods.common.registry.heat.BWMHeatRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockKiln extends BWMBlock {
    public static final PropertyInteger COOK = PropertyInteger.create("cook", 0, 8);
    public static final PropertyObject<IBlockState> HELD_STATE = new PropertyObject<>("held_state", IBlockState.class);
    public static final PropertyObject<IBlockAccess> HELD_WORLD = new PropertyObject<>("held_world", IBlockAccess.class);
    public static final PropertyObject<BlockPos> HELD_POS = new PropertyObject<>("held_pos", BlockPos.class);

    public BlockKiln() {
        super(Material.ROCK);
        this.setTickRandomly(true);
        this.setHardness(2.0F);
        this.setResistance(10.0F);
        this.setHarvestLevel("pickaxe", 0);
    }

    @Override
    public int tickRate(World world) {
        return 20;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        world.scheduleBlockUpdate(pos, this, 20, 5);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!KilnStructureManager.isValidKiln(world, pos)) {
            IExtendedBlockState extendedBlockState = (IExtendedBlockState) getExtendedState(state, world, pos);
            if (state instanceof IExtendedBlockState) {
                IBlockState held_state = extendedBlockState.getValue(HELD_STATE);
                if (held_state != null) {
                    world.removeTileEntity(pos);
                    world.setBlockState(pos, held_state);
                    return;
                }
            }
            world.setBlockToAir(pos);
            return;
        }

        BlockPos craftPos = pos.up();
        if (!world.isAirBlock(craftPos)) {
            TileKiln tile = getTile(world, pos);
            if (tile != null) {
                tile.kiln(world, craftPos, rand);
            }
        }

        world.scheduleBlockUpdate(pos, this, 20, 5);
    }

    public int calculateTickRate(World world, BlockPos pos) {
        int secondaryFire = 0;
        int centerFire = BWMHeatRegistry.getHeat(world, pos.down());
        for (int xP = -1; xP < 2; xP++) {
            for (int zP = -1; zP < 2; zP++) {
                BlockPos bPos = pos.add(xP, -1, zP);
                int currentFire = BWMHeatRegistry.getHeat(world, bPos);
                if (currentFire == centerFire)
                    secondaryFire += currentFire;
            }
        }
        return Math.max(0, Math.max(0, 60 * (8 - Math.max(secondaryFire, 0))) / 8 + 20);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos other) {
        if(world.isAirBlock(pos.up())) {
            setCookCounter(world, pos, 0);
        }
    }


    public int getCookCounterFromMeta(int meta) {
        return meta & 0x7;
    }

    public int getCookCounter(IBlockAccess world, BlockPos pos) {
        return getCookCounterFromMeta(world.getBlockState(pos).getValue(COOK));
    }

    public void setCookCounter(World world, BlockPos pos, int cookCounter) {
        world.setBlockState(pos, world.getBlockState(pos).withProperty(COOK, cookCounter));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(COOK, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(COOK);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[]{COOK}, new IUnlistedProperty[]{HELD_WORLD, HELD_POS, HELD_STATE});
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        state = ((IExtendedBlockState) state).withProperty(HELD_WORLD, world)
                .withProperty(HELD_POS, pos);
        TileEntity te = world instanceof ChunkCache ? ((ChunkCache) world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
        if (te instanceof TileKiln) {
            TileKiln tile = (TileKiln) te;
            IExtendedBlockState camo = ((IExtendedBlockState) state).withProperty(HELD_STATE, tile.camoState);
            return camo;
        } else {
            return state;
        }
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileKiln();
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        IExtendedBlockState extendedBlockState = (IExtendedBlockState) getExtendedState(state, world, pos);
        return BWMRecipes.getStackFromState(extendedBlockState.getValue(HELD_STATE));
    }

    public TileKiln getTile(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileKiln)
            return (TileKiln) tile;
        return null;
    }
}
