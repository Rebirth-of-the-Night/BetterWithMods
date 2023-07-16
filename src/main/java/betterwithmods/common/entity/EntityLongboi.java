package betterwithmods.common.entity;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityLongboi extends EntityWolf {
    public EntityLongboi(World worldIn) {
        super(worldIn);
    }

    @Override
    public EntityLongboi createChild(EntityAgeable ageable) {
        EntityLongboi longboi = new EntityLongboi(this.world);
        UUID uuid = this.getOwnerId();

        if (uuid != null) {
            longboi.setOwnerId(uuid);
            longboi.setTamed(true);
        }

        return longboi;
    }
}
