package betterwithaddons.item;

import betterwithaddons.entity.EntityYa;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemYa extends Item {

    public ItemYa() {
        super();
    }

    public EntityYa createArrow(World worldIn, ItemStack stack, EntityLivingBase shooter) {
        EntityYa entityarrow = new EntityYa(worldIn, shooter);
        entityarrow.setArrowStack(stack);
        return entityarrow;
    }

    public boolean isInfinite(ItemStack stack, ItemStack bow, EntityPlayer player) {
        return false;
    }

    public void hitEntity(EntityYa entityYa, EntityLivingBase living) {
        //NOOP
    }
}