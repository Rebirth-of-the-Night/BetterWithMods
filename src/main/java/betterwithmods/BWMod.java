package betterwithmods;

import betterwithmods.client.BWGuiHandler;
import betterwithmods.common.BWIMCHandler;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.penalties.attribute.BWMAttributes;
import betterwithmods.event.FakePlayerHandler;
import betterwithmods.module.GlobalConfig;
import betterwithmods.module.ModuleLoader;
import betterwithmods.proxy.IProxy;
import betterwithmods.testing.BWMTests;
import betterwithmods.util.commands.HealthCommand;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = BWMod.MODID)
@Mod(modid = BWMod.MODID, name = BWMod.NAME, version = BWMod.VERSION, dependencies = BWMod.DEPENDENCIES, guiFactory = "betterwithmods.client.gui.BWGuiFactory", acceptedMinecraftVersions = "[1.12, 1.13)")
public class BWMod {
    public static final String MODID = "betterwithmods";
    public static final String VERSION = "1.12-2.4.0";
    public static final String NAME = "Better With Mods";
    public static final String DEPENDENCIES = "before:survivalist;after:traverse;after:thaumcraft;after:natura;after:mantle;after:tconstruct;after:minechem;after:natura;after:terrafirmacraft;after:immersiveengineering;after:mekanism;after:thermalexpansion;after:ctm;after:geolosys;after:pvj;after:techreborn";

    public static Logger logger;
    @SuppressWarnings({"CanBeFinal"})
    @SidedProxy(serverSide = "betterwithmods.proxy.ServerProxy", clientSide = "betterwithmods.proxy.ClientProxy")
    public static IProxy proxy;
    @SuppressWarnings({"CanBeFinal"})
    @Mod.Instance(BWMod.MODID)
    public static BWMod instance;

    public static Logger getLog() {
        return logger;
    }

    static {
        FluidRegistry.enableUniversalBucket();
        ForgeModContainer.fullBoundingBoxLadders = true;
    }

    @Mod.EventHandler
    public void onConstruct(FMLConstructionEvent event) { }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        logger = evt.getModLog();
        BWMAttributes.registerAttributes();
        ModuleLoader.preInit(evt);
        BWRegistry.preInit();
        proxy.preInit(evt);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent evt) {
        BWRegistry.init();
        ModuleLoader.init(evt);
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new BWGuiHandler());
        proxy.init(evt);
    }


    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent evt) {
        BWRegistry.postInit();
        ModuleLoader.postInit(evt);

        proxy.postInit(evt);
        BWRegistry.postPostInit();

    }

    @Mod.EventHandler
    public void processIMCMessages(IMCEvent evt) {
        BWIMCHandler.processIMC(evt.getMessages());
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent evt) {
        ModuleLoader.serverStarting(evt);
        if(isDev()) {
            BWMTests.runTests();
        }
        if(GlobalConfig.debug) {
            evt.registerServerCommand(new HealthCommand());
        }
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent evt) {
        FakePlayerHandler.setPlayer(null);
    }

    public static boolean isDev() {
        return BWMod.VERSION.equalsIgnoreCase("${version}");
    }

}
