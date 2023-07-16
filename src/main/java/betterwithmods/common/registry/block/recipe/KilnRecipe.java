package betterwithmods.common.registry.block.recipe;

import betterwithmods.api.tile.IHeatRecipe;
import betterwithmods.common.registry.KilnStructureManager;
import betterwithmods.event.FakePlayerHandler;
import betterwithmods.util.InvUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Created by primetoxinz on 5/16/17.
 */
public class KilnRecipe extends BlockRecipe implements IHeatRecipe {
    private int heat;
    private boolean ignoreHeat;

    public KilnRecipe(BlockIngredient input, List<ItemStack> outputs, int heat) {
        super(input, outputs);
        this.heat = heat;
    }

    @Override
    public int getHeat() {
        return heat;
    }

    @Override
    public boolean ignore() {
        return ignoreHeat;
    }

    public KilnRecipe setIgnoreHeat(boolean ignoreHeat) {
        this.ignoreHeat = ignoreHeat;
        return this;
    }

    @Override
    public boolean craftRecipe(World world, BlockPos pos, Random rand, IBlockState state) {
        InvUtils.ejectStackWithOffset(world, pos, onCraft(world, pos));
        state.getBlock().onBlockHarvested(world, pos, state, FakePlayerHandler.getPlayer());
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
        return true;
    }

    @Override
    public boolean matches(World world, BlockPos pos, IBlockState state) {
        if(super.matches(world,pos,state)) {
           if(!ignore()) {
               int heat =  KilnStructureManager.getHeat(world,pos.down());
               return heat == getHeat();
           }
           return true;
        }
        return false;
    }
}
