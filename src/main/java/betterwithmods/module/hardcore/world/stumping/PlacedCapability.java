package betterwithmods.module.hardcore.world.stumping;

import com.google.common.collect.Sets;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Set;

public class PlacedCapability implements ICapabilitySerializable<NBTTagCompound> {

    @CapabilityInject(PlacedCapability.class)
    public static Capability<PlacedCapability> PLACED_CAPABILITY = null;

    private Set<BlockPos> placed = Sets.newHashSet();

    public Set<BlockPos> getPlaced() {
        return placed;
    }

    public boolean addPlaced(BlockPos pos) {
        return placed.add(pos);
    }

    public boolean addAll(BlockPos[] pos) {
        return placed.addAll(Arrays.asList(pos));
    }

    public boolean isPlaced(BlockPos pos) {
        return placed.contains(pos);
    }

    public void removePlaced(BlockPos pos) {
        placed.remove(pos);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == PLACED_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (hasCapability(capability, facing))
            return PLACED_CAPABILITY.cast(this);
        return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (BlockPos pos : placed) {
            NBTTagLong p = new NBTTagLong(pos.toLong());
            list.appendTag(p);
        }
        tag.setTag("placed", list);
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("placed")) {
            placed.clear();
            NBTTagList list = nbt.getTagList("placed", 4);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagLong p = (NBTTagLong) list.get(i);
                placed.add(BlockPos.fromLong(p.getLong()));
            }
        }
    }

    public static class Storage implements Capability.IStorage<PlacedCapability> {

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<PlacedCapability> capability, PlacedCapability instance, EnumFacing side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<PlacedCapability> capability, PlacedCapability instance, EnumFacing side, NBTBase nbt) {
            instance.deserializeNBT((NBTTagCompound) nbt);
        }
    }
}
