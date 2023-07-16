package betterwithmods.module.tweaks;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWMRecipes;
import betterwithmods.common.blocks.BlockMushroom;
import betterwithmods.module.Feature;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.HashSet;

//This is far too unstable, it is crashing many mods that reference the Blocks field of the mushrooms, removed from the tweaks registry.
@Deprecated
public class MushroomFarming extends Feature {
    public static boolean ALIAS_MUSHROOMS;
    public static boolean SPREAD_ON_MYCELLIUM;
    public static boolean GROW_FAST_ON_DUNG;
    public static int MAX_LIGHT_LEVEL_RED;
    public static int MAX_LIGHT_LEVEL_BROWN;
    public static int MAX_LIGHT_LEVEL_MISC;
    public static HashSet<String> MISC_MUSHROOMS;

    @GameRegistry.ObjectHolder("minecraft:red_mushroom")
    public static Block RED_MUSHROOM;

    @GameRegistry.ObjectHolder("minecraft:brown_mushroom")
    public static Block BROWN_MUSHROOM;

    @Override
    public void setupConfig() {
        MAX_LIGHT_LEVEL_BROWN = loadPropInt("Maximum Light Level Brown", "The highest lightlevel at which brown mushrooms will grow.", 0);
        MAX_LIGHT_LEVEL_RED = loadPropInt("Maximum Light Level Red", "The highest lightlevel at which red mushrooms will grow.", 12);
        MAX_LIGHT_LEVEL_MISC = loadPropInt("Maximum Light Level Misc", "The highest lightlevel at which other mushrooms (see Valid Other Mushrooms) will grow.", 0);
        MISC_MUSHROOMS = loadPropStringHashSet("Valid Other Mushrooms","Registry names of affected mushrooms other than vanilla ones.",new String[]{});
        SPREAD_ON_MYCELLIUM = loadPropBool("Spread On Mycellium","Whether mushrooms can spread on mycellium even at a higher light level",false);
        GROW_FAST_ON_DUNG = loadPropBool("Grow Faster On Dung","Whether mushrooms grow faster on dung blocks",false);
        ALIAS_MUSHROOMS = loadPropBool("Alias Mushrooms","Aliases vanilla mushrooms to truly prevent them from growing. Turn this off if it causes conflicts.",true);
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        if(ALIAS_MUSHROOMS) {
            RED_MUSHROOM = new BlockMushroom(MAX_LIGHT_LEVEL_RED).setRegistryName("minecraft:red_mushroom");
            BROWN_MUSHROOM = new BlockMushroom(MAX_LIGHT_LEVEL_BROWN).setRegistryName("minecraft:brown_mushroom");
            BWMBlocks.registerBlock(RED_MUSHROOM,null);
            BWMBlocks.registerBlock(BROWN_MUSHROOM,null);
        }
    }

    @Override
    public void init(FMLInitializationEvent event) {
        Blocks.BROWN_MUSHROOM.setLightLevel(0);
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @Override
    public String getFeatureDescription() {
        return "Brown mushrooms can only be farmed in complete darkness.";
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void placeMushroom(BlockEvent.PlaceEvent event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState state = event.getPlacedBlock();

        if(isMushroom(state) && !canGrowMushroom(world,pos)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void neighborUpdate(BlockEvent.NeighborNotifyEvent event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();

        if(!world.isRemote) {
            popOffMushroom(world,pos);
            popOffMushroom(world,pos.up());
        }
    }

    private boolean isMushroom(IBlockState state) {
        ResourceLocation loc = state.getBlock().getRegistryName();
        if(loc == null) //WEE WOO WEE WOO
            throw new IllegalStateException("BetterWithMods Handler ("+this.getClass().getSimpleName()+") obtained an unregistered block from a blockstate! (Block -> "+state.getBlock().getClass().getName()+")");
        return MISC_MUSHROOMS.contains(loc.toString());
    }

    private void popOffMushroom(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        if(isMushroom(state) && !canGrowMushroom(world,pos)) {
            state.getBlock().dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    private boolean canGrowMushroom(World world,BlockPos pos) {
        int light = world.getLight(pos);
        IBlockState soil = world.getBlockState(pos.down());

        return light <= MAX_LIGHT_LEVEL_MISC || isMushroomSoil(soil);
    }

    public static boolean isMushroomSoil(IBlockState state) {
        if (state.getBlock() == Blocks.MYCELIUM)
            return true;
        else if (state.getBlock() == Blocks.DIRT && state.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.PODZOL)
            return true;
        return false;
    }

    @SubscribeEvent
    public void onHarvest(BlockEvent.HarvestDropsEvent event) {
        if(event.getState().getBlock() instanceof BlockMushroom) {
            event.getDrops().clear();
            event.getDrops().add(BWMRecipes.getStackFromState(event.getState()));
        }
    }
}
