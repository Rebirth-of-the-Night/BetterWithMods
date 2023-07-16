package betterwithmods.module.hardcore.creatures;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

public class AIGrab extends EntityAIBase {

    public EntityLiving rider;
    public EntityLivingBase target;
    public EntityTentacle tentacle;
    private int min, max;
    private int ticks; //, cooldown;

    public AIGrab(EntityLiving rider, int min, int max) {
        this.rider = rider;
        this.min = min;
        this.max = max;
    }

    @Override
    public void resetTask() {
        if(tentacle != null)
            tentacle.setDead();
    }

    @Override
    public void startExecuting() {
        if(tentacle != null)
            tentacle.setDead();
    }

    @Override
    public boolean shouldExecute() {
        if (rider == null || rider.isRiding())
            return false;
        target = rider.getAttackTarget();
        if (target == null || (target instanceof EntityPlayer && ((EntityPlayer) target).isCreative()) || !(rider.getEntitySenses().canSee(target)))
            return false;

        double distance = rider.getDistanceSq(target);
        return !rider.isRiding() && distance > min * min && distance < max * max;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return super.shouldContinueExecuting();
    }

    @Override
    public void updateTask() {
        if (rider.world.isRemote)
            return;
        if (!target.isBeingRidden()) {

            if(tentacle == null) {

//                cooldown++;
//                if(cooldown > 40)
//                    cooldown = 0;
//                else
//                    return;

                tentacle = new EntityTentacle(rider.world, rider);
                rider.world.spawnEntity(tentacle);
            } else {
                ticks++;
                if(ticks > 10) {
                    tentacle.handleHookRetraction();
                    tentacle.setDead();
                    tentacle = null;
                    ticks = 0;
                }
            }

        }
    }

}
