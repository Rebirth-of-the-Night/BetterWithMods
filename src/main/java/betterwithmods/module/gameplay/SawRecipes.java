package betterwithmods.module.gameplay;

import betterwithmods.api.recipe.impl.RandomCountOutputs;
import betterwithmods.api.recipe.impl.RandomOutput;
import betterwithmods.api.util.IWood;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWOreDictionary;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.registry.block.recipe.BlockDropIngredient;
import betterwithmods.common.registry.block.recipe.BlockIngredient;
import betterwithmods.common.registry.block.recipe.SawRecipe;
import betterwithmods.module.Feature;
import com.google.common.collect.Lists;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

/**
 * Created by primetoxinz on 5/16/17.
 */
public class SawRecipes extends Feature {
    private static int plankCount, barkCount, sawDustCount;

    public SawRecipes() {
        canDisable = false;
    }

    @Override
    public void setupConfig() {
        plankCount = loadPropInt("Saw Plank Output", "Plank count that is output when a log is chopped by a Saw.", 4);
        barkCount = loadPropInt("Saw Bark Output", "Bark count that is output when a log is chopped by a Saw.", 1);
        sawDustCount = loadPropInt("Saw sawdust Output", "Sawdust count that is output when a log is chopped by a Saw.", 2);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        BWRegistry.WOOD_SAW.addSelfdropRecipe(new ItemStack(Blocks.PUMPKIN));
        BWRegistry.WOOD_SAW.addSelfdropRecipe(new ItemStack(Blocks.VINE));
        BWRegistry.WOOD_SAW.addSelfdropRecipe(new ItemStack(Blocks.YELLOW_FLOWER));
        BWRegistry.WOOD_SAW.addSelfdropRecipe(new ItemStack(Blocks.BROWN_MUSHROOM));
        BWRegistry.WOOD_SAW.addSelfdropRecipe(new ItemStack(Blocks.RED_MUSHROOM));
        BWRegistry.WOOD_SAW.addSelfdropRecipe(new ItemStack(BWMBlocks.ROPE));
        for (int i = 0; i < 9; i++)
            BWRegistry.WOOD_SAW.addSelfdropRecipe(new ItemStack(Blocks.RED_FLOWER, 1, i));
        BWRegistry.WOOD_SAW.addRecipe(new SawRecipe(
                new BlockIngredient(new ItemStack(Blocks.MELON_BLOCK)),
                new RandomCountOutputs(new RandomOutput(new ItemStack(Items.MELON), 3, 8))));
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        for (IWood wood : BWOreDictionary.woods) {
            BWRegistry.WOOD_SAW.addRecipe(new BlockDropIngredient(wood.getLog(1)), Lists.newArrayList(wood.getPlank(plankCount), wood.getBark(barkCount), wood.getSawdust(sawDustCount)));
        }
    }


}
