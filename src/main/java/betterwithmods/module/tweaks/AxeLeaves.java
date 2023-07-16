package betterwithmods.module.tweaks;

import betterwithmods.module.Feature;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class AxeLeaves extends Feature {
    @Override
    public String getFeatureDescription() {
        return "Axes are fast at breaking leaves";
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        Blocks.LEAVES.setHarvestLevel("axe", 0);
        Blocks.LEAVES2.setHarvestLevel("axe", 1);
    }

}
