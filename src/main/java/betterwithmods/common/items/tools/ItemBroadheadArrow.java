package betterwithmods.common.items.tools;

import betterwithmods.BWMod;
import betterwithmods.common.entity.EntityBroadheadArrow;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * Purpose:
 *
 * @author primetoxinz
 * @version 11/18/16
 */
@Mod.EventBusSubscriber(modid = BWMod.MODID)
public class ItemBroadheadArrow extends ItemArrow {

    /*@SideOnly(Side.CLIENT) //What the fuck was mike smoking
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(I18n.format("item.bwm:broadhead_arrow.tooltip.0"));
        tooltip.add(I18n.format("item.bwm:broadhead_arrow.tooltip.1"));
    }*/

    @Override
    public EntityArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase shooter) {
        if (!stack.isEmpty() && stack.getItem() == this)
            return new EntityBroadheadArrow(worldIn, shooter);
        return super.createArrow(worldIn, stack, shooter);
    }

    @SubscribeEvent
    public static void onArrowLoose(ArrowLooseEvent event) {
        if(!(event.getBow().getItem() instanceof ItemCompositeBow)) {
            IItemHandler inventory = event.getEntityPlayer().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
            for(int slot = 0; slot < inventory.getSlots(); slot++) {
                Item itemInSlot = inventory.getStackInSlot(slot).getItem();
                if(itemInSlot instanceof ItemArrow) {
                    if(itemInSlot instanceof ItemBroadheadArrow) {
                        event.setCharge(5);
                        break;
                    } else {
                        break;
                    }
                }
            }
        }
    }
}
