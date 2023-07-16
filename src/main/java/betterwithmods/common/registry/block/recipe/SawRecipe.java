package betterwithmods.common.registry.block.recipe;

import betterwithmods.api.recipe.IRecipeOutputs;
import betterwithmods.common.BWSounds;
import betterwithmods.util.StackEjector;
import betterwithmods.util.VectorBuilder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Created by primetoxinz on 5/16/17.
 */
public class SawRecipe extends BlockRecipe {

    public static StackEjector EJECTOR = new StackEjector(new VectorBuilder().rand(0.4f).offset(0.25f, 0, 0.25f), new VectorBuilder().setGaussian(0.01f, 0.01f, 0.01f));

    public SawRecipe(BlockIngredient input, List<ItemStack> outputs) {
        super(input, outputs);
    }

    public SawRecipe(BlockIngredient input, IRecipeOutputs recipeOutput) {
        super(input, recipeOutput);
    }

    @Override
    public boolean craftRecipe(World world, BlockPos pos, Random rand, IBlockState state) {
        List<ItemStack> output = onCraft(world, pos);
        world.setBlockToAir(pos);
        for (ItemStack stack : output) {
            EJECTOR.setStack(stack).ejectStack(world, new Vec3d(pos), Vec3d.ZERO);
        }
        world.playSound(null, pos, BWSounds.SAW_CUT, SoundCategory.BLOCKS, 1.5F + rand.nextFloat() * 0.1F, 2.0F + rand.nextFloat() * 0.1F);
        return true;
    }
}
