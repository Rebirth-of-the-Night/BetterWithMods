package betterwithmods.module.industry.pollution;

import betterwithmods.api.capabilities.PollutionCapability;
import betterwithmods.api.tile.IPollutant;
import betterwithmods.module.Feature;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class Pollution extends Feature {
    public static PollutionHandler handler;

    @Override
    public void preInit(FMLPreInitializationEvent evt) {
        CapabilityManager.INSTANCE.register(IPollutant.class, new PollutionCapability.Impl(), PollutionCapability.Default::new);
        CapabilityManager.INSTANCE.register(IWorldPollution.class, new WorldPollutionCapability.Impl(), WorldPollutionCapability.Default::new);
        MinecraftForge.EVENT_BUS.register(handler = new PollutionHandler());
    }

    @Override
    public void postInit(FMLPostInitializationEvent evt) {
        addBiomeTagToModifierList(BiomeDictionary.Type.DENSE, 0.8F);
        addBiomeTagToModifierList(BiomeDictionary.Type.SPARSE, 1.1F);
        addBiomeTagToModifierList(BiomeDictionary.Type.WET, 0.9F);
        addBiomeTagToModifierList(BiomeDictionary.Type.LUSH, 0.8F);
        addBiomeTagToModifierList(BiomeDictionary.Type.SWAMP, 1.2F);
        addBiomeTagToModifierList(BiomeDictionary.Type.FOREST, 0.9F);
        addBiomeTagToModifierList(BiomeDictionary.Type.MUSHROOM, 0.9F);
        addBiomeTagToModifierList(BiomeDictionary.Type.MAGICAL, 0.7F);
    }

    private void addBiomeTagToModifierList(BiomeDictionary.Type type, float mod) {
        handler.biomeMods.put(type.getName(), mod);
    }

    @Override
    public void serverStarting(FMLServerStartingEvent evt) {
        evt.registerServerCommand(new CommandCheckPollution());
    }
}
