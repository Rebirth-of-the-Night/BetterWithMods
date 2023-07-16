package betterwithmods.module.hardcore.beacons;

import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CapabilityBeacon implements ICapabilitySerializable<NBTTagCompound> {

    @CapabilityInject(CapabilityBeacon.class)
    public static Capability<CapabilityBeacon> BEACON_CAPABILITY = null;

    private HashMap<Long, Integer> beacons = Maps.newHashMap();

    public CapabilityBeacon() {
    }

    private static NBTTagCompound writeBeaconEntry(Long pos, int level) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setLong("pos", pos);
        tag.setInteger("level", level);
        return tag;
    }

    private static Pair<Long, Integer> readBeaconEntry(NBTTagCompound tag) {
        return Pair.of(tag.getLong("pos"), tag.getInteger("level"));
    }

    public void addBeacon(BlockPos pos, int level) {
        beacons.put(pos.toLong(), level);
    }

    public void removeBeacon(BlockPos pos) {
        beacons.remove(pos.toLong());
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == BEACON_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (hasCapability(capability, facing))
            return BEACON_CAPABILITY.cast(this);
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tags = new NBTTagCompound();
        NBTTagList list = new NBTTagList();

        for (Map.Entry<Long, Integer> entry : beacons.entrySet()) {
            list.appendTag(writeBeaconEntry(entry.getKey(), entry.getValue()));
        }

        tags.setTag("list", list);
        return tags;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("list", 10);
        for (Iterator<NBTBase> it = list.iterator(); it.hasNext(); ) {
            NBTTagCompound tag = (NBTTagCompound) it.next();
            Pair<Long, Integer> b = readBeaconEntry(tag);
            beacons.put(b.getKey(), b.getValue());
        }
    }

    public BlockPos getClosest(World world, Entity player) {
        if (beacons.isEmpty())
            return world.getSpawnPoint();
        BlockPos pos = BlockPos.fromLong(beacons.keySet().stream().min(Comparator.comparingDouble(p -> BlockPos.fromLong(p).distanceSq(player.getPosition()))).orElse(world.getSpawnPoint().toLong()));
        return pos;
    }

    public static class Storage implements Capability.IStorage<CapabilityBeacon> {

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<CapabilityBeacon> capability, CapabilityBeacon instance, EnumFacing side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<CapabilityBeacon> capability, CapabilityBeacon instance, EnumFacing side, NBTBase nbt) {
            instance.deserializeNBT((NBTTagCompound) nbt);
        }
    }
}


