package betterwithmods.common.entity.ai;

import betterwithmods.util.player.PlayerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

/**
 * The Entity will flee from threats in the opposite direction.
 *
 * @author Koward
 */
public class EntityAIFlee extends EntityAIBase {
    protected final double speed;
    private final EntityCreature creature;
    private double randPosX;
    private double randPosY;
    private double randPosZ;

    public EntityAIFlee(EntityCreature creature, double speedIn) {
        this.creature = creature;
        this.speed = speedIn;
        this.setMutexBits(1);
    }


    private boolean isValidEntity() {
        if (creature instanceof EntityPolarBear) {
            return creature.isChild();
        }
        return true;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        Vec3d vec3d = null;
        Entity target = creature.getRevengeTarget();
        if (isValidEntity()) {
            if (this.creature.isBurning()) {
                vec3d = RandomPositionGenerator.findRandomTarget(this.creature, 5, 4);
            } else if (target != null && (!(target instanceof EntityPlayer) || PlayerHelper.isSurvival((EntityPlayer) target))) {
                vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.creature, 5, 0, new Vec3d(target.posX, target.posY, target.posZ));
            }
        }

        if (vec3d != null) {
            this.randPosX = vec3d.x;
            this.randPosY = vec3d.y;
            this.randPosZ = vec3d.z;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        this.creature.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean shouldContinueExecuting() {
        if (!this.creature.getNavigator().noPath() && this.creature.getRevengeTarget() != null) {
            EntityLivingBase target = this.creature.getRevengeTarget();

            if (target == null)
                return true;

            double sqDistToPos = this.creature.getDistanceSq(this.randPosX, this.randPosY, this.randPosZ);

            if (sqDistToPos > 2.0D) {
                double sqDistToTarget = this.creature.getDistanceSq(target);
                double sqDistOfTargetToPos = target.getDistanceSq(this.randPosX, this.randPosY, this.randPosZ);

                return sqDistToTarget < sqDistOfTargetToPos;
            }
        }

        return false;
    }
}