package betterwithmods.common.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;


public class EntityAIVillagerMate extends EntityAIBase {
    final World world;
    /**
     * The speed the creature moves at during mating behavior.
     */
    final double moveSpeed;
    private final EntityVillager villager;
    /**
     * Delay preventing a baby from spawning immediately when two mate-able animals find each other.
     */
    int spawnBabyDelay;
    private EntityVillager mate;

    public EntityAIVillagerMate(EntityVillager villager, double speedIn) {
        this.villager = villager;
        this.world = villager.getEntityWorld();
        this.moveSpeed = speedIn;
        this.setMutexBits(3);
    }

    public static boolean isWillingToMate(EntityVillager villager) {
        if (villager != null) {
            return villager.isWillingToMate;
        }
        return false;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    @Override
    public boolean shouldExecute() {
        if (this.villager.getGrowingAge() != 0 || this.villager.getRNG().nextInt(500) != 0) {
            return false;
        }
        if (!isWillingToMate(villager)) {
            return false;
        } else {
            this.mate = this.getNearbyMate();
            return this.mate != null && this.mate.getGrowingAge() == 0;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean shouldContinueExecuting() {
        return this.mate.isEntityAlive() && isWillingToMate(mate) && this.spawnBabyDelay < 60;
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        this.mate = null;
        this.spawnBabyDelay = 0;
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        this.villager.getLookHelper().setLookPositionWithEntity(this.mate, 10.0F, (float) this.villager.getVerticalFaceSpeed());
        this.villager.getNavigator().tryMoveToEntityLiving(this.mate, this.moveSpeed);
        ++this.spawnBabyDelay;
        if (this.spawnBabyDelay >= 60 && this.villager.getDistanceSq(this.mate) < 9.0D) {
            this.spawnBaby();
        }
    }

    /**
     * Loops through nearby animals and finds another animal of the same type that can be mated with. Returns the first
     * valid mate found.
     */
    private EntityVillager getNearbyMate() {
        List<EntityVillager> list = this.world.getEntitiesWithinAABB(this.villager.getClass(), this.villager.getEntityBoundingBox().grow(8.0D));
        double d0 = Double.MAX_VALUE;
        EntityVillager mate = null;
        for (EntityVillager current : list) {
            if (canMateWith(this.villager, current) && this.villager.getDistanceSq(current) < d0) {
                mate = current;
                d0 = this.villager.getDistanceSq(current);
            }
        }
        return mate;
    }

    /**
     * Spawns a baby animal of the same type.
     */
    private void spawnBaby() {
        EntityVillager baby = this.villager.createChild(this.mate);

        if (baby != null) {

            this.villager.setGrowingAge(6000);
            this.mate.setGrowingAge(6000);
            this.villager.setIsWillingToMate(false);
            this.mate.setIsWillingToMate(false);
            baby.setGrowingAge(-24000);
            baby.setLocationAndAngles(this.villager.posX, this.villager.posY, this.villager.posZ, 0.0F, 0.0F);
            boolean profession = world.rand.nextBoolean();
            baby.setProfession(profession ? villager.getProfessionForge() : mate.getProfessionForge());
            this.world.spawnEntity(baby);
            Random random = this.villager.getRNG();

            for (int i = 0; i < 7; ++i) {
                double d0 = random.nextGaussian() * 0.02D;
                double d1 = random.nextGaussian() * 0.02D;
                double d2 = random.nextGaussian() * 0.02D;
                double d3 = random.nextDouble() * (double) this.villager.width * 2.0D - (double) this.villager.width;
                double d4 = 0.5D + random.nextDouble() * (double) this.villager.height;
                double d5 = random.nextDouble() * (double) this.villager.width * 2.0D - (double) this.villager.width;
                this.world.spawnParticle(EnumParticleTypes.HEART, this.villager.posX + d3, this.villager.posY + d4, this.villager.posZ + d5, d0, d1, d2);
            }

            if (this.world.getGameRules().getBoolean("doMobLoot")) {
                this.world.spawnEntity(new EntityXPOrb(this.world, this.villager.posX, this.villager.posY, this.villager.posZ, random.nextInt(7) + 1));
            }
        }
    }

    public boolean canMateWith(EntityVillager village, EntityVillager mate) {
        return village != mate && (isWillingToMate(villager) && isWillingToMate(mate));
    }

}
