package betterwithmods.module.hardcore.world;

import betterwithmods.module.Feature;
import betterwithmods.module.ModuleLoader;
import betterwithmods.module.compat.Quark;
import betterwithmods.util.player.PlayerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HCBoating extends Feature {

    //Quark Boat Sail Compat
    private static final String TAG_BANNER = "quark:banner";
    public static HashMap<Ingredient, Integer> SPEED_ITEMS;
    public static List<ResourceLocation> BOAT_ENTRIES;
    public static int defaultSpeed;

    @Override
    public String getFeatureDescription() {
        return "Boats are much slower as simple oars are not very good for speed. To go faster you must hold a Wind Sail.";
    }

    @Override
    public void setupConfig() {
        loadRecipeCondition("boatshovel", "Boat Requires Oar", "Make boat recipe require a wooden shovel for the oars", true);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        SPEED_ITEMS = loadItemStackIntMap("Speed Items", "Items which speed up a boat when held, value is a percentage of the vanilla speed", new String[]{
                "betterwithmods:material:11=100",
                "minecraft:banner:*=100"
        });
        defaultSpeed = loadPropInt("Default Speed modifier", "Speed modifier when not holding any sail type item", 50);
        BOAT_ENTRIES = loadRLList("Boat List", "Registry name for entities which are considered boats", new String[]{"minecraft:boat"});
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            return;

        if (!event.player.world.isRemote)
            return;
        EntityPlayer player = event.player;
        Entity riding = player.getRidingEntity();
        if (riding != null && BOAT_ENTRIES.stream().anyMatch(r -> EntityList.isMatchingName(riding, r))) {
            Set<ItemStack> stacks = PlayerHelper.getHolding(player);
            int speed = defaultSpeed;
            for (ItemStack stack : stacks) {
                if (speed <= defaultSpeed) {
                    if (!stack.isEmpty()) {
                        speed = SPEED_ITEMS.entrySet().stream().filter(e -> e.getKey().apply(stack)).mapToInt(Map.Entry::getValue).findAny().orElse(defaultSpeed);
                    }
                }
            }
            if (ModuleLoader.isFeatureEnabled(Quark.class)) {
                int quarkCompat = quarkCompatSpeed((EntityBoat) riding);
                if (quarkCompat > 0)
                    speed = quarkCompat;
            }

            riding.motionX *= (speed / 100f);
            riding.motionZ *= (speed / 100f);
        }
    }

    private int quarkCompatSpeed(EntityBoat boat) {
        NBTTagCompound tag = boat.getEntityData();
        if (tag.hasKey(TAG_BANNER)) {
            NBTTagCompound cmp = boat.getEntityData().getCompoundTag(TAG_BANNER);
            ItemStack stack = new ItemStack(cmp);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemBanner) {
                return SPEED_ITEMS.entrySet().stream().filter(e -> e.getKey().apply(stack)).findFirst().map(Map.Entry::getValue).orElse(0);
            }
        }
        return 0;
    }

}
