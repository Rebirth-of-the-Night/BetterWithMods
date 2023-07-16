package betterwithmods.module.tweaks;

import betterwithmods.common.BWMItems;
import betterwithmods.module.Feature;
import betterwithmods.util.player.PlayerHelper;
import com.google.common.collect.Sets;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Set;

public class FoodPoisoning extends Feature {
    public static double chanceForPoison;


    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @Override
    public String getFeatureDescription() {
        return "No one wants to eat when they have food poisoning. When you have the Hunger effect you can't eat. Additionally raw meats can cause food poisoning";
    }

    @Override
    public void setupConfig() {
        chanceForPoison = loadPropDouble("Chance for Food Poisoning", "", 0.3);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        Set<Item> RAW_FOOD =
                Sets.newHashSet(BWMItems.RAW_SCRAMBLED_EGG, BWMItems.RAW_EGG, BWMItems.RAW_OMELET,
                        BWMItems.RAW_KEBAB, Items.FISH, BWMItems.WOLF_CHOP, Items.BEEF, Items.PORKCHOP, Items.RABBIT, Items.CHICKEN, Items.MUTTON, BWMItems.MYSTERY_MEAT);
        RAW_FOOD.stream().map(i -> (ItemFood) i).forEach(i -> i.setPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 1), (float) chanceForPoison));
    }

    //Stops Eating if Hunger Effect is active
    @SubscribeEvent
    public void onFood(LivingEntityUseItemEvent.Start event) {
        if (event.getItem().getItem() instanceof ItemFood && event.getEntityLiving() instanceof EntityPlayer && PlayerHelper.isSurvival((EntityPlayer) event.getEntityLiving())) {
            if (event.getEntityLiving().isPotionActive(MobEffects.HUNGER)) {
                event.setCanceled(true);
            }
        }
    }
}
