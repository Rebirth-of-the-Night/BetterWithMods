package betterwithmods.common.entity.ai.eat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Optional;

/**
 * Created by primetoxinz on 4/22/17.
 */
public abstract class EntityAIEatFood<T extends EntityLivingBase> extends EntityAIBase {
    protected T entity;
    private Ingredient validItem;
    private EntityItem targetItem;

    private double distance;

    public EntityAIEatFood(T entity, Ingredient validItem, double distance) {
        this.entity = entity;
        this.validItem = validItem;
        this.distance = distance;
    }

    private Optional<EntityItem> getTargetItem(List<EntityItem> items) {
        if (items.isEmpty())
            return Optional.empty();
        EntityItem target = null;
        for (EntityItem item : items) {
            if (validItem.apply(item.getItem())) {
                target = item;
                break;
            }
        }
        return Optional.ofNullable(target);
    }

    @Override
    public void resetTask() {
        targetItem = null;
    }

    @Override
    public boolean shouldExecute() {
        if (!isReady())
            return false;

        BlockPos entityPos = entity.getPosition();
        if (targetItem == null) {
            List<EntityItem> entityItems = entity.getEntityWorld().getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(entityPos, entityPos.add(1, 1, 1)).grow(distance), entity -> entity != null && entity.onGround);
            targetItem = getTargetItem(entityItems).orElse(null);
        }

        if (targetItem != null) {
            BlockPos targetPos = targetItem.getPosition();
            if (entityPos.getDistance(targetPos.getX(), targetPos.getY(), targetPos.getZ()) <= distance) {
                processItemEating();
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public abstract boolean isReady();

    public abstract void onEaten(ItemStack food);

    @Override
    public boolean shouldContinueExecuting() {
        return shouldExecute();
    }

    private void processItemEating() {
        if (targetItem != null) {
            ItemStack foodStack = targetItem.getItem();
            if (!foodStack.isEmpty())
                onEaten(foodStack);
        }
    }
}
