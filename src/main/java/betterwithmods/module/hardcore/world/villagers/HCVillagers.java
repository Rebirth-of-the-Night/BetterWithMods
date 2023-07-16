package betterwithmods.module.hardcore.world.villagers;

import betterwithmods.BWMod;
import betterwithmods.module.Feature;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by primetoxinz on 6/11/17.
 */
public class HCVillagers extends Feature {

    private static final ResourceLocation LEVELING = new ResourceLocation(BWMod.MODID, "villager_leveling");

    private static boolean clearTrades = true;
    private static boolean disableZombieCuring = true;

    @Override
    public void setupConfig() {
        disableZombieCuring = loadPropBool("Disable Zombie Curing", "Removes the ability to cure zombie villages", true);
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(VillagerLevel.class, new VillagerLevel.Storage(), VillagerLevel::new);
    }

    @Override
    public void preInitClient(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(ClientSide.class);
        super.preInitClient(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        if (clearTrades) {
        }
    }

    @SubscribeEvent
    public void onAttachCap(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityVillager)
            event.addCapability(LEVELING, new VillagerLevel());
    }

    @Override
    public String getFeatureDescription() {
        return "Changes how Villagers work";
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }


    @SubscribeEvent
    public void onTick(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity instanceof EntityVillager) {
            EntityVillager villager = (EntityVillager) entity;
            if (villager.timeUntilReset <= 0)
                villager.timeUntilReset = Integer.MAX_VALUE;
            if (villager.needsInitilization)
                villager.needsInitilization = false;
        }
    }


    @SubscribeEvent
    public void onCureZombie(PlayerInteractEvent.EntityInteractSpecific event) {
        if (!disableZombieCuring)
            return;
        EntityLivingBase entity = event.getEntityLiving();
        if (entity instanceof EntityZombieVillager) {
            event.setCanceled(true);
        }
    }

    @SideOnly(Side.CLIENT)
    public static class ClientSide {
        @SubscribeEvent
        public static void onRender(GuiScreenEvent event) {
            if (event.getGui() instanceof GuiMerchant) {
                GuiVillager.draw((GuiMerchant) event.getGui());
            }
        }
    }
}
