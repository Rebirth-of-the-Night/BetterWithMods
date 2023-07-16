package betterwithmods.module.gameplay;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWRegistry;
import betterwithmods.module.Feature;
import betterwithmods.util.StackIngredient;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.oredict.OreIngredient;

/**
 * Created by primetoxinz on 5/16/17.
 */
public class CrucibleRecipes extends Feature {
    public CrucibleRecipes() {
        canDisable = false;
    }

    @Override
    public void init(FMLInitializationEvent event) {

        BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.STONE));
        BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(BWMBlocks.COBBLE, 1, 0), new ItemStack(Blocks.STONE,1,1));
        BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(BWMBlocks.COBBLE, 1, 1), new ItemStack(Blocks.STONE,1,3));
        BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(BWMBlocks.COBBLE, 1, 2), new ItemStack(Blocks.STONE,1,5));
        BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(BWMBlocks.AESTHETIC, 1, 7), new ItemStack(BWMBlocks.AESTHETIC, 1, 6));
        BWRegistry.CRUCIBLE.addStokedRecipe(new OreIngredient("sand"), new ItemStack(Blocks.GLASS));
        BWRegistry.CRUCIBLE.addStokedRecipe(StackIngredient.fromStacks(new ItemStack(Blocks.GLASS_PANE,8)), new ItemStack(Blocks.GLASS,3));
    }

}
