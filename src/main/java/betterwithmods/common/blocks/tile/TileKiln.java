package betterwithmods.common.blocks.tile;

/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * <p>
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * <p>
 * File Created @ [Jun 7, 2014, 2:21:28 PM (GMT)]
 */


import betterwithmods.common.BWRegistry;
import betterwithmods.common.blocks.BlockKiln;
import betterwithmods.common.registry.block.recipe.KilnRecipe;
import betterwithmods.util.InvUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

//TODO REDO THIS.
public class TileKiln extends TileBasic {

    private static final String TAG_CAMO = "camo";
    private static final String TAG_CAMO_META = "camoMeta";

    public IBlockState camoState;


    public void kiln(World world, BlockPos pos, Random rand) {
        if (getBlockType() instanceof BlockKiln) {
            BlockKiln block = (BlockKiln) getBlockType();
            BlockPos kilnPos = pos.down();
            int oldCookTime = block.getCookCounter(world, kilnPos);
            int currentTickRate = 20;

            IBlockState state = world.getBlockState(pos);
            KilnRecipe recipe = BWRegistry.KILN.findRecipe(world, pos, state).orElse(null);

            if (recipe != null) {
                int newCookTime = oldCookTime + 1;
                if (newCookTime > 7) {
                    newCookTime = 0;
                    recipe.craftRecipe(world, pos, rand, state);
                    block.setCookCounter(world, kilnPos, 0);
                } else {
                    if (newCookTime > 0) {
                        world.sendBlockBreakProgress(0, pos, newCookTime + 2);
                    }
                    currentTickRate = block.calculateTickRate(world, kilnPos);
                }
                block.setCookCounter(world, kilnPos, newCookTime);
                if (newCookTime == 0) {
                    world.sendBlockBreakProgress(0, pos, -1);
                    block.setCookCounter(world, kilnPos, 0);
                    world.scheduleBlockUpdate(kilnPos, block, currentTickRate, 5);
                }
            } else if (oldCookTime != 0) {
                world.sendBlockBreakProgress(0, pos, -1);
                block.setCookCounter(world, kilnPos, 0);
                world.scheduleBlockUpdate(kilnPos, block, currentTickRate, 5);
            } else {
                world.sendBlockBreakProgress(0, pos, -1);
            }
        }

    }

    public void setCamoState(IBlockState camoState) {
        this.camoState = camoState;
        markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (camoState != null) {
            compound.setString(TAG_CAMO, Block.REGISTRY.getNameForObject(camoState.getBlock()).toString());
            compound.setInteger(TAG_CAMO_META, camoState.getBlock().getMetaFromState(camoState));
        }
        return compound;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        Block b = Block.getBlockFromName(compound.getString(TAG_CAMO));
        if (b != null) {
            camoState = b.getStateFromMeta(compound.getInteger(TAG_CAMO_META));
        }
    }

    @Override
    public void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity packet) {
        super.onDataPacket(manager, packet);
        world.markBlockRangeForRenderUpdate(pos, pos);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public void onBreak() {
        Block block = camoState.getBlock();
        int meta = block.getMetaFromState(camoState);
        InvUtils.ejectStackWithOffset(world, pos, new ItemStack(block, 1, meta));
    }
}