package betterwithmods.module.compat.immersiveengineering;

import betterwithmods.module.CompatFeature;
import betterwithmods.module.hardcore.needs.HCSeeds;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by primetoxinz on 7/26/17.
 */
public class ImmersiveEngineering extends CompatFeature {

    public ImmersiveEngineering() {
        super("immersiveengineering");
    }

    @GameRegistry.ObjectHolder("immersiveengineering:seed")
    public static final Item HEMP_SEED = null;

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        if (HCSeeds.SEED_BLACKLIST != null)
            HCSeeds.SEED_BLACKLIST.add(new ItemStack(HEMP_SEED));
    }
}
