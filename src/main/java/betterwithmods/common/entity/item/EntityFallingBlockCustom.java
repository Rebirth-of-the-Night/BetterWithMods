package betterwithmods.common.entity.item;

import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityFallingBlockCustom extends EntityFallingBlock {
    public EntityFallingBlockCustom(World worldIn) {
        super(worldIn);
    }

    public EntityFallingBlockCustom(EntityFallingBlock entityBlock) {
        super(entityBlock.getEntityWorld(), entityBlock.prevPosX,
                entityBlock.prevPosY, entityBlock.prevPosZ, entityBlock.getBlock());
        NBTTagCompound originalData = new NBTTagCompound();
        writeEntityToNBT(originalData);
        this.readEntityFromNBT(originalData);
    }

    @Override
    public void onUpdate() {
        if (getBlock() == null) return;
        super.onUpdate();
    }
}
