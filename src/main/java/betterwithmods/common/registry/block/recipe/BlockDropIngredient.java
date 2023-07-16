package betterwithmods.common.registry.block.recipe;

import betterwithmods.common.BWMRecipes;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreIngredient;

import javax.annotation.Nullable;

public class BlockDropIngredient extends BlockIngredient {

    public BlockDropIngredient(ItemStack... stack) {
        super(stack);
    }

    public BlockDropIngredient(String ore) {
        this(new OreIngredient(ore));
    }

    public BlockDropIngredient(Ingredient ingredient) {
        super(ingredient);
    }

    @Override
    public boolean apply(World world, BlockPos pos, @Nullable IBlockState state) {
        return apply(BWMRecipes.getStackFromState(state));
    }
}
