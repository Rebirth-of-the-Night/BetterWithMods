package betterwithmods.module.hardcore.world.villagers;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VillagerLevel implements ICapabilitySerializable<NBTTagCompound> {

    @CapabilityInject(VillagerLevel.class)
    public static Capability<VillagerLevel> CAPABILITY_LEVEL;

    private int level;
    private int experience;

    public VillagerLevel() {
        this(0, 0);
    }

    public VillagerLevel(int level, int experience) {
        this.level = level;
        this.experience = experience;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CAPABILITY_LEVEL;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (hasCapability(capability, facing))
            return CAPABILITY_LEVEL.cast(this);
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("level", level);
        tag.setInteger("experience", experience);
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        level = nbt.getInteger("level");
        experience = nbt.getInteger("experience");
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void incrementLevel() {
        this.level++;
    }

    public void addExperience(int add) {
        this.experience += add;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public static class Storage implements Capability.IStorage<VillagerLevel> {

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<VillagerLevel> capability, VillagerLevel instance, EnumFacing side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<VillagerLevel> capability, VillagerLevel instance, EnumFacing side, NBTBase nbt) {
            instance.deserializeNBT((NBTTagCompound) nbt);
        }
    }
}
