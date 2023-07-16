package betterwithmods.module.hardcore.needs.hunger;

import betterwithmods.BWMod;
import betterwithmods.client.gui.GuiHunger;
import betterwithmods.common.BWMItems;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.items.ItemBlockEdible;
import betterwithmods.common.items.ItemEdibleSeeds;
import betterwithmods.common.penalties.FatPenalties;
import betterwithmods.common.penalties.HungerPenalties;
import betterwithmods.module.CompatFeature;
import betterwithmods.module.ConfigHelper;
import betterwithmods.module.hardcore.needs.HCTools;
import betterwithmods.network.BWNetwork;
import betterwithmods.network.messages.MessageHungerShake;
import betterwithmods.util.player.PlayerHelper;
import com.google.common.collect.Maps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import squeek.applecore.api.AppleCoreAPI;
import squeek.applecore.api.food.FoodEvent;
import squeek.applecore.api.food.FoodValues;
import squeek.applecore.api.food.IEdibleBlock;
import squeek.applecore.api.hunger.ExhaustionEvent;
import squeek.applecore.api.hunger.HealthRegenEvent;
import squeek.applecore.api.hunger.HungerEvent;
import squeek.applecore.api.hunger.StarvationEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by primetoxinz on 6/20/17.
 */
public class HCHunger extends CompatFeature {

    private static final DataParameter<Integer> EXHAUSTION_TICK = EntityDataManager.createKey(EntityPlayer.class, DataSerializers.VARINT);

    public static float blockBreakExhaustion;
    public static float passiveExhaustion;
    public static int passiveExhaustionTick;

    public static boolean overridePumpkinSeeds;
    //	public static boolean fat;
    public static boolean overrideMushrooms;
    public static Item PUMPKIN_SEEDS = new ItemEdibleSeeds(Blocks.PUMPKIN_STEM, 1, 0).setRegistryName("minecraft:pumpkin_seeds").setTranslationKey("seeds_pumpkin");
    public static Item BROWN_MUSHROOM = new ItemBlockEdible(Blocks.BROWN_MUSHROOM, 1, 0).setRegistryName("minecraft:brown_mushroom");
    public static Item RED_MUSHROOM = new ItemBlockEdible(Blocks.RED_MUSHROOM, 1, 0).setPotionEffect(new PotionEffect(MobEffects.POISON, 100, 0), 1).setRegistryName("minecraft:red_mushroom");
    public static HungerPenalties hungerPenalties;
    public static FatPenalties fatPenalties;

    public HCHunger() {
        super("applecore");
    }

    @SubscribeEvent
    public static void allowHealthRegen(HealthRegenEvent.AllowRegen event) {
        if (!event.player.world.getGameRules().getBoolean("naturalRegeneration"))
            return;
        //Whether the player can heal
        Event.Result result = BWRegistry.PENALTY_HANDLERS.canHeal(event.player) ? Event.Result.ALLOW : Event.Result.DENY;
        event.setResult(result);
    }

    @Override
    public void setupConfig() {
        blockBreakExhaustion = (float) loadPropDouble("Block Breaking Exhaustion", "Set Exhaustion from breaking a block", 0.1);
        passiveExhaustion = (float) loadPropDouble("Passive Exhaustion", "Passive Exhaustion value", 3f);
        passiveExhaustionTick = loadPropInt("Passive Exhaustion Tick", "Passive exhaustion tick time", 900);

        overrideMushrooms = loadPropBool("Edible Mushrooms", "Override Mushrooms to be edible, be careful with the red one ;)", true);
        overridePumpkinSeeds = loadPropBool("Edible Pumpkin Seeds", "Override Pumpkin Seeds to be edible", true);
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        BWRegistry.PENALTY_HANDLERS.add(hungerPenalties = new HungerPenalties());
        BWRegistry.PENALTY_HANDLERS.add(fatPenalties = new FatPenalties());

        if (overridePumpkinSeeds)
            BWMItems.registerItem(PUMPKIN_SEEDS);
        if (overrideMushrooms) {
            BWMItems.registerItem(BROWN_MUSHROOM);
            BWMItems.registerItem(RED_MUSHROOM);
        }
    }

    @Override
    public void init(FMLInitializationEvent event) {
        FoodHelper.registerFood(new ItemStack(Items.BEEF), 12);
        FoodHelper.registerFood(new ItemStack(Items.PORKCHOP), 12);
        FoodHelper.registerFood(new ItemStack(Items.RABBIT), 12);
        FoodHelper.registerFood(new ItemStack(Items.CHICKEN), 9);
        FoodHelper.registerFood(new ItemStack(Items.MUTTON), 9);
        FoodHelper.registerFood(new ItemStack(Items.FISH), 9);
        FoodHelper.registerFood(new ItemStack(Items.FISH, 1, 1), 9);
        FoodHelper.registerFood(new ItemStack(Items.COOKED_BEEF), 15);
        FoodHelper.registerFood(new ItemStack(Items.COOKED_PORKCHOP), 15);
        FoodHelper.registerFood(new ItemStack(Items.COOKED_RABBIT), 15);
        FoodHelper.registerFood(new ItemStack(Items.COOKED_CHICKEN), 12);
        FoodHelper.registerFood(new ItemStack(Items.COOKED_MUTTON), 12);
        FoodHelper.registerFood(new ItemStack(Items.COOKED_FISH), 12);
        FoodHelper.registerFood(new ItemStack(Items.COOKED_FISH, 1, 1), 12);
        FoodHelper.registerFood(new ItemStack(Items.SPIDER_EYE), 6);
        FoodHelper.registerFood(new ItemStack(Items.ROTTEN_FLESH), 9);
        FoodHelper.registerFood(new ItemStack(Items.MUSHROOM_STEW), 9);
        FoodHelper.registerFood(new ItemStack(Items.BEETROOT_SOUP), 9);
        FoodHelper.registerFood(new ItemStack(Items.RABBIT_STEW), 30);
        FoodHelper.registerFood(new ItemStack(Items.MELON), 2);
        FoodHelper.registerFood(new ItemStack(Items.APPLE), 3);
        FoodHelper.registerFood(new ItemStack(Items.POTATO), 3);
        FoodHelper.registerFood(new ItemStack(Items.CARROT), 3);
        FoodHelper.registerFood(new ItemStack(Items.BEETROOT), 3);
        FoodHelper.registerFood(new ItemStack(Items.BAKED_POTATO), 6);
        FoodHelper.registerFood(new ItemStack(Items.BREAD), 12);
        FoodHelper.registerFood(new ItemStack(Items.GOLDEN_APPLE), 3);
        FoodHelper.registerFood(new ItemStack(Items.GOLDEN_APPLE, 1, 1), 3);
        FoodHelper.registerFood(new ItemStack(Items.GOLDEN_CARROT), 3);
        FoodHelper.registerFood(new ItemStack(BWMItems.BEEF_DINNER), 24);
        FoodHelper.registerFood(new ItemStack(BWMItems.BEEF_POTATOES), 18);
        FoodHelper.registerFood(new ItemStack(BWMItems.RAW_KEBAB), 18);
        FoodHelper.registerFood(new ItemStack(BWMItems.COOKED_KEBAB), 24);
        FoodHelper.registerFood(new ItemStack(BWMItems.CHICKEN_SOUP), 24);
        FoodHelper.registerFood(new ItemStack(BWMItems.CHOWDER), 15);
        FoodHelper.registerFood(new ItemStack(BWMItems.HEARTY_STEW), 30);
        FoodHelper.registerFood(new ItemStack(BWMItems.PORK_DINNER), 24);
        FoodHelper.registerFood(new ItemStack(BWMItems.RAW_EGG), 6);
        FoodHelper.registerFood(new ItemStack(BWMItems.COOKED_EGG), 9);
        FoodHelper.registerFood(new ItemStack(BWMItems.RAW_SCRAMBLED_EGG), 12);
        FoodHelper.registerFood(new ItemStack(BWMItems.COOKED_SCRAMBLED_EGG), 15);
        FoodHelper.registerFood(new ItemStack(BWMItems.RAW_OMELET), 9);
        FoodHelper.registerFood(new ItemStack(BWMItems.COOKED_OMELET), 12);
        FoodHelper.registerFood(new ItemStack(BWMItems.HAM_AND_EGGS), 18);
        FoodHelper.registerFood(new ItemStack(BWMItems.TASTY_SANDWICH), 18);
        FoodHelper.registerFood(new ItemStack(BWMItems.CREEPER_OYSTER), 6);
        FoodHelper.registerFood(new ItemStack(BWMItems.KIBBLE), 9);
        FoodHelper.registerFood(new ItemStack(BWMItems.WOLF_CHOP), 12);
        FoodHelper.registerFood(new ItemStack(BWMItems.COOKED_WOLF_CHOP), 15);
        FoodHelper.registerFood(new ItemStack(BWMItems.MYSTERY_MEAT), 9);
        FoodHelper.registerFood(new ItemStack(BWMItems.COOKED_MYSTERY_MEAT), 12);
        FoodHelper.registerFood(new ItemStack(BWMItems.BAT_WING), 3);
        FoodHelper.registerFood(new ItemStack(BWMItems.COOKED_BAT_WING), 6);
        FoodHelper.registerFood(new ItemStack(Items.CHORUS_FRUIT), 3, 0, true);
        FoodHelper.registerFood(new ItemStack(BWMItems.DONUT), 3, 1.5f, true);
        FoodHelper.registerFood(new ItemStack(BWMItems.APPLE_PIE), 9, 12, true);
        FoodHelper.registerFood(new ItemStack(BWMItems.CHOCOLATE), 6, 3, true);
        FoodHelper.registerFood(new ItemStack(Items.COOKIE), 3, 3, true);
        FoodHelper.registerFood(new ItemStack(Items.PUMPKIN_PIE), 9, 12, true);
        FoodHelper.registerFood(new ItemStack(Items.CAKE), 4, 12, true);
        FoodHelper.registerFood(new ItemStack(PUMPKIN_SEEDS), 1);
        ((IEdibleBlock) Blocks.CAKE).setEdibleAtMaxHunger(true);

        Map<ItemStack, FoodValues> hungerOverrides = loadFoodMap("Food hunger overrides", configCategory, "Allow modifying food values with hchunger, format with each value after the ; being optional, item=hunger;fat;alwaysEdible. An example is minecraft:cooked_beef=8;1.166;false", new String[0]);
        for(Map.Entry<ItemStack,FoodValues> e: hungerOverrides.entrySet()) {
            FoodHelper.registerFood(e.getKey(), e.getValue(), false);
        }

    }

    @Override
    public void preInitClient(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(ClientSide.class);
        super.preInitClient(event);
    }

    //Changes food to correct value.
    @SubscribeEvent
    public void modifyFoodValues(FoodEvent.GetFoodValues event) {
        event.foodValues = FoodHelper.getFoodValue(event.food).orElseGet(() -> new FoodValues(Math.min(event.foodValues.hunger * 3, 60), 0));
    }

    @SubscribeEvent
    public void onFoodStatsAdd(FoodEvent.FoodStatsAddition event) {
        event.setCanceled(true);

        int maxHunger = AppleCoreAPI.accessor.getMaxHunger(event.player);
        int newHunger = Math.min(event.player.getFoodStats().getFoodLevel() + event.foodValuesToBeAdded.hunger, maxHunger);
        AppleCoreAPI.mutator.setHunger(event.player, newHunger);

        float saturationIncrement = event.foodValuesToBeAdded.saturationModifier;
        float newSaturation = Math.min(event.player.getFoodStats().getSaturationLevel() + saturationIncrement, newHunger);
        AppleCoreAPI.mutator.setSaturation(event.player, newSaturation);
    }

    @SubscribeEvent
    public void getPlayerFoodValue(FoodEvent.GetPlayerFoodValues event) {
        if (event.player == null)
            return;
        FoodStats stats = event.player.getFoodStats();
        int playerFoodLevel = stats.getFoodLevel();
        int foodLevel = event.foodValues.hunger;
        int max = AppleCoreAPI.accessor.getMaxHunger(event.player);
        int newFood = (foodLevel + playerFoodLevel);
        if (newFood <= max) {
            event.foodValues = new FoodValues(foodLevel, event.foodValues.saturationModifier);
        } else {
            float fat = event.foodValues.saturationModifier == 0 ? (newFood - max) : event.foodValues.saturationModifier;
            event.foodValues = new FoodValues(foodLevel, fat);
        }
    }

    //Changes exhaustion to reduce food first, then fat.
    @SubscribeEvent
    public void exhaust(ExhaustionEvent.Exhausted event) {
        FoodStats stats = event.player.getFoodStats();
        int saturation = (int) ((stats.getSaturationLevel() - 1) / 6);
        int hunger = stats.getFoodLevel() / 6;
        if (hunger >= saturation) {
            event.deltaSaturation = 0;
            event.deltaHunger = -1;
        } else {
            event.deltaSaturation = -1;
            event.deltaHunger = 0;
        }
    }

    //Adds Exhaustion when Jumping and cancels Jump if too exhausted
    @SubscribeEvent
    public void onJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            player.addExhaustion(0.5f);
        }
    }

    @SubscribeEvent
    public void setMaxFood(HungerEvent.GetMaxHunger event) {
        event.maxHunger = 60;
    }

    //Change Health Regen speed to take 30 seconds
    @SubscribeEvent
    public void healthRegenSpeed(HealthRegenEvent.GetRegenTickPeriod event) {
        event.regenTickPeriod = 600;
    }

    //Stop regen from Fat value.
    @SubscribeEvent
    public void denyFatRegen(HealthRegenEvent.AllowSaturatedRegen event) {
        event.setResult(Event.Result.DENY);
    }

    @SubscribeEvent
    public void entityConstruct(EntityEvent.EntityConstructing e) {
        if (e.getEntity() instanceof EntityPlayer) {
            e.getEntity().getDataManager().register(EXHAUSTION_TICK, 0);
        }
    }

    private int getExhaustionTick(EntityPlayer player) {
        return player.getDataManager().get(EXHAUSTION_TICK);
    }

    private void setExhaustionTick(EntityPlayer player, int tick) {
        player.getDataManager().set(EXHAUSTION_TICK, tick);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.world.isRemote && event.phase == TickEvent.Phase.START) {
            EntityPlayer player = event.player;

            if (!PlayerHelper.isSurvival(player) || player.world.getDifficulty() == EnumDifficulty.PEACEFUL)
                return;
            int tick = getExhaustionTick(player);
            if (tick > passiveExhaustionTick) {
                BWMod.getLog().debug("Adding Exhaustion ({}) after {} ticks", passiveExhaustion, passiveExhaustionTick);
                player.addExhaustion(passiveExhaustion);
                setExhaustionTick(player, 0);
            } else {
                BWMod.getLog().debug(" {} exhaustion ticks", getExhaustionTick(player));
                setExhaustionTick(player, getExhaustionTick(player) + 1);
            }
        }
    }

    //Shake Hunger bar whenever any exhaustion is given?
    @SubscribeEvent
    public void onExhaustAdd(ExhaustionEvent.ExhaustionAddition event) {
        if (event.deltaExhaustion >= blockBreakExhaustion) {
            if (event.player instanceof EntityPlayerMP)
                BWNetwork.INSTANCE.sendTo(new MessageHungerShake(), (EntityPlayerMP) event.player);
            else
                GuiHunger.INSTANCE.shake();
        }
    }

    @SubscribeEvent
    public void onStarve(StarvationEvent.AllowStarvation event) {
        if (event.player.getFoodStats().getFoodLevel() <= 0 && event.player.getFoodStats().getSaturationLevel() <= 0)
            event.setResult(Event.Result.ALLOW);
    }

    @SubscribeEvent
    public void onStarve(StarvationEvent.Starve event) {
        event.setCanceled(true);
        event.player.attackEntityFrom(DamageSource.STARVE, 1);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onHarvest(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        if (event.isCanceled() || !PlayerHelper.isSurvival(player))
            return;
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState state = world.getBlockState(pos);
        ItemStack stack = player.getHeldItemMainhand();
        String tooltype = state.getBlock().getHarvestTool(state);
        if (tooltype != null && state.getBlockHardness(world, pos) <= 0 && stack.getItem().getHarvestLevel(stack, tooltype, player, state) < HCTools.noHungerThreshold)
            return; //doesn't consume hunger if using iron tier axes
        player.addExhaustion(blockBreakExhaustion - 0.005f);
    }

    public String getFeatureDescription() {
        return "This Feature REQUIRES AppleCore!!!.\n" +
                "Completely revamps the hunger system of Minecraft. \n" +
                "The Saturation value is replaced with Fat. \n" +
                "Fat will accumulate if too much food is consumed then need to fill the bar.\n" +
                "Fat will only be burned once the entire hunger bar is emptied \n" +
                "The more fat the slower you will walk.\n" +
                "Food Items values are also changed, while a ton of new foods are add.";
    }

    @Override
    public boolean requiresMinecraftRestartToEnable() {
        return true;
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public static class ClientSide {

        //Replaces Hunger Gui with HCHunger
        @SubscribeEvent
        public static void replaceHungerGui(RenderGameOverlayEvent.Pre event) {
            if (event.getType() == RenderGameOverlayEvent.ElementType.FOOD) {
                event.setCanceled(true);
                GuiHunger.INSTANCE.draw();
            }
        }

    }

    public static Pair<FoodValues, Boolean> loadFoodValue(String value) {

        String[] v = value.split(";");

        int hunger = 0;
        float fat = 0.0f;
        boolean alwaysEdible = false;

        if (v.length > 0)
            hunger = Integer.parseInt(v[0]);

        if (v.length > 1)
            fat = Float.parseFloat(v[1]);

        if (v.length > 2)
            alwaysEdible = Boolean.parseBoolean(v[2]);

        return Pair.of(new FoodValues(hunger, fat), alwaysEdible);
    }

    public static HashMap<ItemStack, FoodValues> loadFoodMap(String propName, String category, String desc, String[] _default) {
        HashMap<ItemStack, FoodValues> map = Maps.newHashMap();
        String[] l = ConfigHelper.loadPropStringList(propName, category, desc, _default);
        for (String s : l) {
            String[] a = s.split("=");
            if (a.length == 2) {
                ItemStack stack = ConfigHelper.stackFromString(a[0]);
                Pair<FoodValues, Boolean> value = loadFoodValue(a[1]);
                if(value.getValue())
                    FoodHelper.setAlwaysEdible(stack);
                map.put(stack, value.getKey());
            }
        }
        return map;
    }

}




