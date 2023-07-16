package betterwithmods.module.gameplay.breeding_harness;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityHarness implements ICapabilitySerializable<NBTTagCompound> {

    @CapabilityInject(CapabilityHarness.class)
    public static Capability<CapabilityHarness> HARNESS_CAPABILITY = null;

    public CapabilityHarness() {
    }

    public ItemStack harness = ItemStack.EMPTY;

    @Nonnull
    public ItemStack getHarness() {
        return harness;
    }

    public void setHarness(ItemStack harness) {
        this.harness = harness;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == HARNESS_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if(hasCapability(capability,facing))
            return HARNESS_CAPABILITY.cast(this);
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("harness", harness.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        harness = new ItemStack(nbt.getCompoundTag("harness"));
    }

    public static class Storage implements Capability.IStorage<CapabilityHarness> {
        @Override
        public NBTBase writeNBT(Capability<CapabilityHarness> capability, CapabilityHarness instance, EnumFacing side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<CapabilityHarness> capability, CapabilityHarness instance, EnumFacing side, NBTBase base) {
            instance.deserializeNBT((NBTTagCompound) base);
        }
    }
}
