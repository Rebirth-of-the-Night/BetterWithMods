package betterwithmods.module.compat;

import betterwithmods.common.BWMRecipes;
import betterwithmods.module.CompatFeature;
import betterwithmods.module.ModuleLoader;
import betterwithmods.module.hardcore.crafting.HCRedstone;
import betterwithmods.module.hardcore.crafting.HCSaw;
import betterwithmods.module.hardcore.needs.HCTools;
import betterwithmods.module.tweaks.MobSpawning;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class Quark extends CompatFeature {

    @GameRegistry.ObjectHolder("quark:custom_chest")
    public static Block CUSTOM_CHEST = null;

    public String[] wood = {"spruce", "birch", "jungle", "acacia", "dark_oak"};

    public Quark() {
        super("quark");
        recipes();
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        if (HCTools.removeLowTools) {
            BWMRecipes.removeRecipe(new ItemStack(Items.STONE_HOE));
            BWMRecipes.removeRecipe(new ItemStack(Items.STONE_SWORD));
        }
        if (ModuleLoader.isFeatureEnabled(HCSaw.class)) {
            BWMRecipes.removeRecipe("quark:chest");
            BWMRecipes.removeRecipe("quark:chest_1");
            BWMRecipes.removeRecipe("quark:chest_2");

            //Chests made of logs
            BWMRecipes.removeRecipe(Pattern.compile("quark:custom_chest_([13579])"));
            //Trapdoors
            BWMRecipes.removeRecipe(Pattern.compile("quark:.*trapdoor"));
        }

        if(ModuleLoader.isFeatureEnabled(HCRedstone.class)) {
            BWMRecipes.removeRecipe("quark:hopper");
        }
    }

    @Override
    public void init(FMLInitializationEvent event) {
        MobSpawning.NETHER.addBlock(getBlock(new ResourceLocation(modid, "basalt")));
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {


    }

    private IRecipe addHERecipe(IRecipe recipe) {
        return BWMRecipes.addHardcoreRecipe("quark", recipe);
    }

}
