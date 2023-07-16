package betterwithmods.module.hardcore.needs;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWOreDictionary;
import betterwithmods.module.Feature;
import betterwithmods.util.InvUtils;
import com.google.common.collect.Sets;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by primetoxinz on 5/21/17.
 */
public class HCSeeds extends Feature {
    private static final Random RANDOM = new Random();
    public static Set<ItemStack> SEED_BLACKLIST;
    public static Set<ItemStack> EXCEPTIONS;
    public static Set<IBlockState> BLOCKS_TO_STOP = Sets.newHashSet();
    private static boolean stopZombieCropLoot;

    private static Predicate<IBlockState> STOP_SEEDS = state -> {
        Block block = state.getBlock();
        return BLOCKS_TO_STOP.contains(state) || block instanceof BlockTallGrass || (block instanceof BlockDoublePlant && (state.getValue(BlockDoublePlant.VARIANT) == BlockDoublePlant.EnumPlantType.GRASS || state.getValue(BlockDoublePlant.VARIANT) == BlockDoublePlant.EnumPlantType.FERN));
    };

    @Override
    public String getFeatureDescription() {
        return "Requires Tilling the ground with a hoe to get seeds.";
    }

    @Override
    public void setupConfig() {
        stopZombieCropLoot = loadPropBool("Stop Zombie Crop Loot", "Stops Zombies from dropping potatoes or carrots", true);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        SEED_BLACKLIST = Sets.newHashSet(loadItemStackList("Seed Blacklist", "Blacklist seeds from being dropped when tilling grass. Defaulted to Wheat seeds for HCVillages.", new ItemStack[]{new ItemStack(Items.WHEAT_SEEDS)}));
        EXCEPTIONS = Sets.newHashSet(loadItemStackList("Exceptions", "Blacklist seeds from being affected by HCSeeds, meaning they will drop from tall grass normally.", new ItemStack[]{new ItemStack(Items.WHEAT_SEEDS)}));
    }

    @SubscribeEvent
    public void onHarvest(BlockEvent.HarvestDropsEvent event) {
        if (STOP_SEEDS.test(event.getState()))
            event.getDrops().removeIf(this::isReplaceable);
    }

    private boolean isReplaceable(ItemStack stack) {
        return EXCEPTIONS.stream().noneMatch(s -> InvUtils.matches(s, stack));
    }

    public NonNullList<ItemStack> getDrops(boolean isGrass, int fortune) {
        if (RANDOM.nextInt(8) != 0) return NonNullList.create();
        ItemStack seed = net.minecraftforge.common.ForgeHooks.getGrassSeed(RANDOM, 0);
        if (SEED_BLACKLIST.stream().anyMatch(s -> InvUtils.matches(s, seed)) || seed.isEmpty() || (!isGrass && seed.getItem().equals(Item.getItemFromBlock(BWMBlocks.HEMP))))
            return NonNullList.create();
        else
            return NonNullList.withSize(1, seed);
    }

    @SubscribeEvent
    public void onHoe(UseHoeEvent e) {
        if (e.getResult() == Event.Result.DENY)
            return;
        World world = e.getWorld();
        if (!world.isRemote) {
            BlockPos pos = e.getPos();
            if (world.isAirBlock(pos.up())) {
                IBlockState state = world.getBlockState(pos);
                if (state.getBlock() instanceof BlockGrass || state.getBlock() instanceof BlockDirt) {
                    InvUtils.ejectStackWithOffset(world, pos.up(), getDrops(state.getBlock() instanceof BlockGrass, 0));
                }
            }
        }
    }

    @SubscribeEvent
    public void mobDrop(LivingDropsEvent e) {
        if (!stopZombieCropLoot || !(e.getEntityLiving() instanceof EntityZombie))
            return;
        Iterator<EntityItem> iter = e.getDrops().iterator();
        EntityItem item;
        while (iter.hasNext()) {
            item = iter.next();
            ItemStack stack = item.getItem();
            if (BWOreDictionary.hasPrefix(stack, "crop"))
                iter.remove();
        }

    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }
}
