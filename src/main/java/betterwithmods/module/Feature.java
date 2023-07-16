/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [18/03/2016, 22:46:32 (GMT)]
 */
package betterwithmods.module;

import betterwithmods.BWMod;
import betterwithmods.common.BWMRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Feature {

    public Module module;

    public boolean loadtimeDone;
    public boolean enabledAtLoadtime;

    public boolean enabledByDefault;
    public boolean enabled;
    public boolean prevEnabled;
    public boolean needsSync;

    public String configCategory;
    public String configName;

    public boolean forceLoad;
    protected boolean canDisable = true;

    public boolean recipeCondition;

    public Feature recipes() {
        recipeCondition = true;
        return this;
    }

    public final void setupConstantConfig() {
        String[] incompat = getIncompatibleMods();
        if (incompat != null && incompat.length > 0) {
            StringBuilder desc = new StringBuilder("This feature disables itself if any of the following mods are loaded: \n");
            for (String s : incompat)
                desc.append(" - ").append(s).append("\n");
            desc.append("This is done to prevent content overlap.\nYou can turn this on to force the feature to be loaded even if the above mods are also loaded.");

            ConfigHelper.needsRestart = true;
            forceLoad = loadPropBool("Force Enabled", desc.toString(), false);
        }
    }

    public void setupConfig() {
        // NO-OP
    }

    public void preInit(FMLPreInitializationEvent event) {
        // NO-OP
    }

    public void init(FMLInitializationEvent event) {
        // NO-OP
    }

    public void postInit(FMLPostInitializationEvent event) {
        // NO-OP
    }

    public void finalInit(FMLPostInitializationEvent event) {
        // NO-OP
    }

    @SideOnly(Side.CLIENT)
    public void preInitClient(FMLPreInitializationEvent event) {
        // NO-OP
    }

    @SideOnly(Side.CLIENT)
    public void initClient(FMLInitializationEvent event) {
        // NO-OP
    }

    @SideOnly(Side.CLIENT)
    public void postInitClient(FMLPostInitializationEvent event) {
        // NO-OP
    }

    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        // NO-OP
    }

    public void serverStarting(FMLServerStartingEvent event) {
        // NO-OP
    }

    public void disabledPreInit(FMLPreInitializationEvent event) {

    }

    public void disabledInit(FMLInitializationEvent event) {

    }

    public void disabledPostInit(FMLPostInitializationEvent event) {

    }

    public String[] getIncompatibleMods() {
        return null;
    }

    public boolean hasSubscriptions() {
        return false;
    }

    public boolean hasTerrainSubscriptions() {
        return false;
    }

    public boolean hasOreGenSubscriptions() {
        return false;
    }

    public String getFeatureDescription() {
        return "";
    }

    public boolean requiresMinecraftRestartToEnable() {
        return hasSubscriptions() || hasOreGenSubscriptions() || hasTerrainSubscriptions();
    }

    public static void registerTile(Class<? extends TileEntity> clazz, String key) {
        GameRegistry.registerTileEntity(clazz, new ResourceLocation(BWMod.MODID, key));
    }

    public static IRecipe registerHardcoreRecipe(String ID, IRecipe recipe) {
        BWMRecipes.addHardcoreRecipe(ID, recipe);
        return recipe;
    }

    public final boolean isClient() {
        return FMLCommonHandler.instance().getSide().isClient();
    }

    public final int loadPropInt(String propName, String desc, String comment, int default_, int min, int max) {
        return ConfigHelper.loadPropInt(propName, configCategory, desc, comment, default_, min, max);
    }

    public final int loadPropInt(String propName, String desc, int default_) {
        return ConfigHelper.loadPropInt(propName, configCategory, desc, default_);
    }

    public final double loadPropDouble(String propName, String desc, double default_) {
        return ConfigHelper.loadPropDouble(propName, configCategory, desc, default_);
    }

    public final boolean loadPropBool(String propName, String desc, boolean default_) {
        return ConfigHelper.loadPropBool(propName, configCategory, desc, default_);
    }

    public final String loadPropString(String propName, String desc, String default_) {
        return ConfigHelper.loadPropString(propName, configCategory, desc, default_);
    }

    public final String[] loadPropStringList(String propName, String desc, String[] default_) {
        return ConfigHelper.loadPropStringList(propName, configCategory, desc, default_);
    }

    public final HashSet<String> loadPropStringHashSet(String propName, String desc, String[] default_) {
        return new HashSet<>(Arrays.asList(ConfigHelper.loadPropStringList(propName, configCategory, desc, default_)));
    }


    public final int[] loadPropIntList(String propName, String comment, int[] default_) {
        return ConfigHelper.loadPropIntList(propName, configCategory, comment, default_);
    }


    public final ItemStack[] loadItemStackArray(String propName, String comment, ItemStack[] default_) {
        return ConfigHelper.loadItemStackArray(propName, configCategory, comment, default_);
    }

    public final List<ItemStack> loadItemStackList(String propName, String comment, ItemStack[] default_) {
        return ConfigHelper.loadItemStackList(propName, configCategory, comment, default_);
    }

    public final List<ItemStack> loadItemStackList(String propName, String comment, String[] default_) {
        return ConfigHelper.loadItemStackList(propName, configCategory, comment, default_);
    }

    public final HashMap<Ingredient, Integer> loadItemStackIntMap(String propName, String comment, String[] _default) {
        return ConfigHelper.loadIngredientIntMap(propName, configCategory, comment, _default);
    }

    public final boolean loadRecipeCondition(String jsonName, String propName, String comment, boolean _default) {
        return ConfigHelper.loadRecipeCondition(jsonName, propName, configCategory, comment, _default);
    }

    public IRecipe addHardcoreRecipe(IRecipe recipe) {
        return registerHardcoreRecipe(getClass().getSimpleName(), recipe);
    }

    public final List<ResourceLocation> loadRLList(String propName, String comment, String[] default_) {
        return ConfigHelper.loadPropRLList(propName, configCategory, comment, default_);
    }

    public void overrideBlock(String str) {
        BWMod.proxy.addResourceOverride("textures", "blocks", str, "png");
    }

    public void overrideItem(String str) {
        BWMod.proxy.addResourceOverride("textures", "items", str, "png");
    }

}
