/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [18/03/2016, 21:52:14 (GMT)]
 */
package betterwithmods.module;

import betterwithmods.BWMod;
import betterwithmods.api.FeatureEnabledEvent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Module {

    public final String name = makeName();
    public final Map<String, Feature> features = Maps.newHashMap();
    public final List<Feature> enabledFeatures = Lists.newArrayList(), disabledFeatures = Lists.newArrayList();
    public boolean enabled;
    protected int priority = 0;

    public void addFeatures() {
        // NO-OP
    }

    public void registerFeature(Feature feature) {
        registerFeature(feature, convertName(feature.getClass().getSimpleName()));
    }

    public void registerFeature(Feature feature, boolean enabledByDefault) {
        registerFeature(feature, convertName(feature.getClass().getSimpleName()), enabledByDefault);
    }

    public String convertName(String origName) {
        return origName;
    }

    public void registerFeature(Feature feature, String name) {
        registerFeature(feature, name, true);
    }

    public void registerFeature(Feature feature, String name, boolean enabledByDefault) {
        ModuleLoader.featureInstances.put(feature.getClass(), feature);
        features.put(name, feature);

        feature.enabledByDefault = enabledByDefault;
        feature.prevEnabled = false;

        feature.module = this;
        feature.configName = name;
        feature.configCategory = this.name + "." + name;
    }

    public void setupConfig() {
        if (features.isEmpty())
            addFeatures();

        forEachFeature(feature -> {
            ConfigHelper.needsRestart = feature.requiresMinecraftRestartToEnable();
            if (feature.canDisable) {
                feature.enabled = loadPropBool(feature.configName, feature.getFeatureDescription(), feature.enabledByDefault) && enabled;
                feature.setupConstantConfig();
            } else {
                feature.enabled = true;
                feature.forceLoad = true;
            }

            if (!feature.forceLoad) {
                String[] incompatibilities = feature.getIncompatibleMods();
                if (incompatibilities != null) {
                    List<String> failiures = Lists.newArrayList();

                    for (String s : incompatibilities)
                        if (Loader.isModLoaded(s)) {
                            feature.enabled = false;
                            failiures.add(s);
                        }

                    if (!failiures.isEmpty())
                        BWMod.logger.info("[BWM] '" + feature.configName + "' is forcefully disabled as it's incompatible with the following loaded mods: " + failiures);
                }
            }

            if (!feature.loadtimeDone) {
                feature.enabledAtLoadtime = feature.enabled;
                feature.loadtimeDone = true;
            }

            if (feature.enabled && !enabledFeatures.contains(feature))
                enabledFeatures.add(feature);
            else if (!feature.enabled && enabledFeatures.contains(feature))
                enabledFeatures.remove(feature);

            if (!feature.enabled)
                disabledFeatures.add(feature);

            feature.setupConfig();

            if (feature.enabled && feature.recipeCondition)
                feature.loadRecipeCondition(feature.configName.toLowerCase(), "Custom Recipes", "Enables custom recipes provided by this feature", true);

            if (!feature.enabled && feature.prevEnabled) {
                if (feature.hasSubscriptions())
                    MinecraftForge.EVENT_BUS.unregister(feature);
                if (feature.hasTerrainSubscriptions())
                    MinecraftForge.TERRAIN_GEN_BUS.unregister(feature);
                if (feature.hasOreGenSubscriptions())
                    MinecraftForge.ORE_GEN_BUS.unregister(feature);
            } else if (feature.enabled && (feature.enabledAtLoadtime || !feature.requiresMinecraftRestartToEnable()) && !feature.prevEnabled) {
                if (feature.hasSubscriptions())
                    MinecraftForge.EVENT_BUS.register(feature);
                if (feature.hasTerrainSubscriptions())
                    MinecraftForge.TERRAIN_GEN_BUS.register(feature);
                if (feature.hasOreGenSubscriptions())
                    MinecraftForge.ORE_GEN_BUS.register(feature);
            }

            feature.prevEnabled = feature.enabled;
        });
    }

    public void preInit(FMLPreInitializationEvent event) {
        forEachEnabled(feature -> {
            BWMod.logger.info("[BWM] Feature PreInit : " + feature.configName);
            feature.preInit(event);
        });
        forEachDisabled(feature -> feature.disabledPreInit(event));
    }

    public void init(FMLInitializationEvent event) {
        forEachEnabled(feature -> feature.init(event));
        forEachDisabled(feature -> feature.disabledInit(event));
    }

    public void postInit(FMLPostInitializationEvent event) {
        forEachEnabled(feature -> {
            feature.postInit(event);
            MinecraftForge.EVENT_BUS.post(new FeatureEnabledEvent(name.toLowerCase(), feature.configName.toLowerCase(), feature.enabled));
        });
        forEachDisabled(feature -> feature.disabledPostInit(event));
    }


    public void finalInit(FMLPostInitializationEvent event) {
        forEachEnabled(feature -> feature.finalInit(event));
    }

    @SideOnly(Side.CLIENT)
    public void preInitClient(FMLPreInitializationEvent event) {
        forEachEnabled(feature -> feature.preInitClient(event));
    }

    @SideOnly(Side.CLIENT)
    public void initClient(FMLInitializationEvent event) {
        forEachEnabled(feature -> feature.initClient(event));
    }

    @SideOnly(Side.CLIENT)
    public void postInitClient(FMLPostInitializationEvent event) {
        forEachEnabled(feature -> feature.postInitClient(event));
    }

    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        forEachEnabled(feature -> feature.registerModels(event));
    }

    public void serverStarting(FMLServerStartingEvent event) {
        forEachEnabled(feature -> feature.serverStarting(event));
    }

    public boolean canBeDisabled() {
        return true;
    }

    public boolean isEnabledByDefault() {
        return true;
    }

    String makeName() {
        return getClass().getSimpleName().toLowerCase();
    }

    public String getModuleDescription() {
        return "";
    }

    public final void forEachFeature(Consumer<Feature> consumer) {
        features.values().forEach(consumer);
    }

    public final void forEachEnabled(Consumer<Feature> consumer) {
        enabledFeatures.forEach(consumer);
    }

    public final void forEachDisabled(Consumer<Feature> consumer) {
        disabledFeatures.forEach(consumer);
    }

    public final int loadPropInt(String propName, String desc, int default_) {
        return ConfigHelper.loadPropInt(propName, name, desc, default_);
    }

    public final double loadPropDouble(String propName, String desc, double default_, double min, double max) {
        return ConfigHelper.loadPropDouble(propName, name, desc, default_, min, max);
    }

    public final double loadPropDouble(String propName, String desc, double default_) {
        return ConfigHelper.loadPropDouble(propName, name, desc, default_);
    }

    public final boolean loadPropBool(String propName, String desc, boolean default_) {
        return ConfigHelper.loadPropBool(propName, name, desc, default_);
    }

    public final String loadPropString(String propName, String desc, String default_) {
        return ConfigHelper.loadPropString(propName, name, desc, default_);
    }

    public final void loadRecipeCondition(String jsonName, String propName, String comment, boolean _default) {
        ConfigHelper.loadRecipeCondition(jsonName, propName, name, comment, _default);
    }


    public List<ResourceLocation> loadPropRLList(String propName, String desc, String[] default_) {
        return ConfigHelper.loadPropRLList(propName, name, desc, default_);
    }


    public int getPriority() {
        return priority;
    }
}
