package betterwithmods.module.tweaks;

import betterwithmods.common.BWOreDictionary;
import betterwithmods.common.BWRegistry;
import betterwithmods.module.Feature;
import betterwithmods.util.InvUtils;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import java.util.Arrays;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class KilnSmelting extends Feature {
    private static int oreProductionCount;

    @Override
    public void setupConfig() {
        oreProductionCount = loadPropInt("Ore Production Count", "Number of Materials returned from Smelting an Ore in the Kiln", 1);
    }

    @Override
    public void finalInit(FMLPostInitializationEvent event) {
        BWOreDictionary.oreNames.stream().flatMap(ore -> Arrays.stream(ore.getMatchingStacks())).filter(s -> s.getItem() instanceof ItemBlock).forEach(input -> {
            ItemStack output = FurnaceRecipes.instance().getSmeltingResult(input).copy();
            BWRegistry.KILN.addStokedRecipe(input, InvUtils.setCount(output, oreProductionCount));
        });
    }

    @Override
    public String getFeatureDescription() {
        return "Allows Kiln to Smelt Ores";
    }
}
