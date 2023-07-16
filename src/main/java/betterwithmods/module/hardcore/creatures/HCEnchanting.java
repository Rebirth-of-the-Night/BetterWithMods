package betterwithmods.module.hardcore.creatures;

import betterwithmods.common.items.ItemArcaneScroll;
import betterwithmods.module.Feature;
import betterwithmods.util.InfernalEnchantment;
import betterwithmods.util.InvUtils;
import betterwithmods.util.WorldUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.DimensionType;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HCEnchanting extends Feature {
    private static final HashMap<Class<? extends EntityLivingBase>, ScrollDrop> SCROLL_DROPS = Maps.newHashMap();

    private static Map<Enchantment, Integer> LEVEL_OVERRIDE = Maps.newHashMap();
    private static double dropChance, lootingDropBonus;
    private static boolean fuckMending;
    private static boolean steelRequiresInfernal;

    public static void addEnchantOverride(Enchantment enchantment, int max) {
        LEVEL_OVERRIDE.put(enchantment, max);
    }

    public static int getMaxLevel(Enchantment enchantment) {
        return LEVEL_OVERRIDE.getOrDefault(enchantment, enchantment.getMaxLevel());
    }

    public static boolean canEnchantSteel(Enchantment enchantment) {
        return !steelRequiresInfernal || enchantment instanceof InfernalEnchantment;
    }

    public static void addScrollDrop(Class<? extends EntityLivingBase> clazz, Enchantment enchantment) {
        addScrollDrop(clazz, (entity) -> ItemArcaneScroll.getScrollWithEnchant(enchantment));
    }

    public static void addScrollDrop(Class<? extends EntityLivingBase> clazz, ItemStack scroll) {
        addScrollDrop(clazz, (entity) -> scroll);
    }

    public static void addScrollDrop(Class<? extends EntityLivingBase> clazz, ScrollDrop scroll) {
        SCROLL_DROPS.put(clazz, scroll);
    }

    @Override
    public void setupConfig() {
        steelRequiresInfernal = loadPropBool("Steel Requires Infernal Enchanter", "Soulforged Steel tools can only be enchanted with the Infernal Enchanter", true);
        dropChance = loadPropDouble("Arcane Scroll Drop Chance", "Percentage chance that an arcane scroll will drop, does not effect some scrolls.", 0.001);
        lootingDropBonus = loadPropDouble("Arcane Scroll Looting Bonu Multiplier", "Increase the chance of getting a scroll with looting enchants baseChance + (lootingDropBonus * lootingLevel)", 0.1);
        fuckMending = loadPropBool("Disable Mending", "Mending is a bad unbalanced pile of poo", true);
    }

    @Override
    public String getFeatureDescription() {
        return "Adds Arcane Scroll drops to specific mobs, used for enchanting with the Infernal Enchanter";
    }

    @Override
    public void init(FMLInitializationEvent event) {

        addScrollDrop(EntitySlime.class, Enchantments.PROTECTION);
        addScrollDrop(EntityPigZombie.class, Enchantments.FIRE_PROTECTION);
        addScrollDrop(EntityBat.class, Enchantments.FEATHER_FALLING);
        addScrollDrop(EntityCreeper.class, Enchantments.BLAST_PROTECTION);
        addScrollDrop(AbstractSkeleton.class, (entity) -> {
            if (entity.world.provider.getDimensionType() == DimensionType.NETHER)
                return ItemArcaneScroll.getScrollWithEnchant(Enchantments.INFINITY);
            else
                return ItemArcaneScroll.getScrollWithEnchant(Enchantments.PROJECTILE_PROTECTION);
        });
        addScrollDrop(EntitySquid.class, Enchantments.RESPIRATION);
        addScrollDrop(EntityWitch.class, Enchantments.AQUA_AFFINITY);
        addScrollDrop(EntityZombie.class, Enchantments.SMITE);
        addScrollDrop(EntitySpider.class, Enchantments.BANE_OF_ARTHROPODS);
        addScrollDrop(EntityMagmaCube.class, Enchantments.FIRE_ASPECT);
        addScrollDrop(EntityEnderman.class, Enchantments.SILK_TOUCH);
        addScrollDrop(EntityGhast.class, Enchantments.PUNCH);
        addScrollDrop(EntityBlaze.class, Enchantments.FLAME);
        addScrollDrop(EntityPolarBear.class, Enchantments.FROST_WALKER);
        addScrollDrop(EntityGuardian.class, Enchantments.DEPTH_STRIDER);
        if (!fuckMending) {
            addScrollDrop(EntityShulker.class, new ScrollDrop() {
                @Override
                public ItemStack getScroll(EntityLivingBase entity) {
                    return ItemArcaneScroll.getScrollWithEnchant(Enchantments.MENDING);
                }

                @Override
                public double getChance() {
                    return 0.001;
                }
            });
        }
        addScrollDrop(EntityDragon.class, new ScrollDrop() {
            @Override
            public ItemStack getScroll(EntityLivingBase entity) {
                return ItemArcaneScroll.getScrollWithEnchant(Enchantments.SWEEPING);
            }

            //Always drops
            @Override
            public double getChance() {
                return 1;
            }
        });
        addScrollDrop(EntityWither.class, new ScrollDrop() {
            @Override
            public ItemStack getScroll(EntityLivingBase entity) {
                return ItemArcaneScroll.getScrollWithEnchant(Enchantments.KNOCKBACK);
            }

            //Always drops
            @Override
            public double getChance() {
                return 1;
            }
        });
        addScrollDrop(EntitySilverfish.class, entity -> {
            if (entity.world.provider.getDimensionType() == DimensionType.THE_END) {
                return ItemArcaneScroll.getScrollWithEnchant(Enchantments.EFFICIENCY);
            }
            return null;
        });

        addEnchantOverride(Enchantments.RESPIRATION, 5);


        EntityVillager.ITradeList sharpness = new PricedTradeList(
                new ItemStack(Items.EMERALD),
                new EntityVillager.PriceInfo(35, 50), ItemArcaneScroll.getScrollWithEnchant(Enchantments.SHARPNESS));

        EntityVillager.ITradeList looting = new PricedTradeList(
                new ItemStack(Items.EMERALD),
                new EntityVillager.PriceInfo(15, 30), ItemArcaneScroll.getScrollWithEnchant(Enchantments.LOOTING));

        EntityVillager.ITradeList unbreaking = new PricedTradeList(
                new ItemStack(Items.EMERALD),
                new EntityVillager.PriceInfo(34, 47), ItemArcaneScroll.getScrollWithEnchant(Enchantments.UNBREAKING));

        EntityVillager.ITradeList efficiency = new PricedTradeList(
                new ItemStack(Items.EMERALD),
                new EntityVillager.PriceInfo(34, 63), ItemArcaneScroll.getScrollWithEnchant(Enchantments.EFFICIENCY));

        EntityVillager.ITradeList fortune = new PricedTradeList(
                new ItemStack(Items.EMERALD),
                new EntityVillager.PriceInfo(48, 62), ItemArcaneScroll.getScrollWithEnchant(Enchantments.FORTUNE));

        EntityVillager.ITradeList power = new PricedTradeList(
                new ItemStack(Items.EMERALD),
                new EntityVillager.PriceInfo(32, 40), ItemArcaneScroll.getScrollWithEnchant(Enchantments.POWER));


        EntityVillager.ITradeList luckofsea = new PricedTradeList(
                new ItemStack(Items.EMERALD),
                new EntityVillager.PriceInfo(15, 30), ItemArcaneScroll.getScrollWithEnchant(Enchantments.LUCK_OF_THE_SEA));

        EntityVillager.ITradeList lure = new PricedTradeList(
                new ItemStack(Items.EMERALD),
                new EntityVillager.PriceInfo(10, 20), ItemArcaneScroll.getScrollWithEnchant(Enchantments.LUCK_OF_THE_SEA));



        addVillagerTrade("minecraft:butcher", 0, 5, sharpness);
        addVillagerTrade("minecraft:farmer", 0, 5, looting);
        addVillagerTrade("minecraft:smith", 2, 5, unbreaking);
        addVillagerTrade("minecraft:smith", 2, 5, efficiency);
        addVillagerTrade("minecraft:priest", 0, 5, fortune);
        addVillagerTrade("minecraft:librarian", 0, 5, power);
        addVillagerTrade("minecraft:farmer", 1, 5, luckofsea);
        addVillagerTrade("minecraft:farmer", 1, 5, lure);

    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @SubscribeEvent
    public void onDeath(LivingDropsEvent event) {
        for (Class<? extends EntityLivingBase> entity : SCROLL_DROPS.keySet()) {
            if (entity.isAssignableFrom(event.getEntityLiving().getClass())) {
                ScrollDrop drop = SCROLL_DROPS.get(entity);
                if (drop.getScroll(event.getEntityLiving()) != null) {
                    double chance = event.getEntityLiving().getRNG().nextDouble() + (lootingDropBonus * event.getLootingLevel());
                    if (chance <= drop.getChance()) {
                        WorldUtils.addDrop(event, drop.getScroll(event.getEntityLiving()));
                    }
                }
            }
        }
    }

    @FunctionalInterface
    public interface ScrollDrop {
        ItemStack getScroll(EntityLivingBase entity);

        default double getChance() {
            return dropChance;
        }
    }


    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            return;

        if (!event.player.getEntityWorld().isRemote) {
            int mod = EnchantmentHelper.getRespirationModifier(event.player);
            if (mod >= 5) {
                event.player.setAir(300);
            }
        }
    }


    public static void addVillagerTrade(String profession, int careerId, int level, EntityVillager.ITradeList trade) {
        VillagerRegistry.VillagerProfession profression = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation(profession));
        if (profression != null) {
            VillagerRegistry.VillagerCareer career = profression.getCareer(careerId);
            career.addTrade(level, trade);
        }

    }

    public static class TradeList implements EntityVillager.ITradeList {
        private List<MerchantRecipe> recipes;

        public TradeList(MerchantRecipe... recipes) {
            this.recipes = Lists.newArrayList(recipes);
        }

        @Override
        public void addMerchantRecipe(@Nonnull IMerchant merchant, @Nonnull MerchantRecipeList recipeList, @Nonnull Random random) {
            recipeList.addAll(recipes);
        }

    }

    public static class PricedTradeList implements EntityVillager.ITradeList {
        @Nonnull
        private ItemStack input;
        @Nonnull
        private EntityVillager.PriceInfo info;
        @Nonnull
        private ItemStack output;

        public PricedTradeList(@Nonnull ItemStack input, @Nonnull EntityVillager.PriceInfo info, @Nonnull ItemStack output) {
            this.input = input;
            this.info = info;
            this.output = output;
        }

        private ItemStack getInput(Random random) {

            ItemStack copy = input.copy();
            InvUtils.setCount(copy, info.getPrice(random));
            return copy;
        }

        @Override
        public void addMerchantRecipe(@Nonnull IMerchant merchant, @Nonnull MerchantRecipeList recipeList, @Nonnull Random random) {
            recipeList.add(new MerchantRecipe(getInput(random), output));
        }
    }

}
