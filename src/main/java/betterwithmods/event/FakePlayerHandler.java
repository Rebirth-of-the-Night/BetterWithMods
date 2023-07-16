package betterwithmods.event;

import betterwithmods.BWMod;
import betterwithmods.common.BWMItems;
import betterwithmods.util.player.Profiles;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = BWMod.MODID)
public class FakePlayerHandler {
    private static FakePlayer player, shoveler;

    public static FakePlayer getShoveler() {
        return shoveler;
    }

    public static FakePlayer getPlayer() {
        return player;
    }

    public static void setPlayer(FakePlayer player) {
        FakePlayerHandler.player = player;
    }

    //Initializing a static fake player for saws, so spawn isn't flooded with player equipping sounds when mobs hit the saw.
    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load evt) {
        if (evt.getWorld() instanceof WorldServer) {
            player = FakePlayerFactory.get((WorldServer) evt.getWorld(), Profiles.BWMSAW);
            ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
            sword.addEnchantment(Enchantment.getEnchantmentByLocation("looting"), 2);
            player.setHeldItem(EnumHand.MAIN_HAND, sword);

            shoveler = FakePlayerFactory.get((WorldServer) evt.getWorld(), Profiles.BWMSSHOVELER);
            ItemStack shovel = new ItemStack(BWMItems.STEEL_MATTOCK);
            shoveler.setHeldItem(EnumHand.MAIN_HAND, shovel);
        }
    }
}
