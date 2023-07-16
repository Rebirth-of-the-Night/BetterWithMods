package betterwithmods.common.blocks.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;

public abstract class TileFluid extends TileBasic {
    protected FluidTank tank;

    public FluidTank createTank() {
        FluidTank tank = new FluidTank(getCapacity());
        tank.setTileEntity(this);
        return tank;
    }

    public TileFluid() {
        this.tank = createTank();
    }

    public FluidTank getTank() {
        return tank;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagCompound t = new NBTTagCompound();
        tank.writeToNBT(t);
        tag.setTag("tank", t);
        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if (tank == null)
            tank = createTank();
        tank.readFromNBT(compound.getCompoundTag("tank"));
        super.readFromNBT(compound);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (hasFluid(facing) && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tank);
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (hasFluid(facing) && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return true;
        return super.hasCapability(capability, facing);
    }

    public boolean isFull() {
        return tank.getFluidAmount() >= tank.getCapacity();
    }

    public boolean fill(FluidStack stack, boolean doFill) {
        if (tank.fill(stack, doFill) != 0) {
            SoundEvent soundevent = stack.getFluid().getFillSound(stack);
            world.playSound(null, pos, soundevent, SoundCategory.BLOCKS, 1f, 1f);
            return true;
        }
        return false;
    }

    public abstract int getCapacity();

    public abstract boolean hasFluid(EnumFacing facing);
}
