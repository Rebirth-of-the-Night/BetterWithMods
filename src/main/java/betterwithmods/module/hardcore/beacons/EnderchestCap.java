package betterwithmods.module.hardcore.beacons;

import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnderchestCap implements ICapabilitySerializable<NBTTagCompound> {

    @CapabilityInject(EnderchestCap.class)
    public static Capability<EnderchestCap> ENDERCHEST_CAPABILITY = null;

    private EnumFacing facing;
    private InventoryEnderChest inventory;

    public EnderchestCap(EnumFacing facing, InventoryEnderChest inventory) {
        this.facing = facing;
        this.inventory = inventory;
    }

    public EnderchestCap(EnumFacing facing) {
        this(facing, new InventoryEnderChest());
    }

    public EnderchestCap() {
        this(EnumFacing.NORTH, new InventoryEnderChest());
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return facing == this.facing && capability.equals(ENDERCHEST_CAPABILITY);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (hasCapability(capability, facing))
            return ENDERCHEST_CAPABILITY.cast(this);
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("facing", facing.getIndex());
        if (inventory != null) {
            tag.setTag("inventory", inventory.saveInventoryToNBT());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("inventory")) {
            NBTTagList list = nbt.getTagList("inventory", 10);
            inventory = new InventoryEnderChest();
            inventory.loadInventoryFromNBT(list);
        }
        facing = EnumFacing.byIndex(nbt.getInteger("facing"));
    }

    public InventoryEnderChest getInventory() {
        return inventory;
    }

    public static class Storage implements Capability.IStorage<EnderchestCap> {

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<EnderchestCap> capability, EnderchestCap instance, EnumFacing side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<EnderchestCap> capability, EnderchestCap instance, EnumFacing side, NBTBase nbt) {
            instance.deserializeNBT((NBTTagCompound) nbt);
        }
    }

}

