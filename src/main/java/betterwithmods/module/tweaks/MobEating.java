package betterwithmods.module.tweaks;

import betterwithmods.common.entity.ai.eat.EntityAIMonsterEat;
import betterwithmods.module.Feature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreIngredient;

public class MobEating extends Feature {
    private static final int radius = 4;

    @Override
    public String getFeatureDescription() {
        return "Mobs are hungry too, if they see some food on the ground that they like they might just eat!";
    }

    @SubscribeEvent
    public void addEntityAI(EntityJoinWorldEvent event) {

        Entity entity = event.getEntity();
        if (entity instanceof EntitySpider) {
            ((EntitySpider) entity).tasks.addTask(0, new EntityAIMonsterEat((EntityCreature) entity, new OreIngredient("meatChicken"), radius));
        } else if (entity instanceof EntityZombie) {
            ((EntityZombie) entity).tasks.addTask(0, new EntityAIMonsterEat((EntityCreature) entity, new OreIngredient("listAllmeat"), radius));
        } else if (entity instanceof EntityWolf) {
            ((EntityWolf) entity).tasks.addTask(0, new EntityAIMonsterEat((EntityCreature) entity, new OreIngredient("listAllmeat"), radius));
        }
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }
}


