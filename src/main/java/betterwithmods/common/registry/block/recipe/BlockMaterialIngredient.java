package betterwithmods.common.registry.block.recipe;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Predicate;

public class BlockMaterialIngredient extends BlockIngredient {

    private Predicate<Material> predicate;

    public BlockMaterialIngredient(Material... materials) {
        this(m -> Arrays.stream(materials).anyMatch(m::equals));
    }

    public BlockMaterialIngredient(Predicate<Material> materialPredicate) {
        this.predicate = materialPredicate;
    }

    @Override
    public boolean apply(World world, BlockPos pos, @Nullable IBlockState state) {
        return predicate.test(state.getMaterial());
    }
}
