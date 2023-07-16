package betterwithmods.module.hardcore.creatures;

import betterwithmods.module.Feature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

public class HCBabyZombies extends Feature {
    private static final UUID BABY_SLOWNESS_ID = UUID.fromString("1bb3fa0f-1ae3-4293-bda9-2c56adb0b49d");
    private static final UUID BABY_DAMAGE_ID = UUID.fromString("bdaf090f-6d0c-4c4f-bc18-0c117c5f312f");

    private static final AttributeModifier BABY_SLOWNESS = new AttributeModifier(BABY_SLOWNESS_ID, "Baby slowness", -0.25, 1);
    private static final AttributeModifier BABY_DAMAGE = new AttributeModifier(BABY_DAMAGE_ID, "Baby damage", -0.25, 1);


    @Override
    public String getFeatureDescription() {
        return "Change baby zombies to be a more reasonable addition; Slightly slower and do less damage than adults";
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityZombie) {
            EntityZombie entity = (EntityZombie) event.getEntity();
            if(entity.isChild()) {

                IAttributeInstance movement = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
                if (!movement.hasModifier(BABY_SLOWNESS))
                    movement.applyModifier(BABY_SLOWNESS);

                IAttributeInstance damage = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
                if (!damage.hasModifier(BABY_DAMAGE))
                    damage.applyModifier(BABY_DAMAGE);
            }
        }
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }
}
