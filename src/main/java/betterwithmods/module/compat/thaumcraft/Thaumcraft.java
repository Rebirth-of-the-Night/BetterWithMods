package betterwithmods.module.compat.thaumcraft;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWMItems;
import betterwithmods.common.BWMRecipes;
import betterwithmods.common.blocks.*;
import betterwithmods.common.blocks.mechanical.BlockCookingPot;
import betterwithmods.common.items.ItemArcaneScroll;
import betterwithmods.common.items.ItemMaterial;
import betterwithmods.common.registry.anvil.AnvilCraftingManager;
import betterwithmods.module.CompatFeature;
import betterwithmods.module.gameplay.miniblocks.ItemMini;
import betterwithmods.module.gameplay.miniblocks.MiniBlocks;
import betterwithmods.module.gameplay.miniblocks.MiniType;
import betterwithmods.module.gameplay.miniblocks.blocks.BlockMini;
import betterwithmods.module.tweaks.LongBoi;
import net.minecraft.block.BlockSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class Thaumcraft extends CompatFeature {

    public static boolean changeStart;

    public Thaumcraft() {
        super("thaumcraft");
    }

    private static AspectList getMini(ItemStack stack) {
        IBlockState state = ItemMini.getState(stack);
        AspectList list = new AspectList(BWMRecipes.getStackFromState(state));
        for (Aspect aspect : list.getAspects()) {
            int count = list.getAmount(aspect);
            list.remove(aspect, count / 2);
        }
        return list.copy();
    }

    public static void registerAnvilRecipeAspects() {
        ArrayList<String> history = new ArrayList<>();
        for (IRecipe recipe : AnvilCraftingManager.ANVIL_CRAFTING) {

            AspectList tmp = null;
            //TODO temp disable this until I actually decide to fix it

            Method method = ObfuscationReflectionHelper.findMethod(ThaumcraftCraftingManager.class, "getAspectsFromIngredients", AspectList.class, ItemStack.class, IRecipe.class, ArrayList.class);
            method.setAccessible(true);

            try {
                tmp = (AspectList) method.invoke(null, recipe.getIngredients(), recipe.getRecipeOutput(), null, history);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            if (tmp != null)
                ThaumcraftApi.registerComplexObjectTag(recipe.getRecipeOutput(), tmp);
        }
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMBlocks.STEEL_BROKEN_GEARBOX), new AspectList(new ItemStack(BWMBlocks.STEEL_GEARBOX)));
        ThaumcraftApi.registerComplexObjectTag(BlockAesthetic.getStack(BlockAesthetic.EnumType.CHOPBLOCKBLOOD), new AspectList(BlockAesthetic.getStack(BlockAesthetic.EnumType.CHOPBLOCK)).add(Aspect.DEATH, 5));
    }

    @Override
    public void setupConfig() {
        changeStart = loadPropBool("Change Thaumcraft Start", "Due to HCBeds stopping sleeping, Thaumcraft can not be started the normal way. This changes it to only require you to right click on a bed to have the dream", true);
        if (changeStart) {
            MinecraftForge.EVENT_BUS.register(new StartChanges());
        }

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        registerAspectOverrides();
        registerAspects();
        registerAnvilRecipeAspects();
    }

    public void registerAspectOverrides() {

        //Fix the Axe recipe changing the aspects to have metal
        //TODO remove this if fixed by Azanor
        ThaumcraftApi.registerComplexObjectTag("plankWood", new AspectList().add(Aspect.PLANT, 1));
        //This happens due to shears too
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.LEATHER_CUT), new AspectList().add(Aspect.BEAST, 2).add(Aspect.PROTECT, 2));

        //Netherrack in our lore has souls in it
        ThaumcraftApi.registerObjectTag(new ItemStack(Blocks.NETHERRACK), new AspectList(new ItemStack(Blocks.NETHERRACK)).add(Aspect.SOUL, 1));

    }

    public AspectList fromItemStacks(ItemStack... stacks) {

        AspectList tmp = new AspectList();
        for (ItemStack stack : stacks) {
            AspectList l = new AspectList(stack);
            for (int i = 0; i < stack.getCount(); i++)
                tmp.add(l);
        }
        return tmp;
    }

    public AspectList getEnchantmentAspects(Enchantment enchantment) {
        AspectList tmp = new AspectList();
        //Borrowed from ThaumcraftCraftingManager

        int lvl = enchantment.getMaxLevel();
        int magic = lvl;
        if (enchantment == Enchantments.AQUA_AFFINITY) {
            tmp.merge(Aspect.WATER, lvl);
        } else if (enchantment == Enchantments.BANE_OF_ARTHROPODS) {
            tmp.merge(Aspect.BEAST, lvl / 2).merge(Aspect.AVERSION, lvl / 2);
        } else if (enchantment == Enchantments.BLAST_PROTECTION) {
            tmp.merge(Aspect.PROTECT, lvl / 2).merge(Aspect.ENTROPY, lvl / 2);
        } else if (enchantment == Enchantments.EFFICIENCY) {
            tmp.merge(Aspect.TOOL, lvl);
        } else if (enchantment == Enchantments.FEATHER_FALLING) {
            tmp.merge(Aspect.FLIGHT, lvl);
        } else if (enchantment == Enchantments.FIRE_ASPECT) {
            tmp.merge(Aspect.FIRE, lvl / 2).merge(Aspect.AVERSION, lvl / 2);
        } else if (enchantment == Enchantments.FIRE_PROTECTION) {
            tmp.merge(Aspect.PROTECT, lvl / 2).merge(Aspect.FIRE, lvl / 2);
        } else if (enchantment == Enchantments.FLAME) {
            tmp.merge(Aspect.FIRE, lvl);
        } else if (enchantment == Enchantments.FORTUNE) {
            tmp.merge(Aspect.DESIRE, lvl);
        } else if (enchantment == Enchantments.INFINITY) {
            tmp.merge(Aspect.CRAFT, lvl);
        } else if (enchantment == Enchantments.KNOCKBACK) {
            tmp.merge(Aspect.AIR, lvl);
        } else if (enchantment == Enchantments.LOOTING) {
            tmp.merge(Aspect.DESIRE, lvl);
        } else if (enchantment == Enchantments.POWER) {
            tmp.merge(Aspect.AVERSION, lvl);
        } else if (enchantment == Enchantments.PROJECTILE_PROTECTION) {
            tmp.merge(Aspect.PROTECT, lvl);
        } else if (enchantment == Enchantments.PROTECTION) {
            tmp.merge(Aspect.PROTECT, lvl);
        } else if (enchantment == Enchantments.PUNCH) {
            tmp.merge(Aspect.AIR, lvl);
        } else if (enchantment == Enchantments.RESPIRATION) {
            tmp.merge(Aspect.AIR, lvl);
        } else if (enchantment == Enchantments.SHARPNESS) {
            tmp.merge(Aspect.AVERSION, lvl);
        } else if (enchantment == Enchantments.SILK_TOUCH) {
            tmp.merge(Aspect.EXCHANGE, lvl);
        } else if (enchantment == Enchantments.THORNS) {
            tmp.merge(Aspect.AVERSION, lvl);
        } else if (enchantment == Enchantments.SMITE) {
            tmp.merge(Aspect.UNDEAD, lvl / 2).merge(Aspect.AVERSION, lvl / 2);
        } else if (enchantment == Enchantments.UNBREAKING) {
            tmp.merge(Aspect.EARTH, lvl);
        } else if (enchantment == Enchantments.DEPTH_STRIDER) {
            tmp.merge(Aspect.WATER, lvl);
        } else if (enchantment == Enchantments.LUCK_OF_THE_SEA) {
            tmp.merge(Aspect.DESIRE, lvl);
        } else if (enchantment == Enchantments.LURE) {
            tmp.merge(Aspect.BEAST, lvl);
        } else if (enchantment == Enchantments.FROST_WALKER) {
            tmp.merge(Aspect.COLD, lvl);
        } else if (enchantment == Enchantments.MENDING) {
            tmp.merge(Aspect.CRAFT, lvl);
        }
        if (enchantment.getRarity() == Enchantment.Rarity.UNCOMMON) {
            magic += 2;
        }

        if (enchantment.getRarity() == Enchantment.Rarity.RARE) {
            magic += 4;
        }

        if (enchantment.getRarity() == Enchantment.Rarity.VERY_RARE) {
            magic += 6;
        }

        if (magic > 0) {
            tmp.merge(Aspect.MAGIC, magic);
        }
        return tmp;

    }

    public void registerAspects() {

        ThaumcraftApi.registerEntityTag("longboi", (new AspectList()).add(Aspect.BEAST, 15).add(Aspect.EARTH, 10).add(Aspect.CRAFT, 5).add(Aspect.MAN, 5));
        ThaumcraftApi.registerEntityTag("bwm_jungle_spider", (new AspectList()).add(Aspect.BEAST, 5).add(Aspect.DEATH, 10).add(Aspect.TRAP, 10));


        ThaumcraftApi.registerObjectTag(new ItemStack(BWMBlocks.WOLF), (new AspectList()).add(Aspect.BEAST, 15).add(Aspect.EARTH, 10).add(Aspect.AVERSION, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(LongBoi.LONG_FRIEND), (new AspectList()).add(Aspect.BEAST, 15).add(Aspect.EARTH, 10).add(Aspect.CRAFT, 5).add(Aspect.MAN, 5));

        //Wood
        ThaumcraftApi.registerObjectTag("barkWood", new AspectList().add(Aspect.PLANT, 2));
        ThaumcraftApi.registerObjectTag("dustWood", new AspectList().add(Aspect.PLANT, 2));
        ThaumcraftApi.registerComplexObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SOUL_DUST), new AspectList().add(Aspect.SOUL, 2));

        //Hemp
        ThaumcraftApi.registerObjectTag("cropHemp", new AspectList().add(Aspect.PLANT, 6).add(Aspect.CRAFT, 6));
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMBlocks.HEMP), new AspectList().add(Aspect.PLANT, 5).add(Aspect.LIFE, 1));

        ThaumcraftApi.registerObjectTag("fiberHemp", new AspectList().add(Aspect.PLANT, 2).add(Aspect.CRAFT, 2));
        ThaumcraftApi.registerObjectTag("fabricHemp", new AspectList().add(Aspect.PLANT, 18).add(Aspect.CRAFT, 18));

        //Misc
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.CREEPER_OYSTER), new AspectList().add(Aspect.PLANT, 5).add(Aspect.FIRE, 5).add(Aspect.ENERGY, 5));


        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.ELEMENT), fromItemStacks(new ItemStack(Items.BLAZE_POWDER), new ItemStack(Items.STRING), new ItemStack(Items.REDSTONE)));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.FILAMENT), fromItemStacks(new ItemStack(Items.GLOWSTONE_DUST), new ItemStack(Items.STRING), new ItemStack(Items.REDSTONE)));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.DUNG), new AspectList().add(Aspect.BEAST, 5).add(Aspect.UNDEAD, 2));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.GLUE), new AspectList(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.GLUE)).add(Aspect.BEAST, 5));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.POTASH), new AspectList().add(Aspect.FIRE, 4).add(Aspect.ENTROPY, 4).add(Aspect.EARTH, 4));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.NETHER_SLUDGE), fromItemStacks(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.HELLFIRE_DUST, 4), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.POTASH)));

        ThaumcraftApi.registerObjectTag(new ItemStack(Items.NETHERBRICK), new AspectList().add(Aspect.ORDER, 4).add(Aspect.FIRE, 4).add(Aspect.EARTH, 4));

        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.GROUND_NETHERRACK), new AspectList(new ItemStack(Blocks.NETHERRACK)));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.HELLFIRE_DUST), new AspectList().add(Aspect.FIRE, 4));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.CONCENTRATED_HELLFIRE), new AspectList().add(Aspect.FIRE, 32));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SOAP), fromItemStacks(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.POTASH), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.TALLOW)));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.NETHERCOAL), new AspectList().add(Aspect.FIRE, 10).add(Aspect.ENERGY, 10));


        ThaumcraftApi.registerObjectTag(BlockAesthetic.getStack(BlockAesthetic.EnumType.NETHERCLAY), new AspectList(new ItemStack(BWMBlocks.NETHER_CLAY)).add(Aspect.FIRE, 2));
        ThaumcraftApi.registerObjectTag(BlockAesthetic.getStack(BlockAesthetic.EnumType.PADDING), fromItemStacks(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.PADDING, 9)));
        ThaumcraftApi.registerObjectTag(BlockAesthetic.getStack(BlockAesthetic.EnumType.WHITECOBBLE), new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 1));
        ThaumcraftApi.registerObjectTag(BlockAesthetic.getStack(BlockAesthetic.EnumType.WHITESTONE), new AspectList().add(Aspect.EARTH, 5));
        ThaumcraftApi.registerObjectTag(BlockAesthetic.getStack(BlockAesthetic.EnumType.WICKER), fromItemStacks(new ItemStack(BWMBlocks.WICKER, 4)));
        ThaumcraftApi.registerObjectTag(BlockAesthetic.getStack(BlockAesthetic.EnumType.ROPE), fromItemStacks(new ItemStack(BWMBlocks.ROPE, 9)));

        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.FERTILIZER), new AspectList().add(Aspect.LIFE, 5).add(Aspect.UNDEAD, 1).add(Aspect.PLANT, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.FERTILIZER), new AspectList().add(Aspect.LIFE, 5).add(Aspect.UNDEAD, 1).add(Aspect.PLANT, 1));

        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.ENDER_SLAG), new AspectList().add(Aspect.DARKNESS, 5));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SOUL_FLUX), new AspectList().add(Aspect.MAGIC, 5));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.BRIMSTONE), new AspectList().add(Aspect.ALCHEMY, 5).add(Aspect.ENTROPY, 5));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.BLASTING_OIL), fromItemStacks(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.TALLOW), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.HELLFIRE_DUST)).add(Aspect.ALCHEMY, 5));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.NITER), new AspectList().add(Aspect.ENTROPY, 5));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.FUSE), fromItemStacks(new ItemStack(Items.GUNPOWDER), new ItemStack(Items.STRING)));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.DIAMOND_NUGGET), new AspectList().add(Aspect.METAL, 1).add(Aspect.CRYSTAL, 1).add(Aspect.DESIRE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.KIBBLE), new AspectList().add(Aspect.LIFE, 10).add(Aspect.MAN, 10).add(Aspect.ENTROPY, 10).add(Aspect.SENSES, 5).add(Aspect.ENERGY, 1));

        ThaumcraftApi.registerComplexObjectTag(new ItemStack(BWMBlocks.SAW), new AspectList().add(Aspect.TRAP, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(BWMBlocks.DETECTOR), new AspectList().add(Aspect.SENSES, 5));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(BWMBlocks.BUDDY_BLOCK), new AspectList().add(Aspect.SENSES, 5));

        ThaumcraftApi.registerComplexObjectTag("blockCandle", new AspectList().add(Aspect.LIGHT, 8));
        ThaumcraftApi.registerComplexObjectTag(new ItemStack(BWMBlocks.STEEL_PRESSURE_PLATE), new AspectList().add(Aspect.MECHANISM, 5));

        ThaumcraftApi.registerObjectTag(new ItemStack(BWMBlocks.NETHER_GROWTH), fromItemStacks(
                BlockUrn.getStack(BlockUrn.EnumType.FULL, 8),
                new ItemStack(Blocks.BROWN_MUSHROOM),
                new ItemStack(Blocks.RED_MUSHROOM),
                new ItemStack(Items.NETHER_WART),
                new ItemStack(Items.ROTTEN_FLESH)
        ));


        ThaumcraftApi.registerObjectTag(new ItemStack(BWMBlocks.FERTILE_FARMLAND), new AspectList(new ItemStack(Blocks.FARMLAND)).merge(new AspectList(new ItemStack(Items.DYE, EnumDyeColor.WHITE.getDyeDamage()))));
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMBlocks.DIRT_SLAB, 1, BlockDirtSlab.DirtSlabType.GRASS.getMetadata()), new AspectList().add(Aspect.PLANT, 1).add(Aspect.EARTH, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMBlocks.DIRT_SLAB, 1, BlockDirtSlab.DirtSlabType.MYCELIUM.getMetadata()), new AspectList().add(Aspect.PLANT, 1).add(Aspect.EARTH, 1).add(Aspect.FLUX, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMBlocks.SHAFT), new AspectList(new ItemStack(Items.STICK)));

        //Leather
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SCOURED_LEATHER), new AspectList().add(Aspect.BEAST, 5).add(Aspect.PROTECT, 5));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SCOURED_LEATHER_CUT), new AspectList().add(Aspect.BEAST, 2).add(Aspect.PROTECT, 2));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.TANNED_LEATHER), new AspectList().add(Aspect.BEAST, 4).add(Aspect.PROTECT, 6));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.TANNED_LEATHER_CUT), new AspectList().add(Aspect.BEAST, 2).add(Aspect.PROTECT, 3));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.LEATHER_STRAP), new AspectList().add(Aspect.BEAST, 1).add(Aspect.CRAFT, 1));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.LEATHER_BELT), new AspectList().add(Aspect.BEAST, 4).add(Aspect.CRAFT, 4));

        //Tanned leather armor
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.LEATHER_TANNED_HELMET), new AspectList(new ItemStack(Items.LEATHER_HELMET)).add(Aspect.PROTECT, 4));
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.LEATHER_TANNED_CHEST), new AspectList(new ItemStack(Items.LEATHER_CHESTPLATE)).add(Aspect.PROTECT, 4));
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.LEATHER_TANNED_PANTS), new AspectList(new ItemStack(Items.LEATHER_LEGGINGS)).add(Aspect.PROTECT, 4));
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.LEATHER_TANNED_BOOTS), new AspectList(new ItemStack(Items.LEATHER_BOOTS)).add(Aspect.PROTECT, 4));

        //Unfired pottery
        ThaumcraftApi.registerObjectTag(BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.CRUCIBLE), new AspectList().add(Aspect.WATER, 15).add(Aspect.EARTH, 15));
        ThaumcraftApi.registerObjectTag(BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.PLANTER), new AspectList().add(Aspect.WATER, 15).add(Aspect.EARTH, 15));
        ThaumcraftApi.registerObjectTag(BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.VASE), new AspectList().add(Aspect.WATER, 10).add(Aspect.EARTH, 10));
        ThaumcraftApi.registerObjectTag(BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.URN), new AspectList().add(Aspect.WATER, 5).add(Aspect.EARTH, 5));
        ThaumcraftApi.registerObjectTag(BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.BRICK), new AspectList().add(Aspect.WATER, 5).add(Aspect.EARTH, 5));

        ThaumcraftApi.registerObjectTag("blockVase", new AspectList(BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.VASE)).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag("blockUrn", new AspectList(BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.URN)).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag("blockSoulUrn", new AspectList(BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.URN)).add(Aspect.FIRE, 1).add(Aspect.SOUL, 16));
        ThaumcraftApi.registerObjectTag(BlockCookingPot.getStack(BlockCookingPot.EnumType.CRUCIBLE), new AspectList(BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.CRUCIBLE)).add(Aspect.FIRE, 1).add(Aspect.VOID, 10));
        ThaumcraftApi.registerObjectTag(BlockCookingPot.getStack(BlockCookingPot.EnumType.DRAGONVESSEL), new AspectList().add(Aspect.MIND, 10).add(Aspect.ELDRITCH, 10).add(Aspect.MAGIC, 10).add(Aspect.VOID, 10));

        ThaumcraftApi.registerObjectTag("slats", new AspectList().add(Aspect.PLANT, 1).add(Aspect.CRAFT, 1));
        ThaumcraftApi.registerObjectTag("grates", new AspectList().add(Aspect.PLANT, 1).add(Aspect.CRAFT, 1));
        ThaumcraftApi.registerObjectTag("blockWoodTable", new AspectList().add(Aspect.PLANT, 1).add(Aspect.CRAFT, 1));
        ThaumcraftApi.registerObjectTag("blockWoodBench", new AspectList().add(Aspect.PLANT, 1).add(Aspect.CRAFT, 1));


        //Planters
        ThaumcraftApi.registerObjectTag(BlockPlanter.getStack(BlockPlanter.EnumType.EMPTY), new AspectList(BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.PLANTER)).add(Aspect.FIRE, 1));
        ThaumcraftApi.registerObjectTag(BlockPlanter.getStack(BlockPlanter.EnumType.FARMLAND), new AspectList(BlockPlanter.getStack(BlockPlanter.EnumType.EMPTY)).add(new AspectList(new ItemStack(Blocks.FARMLAND))));
        ThaumcraftApi.registerObjectTag(BlockPlanter.getStack(BlockPlanter.EnumType.FERTILE), new AspectList(BlockPlanter.getStack(BlockPlanter.EnumType.EMPTY)).add(new AspectList(new ItemStack(BWMBlocks.FERTILE_FARMLAND))));
        ThaumcraftApi.registerObjectTag(BlockPlanter.getStack(BlockPlanter.EnumType.GRASS), new AspectList(BlockPlanter.getStack(BlockPlanter.EnumType.EMPTY)).add(new AspectList(new ItemStack(Blocks.GRASS))));
        ThaumcraftApi.registerObjectTag(BlockPlanter.getStack(BlockPlanter.EnumType.DIRT), new AspectList(BlockPlanter.getStack(BlockPlanter.EnumType.EMPTY)).add(new AspectList(new ItemStack(Blocks.DIRT))));
        ThaumcraftApi.registerObjectTag(BlockPlanter.getStack(BlockPlanter.EnumType.SOULSAND), new AspectList(BlockPlanter.getStack(BlockPlanter.EnumType.EMPTY)).add(new AspectList(new ItemStack(Blocks.SOUL_SAND))));
        ThaumcraftApi.registerObjectTag(BlockPlanter.getStack(BlockPlanter.EnumType.SAND), new AspectList(BlockPlanter.getStack(BlockPlanter.EnumType.EMPTY)).add(new AspectList(new ItemStack(Blocks.SAND))));
        ThaumcraftApi.registerObjectTag(BlockPlanter.getStack(BlockPlanter.EnumType.REDSAND), new AspectList(BlockPlanter.getStack(BlockPlanter.EnumType.EMPTY)).add(new AspectList(new ItemStack(Blocks.SAND, 1, BlockSand.EnumType.RED_SAND.getMetadata()))));
        ThaumcraftApi.registerObjectTag(BlockPlanter.getStack(BlockPlanter.EnumType.GRAVEL), new AspectList(BlockPlanter.getStack(BlockPlanter.EnumType.EMPTY)).add(new AspectList(new ItemStack(Blocks.GRAVEL))));
        ThaumcraftApi.registerObjectTag(BlockPlanter.getStack(BlockPlanter.EnumType.WATER), new AspectList(BlockPlanter.getStack(BlockPlanter.EnumType.EMPTY)).add(Aspect.WATER, 20));

        //Redstone components
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.REDSTONE_LATCH), new AspectList().add(Aspect.METAL, 3).add(Aspect.DESIRE, 3).add(Aspect.ENERGY, 10));
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMBlocks.WOODEN_GEARBOX), new AspectList(new ItemStack(BWMBlocks.WOODEN_GEARBOX)).add(new AspectList(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.REDSTONE_LATCH))));

        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.CHARCOAL_DUST), new AspectList().add(Aspect.ENERGY, 10).add(Aspect.FIRE, 10));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.COAL_DUST), new AspectList().add(Aspect.ENERGY, 10).add(Aspect.FIRE, 10));

        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.INGOT_STEEL), new AspectList().add(Aspect.METAL, 15).add(Aspect.ENERGY, 10).add(Aspect.FIRE, 10).add(Aspect.SOUL, 16));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.PLATE_STEEL), new AspectList(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.INGOT_STEEL)));

        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.TALLOW), new AspectList().add(Aspect.BEAST, 5).add(Aspect.LIFE, 5).add(Aspect.FIRE, 1).add(Aspect.EARTH, 5));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.BROADHEAD), new AspectList().add(Aspect.METAL, 1).add(Aspect.ENERGY, 1).add(Aspect.FIRE, 1).add(Aspect.SOUL, 1));

        //Food
        AspectList milk = new AspectList().add(Aspect.BEAST, 5).add(Aspect.LIFE, 5).add(Aspect.WATER, 5);
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.CAKE), new AspectList().add(Aspect.PLANT, 15).add(Aspect.LIFE, 13).add(Aspect.WATER, 3).add(Aspect.DESIRE, 2).add(Aspect.ENERGY, 1).add(Aspect.AIR, 1));

        ThaumcraftApi.registerObjectTag(BlockRawPastry.getStack(BlockRawPastry.EnumType.BREAD), new AspectList(new ItemStack(Items.WHEAT)));
        ThaumcraftApi.registerObjectTag(BlockRawPastry.getStack(BlockRawPastry.EnumType.CAKE), new AspectList(new ItemStack(Items.CAKE)));


        ThaumcraftApi.registerObjectTag(new ItemStack(Items.BREAD), new AspectList(BlockRawPastry.getStack(BlockRawPastry.EnumType.BREAD)).add(Aspect.CRAFT, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.APPLE_PIE), new AspectList(BlockRawPastry.getStack(BlockRawPastry.EnumType.APPLE)).add(Aspect.CRAFT, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.APPLE_PIE), new AspectList(BlockRawPastry.getStack(BlockRawPastry.EnumType.APPLE)).add(Aspect.CRAFT, 1));


        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.RAW_EGG), new AspectList(new ItemStack(Items.EGG)));
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.COOKED_EGG), new AspectList(new ItemStack(BWMItems.RAW_EGG)).add(Aspect.CRAFT, 1));

        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.RAW_SCRAMBLED_EGG), new AspectList(new ItemStack(Items.EGG)).add(milk));
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.COOKED_SCRAMBLED_EGG), new AspectList(new ItemStack(BWMItems.RAW_SCRAMBLED_EGG)).add(Aspect.CRAFT, 1));

        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.COOKED_KEBAB), new AspectList(new ItemStack(BWMItems.COOKED_KEBAB)).add(Aspect.CRAFT, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.COOKED_OMELET), new AspectList(new ItemStack(BWMItems.RAW_OMELET)).add(Aspect.CRAFT, 1));

        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.BAT_WING), new AspectList(new ItemStack(Items.PORKCHOP)).add(Aspect.FLIGHT, 5).add(Aspect.DARKNESS, 5));
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.COOKED_BAT_WING), new AspectList(new ItemStack(BWMItems.BAT_WING)).add(Aspect.CRAFT, 1));


        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.WOLF_CHOP), new AspectList(new ItemStack(Items.PORKCHOP)).add(Aspect.AVERSION, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.COOKED_WOLF_CHOP), new AspectList(new ItemStack(BWMItems.WOLF_CHOP)).add(Aspect.CRAFT, 1));

        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.COCOA_POWDER), new AspectList().add(Aspect.DESIRE, 2).add(Aspect.ENERGY, 2));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.POISON_SAC), new AspectList().add(Aspect.DEATH, 2).add(Aspect.BEAST, 2).add(Aspect.TRAP, 2).add(Aspect.ALCHEMY, 2));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.MYSTERY_GLAND), new AspectList().add(Aspect.LIFE, 2).add(Aspect.BEAST, 2).add(Aspect.WATER, 1).add(Aspect.ALCHEMY, 2));
        ThaumcraftApi.registerObjectTag(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.WITCH_WART), new AspectList().add(Aspect.MAN, 5).add(Aspect.MAGIC, 5).add(Aspect.ALCHEMY, 5));


        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.CHICKEN_SOUP), fromItemStacks(new ItemStack(Items.BOWL), new ItemStack(Items.POTATO), new ItemStack(Items.CARROT), new ItemStack(Items.CHICKEN)));

        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.CHOCOLATE), fromItemStacks(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.COCOA_POWDER), new ItemStack(Items.SUGAR)).add(milk));
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.DONUT), new AspectList().add(Aspect.LIFE, 1).add(Aspect.ENERGY, 1).add(Aspect.DESIRE, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.CHOWDER), fromItemStacks(new ItemStack(Items.BOWL), new ItemStack(Items.FISH)).add(milk));
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.MYSTERY_MEAT), new AspectList().add(Aspect.MAN, 5).add(Aspect.LIFE, 5).add(Aspect.ELDRITCH, 1));
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.COOKED_MYSTERY_MEAT), fromItemStacks(new ItemStack(BWMItems.MYSTERY_MEAT)).add(Aspect.FIRE, 1));

        ThaumcraftApi.registerObjectTag(new ItemStack(BWMItems.HEARTY_STEW), fromItemStacks(new ItemStack(Items.BOWL), new ItemStack(Items.POTATO), new ItemStack(Items.CARROT), new ItemStack(Items.COOKED_BEEF), new ItemStack(Items.BOWL), new ItemStack(Blocks.BROWN_MUSHROOM, 3)));

        ThaumcraftApi.registerObjectTag(new ItemStack(BWMBlocks.BLOOD_SAPLING), fromItemStacks(new ItemStack(Blocks.SAPLING, 6), new ItemStack(Items.NETHER_WART), BlockUrn.getStack(BlockUrn.EnumType.FULL, 1)));
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMBlocks.BLOOD_LOG), new AspectList().add(Aspect.PLANT, 20).add(Aspect.DEATH, 5).add(Aspect.SOUL, 2));
        ThaumcraftApi.registerObjectTag(new ItemStack(BWMBlocks.BLOOD_LEAVES), new AspectList().add(Aspect.PLANT, 5).add(Aspect.DEATH, 1).add(Aspect.SOUL, 1));


        ThaumcraftApi.registerObjectTag(new ItemStack(BWMBlocks.WOODEN_BROKEN_GEARBOX), new AspectList().add(Aspect.ENERGY, 10).add(Aspect.DESIRE, 3).add(Aspect.METAL, 3).add(Aspect.PLANT, 9));


        //Arcane Scrolls
        CreativeTabs tab = BWMItems.ARCANE_SCROLL.getCreativeTab();
        if (tab != null) {
            NonNullList<ItemStack> scrolls = NonNullList.create();
            BWMItems.ARCANE_SCROLL.getSubItems(tab, scrolls);
            for (ItemStack scroll : scrolls) {
                Enchantment enchantment = ItemArcaneScroll.getEnchantment(scroll);
                ThaumcraftApi.registerObjectTag(scroll, new AspectList(new ItemStack(Items.PAPER)).merge(getEnchantmentAspects(enchantment)));
            }
        }

        //Mini blocks
        for (MiniType type : MiniBlocks.MINI_MATERIAL_BLOCKS.keySet()) {
            for (BlockMini mini : MiniBlocks.MINI_MATERIAL_BLOCKS.get(type).values()) {
                ItemMini item = (ItemMini) Item.getItemFromBlock(mini);
                if (item.getCreativeTab() != null) {
                    NonNullList<ItemStack> subItems = NonNullList.create();
                    item.getSubItems(item.getCreativeTab(), subItems);
                    for (ItemStack stack : subItems) {
                        ThaumcraftApi.registerComplexObjectTag(stack, getMini(stack));
                    }
                }
            }
        }
    }


    public static class StartChanges {
        @SubscribeEvent
        public void onAttemptSleep(PlayerSleepInBedEvent event) {
            IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(event.getEntityPlayer());
            if (event.getEntityPlayer() != null && !event.getEntityPlayer().world.isRemote && knowledge.isResearchKnown("!gotcrystals") && !knowledge.isResearchKnown("!gotdream")) {
                knowledge.addResearch("!gotdream");
                knowledge.sync((EntityPlayerMP) event.getEntityPlayer());
                ItemStack book = ConfigItems.startBook.copy();
                book.getTagCompound().setString("author", event.getEntityPlayer().getName());
                if (!event.getEntityPlayer().inventory.addItemStackToInventory(book)) {
                    event.getEntityPlayer().entityDropItem(book, 2.0F);
                }

                try {
                    event.getEntityPlayer().sendMessage(new TextComponentTranslation(TextFormatting.DARK_PURPLE + "bwm.got.feverdream"));
                } catch (Exception ignore) {
                }
            }
        }
    }
}
