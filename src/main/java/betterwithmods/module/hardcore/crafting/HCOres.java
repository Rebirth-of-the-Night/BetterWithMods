package betterwithmods.module.hardcore.crafting;

import betterwithmods.common.BWMRecipes;
import betterwithmods.common.BWOreDictionary;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.registry.bulk.recipes.CookingPotRecipe;
import betterwithmods.common.registry.heat.BWMHeatRegistry;
import betterwithmods.module.Feature;
import betterwithmods.util.StackIngredient;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class HCOres extends Feature {

    private static boolean oreNuggetSmelting, dustNuggetSmelting, fixVanillaRecipes;
    private static Set<String> oreExclude, dustExclude;
    private static int oreProductionCount, dustProductionCount;

    public HCOres() {
    }

    @Override
    public void setupConfig() {
        oreNuggetSmelting = loadPropBool("Ore to Nugget Smelting", "Make Ores (oredict ore.* )smelt into nuggets instead of ingots", true);

        oreExclude = Arrays.stream(loadPropStringList("Ore Exclude", "Oredictionary entries to exclude from ore to nugget smelting. Remove the prefix of the oredictionary. example 'oreIron' would be just 'iron' ", new String[0])).collect(Collectors.toSet());
        dustExclude = Arrays.stream(loadPropStringList("Dust Exclude", "Oredictionary entries to exclude from dust to nugget smelting  Remove the prefix of the oredictionary. example 'dustIron' would be just 'iron'", new String[0])).collect(Collectors.toSet());

        dustNuggetSmelting = loadPropBool("Dust to Nugget Smelting", "Make Dusts ( oredict dust.* ) smelt into nuggets instead of ingots", true);
        fixVanillaRecipes = loadPropBool("Fix Vanilla Recipes", "Make certain recipes cheaper to be more reasonable with nugget smelting, including Compass, Clock, and Bucket", true);

        oreProductionCount = loadPropInt("Ore Production Count", "Number of Materials returned from Smelting an Ore", 1);
        dustProductionCount = loadPropInt("Dust Production Count", "Number of Materials returned from Smelting a Dust", 1);
    }

    @Override
    public String getFeatureDescription() {
        return "Makes Ores only smelt into a single nugget, making it much harder to create large amounts of metal";
    }

    @Override
    public boolean requiresMinecraftRestartToEnable() {
        return true;
    }

    @Override
    public void init(FMLInitializationEvent event) {
        if (fixVanillaRecipes) {
            addHardcoreRecipe(new ShapedOreRecipe(null, Items.COMPASS, " N ", "NRN", " N ", 'N', "nuggetIron", 'R', "dustRedstone").setRegistryName(new ResourceLocation("minecraft", "compass")));
            addHardcoreRecipe(new ShapedOreRecipe(null, Items.CLOCK, " N ", "NQN", " N ", 'N', "nuggetGold", 'Q', "gemQuartz").setRegistryName(new ResourceLocation("minecraft", "clock")));
            addHardcoreRecipe(new ShapedOreRecipe(null, Items.BUCKET, "N N", "N N", "NNN", 'N', "nuggetIron").setRegistryName(new ResourceLocation("minecraft", "bucket")));
            addHardcoreRecipe(new ShapelessOreRecipe(null, Items.FLINT_AND_STEEL, Items.FLINT, "nuggetIron").setRegistryName(new ResourceLocation("minecraft", "flint_and_steel")));
        }

        addMeltingRecipeWithoutReturn(new ItemStack(Items.BUCKET), new ItemStack(Items.IRON_NUGGET, 7));
        addMeltingRecipeWithoutReturn(new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.IRON_NUGGET, 7));
        addMeltingRecipeWithoutReturn(new ItemStack(Items.MILK_BUCKET), new ItemStack(Items.IRON_NUGGET, 7));
        BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.MAP), new ItemStack(Items.IRON_NUGGET, 4));
        BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.COMPASS), new ItemStack(Items.IRON_NUGGET, 4));
        BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Blocks.TRIPWIRE_HOOK, 2), new ItemStack(Items.IRON_NUGGET));

        BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.CLOCK), new ItemStack(Items.GOLD_NUGGET, 4));
    }

    private void addMeltingRecipeWithoutReturn(ItemStack input, ItemStack output) {
        BWRegistry.CRUCIBLE.addRecipe(new CookingPotRecipe(Lists.newArrayList(StackIngredient.fromStacks(input)), Lists.newArrayList(output), BWMHeatRegistry.STOKED_HEAT) {
            @Override
            protected boolean consumeIngredients(ItemStackHandler inventory, NonNullList<ItemStack> containItems) {
                boolean success = super.consumeIngredients(inventory, containItems);
                containItems.clear();
                return success;
            }
        });
    }

    @Override
    public void disabledInit(FMLInitializationEvent event) {
        addMeltingRecipeWithoutReturn(new ItemStack(Items.BUCKET), new ItemStack(Items.IRON_INGOT, 3));
        addMeltingRecipeWithoutReturn(new ItemStack(Items.WATER_BUCKET), new ItemStack(Items.IRON_INGOT, 3));
        addMeltingRecipeWithoutReturn(new ItemStack(Items.MILK_BUCKET), new ItemStack(Items.IRON_INGOT, 3));
        BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.MAP), new ItemStack(Items.IRON_INGOT, 4));
        BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.COMPASS), new ItemStack(Items.IRON_INGOT, 4));
        BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Blocks.TRIPWIRE_HOOK, 2), new ItemStack(Items.IRON_INGOT));
        BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(Items.CLOCK), new ItemStack(Items.GOLD_INGOT, 4));
    }


    @Override
    public void postInit(FMLPostInitializationEvent event) {
        Set<String> oreExcludes = Sets.union(oreExclude, Sets.newHashSet("oreDiamond"));
        if (oreNuggetSmelting) {
            for (BWOreDictionary.Ore ore : BWOreDictionary.oreNames) {
                replaceRecipe(oreExcludes, ore, oreProductionCount);
            }
        }
        Set<String> dustExcludes = Sets.union(dustExclude, Sets.newHashSet("dustDiamond"));
        if (dustNuggetSmelting) {
            for (BWOreDictionary.Ore dust : BWOreDictionary.dustNames) {
                replaceRecipe(dustExcludes, dust, dustProductionCount);
            }
        }
    }

    private void replaceRecipe(Set<String> oreExcludes, BWOreDictionary.Ore ore, int oreProductionCount) {
        if (!oreExcludes.contains(ore.getOre())) {
            Optional<ItemStack> optionalNugget = BWOreDictionary.nuggetNames.stream().filter(o -> o.getSuffix().equals(ore.getSuffix())).flatMap(o -> o.getOres().stream()).findFirst();
            if (optionalNugget.isPresent()) {
                for (ItemStack oreStack : ore.getOres()) {
                    if (BWMRecipes.removeFurnaceRecipe(oreStack)) {
                        ItemStack nugget = optionalNugget.get().copy();
                        nugget.setCount(oreProductionCount);
                        BWMRecipes.addFurnaceRecipe(oreStack, nugget);
                    }
                }
            }
        }
    }

}
