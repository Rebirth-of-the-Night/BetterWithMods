package betterwithmods.module.hardcore.creatures;

import betterwithmods.common.entity.ai.ShooterSpiderWeb;
import betterwithmods.module.Feature;
import betterwithmods.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

/**
 * Created by primetoxinz on 4/22/17.
 */
public class HCHunting extends Feature {

    public static boolean spidersShootWebs;

    public static List<Class<? extends Entity>> zombiesAttack, spiderAttack;

    @Override
    public void setupConfig() {
        spidersShootWebs = loadPropBool("Spiders Shoot Web", "Spiders shoot webs at targets", true);
        String[] zombieStrings = loadPropStringList("Mobs Zombies Attack", "List of entity classes which zombies will attack", new String[]{"minecraft:cow", "minecraft:sheep", "minecraft:pig", "minecraft:llama"});
        String[] spiderStrings = loadPropStringList("Mobs Spider Attack", "List of entity classes which spiders will attack", new String[]{"minecraft:chicken","minecraft:rabbit"});
        zombiesAttack = EntityUtils.loadEntitiesFromStrings(zombieStrings);
        spiderAttack = EntityUtils.loadEntitiesFromStrings(spiderStrings);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @SubscribeEvent
    public void addEntityAI(EntityJoinWorldEvent evt) {
        if (evt.getEntity() instanceof EntityCreature) {
            EntityCreature entity = (EntityCreature) evt.getEntity();
            if (entity instanceof EntityZombie) {
                for (Class<? extends Entity> clazz : zombiesAttack) {
                    if (EntityLivingBase.class.isAssignableFrom(clazz)) ((EntityZombie) entity).targetTasks.addTask(3, new EntityAINearestAttackableTarget(entity, clazz, true));
                }
            }
            if (entity instanceof EntitySpider) {
                for (Class<? extends Entity> clazz : spiderAttack) {
                    ((EntitySpider) entity).targetTasks.addTask(3, new EntityAINearestAttackableTarget(entity, clazz, false));
                }
                if (spidersShootWebs) {
                    ((EntitySpider) entity).tasks.addTask(3, new ShooterSpiderWeb((EntitySpider) entity, 200, 15.0F));
                }
            }
        }
    }


    @Override
    public String getFeatureDescription() {
        return "Makes it so Mobs hunt other animals too. Zombies attack herd animals, Spiders eat Chickens, Wolves will eat anything";
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }
}
