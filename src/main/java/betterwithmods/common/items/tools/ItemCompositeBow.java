package betterwithmods.common.items.tools;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * Purpose:
 *
 * @author primetoxinz
 * @version 11/18/16
 */
public class ItemCompositeBow extends ItemBow {
    public ItemCompositeBow() {
        this.setMaxStackSize(1);
        this.setMaxDamage(576);
        this.addPropertyOverride(new ResourceLocation("pull"), (stack, worldIn, entityIn) -> {
            if (entityIn == null) {
                return 0.0F;
            } else {
                ItemStack itemstack = entityIn.getActiveItemStack();
                return !itemstack.isEmpty() && itemstack.getItem() == this ? (float) (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F : 0.0F;
            }
        });
        this.addPropertyOverride(new ResourceLocation("pulling"), (stack, worldIn, entityIn) ->
                entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F
        );

    }

    private boolean isBroadHead(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemBroadheadArrow;
    }

    @Override
    protected boolean isArrow(ItemStack stack) {
        return isBroadHead(stack) || super.isArrow(stack);
    }

    @Override
    protected ItemStack findAmmo(EntityPlayer player) {
        if (this.isArrow(player.getHeldItem(EnumHand.OFF_HAND)))
            return player.getHeldItem(EnumHand.OFF_HAND);
        else if (this.isArrow(player.getHeldItem(EnumHand.MAIN_HAND)))
            return player.getHeldItem(EnumHand.MAIN_HAND);
        else {
            IItemHandler inv = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
            for (int i = 0; i < inv.getSlots(); ++i) {
                ItemStack itemstack = inv.getStackInSlot(i);
                if (this.isArrow(itemstack))
                    return itemstack;
            }
            return ItemStack.EMPTY;
        }
    }

    public float getArrowVelocity(ItemStack stack, int charge) {
        int max = isBroadHead(stack) ? 2 : 1;
        float f = charge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > max)
            f = max;
        return f;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityLiving;
            boolean flag = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack itemstack = this.findAmmo(player);

            int i = this.getMaxItemUseDuration(stack) - timeLeft;
            i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, (EntityPlayer) entityLiving, i, !itemstack.isEmpty() || flag);
            if (i < 0) return;

            if (!itemstack.isEmpty() || flag) {
                if (itemstack .isEmpty() ) {
                    itemstack = new ItemStack(Items.ARROW);
                }

                float velocity = getArrowVelocity(itemstack, i);

                if ((double) velocity >= 0.1D) {
                    boolean flag1 = player.capabilities.isCreativeMode || (itemstack.getItem() instanceof ItemArrow && ((ItemArrow) itemstack.getItem()).isInfinite(itemstack, stack, player));

                    if (!worldIn.isRemote) {
                        ItemArrow itemarrow = (ItemArrow) (itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW);
                        EntityArrow entityarrow = itemarrow.createArrow(worldIn, itemstack, player);
                        entityarrow.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, velocity * 3.0F, 1.0F);

                        if (velocity >= 1.0F)
                            entityarrow.setIsCritical(true);

                        int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);

                        if (j > 0) {
                            entityarrow.setDamage(entityarrow.getDamage() + (double) j * 0.5D + 0.5D);
                        }

                        int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);

                        if (k > 0) {
                            entityarrow.setKnockbackStrength(k);
                        }

                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
                            entityarrow.setFire(100);
                        }

                        stack.damageItem(1, player);

                        if (flag1) {
                            entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                        }

                        worldIn.spawnEntity(entityarrow);
                    }

                    worldIn.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + velocity * 0.5F);

                    if (!flag1) {
                        itemstack.shrink(1);

                        if (itemstack.getCount() == 0) {
                            player.inventory.deleteStack(itemstack);
                        }
                    }

                    player.addStat(StatList.getObjectUseStats(this));
                }
            }
        }
    }
}
