package betterwithmods.common.entity.ai.eat;

import betterwithmods.module.hardcore.creatures.chicken.EggLayer;
import betterwithmods.module.hardcore.creatures.chicken.HCChickens;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class EntityAIAnimalEat extends EntityAIEatFood<EntityAnimal> {

    public EntityAIAnimalEat(EntityAnimal entity, Ingredient validItem, double distance) {
        super(entity, validItem, distance);
    }

    @Override
    public boolean isReady() {
        return canBreed(entity);
    }

    @Override
    public void onEaten(ItemStack food) {
        EggLayer layer = HCChickens.getLayer(entity);
        if (layer != null) {
            layer.feed(entity, food);
        } else {
            entity.setInLove(null);
            food.shrink(1);
        }
    }

    private boolean canBreed(EntityAnimal entity) {
        //Handle HCChickens
        EggLayer layer = HCChickens.getLayer(entity);
        if (layer != null) {
            return !layer.isFeed();
        }

        //Handle tamed horses
        if (entity instanceof AbstractHorse) {
            return ((AbstractHorse) entity).isTame();
        }

        //Handle tamed animals
        if (entity instanceof EntityTameable) {
            return ((EntityTameable) entity).isTamed() && !((EntityTameable) entity).isSitting();
        }

        //Only adults that are read to breed
        if (!entity.isChild()) {
            return !entity.isInLove();
        }
        return false;
    }
}
