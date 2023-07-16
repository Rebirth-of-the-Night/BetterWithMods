package betterwithmods.module.tweaks;

import betterwithmods.module.Feature;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/**
 * Created by primetoxinz on 5/14/17.
 */
public class RSBlockGlow extends Feature {
    @Override
    public void init(FMLInitializationEvent event) {
        Blocks.REDSTONE_BLOCK.setLightLevel(0.7F);
    }

    @Override
    public String getFeatureDescription() {
        return "Make Redstone blocks emit a little light";
    }
}
