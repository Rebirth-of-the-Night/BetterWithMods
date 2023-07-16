package betterwithmods.module.gameplay;

import betterwithmods.api.recipe.IRecipeOutputs;
import betterwithmods.api.recipe.impl.ChanceOutputs;
import betterwithmods.api.recipe.impl.CombinedOutputs;
import betterwithmods.api.recipe.impl.ListOutputs;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.blocks.BlockAesthetic;
import betterwithmods.common.items.ItemMaterial;
import betterwithmods.module.Feature;
import betterwithmods.util.StackIngredient;
import com.google.common.collect.Lists;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.oredict.OreIngredient;

/**
 * Created by primetoxinz on 5/10/17.
 */
public class HarderSteelRecipe extends Feature {

    public static double urnReturnChance;

    @Override
    public void setupConfig() {
        urnReturnChance = loadPropDouble("Urn Return Chance", "Percent chance (0.0-1.0) that the urn is returned when creating the steel.", 0.75);
    }

    @Override
    public String getFeatureDescription() {
        return "Whether Steel requires End Slag, a material only available after the End.";
    }

    @Override
    public void init(FMLInitializationEvent event) {
        BWRegistry.CRUCIBLE.addStokedRecipe(StackIngredient.fromStacks(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.ENDER_SLAG)), Lists.newArrayList(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.BRIMSTONE), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SOUL_FLUX)));
        BWRegistry.CRUCIBLE.addStokedRecipe(Lists.newArrayList(new OreIngredient("blockSoulUrn"), new OreIngredient("ingotIron"), new OreIngredient("dustCarbon"),StackIngredient.fromStacks(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SOUL_FLUX))), getOutputs());
        BWRegistry.KILN.addStokedRecipe(new ItemStack(Blocks.END_STONE), Lists.newArrayList(BlockAesthetic.getStack(BlockAesthetic.EnumType.WHITECOBBLE,1),ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.ENDER_SLAG)));
    }

    @Override
    public void disabledInit(FMLInitializationEvent event) {
        BWRegistry.CRUCIBLE.addStokedRecipe(StackIngredient.fromStacks(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.ENDER_SLAG)), Lists.newArrayList(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.BRIMSTONE)));
        BWRegistry.CRUCIBLE.addStokedRecipe(Lists.newArrayList(new OreIngredient("blockSoulUrn"), new OreIngredient("ingotIron"), new OreIngredient("dustCarbon")), getOutputs());
        BWRegistry.KILN.addStokedRecipe(new ItemStack(Blocks.END_STONE), Lists.newArrayList(BlockAesthetic.getStack(BlockAesthetic.EnumType.WHITECOBBLE,1), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.BRIMSTONE)));
    }

    public IRecipeOutputs getOutputs() {
        return new CombinedOutputs(new ListOutputs(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.INGOT_STEEL)), new ChanceOutputs(new ItemStack(BWMBlocks.URN, 1, 0), urnReturnChance));
    }
}
