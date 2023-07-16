package betterwithmods.module.industry;

import betterwithmods.module.Feature;
import betterwithmods.module.ModuleLoader;
import betterwithmods.module.hardcore.beacons.HCBeacons;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by primetoxinz on 7/26/17.
 */
public class WeatherControl extends Feature {

    @Override
    public String getFeatureDescription() {
        return super.getFeatureDescription();
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        if(ModuleLoader.isFeatureEnabled(HCBeacons.class)) {
            //HCBeacons.BEACON_EFFECTS.put(Blocks.SEA_LANTERN.getDefaultState(), new WeatherBeaconEffect());
        }
    }
}
