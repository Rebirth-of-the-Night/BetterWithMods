package betterwithmods.module.gameplay;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.BlockBUD;
import betterwithmods.common.blocks.BlockDetector;
import betterwithmods.common.blocks.BlockHemp;
import betterwithmods.common.blocks.mechanical.tile.TileEntityWaterwheel;
import betterwithmods.common.registry.block.recipe.BlockDropIngredient;
import betterwithmods.common.registry.block.recipe.BlockIngredientSpecial;
import betterwithmods.common.registry.block.recipe.StateIngredient;
import betterwithmods.module.ConfigHelper;
import betterwithmods.module.Module;
import betterwithmods.module.gameplay.breeding_harness.BreedingHarness;
import betterwithmods.module.gameplay.miniblocks.MiniBlocks;
import betterwithmods.util.SetBlockIngredient;
import betterwithmods.util.WorldUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class Gameplay extends Module {
    public static double generatorRenderDistance;

    public static double crankExhaustion;
    public static boolean kidFriendly, disableBlastingOilEvents;
    public static float cauldronNormalSpeedFactor, cauldronStokedSpeedFactor, cauldronMultipleFiresFactor;

    public static String[] blockDispenserWhitelistConfig,blockDispenserBlacklistConfig;

    public static boolean dropHempSeeds;
    public static List<String> blacklistDamageSources;
    private String[] waterwheelFluidConfig;
    public static Set<IBlockState> blockDispenserWhitelist, blockDispenserBlacklist;
    public static boolean pumpFillsContainers;

    @Override
    public void addFeatures() {
        registerFeature(new MechanicalBreakage());
        registerFeature(new MetalReclaming());
        registerFeature(new NuggetCompression());
        registerFeature(new HarderSteelRecipe());
        registerFeature(new AnvilRecipes());
        registerFeature(new CraftingRecipes());
        registerFeature(new CauldronRecipes());
        registerFeature(new CrucibleRecipes());
        registerFeature(new KilnRecipes());
        registerFeature(new MillRecipes());
        registerFeature(new SawRecipes());
        registerFeature(new TurntableRecipes());
        registerFeature(new HopperRecipes());
        registerFeature(new NetherGrowth());
        registerFeature(new BreedingHarness().recipes());
        registerFeature(new PlayerDataHandler());
        registerFeature(new ReadTheFingManual());
        registerFeature(new MiniBlocks());
    }

    @Override
    public void setupConfig() {
        generatorRenderDistance = loadPropDouble("Render Distance for Axle Generators", "Allows expanding the render distance radius for Windmills and Waterwheels", 256);
        crankExhaustion = loadPropDouble("Crank Exhaustion", "How much saturation turning the crank eats. Set to 0.0 to disable.", 6.0, 0.0, 6.0);
        kidFriendly = loadPropBool("Kid Friendly", "Makes some features more kid friendly", false);
        disableBlastingOilEvents = loadPropBool("Disable Blasting Oil", "Don't process blasting oil explosions, as they are have major performance impact", false);
        loadRecipeCondition("higheff", "High Efficiency Recipes", "Enables High Efficiency Recipes", true);
        cauldronNormalSpeedFactor = (float) loadPropDouble("Cauldron normal speed factor", "Cooking speed multiplier for unstoked cauldrons.", 1.0);
        cauldronStokedSpeedFactor = (float) loadPropDouble("Cauldron stoked speed factor", "Cooking speed multiplier for stoked cauldrons and crucibles.", 1.0);
        cauldronMultipleFiresFactor = (float) loadPropDouble("Cauldron Multiple fires factor", "Sets how strongly multiple fire sources (in a 3x3 grid below the pot) affect cooking times.", 1.0);
        dropHempSeeds = loadPropBool("Drop Hemp Seeds", "Adds Hemp seeds to the grass seed drop list.", true);
        waterwheelFluidConfig = ConfigHelper.loadPropStringList("Waterwheel fluids", name, "Additional Fluids which will allow the Waterwheel to turn and screw pump to pump, format fluid_name. (Vanilla water will always work)", new String[]{
                "swamp_water"
        });
        pumpFillsContainers = loadPropBool("Pump Fills Tanks","Whether pumps can fill tanks above the output with water.", true);

        blacklistDamageSources = Lists.newArrayList(ConfigHelper.loadPropStringList("Blasting oil damage source blacklist", name, "Disallow these damage sources from disturbing blasting oil", new String[]{
                "drown",
                "cramming",
                "generic",
                "wither",
                "starve",
                "outOfWorld"
        }));

        blockDispenserWhitelistConfig =  ConfigHelper.loadPropStringList("Block Dispenser Whitelist", name, "Whitelist for Block Dispenser actions. Empty is ignored", new String[0]);
        blockDispenserBlacklistConfig =  ConfigHelper.loadPropStringList("Block Dispenser Blacklist", name, "Blacklist for Block Dispenser actions. Empty is ignored", new String[0]);
        super.setupConfig();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);


        if (dropHempSeeds) {
            MinecraftForge.addGrassSeed(new ItemStack(BWMBlocks.HEMP, 1), 5);
        }
    }



    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        Arrays.stream(waterwheelFluidConfig).map(FluidRegistry::getFluid).filter(Objects::nonNull).collect(Collectors.toList()).forEach(fluid -> TileEntityWaterwheel.registerWater(fluid.getBlock()));

        blockDispenserWhitelist = Arrays.stream(blockDispenserWhitelistConfig).map(ConfigHelper::statesFromString).flatMap(Collection::stream).collect(Collectors.toSet());
        blockDispenserBlacklist = Arrays.stream(blockDispenserBlacklistConfig).map(ConfigHelper::statesFromString).flatMap(Collection::stream).collect(Collectors.toSet());

        //Set blacklist for Buddy Block detection
        BlockBUD.BLACKLIST = new SetBlockIngredient(
                new StateIngredient(Blocks.REDSTONE_WIRE, Items.REDSTONE),
                new StateIngredient(Blocks.POWERED_REPEATER, Items.REPEATER),
                new StateIngredient(Blocks.UNPOWERED_REPEATER, Items.REPEATER),
                new StateIngredient(Blocks.UNLIT_REDSTONE_TORCH),
                new StateIngredient(Blocks.REDSTONE_TORCH),
                new BlockDropIngredient(new ItemStack(BWMBlocks.LIGHT)),
                new BlockDropIngredient(new ItemStack(BWMBlocks.BUDDY_BLOCK))
        );

        BlockDetector.DETECTION_HANDLERS = Sets.newHashSet(
                new BlockDetector.IngredientDetection(new BlockIngredientSpecial(WorldUtils::isPrecipitationAt), facing -> facing == EnumFacing.UP),
                new BlockDetector.IngredientDetection(new BlockIngredientSpecial(((world, pos) -> world.getBlockState(pos).getMaterial().isSolid()))),
                new BlockDetector.IngredientDetection(new BlockIngredientSpecial(((world, pos) -> world.getBlockState(pos).getBlock() instanceof BlockVine))),
                new BlockDetector.IngredientDetection(new BlockIngredientSpecial(((world, pos) -> world.getBlockState(pos).getBlock() instanceof BlockReed))),
                new BlockDetector.IngredientDetection(new BlockIngredientSpecial(((world, pos) -> world.getBlockState(pos).getBlock().equals(BWMBlocks.LIGHT_SOURCE)))),
                new BlockDetector.IngredientDetection(new StateIngredient(Lists.newArrayList(BWMBlocks.HEMP.getDefaultState().withProperty(BlockHemp.TOP, true)), Lists.newArrayList(new ItemStack(BWMBlocks.HEMP)))),
                new BlockDetector.EntityDetection(),
                new BlockDetector.IngredientDetection(new BlockIngredientSpecial(((world, pos) -> {
                    BlockPos downOffset = pos.down();
                    IBlockState downState = world.getBlockState(downOffset);
                    Block downBlock = downState.getBlock();
                    if (!(downBlock instanceof BlockHemp) && downBlock instanceof BlockCrops) {
                        return ((BlockCrops) downBlock).isMaxAge(downState);
                    } else if (downBlock == Blocks.NETHER_WART) {
                        return downState.getValue(BlockNetherWart.AGE) >= 3;
                    }
                    return false;
                })))
        );


    }

    @Override
    public boolean canBeDisabled() {
        return false;
    }


}

