package betterwithmods.module.hardcore.world.spawn;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class SpawnSaving implements ICapabilitySerializable<NBTTagCompound> {

    public static Optional<SpawnSaving> getCapability(EntityPlayer player) {
        if (player.hasCapability(SpawnSaving.SPAWN_CAP, null)) {
           return Optional.ofNullable(player.getCapability(SpawnSaving.SPAWN_CAP, null));
        }
        return Optional.empty();
    }

    @CapabilityInject(SpawnSaving.class)
    public static Capability<SpawnSaving> SPAWN_CAP = null;

    private BlockPos pos;

    public SpawnSaving() {
    }

    public SpawnSaving(EntityPlayer player) {
        pos = player.world.getSpawnPoint();
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == SPAWN_CAP;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == SPAWN_CAP)
            return SPAWN_CAP.cast(this);
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setLong("spawn", pos.toLong());
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        pos = BlockPos.fromLong(nbt.getLong("spawn"));
    }

    public static class Storage implements Capability.IStorage<SpawnSaving> {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<SpawnSaving> capability, SpawnSaving instance, EnumFacing side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<SpawnSaving> capability, SpawnSaving instance, EnumFacing side, NBTBase nbt) {
            instance.deserializeNBT((NBTTagCompound) nbt);
        }
    }
}