package betterwithmods.common.blocks.behaviors;

import betterwithmods.api.tile.dispenser.IBehaviorEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by primetoxinz on 5/25/17.
 */
public class BehaviorEntity implements IBehaviorEntity {
    @Override
    public NonNullList<ItemStack> collect(World world, BlockPos pos, Entity entity, ItemStack stack) {
        return NonNullList.create();
    }
}
