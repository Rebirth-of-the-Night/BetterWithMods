package betterwithmods.common.items;

import betterwithmods.BWMod;
import betterwithmods.client.BWCreativeTabs;
import betterwithmods.common.entity.EntityDynamite;
import betterwithmods.network.BWNetwork;
import betterwithmods.network.messages.MessageFXDynamite;
import betterwithmods.util.ExplosionHelper;
import betterwithmods.util.InvUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


import javax.annotation.Nullable;
import java.util.List;

public class ItemDynamite extends Item {

    private final static Ingredient FLINT_AND_STEEL = Ingredient.fromStacks(new ItemStack(Items.FLINT_AND_STEEL, 1, OreDictionary.WILDCARD_VALUE));

    public static boolean needsOffhand;
    public static boolean newtonianThrow;
    public static boolean bloodThrow;
    public static boolean dispenseLit;

    public static ThreadLocal<Float> fuseOverride = new ThreadLocal<>();

    public ItemDynamite() {
        super();
        this.setCreativeTab(BWCreativeTabs.BWTAB);
        addPropertyOverride(new ResourceLocation(BWMod.MODID, "fuse"), (stack, worldIn, entityIn) -> {
            if (fuseOverride.get() != null)
                return fuseOverride.get();
            if (entityIn == null)
                return 0.0F;
            if (!entityIn.isHandActive() || entityIn.getActiveItemStack() != stack)
                return 0.0F;
            float value = (float) (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / stack.getMaxItemUseDuration();
            return value;
        });
    }

    public static void setFuseOverride(float fuseSlide) {
        fuseOverride.set(fuseSlide);
    }

    public static void resetFuseOverride() {
        fuseOverride.set(null);
    }

    public int getFuseTime() {
        return 100;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return bloodThrow ? getFuseTime() : super.getMaxItemUseDuration(stack);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return bloodThrow ? EnumAction.BOW : super.getItemUseAction(stack);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack held = player.getHeldItem(hand);

        if (!world.isRemote) {
            ItemStack activator = getActivator(player, hand);
            if (!activator.isEmpty()) {
                activator.damageItem(1, player);
            }
            boolean lit = !activator.isEmpty();

            if(bloodThrow && lit) {
                world.playSound(null, player.getPosition(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.AMBIENT, 1.0F, 1.0F);
                player.setActiveHand(hand);
            } else {
                if(lit)
                    world.playSound(null, player.getPosition(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.AMBIENT, 1.0F, 1.0F);
                throwDynamite(world, player, held, lit ? getFuseTime() : 0);
                held.shrink(1);
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, held);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, held);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack held, World world, EntityLivingBase entity, int timeLeft) {
        if(!world.isRemote)
            throwDynamite(world, entity, held, Math.max(timeLeft,1));
        held.shrink(1);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack held, World world, EntityLivingBase entity) {
        if(!world.isRemote)
            throwDynamite(world, entity, held, 1);
        held.shrink(1);
        return held;
    }

    private void throwDynamite(World world, EntityLivingBase entity, ItemStack held, int fuse) {
        EntityDynamite dynamite = new EntityDynamite(world, entity, fuse);
        dynamite.setDynamiteStack(held);
        if(newtonianThrow)
            dynamite.addVelocity(entity.motionX,entity.motionY,entity.motionZ);
        world.spawnEntity(dynamite);
        world.playSound(null, new BlockPos(dynamite.posX, dynamite.posY, dynamite.posZ), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.AMBIENT, 0.5F, 0.4F / (Item.itemRand.nextFloat() * 0.4F + 0.8F));
    }

    public void explode(EntityDynamite entity) {
        float intensity = 1.5f;
        Explosion explosion = new Explosion(entity.world, entity, entity.posX, entity.posY, entity.posZ, intensity, false, true);
        ExplosionHelper helper = new ExplosionHelper(explosion);
        helper.calculateBlocks(3.0f,true);
        helper.createExplosion();
        entity.redneckFishing(entity.getPosition(), helper.getAffectedBlocks(), 0.05f);
        BWNetwork.sendToAllAround(new MessageFXDynamite(explosion.getPosition(), explosion.size, helper.getAffectedBlocks(), explosion.getAffectedBlockPositions()), entity.world, entity.getPosition());
    }

    private ItemStack getActivator(EntityPlayer player, EnumHand hand) {
        if(needsOffhand) {
            ItemStack otherHand = player.getHeldItem(InvUtils.otherHand(hand));
            if(FLINT_AND_STEEL.apply(otherHand))
                return otherHand;
        } else {
            return InvUtils.findItemInInventory(FLINT_AND_STEEL, InvUtils.getPlayerInventory(player, null));
        }
        return ItemStack.EMPTY;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("lore.bwm:"+getRegistryName().getPath()));
        if(bloodThrow)
            tooltip.add(I18n.format("lore.bwm:dynamite_blood"));
    }

}
