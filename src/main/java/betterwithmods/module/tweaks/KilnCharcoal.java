package betterwithmods.module.tweaks;

import betterwithmods.common.BWMRecipes;
import betterwithmods.common.BWRegistry;
import betterwithmods.module.Feature;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;


/**
 * Created by primetoxinz on 5/16/17.
 */
public class KilnCharcoal extends Feature {

    private boolean disableFurnaceCharcoal;

    @Override
    public void setupConfig() {
        disableFurnaceCharcoal = loadPropBool("Disable Furnace Charcoal", "Remove recipes to make Charcoal in a Furnace", true);
    }

    @Override
    public String getFeatureDescription() {
        return "Add Charcoal smelting to the Kiln";
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        List<ItemStack> logs = Lists.newArrayList();
        logs.addAll(OreDictionary.getOres("logWood"));

        for (ItemStack stack : logs) {
            if (stack.getItem() instanceof ItemBlock) {
                ItemStack charcoalOutput = FurnaceRecipes.instance().getSmeltingResult(stack).copy();
                if (charcoalOutput.isEmpty())
                    continue;
                if (disableFurnaceCharcoal)
                    BWMRecipes.removeFurnaceRecipe(stack);
                BWRegistry.KILN.addStokedRecipe(stack,charcoalOutput);
            }
        }
    }

}
