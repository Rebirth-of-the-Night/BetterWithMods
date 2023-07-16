package betterwithmods.common.blocks.behaviors;

import betterwithmods.api.tile.dispenser.IBehaviorCollect;
import betterwithmods.common.blocks.BlockBDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by primetoxinz on 5/25/17.
 */
public class BehaviorBreakBlock implements IBehaviorCollect {
    @Override
    public NonNullList<ItemStack> collect(IBlockSource source) {
        NonNullList<ItemStack> list = NonNullList.create();
        if (BlockBDispenser.permitState(source.getBlockState())) {
            list = getDrops(source.getWorld(), source.getBlockPos(), source.getBlockState(), 0);
            breakBlock(source.getWorld(), source.getBlockState(), source.getBlockPos());
        }
        return list;
    }


    public NonNullList<ItemStack> getDrops(World world, BlockPos pos, IBlockState state, int fortune) {
        NonNullList<ItemStack> finalDrops = NonNullList.create();
        if (!world.isRemote && !world.restoringBlockSnapshots) // do not drop items while restoring blockstates, prevents item dupe
        {
            float chance = 1.0f;
            NonNullList<ItemStack> drops = NonNullList.create();
            state.getBlock().getDrops(drops, world, pos, state, fortune);
            chance = net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(drops, world, pos, state, fortune, chance, false, null);

            for (ItemStack drop : drops) {
                if (world.rand.nextFloat() <= chance) {
                    finalDrops.add(drop);
                }
            }
        }
        return finalDrops;
    }
}
