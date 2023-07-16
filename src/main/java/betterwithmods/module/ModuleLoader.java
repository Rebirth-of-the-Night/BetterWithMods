/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [18/03/2016, 21:52:08 (GMT)]
 */
package betterwithmods.module;

import betterwithmods.BWMod;
import betterwithmods.module.gameplay.Gameplay;
import betterwithmods.module.hardcore.Hardcore;
import betterwithmods.module.tweaks.Tweaks;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class ModuleLoader {

    public static Map<Class<? extends Module>, Module> moduleInstances = Maps.newHashMap();
    public static Map<Class<? extends Feature>, Feature> featureInstances = Maps.newHashMap();
    public static List<Module> enabledModules;
    public static Configuration config;
    public static File configFile;
    private static List<Class<? extends Module>> moduleClasses;

    static {
        moduleClasses = Lists.newArrayList();
        registerModule(Gameplay.class);
        registerModule(Hardcore.class);
        registerModule(Tweaks.class);
        registerModule(CompatModule.class);
//        registerModule(Industry.class);
    }

    public static void preInit(FMLPreInitializationEvent event) {
        moduleClasses.stream().forEachOrdered(clazz -> {
            try {
                moduleInstances.put(clazz, clazz.newInstance());
            } catch (Exception e) {
                throw new RuntimeException("Can't initialize module " + clazz, e);
            }
        });

        setupConfig(event);

        forEachModule(module -> BWMod.logger.info("[BWM] Module " + module.name + " is " + (module.enabled ? "enabled" : "disabled")));
        enabledModules.sort(Comparator.comparingInt(Module::getPriority));
        forEachEnabled(module -> {
            BWMod.logger.info("[BWM] Module PreInit : " + module.name);
            module.preInit(event);
        });

        if (config.hasChanged())
            config.save();
    }

    public static void init(FMLInitializationEvent event) {
        forEachEnabled(module -> module.init(event));

        if (config.hasChanged())
            config.save();
    }


    public static void postInit(FMLPostInitializationEvent event) {
        forEachEnabled(module -> module.postInit(event));
        forEachEnabled(module -> module.finalInit(event));
        if (config.hasChanged())
            config.save();
    }

    public static void finalInit(FMLPostInitializationEvent event) {
        forEachEnabled(module -> module.finalInit(event));
    }

    @SideOnly(Side.CLIENT)
    public static void preInitClient(FMLPreInitializationEvent event) {
        GlobalConfig.initGlobalClient();
        forEachEnabled(module -> module.preInitClient(event));
    }

    @SideOnly(Side.CLIENT)
    public static void initClient(FMLInitializationEvent event) {

        forEachEnabled(module -> module.initClient(event));
    }

    @SideOnly(Side.CLIENT)
    public static void postInitClient(FMLPostInitializationEvent event) {
        forEachEnabled(module -> module.postInitClient(event));
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels(ModelRegistryEvent event) {
        forEachEnabled(module -> module.registerModels(event));
    }

    public static void serverStarting(FMLServerStartingEvent event) {
        forEachEnabled(module -> module.serverStarting(event));
    }

    public static void setupConfig(FMLPreInitializationEvent event) {
        configFile = event.getSuggestedConfigurationFile();
        config = new Configuration(configFile);
        config.load();

        GlobalConfig.initGlobalConfig();

        forEachModule(module -> {
            module.enabled = true;
            if (module.canBeDisabled()) {
                ConfigHelper.needsRestart = true;
                module.enabled = ConfigHelper.loadPropBool(module.name, "_modules", module.getModuleDescription(), module.isEnabledByDefault());
            }
        });

        enabledModules = new ArrayList<>(moduleInstances.values());
        enabledModules.removeIf(module -> !module.enabled);

        loadModuleConfigs();

        MinecraftForge.EVENT_BUS.register(new ChangeListener());
    }

    private static void loadModuleConfigs() {
        forEachModule(Module::setupConfig);

        if (config.hasChanged())
            config.save();
    }

    @SuppressWarnings("unchecked")
    public static boolean isModuleEnabled(String name) {
        try {
            Class<? extends Module> clazz = (Class<? extends Module>) Class.forName(name);
            return isModuleEnabled(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isModuleEnabled(Class<? extends Module> clazz) {
        return moduleInstances.get(clazz).enabled;
    }


    public static Feature getFeature(String name) {
        for (Module module : enabledModules) {
            for (Feature feature : module.enabledFeatures) {
                if (feature.configName.equalsIgnoreCase(name)) {
                    return feature;
                }
            }
        }
        return null;
    }

    public static boolean isFeatureEnabledSimple(String name) {
        return getFeature(name) != null;
    }

    @SuppressWarnings("unchecked")
    public static boolean isFeatureEnabled(String clazzName) {
        try {
            Class<? extends Feature> clazz = (Class<? extends Feature>) Class.forName(clazzName);
            return isFeatureEnabled(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isFeatureEnabled(Class<? extends Feature> clazz) {
        Feature f = getFeature(clazz);
        return f != null && f.enabled;
    }

    public static Feature getFeature(Class<? extends Feature> clazz) {
        if (featureInstances.containsKey(clazz))
            return featureInstances.get(clazz);
        return null;
    }

    public static void forEachModule(Consumer<Module> consumer) {
        moduleInstances.values().forEach(consumer);
    }

    public static void forEachEnabled(Consumer<Module> consumer) {
        enabledModules.forEach(consumer);
    }

    private static void registerModule(Class<? extends Module> clazz) {
        if (!moduleClasses.contains(clazz))
            moduleClasses.add(clazz);
    }

    public static class ChangeListener {

        @SubscribeEvent
        public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
            if (eventArgs.getModID().equals(BWMod.MODID))
                loadModuleConfigs();
        }

    }

}
