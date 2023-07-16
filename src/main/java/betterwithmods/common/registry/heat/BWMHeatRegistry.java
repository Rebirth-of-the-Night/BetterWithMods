package betterwithmods.common.registry.heat;

import betterwithmods.api.tile.IHeatSource;
import betterwithmods.common.registry.block.recipe.BlockIngredient;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BWMHeatRegistry {
    public static final int UNSTOKED_HEAT = 1;
    public static final int STOKED_HEAT = 2;
    private static final List<HeatSource> HEAT_SOURCES = Lists.newArrayList();

    public static void addHeatSource(BlockIngredient ingredient, int heat) {
        HEAT_SOURCES.add(new HeatSource(ingredient, heat));
    }

    public static int getHeat(World world, BlockPos pos) {
        HeatSource source = get(world, pos);
        if (source != null)
            return source.getHeat();
        return 0;
    }

    public static HeatSource get(World world, BlockPos pos) {
        for(HeatSource bm: HEAT_SOURCES) {
            if(bm.matches(world, pos))
                return bm;
        }
        return null;
    }

    public static int[] allHeatLevels() {
        return HEAT_SOURCES.stream().mapToInt(h -> h.heat).distinct().toArray();
    }

    public static List<ItemStack> getStacks(int heat) {
        return HEAT_SOURCES.stream().filter(s -> s.heat == heat).map(s -> s.ingredient.getMatchingStacks()).flatMap(Arrays::stream).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    public static class HeatSource implements IHeatSource {
        private BlockIngredient ingredient;
        private int heat;

        public HeatSource(BlockIngredient ingredient, int heat) {
            this.ingredient = ingredient;
            this.heat = heat;
        }

        public int getHeat() {
            return heat;
        }

        public boolean matches(World world, BlockPos pos) {
            return ingredient.apply(world, pos, world.getBlockState(pos));
        }
    }
}