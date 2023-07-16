package betterwithmods.module.hardcore.creatures.chicken;

import betterwithmods.util.InvUtils;
import betterwithmods.util.WorldUtils;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EggLayer implements ICapabilitySerializable<NBTTagCompound> {
    @SuppressWarnings("CanBeFinal")
    @CapabilityInject(EggLayer.class)
    public static Capability<EggLayer> EGG_LAYER_CAP = null;

    private int ticks;
    private boolean feed;
    private ItemStack egg;
    private Ingredient feedItems;

    public EggLayer(ItemStack egg, Ingredient feedItems) {
        this.egg = egg;
        this.feedItems = feedItems;
    }

    public EggLayer() {
    }

    public boolean isValidLayer(EntityLivingBase entity) {
        return !entity.isChild();
    }

    public boolean canLayEgg() {
        return getTicks() <= 0;
    }

    public boolean isFeed() {
        return feed;
    }

    public void setFeed(boolean feed) {
        this.feed = feed;
    }

    public int getTicks() {
        return this.ticks;
    }

    public void setTicks(int tick) {
        this.ticks = tick;
    }

    public ItemStack getEggItem() {
        return egg;
    }

    public Ingredient getFeedItems() {
        return feedItems;
    }

    private int calculateLayTime(EntityLiving entity) {
        World world = entity.world;

        int time = (int) (world.getTotalWorldTime() % WorldUtils.Time.DAY.getTicks());
        int timeLeft = (int) (WorldUtils.Time.DAY.getTicks() - time);
        int ticks = timeLeft + WorldUtils.TimeFrame.DAWN.randomBetween() / 2;
        if (WorldUtils.isPast(world, WorldUtils.TimeFrame.NIGHT)) {
            ticks += WorldUtils.Time.DAY.getTicks();
        }

        return ticks;
    }

    public void feed(EntityLiving entity, ItemStack stack) {
        if(!isValidLayer(entity))
            return;

        if (!isFeed()) {
            if (getFeedItems().apply(stack)) {
                setFeed(true);
                setTicks(calculateLayTime(entity));
                stack.shrink(1);
                entity.attackEntityFrom(new DamageSource("feed"), 0);
            }
        }
    }

    public void lay(EntityLivingBase entityLiving) {
        if(!isValidLayer(entityLiving))
            return;

        entityLiving.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (entityLiving.getRNG().nextFloat() - entityLiving.getRNG().nextFloat()) * 0.2F + 1.0F);
        InvUtils.ejectStackWithOffset(entityLiving.world, entityLiving.getPosition(), getEggItem());
        setFeed(false);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == EGG_LAYER_CAP;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == EGG_LAYER_CAP)
            return EGG_LAYER_CAP.cast(this);
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("ticks", getTicks());
        tag.setBoolean("feed", isFeed());
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        setTicks(tag.getInteger("ticks"));
        setFeed(tag.getBoolean("feed"));
    }

    public static class CapabilityEggLayer implements Capability.IStorage<EggLayer> {

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<EggLayer> capability, EggLayer instance, EnumFacing side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<EggLayer> capability, EggLayer instance, EnumFacing side, NBTBase nbt) {
            instance.deserializeNBT((NBTTagCompound) nbt);
        }
    }
}