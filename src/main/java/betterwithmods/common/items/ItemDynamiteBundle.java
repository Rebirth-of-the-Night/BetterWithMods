package betterwithmods.common.items;

import betterwithmods.common.entity.EntityDynamite;
import betterwithmods.network.BWNetwork;
import betterwithmods.network.messages.MessageFXDynamite;
import betterwithmods.util.ExplosionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Explosion;

public class ItemDynamiteBundle extends ItemDynamite {
    @Override
    public int getFuseTime() {
        return 50;
    }

    @Override
    public void explode(EntityDynamite entity) {
        float intensity = 2.0f;
        Explosion explosion = new Explosion(entity.world, entity, entity.posX, entity.posY, entity.posZ, intensity, false, true);
        ExplosionHelper helper = new ExplosionHelper(explosion);
        helper.calculateBlocks(4.0f,true);
        helper.calculateEntities(intensity);
        for (Entity hitEntity : helper.getAffectedEntities()) {
            hitEntity.attackEntityFrom(DamageSource.causeExplosionDamage(explosion),15);
        }
        helper.createExplosion();
        entity.redneckFishing(entity.getPosition(), helper.getAffectedBlocks(), 0.03f);
        BWNetwork.sendToAllAround(new MessageFXDynamite(explosion.getPosition(), explosion.size, helper.getAffectedBlocks(), explosion.getAffectedBlockPositions()), entity.world, entity.getPosition());
    }
}
