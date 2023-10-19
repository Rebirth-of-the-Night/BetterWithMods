package betterwithmods.common;

import betterwithmods.BWMod;
import betterwithmods.api.BWMAPI;
import betterwithmods.api.capabilities.CapabilityAxle;
import betterwithmods.api.capabilities.CapabilityMechanicalPower;
import betterwithmods.api.tile.IAxle;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.api.tile.dispenser.IBehaviorEntity;
import betterwithmods.common.advancements.BWAdvancements;
import betterwithmods.common.blocks.BlockBDispenser;
import betterwithmods.common.blocks.BlockUnfiredPottery;
import betterwithmods.common.blocks.behaviors.BehaviorDiodeDispense;
import betterwithmods.common.blocks.behaviors.BehaviorSilkTouch;
import betterwithmods.common.entity.*;
import betterwithmods.common.entity.item.EntityFallingBlockCustom;
import betterwithmods.common.items.ItemDynamite;
import betterwithmods.common.penalties.PenaltyHandlerRegistry;
import betterwithmods.common.potion.BWPotion;
import betterwithmods.common.potion.PotionSlowfall;
import betterwithmods.common.potion.PotionTruesight;
import betterwithmods.common.registry.BellowsManager;
import betterwithmods.common.registry.HopperFilters;
import betterwithmods.common.registry.KilnStructureManager;
import betterwithmods.common.registry.block.managers.KilnManagerBlock;
import betterwithmods.common.registry.block.managers.SawManagerBlock;
import betterwithmods.common.registry.block.managers.TurntableManagerBlock;
import betterwithmods.common.registry.block.recipe.BlockIngredient;
import betterwithmods.common.registry.block.recipe.StateIngredient;
import betterwithmods.common.registry.bulk.manager.CookingPotManager;
import betterwithmods.common.registry.bulk.manager.MillManager;
import betterwithmods.common.registry.heat.BWMHeatRegistry;
import betterwithmods.manual.api.API;
import betterwithmods.manual.common.api.ManualDefinitionImpl;
import betterwithmods.module.Feature;
import betterwithmods.module.compat.Quark;
import betterwithmods.module.compat.bop.BiomesOPlenty;
import betterwithmods.module.gameplay.CraftingRecipes;
import betterwithmods.module.gameplay.miniblocks.MiniBlocks;
import betterwithmods.module.hardcore.crafting.*;
import betterwithmods.module.hardcore.creatures.EntityTentacle;
import betterwithmods.module.hardcore.needs.HCTools;
import betterwithmods.module.hardcore.world.HCTorches;
import betterwithmods.network.BWNetwork;
import betterwithmods.util.*;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = BWMod.MODID)
public class BWRegistry {

    public static final PenaltyHandlerRegistry PENALTY_HANDLERS = new PenaltyHandlerRegistry();

    public static final CookingPotManager CAULDRON = new CookingPotManager();
    public static final CookingPotManager CRUCIBLE = new CookingPotManager();
    public static final MillManager MILLSTONE = new MillManager();
    public static final SawManagerBlock WOOD_SAW = new SawManagerBlock();
    public static final KilnManagerBlock KILN = new KilnManagerBlock();
    public static final TurntableManagerBlock TURNTABLE = new TurntableManagerBlock();
    public static final HopperFilters HOPPER_FILTERS = new HopperFilters();

    @GameRegistry.ObjectHolder("betterwithmods:true_sight")
    public static final Potion POTION_TRUESIGHT = null;
    @GameRegistry.ObjectHolder("betterwithmods:fortune")
    public static final Potion POTION_FORTUNE = null;
    @GameRegistry.ObjectHolder("betterwithmods:looting")
    public static final Potion POTION_LOOTING = null;
    @GameRegistry.ObjectHolder("betterwithmods:slow_fall")
    public static final Potion POTION_SLOWFALL = null;

    private static int availableEntityId = 0;

    static {
        BWMAPI.IMPLEMENTATION = new MechanicalUtil();
    }

    public static void preInit() {
        API.manualAPI = ManualDefinitionImpl.INSTANCE;
        BWAdvancements.registerAdvancements();
        BWNetwork.registerNetworking();
        BWMBlocks.registerBlocks();
        BWMItems.registerItems();
        BWMBlocks.registerTileEntities();
        BWRegistry.registerEntities();
        BWRegistry.registerBlockDispenserBehavior();
        CapabilityManager.INSTANCE.register(IMechanicalPower.class, new CapabilityMechanicalPower.Impl(), CapabilityMechanicalPower.Default::new);
        CapabilityManager.INSTANCE.register(IAxle.class, new CapabilityAxle.Impl(), CapabilityAxle.Default::new);
        KilnStructureManager.registerKilnBlock(Blocks.BRICK_BLOCK.getDefaultState());
        KilnStructureManager.registerKilnBlock(Blocks.NETHER_BRICK.getDefaultState());
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        BWMBlocks.getBlocks().forEach(event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        BWMItems.getItems().forEach(event.getRegistry()::register);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        ForgeRegistry<IRecipe> reg = (ForgeRegistry<IRecipe>) event.getRegistry();

        for (IRecipe recipe : BWMRecipes.getRecipes()) {
            event.getRegistry().register(recipe);
        }

        for (IRecipe recipe : reg) {
            for (Pattern pattern : BWMRecipes.REMOVE_BY_REGEX) {
                Matcher matcher = pattern.matcher(recipe.getRegistryName().toString());
                if (matcher.matches()) {
                    reg.remove(recipe.getRegistryName());
                }
            }
            for (ResourceLocation loc : BWMRecipes.REMOVE_RECIPE_BY_RL) {
                if (loc.equals(recipe.getRegistryName()))
                    reg.remove(recipe.getRegistryName());
            }
            for (ItemStack output : BWMRecipes.REMOVE_RECIPE_BY_OUTPUT) {
                if (InvUtils.matches(recipe.getRecipeOutput(), output)) {
                    reg.remove(recipe.getRegistryName());
                }
            }
            for (List<Ingredient> inputs : BWMRecipes.REMOVE_RECIPE_BY_INPUT) {
                if (InvUtils.containsIngredient(recipe.getIngredients(), inputs)) {
                    reg.remove(recipe.getRegistryName());
                }
            }
        }
    }

    public static void init() {
        BWRegistry.registerHeatSources();
        BWOreDictionary.registerOres();
    }

    public static void postInit() {
        BWOreDictionary.postInitOreDictGathering();
        BellowsManager.postInit();
    }

    public static void postPostInit() {
        registerRecipes();
    }

    /**
     * All names should be snake_case by convention (enforced in 1.11).
     */
    private static void registerEntities() {
        BWRegistry.registerEntity(EntityExtendingRope.class, "extending_rope", 64, 20, true);
        BWRegistry.registerEntity(EntityDynamite.class, "bwm_dynamite", 10, 50, true);
        BWRegistry.registerEntity(EntityUrn.class, "bwm_urn", 10, 50, true);
        BWRegistry.registerEntity(EntityMiningCharge.class, "bwm_mining_charge", 10, 50, true);
        BWRegistry.registerEntity(EntityShearedCreeper.class, "entity_sheared_creeper", 64, 1, true);
        BWRegistry.registerEntity(EntityBroadheadArrow.class, "entity_broadhead_arrow", 64, 1, true);
        BWRegistry.registerEntity(EntityFallingGourd.class, "entity_falling_gourd", 64, 1, true);
        BWRegistry.registerEntity(EntityFallingBlockCustom.class, "falling_block_custom", 64, 20, true);
        BWRegistry.registerEntity(EntitySpiderWeb.class, "bwm_spider_web", 64, 20, true);
        BWRegistry.registerEntity(EntityHCFishHook.class, "bwm_fishing_hook", 64, 20, true);
        BWRegistry.registerEntity(EntityTentacle.class, "bwm_tentacle", 64, 1, true);

        BWRegistry.registerEntity(EntityJungleSpider.class, "bwm_jungle_spider", 64, 1, true, 0x3C6432, 0x648C50);
    }

    public static void registerBlockDispenserBehavior() {
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(BWMItems.DYNAMITE, new DispenserBehaviorDynamite(ItemDynamite.dispenseLit));
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(BWMItems.DYNAMITE_BUNDLE, new DispenserBehaviorDynamite(ItemDynamite.dispenseLit));
        BlockBDispenser.BLOCK_DISPENSER_REGISTRY.putObject(BWMItems.DYNAMITE, new DispenserBehaviorDynamite(ItemDynamite.dispenseLit));
        BlockBDispenser.BLOCK_DISPENSER_REGISTRY.putObject(BWMItems.DYNAMITE_BUNDLE, new DispenserBehaviorDynamite(ItemDynamite.dispenseLit));
        BlockBDispenser.BLOCK_DISPENSER_REGISTRY.putObject(Items.REPEATER, new BehaviorDiodeDispense());
        BlockBDispenser.BLOCK_DISPENSER_REGISTRY.putObject(Items.COMPARATOR, new BehaviorDiodeDispense());
        BlockBDispenser.BLOCK_DISPENSER_REGISTRY.putObject(Items.CLAY_BALL, new DispenserBlockOverride(BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.BRICK)));
        BlockBDispenser.BLOCK_DISPENSER_REGISTRY.putObject(Items.CLAY_BALL, new DispenserBlockOverride(BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.BRICK)));
        BlockBDispenser.BLOCK_DISPENSER_REGISTRY.putObject(Item.getItemFromBlock(BWMBlocks.MINING_CHARGE),
                (source, stack) -> {
                    World worldIn = source.getWorld();
                    EnumFacing facing = source.getBlockState().getValue(BlockDispenser.FACING);
                    BlockPos pos = source.getBlockPos().offset(facing);
                    EntityMiningCharge miningCharge = new EntityMiningCharge(worldIn, pos.getX() + 0.5F, pos.getY(),
                            pos.getZ() + 0.5F, null, facing);
                    miningCharge.setNoGravity(false);
                    worldIn.spawnEntity(miningCharge);
                    worldIn.playSound(null, miningCharge.posX, miningCharge.posY, miningCharge.posZ,
                            SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    return stack;
                });
        BlockBDispenser.BLOCK_COLLECT_REGISTRY.putObject(Blocks.STONE, new BehaviorSilkTouch());
        BlockBDispenser.BLOCK_COLLECT_REGISTRY.putObject(Blocks.LOG, new BehaviorSilkTouch());
        BlockBDispenser.BLOCK_COLLECT_REGISTRY.putObject(Blocks.LOG2, new BehaviorSilkTouch());

        // Dispense minecarts
        for (Item minecart : Sets.newHashSet(Items.MINECART, Items.CHEST_MINECART, Items.FURNACE_MINECART, Items.HOPPER_MINECART, Items.TNT_MINECART, Items.COMMAND_BLOCK_MINECART)) {
            BlockBDispenser.BLOCK_DISPENSER_REGISTRY.putObject(minecart, ItemMinecart.MINECART_DISPENSER_BEHAVIOR);
        }

        IBehaviorEntity MINECART_COLLECT_BEHAVIOR = (world, pos, entity, stack) -> {
            EntityMinecart minecart = (EntityMinecart) entity;

            if (minecart instanceof IInventory) {
                InventoryHelper.dropInventoryItems(world, minecart, (IInventory) minecart);
            }

            IBlockState state = minecart.getDisplayTile();
            if (state == Blocks.LIT_FURNACE.getDefaultState())
                state = Blocks.FURNACE.getDefaultState();
            ItemStack tile = BWMRecipes.getStackFromState(state);
            minecart.setDead();

            return InvUtils.asNonnullList(new ItemStack(Items.MINECART), tile);
        };

        for (String name : Sets.newHashSet("commandblock_minecart", "minecart", "chest_minecart", "furnace_minecart", "tnt_minecart", "hopper_minecart", "spawner_minecart")) {
            ResourceLocation loc = new ResourceLocation(name);
            BlockBDispenser.ENTITY_COLLECT_REGISTRY.putObject(loc, MINECART_COLLECT_BEHAVIOR);
        }


        BlockBDispenser.ENTITY_COLLECT_REGISTRY.putObject(new ResourceLocation("minecraft:sheep"), (world, pos, entity, stack) -> {
            EntitySheep sheep = (EntitySheep) entity;
            if (sheep.isShearable(new ItemStack(Items.SHEARS), world, pos)) {
                return InvUtils.asNonnullList(sheep.onSheared(new ItemStack(Items.SHEARS), world, pos, 0));
            }
            return NonNullList.create();
        });
        BlockBDispenser.ENTITY_COLLECT_REGISTRY.putObject(new ResourceLocation("minecraft:chicken"), (world, pos, entity, stack) -> {
            if (((EntityAgeable) entity).isChild())
                return NonNullList.create();
            InvUtils.ejectStackWithOffset(world, pos, new ItemStack(Items.FEATHER, 1 + world.rand.nextInt(2)));
            world.playSound(null, pos, SoundEvents.ENTITY_CHICKEN_HURT, SoundCategory.NEUTRAL, 0.75F, 1.0F);
            entity.setDead();
            return InvUtils.asNonnullList(new ItemStack(Items.EGG));
        });
        BlockBDispenser.ENTITY_COLLECT_REGISTRY.putObject(new ResourceLocation("minecraft:cow"), (world, pos, entity, stack) -> {
            if (((EntityAgeable) entity).isChild())
                return NonNullList.create();
            if (stack.isItemEqual(new ItemStack(Items.BUCKET))) {
                stack.shrink(1);
                world.playSound(null, pos, SoundEvents.ENTITY_COW_MILK, SoundCategory.BLOCKS, 1.0F, 1.0F);

                InvUtils.ejectStackWithOffset(world, pos, new ItemStack(Items.MILK_BUCKET));
            }
            return NonNullList.create();
        });


    }

    /**
     * Registers an entity for this mod. Handles automatic available ID
     * assignment.
     */
    public static void registerEntity(Class<? extends Entity> entityClass, String entityName, int trackingRange,
                                      int updateFrequency, boolean sendsVelocityUpdates) {
        EntityRegistry.registerModEntity(new ResourceLocation(BWMod.MODID, entityName), entityClass, entityName, availableEntityId, BWMod.instance, trackingRange,
                updateFrequency, sendsVelocityUpdates);
        availableEntityId++;
    }


    public static void registerEntity(Class<? extends Entity> entityClass, String entityName, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, int primaryColor, int secondaryColor) {
        EntityRegistry.registerModEntity(new ResourceLocation(BWMod.MODID, entityName), entityClass, entityName, availableEntityId, BWMod.instance, trackingRange,
                updateFrequency, sendsVelocityUpdates, primaryColor, secondaryColor);
        availableEntityId++;
    }

    public static void registerHeatSources() {
        BWMHeatRegistry.addHeatSource(new StateIngredient(Blocks.FIRE, Items.AIR), 1);
        BWMHeatRegistry.addHeatSource(new StateIngredient(BWMBlocks.STOKED_FLAME, Items.AIR), 2);
    }

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event) {
        event.getRegistry().register(registerPotion(new PotionTruesight("true_sight", true, 14270531).setIconIndex(4, 1)));
        event.getRegistry().register(registerPotion(new BWPotion("fortune", true, 14270531).setIconIndex(5, 2)));
        event.getRegistry().register(registerPotion(new BWPotion("looting", true, 14270531).setIconIndex(6, 2)));
        event.getRegistry().register(registerPotion(new PotionSlowfall("slow_fall", true, 0xF46F20).setIconIndex(4, 1)));
    }

    private static Potion registerPotion(Potion potion) {
        String potionName = potion.getRegistryName().getPath();
        potion.setPotionName("bwm.effect." + potionName);
        return potion;
    }

    private static void registerFireInfo() {
        Blocks.FIRE.setFireInfo(BWMBlocks.WOODEN_AXLE, 5, 20);
        Blocks.FIRE.setFireInfo(BWMBlocks.WOODEN_BROKEN_GEARBOX, 5, 20);
        Blocks.FIRE.setFireInfo(BWMBlocks.WOODEN_GEARBOX, 5, 20);
        Blocks.FIRE.setFireInfo(BWMBlocks.WINDMILL, 5, 20);
        Blocks.FIRE.setFireInfo(BWMBlocks.WATERWHEEL, 5, 20);
        Blocks.FIRE.setFireInfo(BWMBlocks.VINE_TRAP, 5, 20);
        //TODO 1.13 block of nethercoal

        registerFireInfo(new BlockIngredient("blockCandle"), 5, 20);
        registerFireInfo(new BlockIngredient("slats"), 5, 20);
        registerFireInfo(new BlockIngredient("grates"), 5, 20);
    }

    public static void registerFireInfo(BlockIngredient ingredient, int encouragement, int flammability) {
        for (IBlockState state : ingredient.getStates()) {
            Blocks.FIRE.setFireInfo(state.getBlock(), encouragement, flammability);
        }
    }

    public static void registerRecipes() {
        ForgeRegistry<IRecipe> reg = (ForgeRegistry<IRecipe>) ForgeRegistries.RECIPES;

        replaceIRecipe(CraftingRecipes.class, reg);
        replaceIRecipe(HCTools.class, reg);
        replaceIRecipe(HCDiamond.class, reg);
        replaceIRecipe(HCLumber.class, reg);
        replaceIRecipe(HCOres.class, reg);
        replaceIRecipe(HCRedstone.class, reg);
        replaceIRecipe(HCTorches.class, reg);
        replaceIRecipe(HCFishing.class, reg);
        replaceIRecipe(MiniBlocks.class, reg);

        replaceIRecipe(BiomesOPlenty.class, reg);
        replaceIRecipe(Quark.class, reg);
    }

    private static void retrieveRecipes(String category, ForgeRegistry<IRecipe> reg) {
        List<IRecipe> recipes = BWMRecipes.getHardcoreRecipes(category);
        if (recipes != null) {
            for (IRecipe recipe : recipes) {
                ResourceLocation location = recipe.getRegistryName();
                if (reg.containsKey(location))
                    registerReplacements(reg.getValue(location), recipe);
                else
                    reg.register(recipe);
            }
        }
    }

    private static void replaceIRecipe(Class<? extends Feature> clazz, IForgeRegistry<IRecipe> reg) {
        List<IRecipe> recipes = BWMRecipes.getHardcoreRecipes(clazz.getSimpleName());
        if (recipes != null) {
            recipes.forEach(reg::register);
        }
    }

    private static void registerReplacements(IRecipe original, IRecipe from) {
        NonNullList<Ingredient> ing = original.getIngredients();
        for (int i = 0; i < ing.size(); i++) {
            ing.set(i, from.getIngredients().get(i));
        }
    }
}

