package betterwithmods.module.gameplay;

import betterwithmods.BWMod;
import betterwithmods.common.BWMItems;
import betterwithmods.module.Feature;
import betterwithmods.util.InvUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.Locale;

public class ReadTheFingManual extends Feature {


    @SubscribeEvent
    public void onCraftedEvent(PlayerEvent.ItemCraftedEvent event) {
        if (!event.player.world.isRemote && !event.crafting.isEmpty() && event.crafting.getItem() != BWMItems.MANUAL) {
            ResourceLocation name = event.crafting.getItem().getRegistryName();
            if (name != null && name.toString().toLowerCase(Locale.ROOT).contains(BWMod.MODID)) {
                PlayerDataHandler.PlayerInfo save = PlayerDataHandler.getPlayerInfo(event.player);
                if (save != null && !save.givenManual) {
                    save.givenManual = true;
                    InvUtils.spawnStack(event.player.world, event.player.getPosition(), new ItemStack(BWMItems.MANUAL), 10);
                    event.player.sendMessage(new TextComponentTranslation("bwm.manual.message"));
                }
            }
        }
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @Override
    public String getFeatureDescription() {
        return "Gives the Player a BWM Manual the first time they craft an item from BWM";
    }
}
