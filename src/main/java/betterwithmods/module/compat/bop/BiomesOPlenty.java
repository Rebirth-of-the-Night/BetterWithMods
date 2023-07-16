package betterwithmods.module.compat.bop;

import betterwithmods.module.CompatFeature;
import betterwithmods.module.hardcore.needs.HCSeeds;
import betterwithmods.module.tweaks.MobSpawning;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class BiomesOPlenty extends CompatFeature {

    public BiomesOPlenty() {
        super("biomesoplenty");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void init(FMLInitializationEvent event) {
        MobSpawning.NETHER.addBlock(new ItemStack(getBlock(new ResourceLocation(modid, "grass")), 1,1));
        MobSpawning.NETHER.addBlock(new ItemStack(getBlock(new ResourceLocation(modid, "grass")), 1,6));
        MobSpawning.NETHER.addBlock(getBlock(new ResourceLocation(modid, "flesh")));
        MobSpawning.NETHER.addBlock(getBlock(new ResourceLocation(modid, "ash_block")));

        HCSeeds.BLOCKS_TO_STOP.add(getBlock(new ResourceLocation(modid, "plant_0")).getStateFromMeta(0));
        HCSeeds.BLOCKS_TO_STOP.add(getBlock(new ResourceLocation(modid, "plant_0")).getStateFromMeta(1));
        HCSeeds.BLOCKS_TO_STOP.add(getBlock(new ResourceLocation(modid, "plant_0")).getStateFromMeta(7));
        HCSeeds.BLOCKS_TO_STOP.add(getBlock(new ResourceLocation(modid, "plant_0")).getStateFromMeta(8));
    }

}
