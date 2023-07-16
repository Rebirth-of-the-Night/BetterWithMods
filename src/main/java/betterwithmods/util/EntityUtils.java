package betterwithmods.util;

import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.List;
import java.util.Optional;

public class EntityUtils {

    public static void removeAI(EntityLiving entity, Class<? extends EntityAIBase> clazz) {
        entity.tasks.taskEntries.removeIf(entityAITaskEntry -> clazz.isAssignableFrom(entityAITaskEntry.action.getClass()));
    }

    public static boolean hasAI(EntityLiving entity, Class<? extends EntityAIBase> clazz) {
        return entity.tasks.taskEntries.stream().anyMatch(entityAITaskEntry -> clazz.isAssignableFrom(entityAITaskEntry.action.getClass()));
    }

    @SuppressWarnings("unchecked")
    public static <T extends EntityAIBase> Optional<T> findFirst(EntityLiving entity, Class<? extends EntityAIBase> clazz) {
        return entity.tasks.taskEntries.stream().filter( t -> clazz.isAssignableFrom(t.getClass())).map(t -> (T) t.action).findFirst();
    }

    public static void copyEntityInfo(EntityLivingBase copyFrom, EntityLivingBase copyTo) {
        copyTo.setHealth(copyFrom.getHealth());
        copyTo.setPositionAndRotation(copyFrom.posX, copyFrom.posY, copyFrom.posZ, copyFrom.rotationYaw, copyFrom.rotationPitch);
        copyTo.setRotationYawHead(copyFrom.getRotationYawHead());
    }

    public static List<Class<? extends Entity>> loadEntitiesFromStrings(String[] strings) {
        List<Class<? extends Entity>> entities = Lists.newArrayList();
        for (String s : strings) {
            if (ForgeRegistries.ENTITIES.containsKey(new ResourceLocation(s))) {
                entities.add(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(s)).getEntityClass());
            }
        }

        return entities;
    }
}
