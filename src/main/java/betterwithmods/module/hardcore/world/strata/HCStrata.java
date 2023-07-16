package betterwithmods.module.hardcore.world.strata;

import betterwithmods.BWMod;
import betterwithmods.common.BWMRecipes;
import betterwithmods.common.BWOreDictionary;
import betterwithmods.common.registry.BrokenToolRegistry;
import betterwithmods.module.Feature;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HCStrata extends Feature {
    private static final Pattern PATTERN = Pattern.compile("^([\\-]?\\d+)=(\\d{1,255}),(\\d{1,255}).*");
    public static boolean CTM;
    public static float[] STRATA_SPEEDS;
    public static float INCORRECT_STRATA_SCALE;
    public static HashMap<IBlockState, BlockType> STATES = Maps.newHashMap();
    public static HashMap<Integer, StrataConfig> STRATA_CONFIGS = Maps.newHashMap();
    public static StrataConfig DEFAULT = new StrataConfig(-1, -1);
    public static NoiseGeneratorPerlin STRATA_NOISE1, STRATA_NOISE2;
    private static Random random;
    private boolean debugging;

    public HCStrata() {
        enabledByDefault = false;
    }

    public static void addStone(IBlockState state) {
        STATES.put(state, BlockType.STONE);
    }

    public static void addStone(Block block) {
        for (IBlockState state : block.getBlockState().getValidStates())
            addStone(state);
    }

    public static void addOre(Block block) {
        for (IBlockState state : block.getBlockState().getValidStates())
            STATES.put(state, BlockType.ORE);
    }

    public static boolean shouldStratify(World world, BlockPos pos) {
        return shouldStratify(world, world.getBlockState(pos));
    }

    public static boolean shouldStratify(World world, IBlockState state) {
        return STRATA_CONFIGS.containsKey(world.provider.getDimension()) && STATES.keySet().stream().anyMatch(s -> s.equals(state));
    }

    public static Stratification getStratification(World world, BlockPos pos, int dimension) {
        return STRATA_CONFIGS.getOrDefault(dimension, DEFAULT).getStrata(pos.getY() + (int) getNoise(world, pos.getY()).getValue(pos.getX(), pos.getZ()));
    }

    private static void loadStrataConfig(String entry) {
        Matcher matcher = PATTERN.matcher(entry);
        if (matcher.matches()) {
            int dim = Integer.parseInt(matcher.group(1));
            int medium = Integer.parseInt(matcher.group(2));
            int hard = Integer.parseInt(matcher.group(3));
            STRATA_CONFIGS.put(dim, new StrataConfig(medium, hard));
        }
    }

    public static NoiseGeneratorPerlin getNoise(World world, int y) {
        if (random == null) {
            random = new Random(world.getSeed());
        }
        if (y < 50) {
            if (STRATA_NOISE2 == null)
                STRATA_NOISE2 = new NoiseGeneratorPerlin(random, 3);
            return STRATA_NOISE2;
        } else {
            if (STRATA_NOISE1 == null)
                STRATA_NOISE1 = new NoiseGeneratorPerlin(random, 2);
            return STRATA_NOISE1;
        }
    }

    @SubscribeEvent
    public void onJoinWorld(PlayerLoggedInEvent event) {
        getNoise(event.player.world, 50);
        getNoise(event.player.world, 0);
    }

    @Override
    public void setupConfig() {
        debugging = false;

        STRATA_SPEEDS = new float[]{(float) loadPropDouble("Light Strata", "Speed for Light Strata", 1.0),
                (float) loadPropDouble("Medium Strata", "Speed for Medium Strata", 1.0),
                (float) loadPropDouble("Dark Strata", "Speed for Dark Strata", 1.0)
        };
        INCORRECT_STRATA_SCALE = (float) loadPropDouble("Incorrect Strata", "Speed scale for when the Strata is higher than the tool", 0.10);

        CTM = loadPropBool("CTM Support", "Use the ConnectedTextureMod to visualize the stratas", true) && enabled;

        Arrays.stream(loadPropStringList("Strata Configs", "Set the strata levels for a given dimension, <dim>=< medium start y>,<hard start y>", new String[]{
                "0=42,21"
        })).map(s -> s.replaceAll(" ", "")).forEach(HCStrata::loadStrataConfig);
    }

    @Override
    public String getFeatureDescription() {
        return "Divides the underground into three strata. Each strata requires the next tool tier to properly mine";
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        for (BWOreDictionary.Ore ore : BWOreDictionary.oreNames) {
            for (ItemStack stack : ore.getOres()) {
                if (stack.getItem() instanceof ItemBlock) {
                    addOre(((ItemBlock) stack.getItem()).getBlock());
                }
            }
        }
        List<ItemStack> stones = loadItemStackList("Strata Stones", "Blocks that are considered as stone to HCStrata", new ItemStack[]{new ItemStack(Blocks.STONE, 1, OreDictionary.WILDCARD_VALUE)});
        stones.stream().map(BWMRecipes::getStatesFromStack).flatMap(Set::stream).forEach(HCStrata::addStone);
    }

    @SubscribeEvent
    public void onHarvest(BlockEvent.HarvestDropsEvent event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        if (event.getHarvester() == null)
            return;
        IBlockState state = event.getState();
        if (shouldStratify(world, state)) {
            ItemStack stack = BrokenToolRegistry.findItem(event.getHarvester(), event.getState());
            int strata = getStratification(world, pos, world.provider.getDimension()).ordinal();
            if (STATES.getOrDefault(event.getState(), BlockType.STONE) == BlockType.STONE) {
                int level = Math.max(1, stack.getItem().getHarvestLevel(stack, "pickaxe", event.getHarvester(), event.getState()));
                if (level <= strata) {
                    event.getDrops().clear();
                }
            }
            if(debugging)
                BWMod.logger.info("HarvestDropsEvent pos: {}, state: {}, held: {}, strata: {}", event.getPos(), event.getState(), stack, strata);
        }

    }

    @SubscribeEvent
    public void getBreakSpeed(PlayerEvent.BreakSpeed event) {
        World world = event.getEntityPlayer().getEntityWorld();
        BlockPos pos = event.getPos();
        if (shouldStratify(world, pos)) {
            ItemStack stack = BrokenToolRegistry.findItem(event.getEntityPlayer(), event.getState());
            int strata = getStratification(world, pos, world.provider.getDimension()).ordinal();
            if (STATES.getOrDefault(event.getState(), BlockType.STONE) == BlockType.STONE) {
                int level = Math.max(1, stack.getItem().getHarvestLevel(stack, "pickaxe", event.getEntityPlayer(), event.getState()));
                if (level <= strata) {
                    event.setNewSpeed(INCORRECT_STRATA_SCALE * event.getOriginalSpeed());
                }
            }
        }
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    private enum BlockType {
        STONE,
        ORE;
    }

    public enum Stratification {
        NORMAL,
        MEDIUM,
        HARD
    }

    private static class StrataConfig {
        private int medium;
        private int hard;


        public StrataConfig(int medium, int hard) {
            this.medium = medium;
            this.hard = hard;
        }

        public Stratification getStrata(int y) {
            return y <= hard ? Stratification.HARD : y <= medium ? Stratification.MEDIUM : Stratification.NORMAL;
        }
    }


}
