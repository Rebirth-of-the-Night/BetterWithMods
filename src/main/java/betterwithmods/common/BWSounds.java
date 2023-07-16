package betterwithmods.common;

import betterwithmods.BWMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber(modid = BWMod.MODID)
public class BWSounds {
    @GameRegistry.ObjectHolder("betterwithmods:block.wood.creak")
    public static SoundEvent WOODCREAK = null;
    @GameRegistry.ObjectHolder("betterwithmods:block.stone.grind")
    public static SoundEvent STONEGRIND = null;
    @GameRegistry.ObjectHolder("betterwithmods:block.wood.bellow")
    public static SoundEvent BELLOW = null;
    @GameRegistry.ObjectHolder("betterwithmods:block.wood.chime")
    public static SoundEvent WOODCHIME = null;
    @GameRegistry.ObjectHolder("betterwithmods:block.metal.chime")
    public static SoundEvent METALCHIME = null;
    @GameRegistry.ObjectHolder("betterwithmods:entity.player.oof")
    public static SoundEvent OOF = null;
    @GameRegistry.ObjectHolder("betterwithmods:block.metal.hacksaw")
    public static SoundEvent METAL_HACKSAW = null;
    @GameRegistry.ObjectHolder("betterwithmods:block.millstone.netherrack")
    public static SoundEvent MILLSTONE_NETHERRACK = null;
    @GameRegistry.ObjectHolder("betterwithmods:block.saw.cut")
    public static SoundEvent SAW_CUT = null;
    @GameRegistry.ObjectHolder("betterwithmods:block.bloodwood.break")
    public static SoundEvent BLOODWOOD_BREAK = null;

    @GameRegistry.ObjectHolder("betterwithmods:block.mechanical.overpower")
    public static SoundEvent MECHANICAL_OVERPOWER = null;

    @GameRegistry.ObjectHolder("betterwithmods:ambient.gloom")
    public static SoundEvent GLOOM = null;
    @GameRegistry.ObjectHolder("betterwithmods:ambient.spook")
    public static SoundEvent SPOOK = null;

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().register(registerSound("block.wood.creak"));
        event.getRegistry().register(registerSound("block.stone.grind"));
        event.getRegistry().register(registerSound("block.wood.bellow"));
        event.getRegistry().register(registerSound("block.wood.chime"));
        event.getRegistry().register(registerSound("block.metal.chime"));
        event.getRegistry().register(registerSound("block.metal.hacksaw"));
        event.getRegistry().register(registerSound("block.millstone.netherrack"));
        event.getRegistry().register(registerSound("block.saw.cut"));
        event.getRegistry().register(registerSound("block.mechanical.overpower"));
        event.getRegistry().register(registerSound("entity.player.oof"));
        event.getRegistry().register(registerSound("ambient.gloom"));
        event.getRegistry().register(registerSound("ambient.spook"));
    }

    public static SoundEvent registerSound(String soundName) {
        ResourceLocation soundID = new ResourceLocation(BWMod.MODID, soundName);
        SoundEvent sound = new SoundEvent(soundID).setRegistryName(soundID);
        return sound;
    }
}
