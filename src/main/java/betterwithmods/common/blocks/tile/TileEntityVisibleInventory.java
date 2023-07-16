package betterwithmods.common.blocks.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class TileEntityVisibleInventory extends TileBasicInventory implements ITickable {
    public short occupiedSlots;
    private int facing;

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    public int getFacing() {
        return this.facing;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.facing = tag.getByte("facing");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagCompound t = super.writeToNBT(tag);
        t.setByte("facing", (byte) this.facing);
        return t;
    }

    public int filledSlots() {
        int fill = 0;
        for (int i = 0; i < this.getMaxVisibleSlots(); i++) {
            if (!inventory.getStackInSlot(i).isEmpty())
                fill++;
        }
        return fill;
    }

    public abstract String getName();

    //Mostly for aesthetic purposes, primarily so the filter in the filtered hopper doesn't reclaimCount.
    public abstract int getMaxVisibleSlots();

}
