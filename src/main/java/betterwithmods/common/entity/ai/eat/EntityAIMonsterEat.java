package betterwithmods.common.entity.ai.eat;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class EntityAIMonsterEat extends EntityAIEatFood<EntityCreature> {

    private int cooldown;

    public EntityAIMonsterEat(EntityCreature entity, Ingredient validItem, double squareDistance) {
        super(entity, validItem, squareDistance);
    }

    @Override
    public boolean isReady() {
        if (cooldown-- < 0)
            cooldown = 0;
        return cooldown == 0;
    }

    @Override
    public void onEaten(ItemStack food) {
        // TODO: remove the task when a wolf is being tamed
        if (entity instanceof EntityWolf) {
            if (((EntityWolf) entity).isTamed()) {
                cooldown = 6000; // 5 minutes
                return;
            }
        }
        entity.playSound(SoundEvents.ENTITY_PLAYER_BURP, 1.0F, (entity.world.rand.nextFloat() - entity.world.rand.nextFloat()) * 0.2F + 1.0F);
        food.shrink(1);
        cooldown = 200;
    }

}
