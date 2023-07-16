package betterwithmods.module.hardcore.creatures;

import betterwithmods.network.BWNetwork;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketSetPassengers;

public class AIMount extends EntityAIBase {

    public EntityLiving rider;
    public EntityLivingBase target;

    private int range;

    public AIMount(EntityLiving rider, int range) {
        this.rider = rider;
        this.range = range;
    }

    @Override
    public boolean shouldExecute() {
        target = rider.getAttackTarget();
        return !rider.isRiding() && target != null && rider.getDistanceSq(target) < range;
    }

    @Override
    public void startExecuting() {
        if (rider.world.isRemote)
            return;
        if (!target.isBeingRidden()) {
            if (rider.startRiding(target, true)) {
                if (target instanceof EntityPlayerMP) {
                    BWNetwork.sendPacket(target, new SPacketSetPassengers(target));
                }
            }
        }
    }
}
