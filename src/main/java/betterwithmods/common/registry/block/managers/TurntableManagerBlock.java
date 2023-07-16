package betterwithmods.common.registry.block.managers;

import betterwithmods.common.BWMRecipes;
import betterwithmods.common.blocks.mechanical.tile.TileEntityTurntable;
import betterwithmods.common.registry.block.recipe.BlockIngredient;
import betterwithmods.common.registry.block.recipe.TurntableRecipe;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

public class TurntableManagerBlock extends CraftingManagerBlock<TurntableRecipe> {

    public TurntableRecipe addDefaultRecipe(ItemStack input, ItemStack productState) {
        return addDefaultRecipe(new BlockIngredient(input), BWMRecipes.getStateFromStack(productState), Lists.newArrayList());
    }

    public TurntableRecipe addDefaultRecipe(ItemStack input, ItemStack productState, List<ItemStack> outputs) {
        return addDefaultRecipe(new BlockIngredient(input), BWMRecipes.getStateFromStack(productState), outputs);
    }

    public TurntableRecipe addDefaultRecipe(ItemStack input, IBlockState productState, List<ItemStack> outputs) {
        return addDefaultRecipe(new BlockIngredient(input), productState, outputs);
    }

    public TurntableRecipe addDefaultRecipe(BlockIngredient input, IBlockState productState, List<ItemStack> outputs) {
        return addRecipe(input, productState, outputs, 8);
    }

    public TurntableRecipe addRecipe(BlockIngredient input, IBlockState productState, List<ItemStack> outputs, int rotations) {
        return addRecipe(new TurntableRecipe(input, productState, outputs, rotations));
    }

    protected List<TurntableRecipe> findRecipe(IBlockState output) {
        return recipes.stream().filter(r -> r.getProductState() == output).collect(Collectors.toList());
    }

    public boolean remove(IBlockState output) {
        return recipes.removeAll(findRecipe(output));
    }

    @Override
    public TurntableRecipe addRecipe(TurntableRecipe recipe) {
         return super.addRecipe(recipe);
    }

    public static TileEntityTurntable findTurntable(World world, BlockPos craftingPos) {
        for (int i = 1; i <= 2; i++) {
            TileEntity tile = world.getTileEntity(craftingPos.down(i));
            if (tile instanceof TileEntityTurntable) {
                return (TileEntityTurntable) tile;
            }
        }
        return null;
    }
}
