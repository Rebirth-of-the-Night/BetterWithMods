package betterwithmods.common.registry.block.recipe;

import betterwithmods.api.recipe.IRecipeOutputs;
import betterwithmods.common.blocks.mechanical.tile.TileEntityTurntable;
import betterwithmods.common.registry.block.managers.TurntableManagerBlock;
import betterwithmods.event.FakePlayerHandler;
import betterwithmods.util.InvUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class TurntableRecipe extends BlockRecipe {
    private int rotations;

    private IBlockState productState;
    private ItemStack representative;

    public TurntableRecipe(BlockIngredient input, List<ItemStack> outputs, IBlockState productState, int rotations) {
        this(input, productState, outputs, rotations);
    }

    public TurntableRecipe(BlockIngredient input, IBlockState productState, List<ItemStack> outputs, int rotations) {
        this(input, productState, new ItemStack(productState.getBlock(), 1, productState.getBlock().getMetaFromState(productState)), outputs, rotations);
    }

    public TurntableRecipe(BlockIngredient input, IBlockState productState, ItemStack representative, List<ItemStack> outputs, int rotations) {
        super(input, outputs);
        this.rotations = rotations;
        this.productState = productState;
        this.representative = representative;
    }

    public TurntableRecipe(BlockIngredient input, IBlockState productState, ItemStack representative, IRecipeOutputs outputs, int rotations) {
        super(input, outputs);
        this.rotations = rotations;
        this.productState = productState;
        this.representative = representative;
    }


    public int getRotations() {
        return rotations;
    }

    public ItemStack getRepresentative() {
        return representative;
    }

    public IBlockState getProductState() {
        return Optional.ofNullable(productState).orElse(Blocks.AIR.getDefaultState());
    }

    @Override
    public boolean craftRecipe(World world, BlockPos pos, Random rand, IBlockState state) {
        TileEntityTurntable turntable = TurntableManagerBlock.findTurntable(world, pos);
        if(turntable != null && turntable.getPotteryRotation() >= getRotations()) {
            InvUtils.ejectStackWithOffset(world, pos, onCraft(world, pos));
            state.getBlock().onBlockHarvested(world, pos, state, FakePlayerHandler.getPlayer());
            world.setBlockState(pos, getProductState(), world.isRemote ? 11 : 3);
            return true;
        }
        return false;
    }

    @Override
    public boolean isInvalid() {
        return getInput().isSimple() && ArrayUtils.isEmpty(getInput().getMatchingStacks());
    }

}
