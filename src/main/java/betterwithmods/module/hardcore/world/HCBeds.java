package betterwithmods.module.hardcore.world;

import betterwithmods.module.Feature;
import betterwithmods.util.player.PlayerHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class HCBeds extends Feature {
    public static final EntityPlayer.SleepResult TOO_RESTLESS = EnumHelper.addEnum(EntityPlayer.SleepResult.class, "TOO_RESTLESS", new Class[0]);

    public static boolean stillSetSpawn;

    @Override
    public void setupConfig() {
        stillSetSpawn = loadPropBool("Still Set Spawn", ":( I guess I'll be nice and give this config", false);
    }

    @Override
    public String getFeatureDescription() {
        return "Disables the ability to sleep in a bed and can no longer set spawn";
    }

    /**
     * Disable Beds
     */
    @SubscribeEvent
    public void onSleepInBed(PlayerSleepInBedEvent event) {
        if (PlayerHelper.isSurvival(event.getEntityPlayer())) {
            event.getEntityPlayer().sendStatusMessage(new TextComponentTranslation("tile.bed.tooRestless"), true);
            event.setResult(TOO_RESTLESS);
            if(stillSetSpawn) {
                event.getEntityPlayer().setSpawnPoint(event.getPos(),true);
            }
        }
    }

    @Override
    public boolean requiresMinecraftRestartToEnable() {
        return true;
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }
}
