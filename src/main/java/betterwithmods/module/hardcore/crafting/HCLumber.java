package betterwithmods.module.hardcore.crafting;

import betterwithmods.api.util.IWood;
import betterwithmods.common.BWMRecipes;
import betterwithmods.common.BWOreDictionary;
import betterwithmods.common.registry.BrokenToolRegistry;
import betterwithmods.module.Feature;
import betterwithmods.util.player.PlayerHelper;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class HCLumber extends Feature {
    public static int plankAmount, barkAmount, sawDustAmount;

    public static int axePlankAmount = 4, axeBarkAmount = 1, axeSawDustAmount = 2;

    public static boolean hasAxe(EntityPlayer harvester, BlockPos pos, IBlockState state) {
        if (harvester == null)
            return false;
        return PlayerHelper.isCurrentToolEffectiveOnBlock(harvester, pos, state);
    }

    @Override
    public void setupConfig() {
        plankAmount = loadPropInt("Plank Amount", "Amount of Planks dropped when Punching Wood", 2);
        barkAmount = loadPropInt("Bark Amount", "Amount of Bark dropped when Punching Wood", 1);
        sawDustAmount = loadPropInt("Sawdust Amount", "Amount of Sawdust dropped when Punching Wood", 2);

        axePlankAmount = loadPropInt("Axe Plank Amount", "Amount of Planks dropped when crafted with an axe", 3);
        axeBarkAmount = loadPropInt("Axe Bark Amount", "Amount of Bark dropped when crafted with an axe", 1);
        axeSawDustAmount = loadPropInt("Axe Sawdust Amount", "Amount of Sawdust dropped when crafted with an axe", 2);
    }

    @Override
    public String getFeatureDescription() {
        return "Makes Punching Wood return a single plank and secondary drops instead of a log, to get a log an axe must be used.";
    }

    @Override
    public void init(FMLInitializationEvent event) {
        BrokenToolRegistry.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        for (IRecipe recipe : BWOreDictionary.logRecipes) {
            BWMRecipes.removeRecipe(recipe);
        }
    }

    @Override
    public boolean requiresMinecraftRestartToEnable() {
        return true;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void harvestLog(BlockEvent.HarvestDropsEvent event) {
        if (!event.getWorld().isRemote) {
            IWood wood = BWOreDictionary.getWoodFromState(event.getState());
            if (wood != null) {
                if (event.isSilkTouching() || hasAxe(event.getHarvester(), event.getPos(), event.getState()))
                    return;
                event.setDropChance(1);
                event.getDrops().clear();
                event.getDrops().addAll(Lists.newArrayList(wood.getPlank(plankAmount), wood.getSawdust(sawDustAmount), wood.getBark(barkAmount)));
            }
        }
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }
}
