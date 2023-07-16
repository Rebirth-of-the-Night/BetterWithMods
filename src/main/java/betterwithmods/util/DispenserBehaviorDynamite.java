package betterwithmods.util;

import betterwithmods.common.entity.EntityDynamite;
import betterwithmods.common.items.ItemDynamite;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DispenserBehaviorDynamite extends BehaviorProjectileDispense {
    boolean lit;

    public DispenserBehaviorDynamite(boolean lit) {
        this.lit = lit;
    }

    @Override
    protected IProjectile getProjectileEntity(World world, IPosition pos, ItemStack stack) {
        EntityDynamite entity = new EntityDynamite(world, pos.getX(), pos.getY(), pos.getZ(), lit ? getFuseTime(stack) : 0);
        entity.setDynamiteStack(stack);
        return entity;
    }

    private int getFuseTime(ItemStack stack) {
        Item item = stack.getItem();
        if(item instanceof ItemDynamite)
            return ((ItemDynamite) item).getFuseTime();
        return 0;
    }
}
