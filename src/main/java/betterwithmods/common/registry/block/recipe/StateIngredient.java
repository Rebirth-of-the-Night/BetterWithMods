package betterwithmods.common.registry.block.recipe;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class StateIngredient extends BlockIngredient {
    private List<IBlockState> states = Lists.newArrayList();

    public StateIngredient(List<IBlockState> states, List<ItemStack> stacks) {
        super(stacks);
        this.states = states;
    }

    public StateIngredient(Block block) {
        this(block, Item.getItemFromBlock(block));
    }

    public StateIngredient(Block block, Item item) {
        super(new ItemStack(item));
        this.states.addAll(block.getBlockState().getValidStates());
    }

    @Override
    public boolean apply(World world, BlockPos pos, @Nullable IBlockState state) {
        return state != null && states.contains(state);
    }

    @Override
    public boolean isSimple() {
        return false;
    }
}
