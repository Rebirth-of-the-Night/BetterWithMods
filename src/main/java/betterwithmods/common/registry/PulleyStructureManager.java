package betterwithmods.common.registry;

import betterwithmods.common.BWMBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by primetoxinz on 6/6/17.
 */
public class PulleyStructureManager {

    public static Set<Predicate<IBlockState>> PULLEY_BLOCKS = new HashSet<>();

    static {
        registerPulleyBlock(BWMBlocks.PLATFORM.getDefaultState());
        registerPulleyBlock(BWMBlocks.IRON_WALL.getDefaultState());
    }

    public static void registerPulleyBlock(Predicate<IBlockState> state) {
        PULLEY_BLOCKS.add(state);
    }

    public static void registerPulleyBlock(Class<? extends Block> clazz) {
        registerPulleyBlock(state -> state.getBlock().getClass().isAssignableFrom(clazz));
    }

    public static void registerPulleyBlock(IBlockState state) {
        registerPulleyBlock(state::equals);
    }

    public static boolean isPulleyBlock(IBlockState state) {
        return PULLEY_BLOCKS.stream().anyMatch(s -> s.test(state));
    }

}
