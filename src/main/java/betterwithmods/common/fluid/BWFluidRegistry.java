package betterwithmods.common.fluid;

import betterwithmods.BWMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class BWFluidRegistry {
    public static final Fluid STEAM = new Fluid("steam", new ResourceLocation(BWMod.MODID, "blocks/steam_still"), new ResourceLocation(BWMod.MODID, "blocks/steam_flowing"));

    public static void init() {
        FluidRegistry.registerFluid(STEAM);
    }
}
