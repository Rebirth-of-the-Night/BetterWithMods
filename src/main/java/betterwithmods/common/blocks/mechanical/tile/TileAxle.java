package betterwithmods.common.blocks.mechanical.tile;

import betterwithmods.api.BWMAPI;
import betterwithmods.api.block.IOverpower;
import betterwithmods.api.capabilities.CapabilityAxle;
import betterwithmods.api.capabilities.CapabilityMechanicalPower;
import betterwithmods.api.tile.IAxle;
import betterwithmods.api.tile.IAxleTick;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.mechanical.BlockAxle;
import betterwithmods.common.blocks.tile.TileBasic;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by primetoxinz on 7/18/17.
 */
public class TileAxle extends TileBasic implements IAxle, ITickable {
    private byte maxSignal;
    private int maxPower;
    private int minPower;

    private byte signal;
    private int power;

    private boolean dirty;

    public static List<IAxleTick> tickHandlers = Lists.newArrayList();

    public TileAxle() {

    }

    public TileAxle(int maxPower, int minPower, byte maxSignal) {
        this.maxPower = maxPower;
        this.minPower = minPower;
        this.maxSignal = maxSignal;
    }

    public void onChanged() {
        byte findSignal = 0;
        int findPower = 0;
        int sources = 0;

        for (EnumFacing facing : getDirections()) {
            BlockPos offset = pos.offset(facing);
            IMechanicalPower mech = BWMAPI.IMPLEMENTATION.getMechanicalPower(world, offset, facing.getOpposite());
            if (mech != null) {

                IAxle axle = BWMAPI.IMPLEMENTATION.getAxle(world, offset, facing.getOpposite());
                if (axle != null) {
                    if (isFacing(axle)) {
                        byte next = axle.getSignal();
                        if (next > findSignal) {
                            findSignal = next;
                        }
                    }
                }

                int power = mech.getMechanicalOutput(facing.getOpposite());

                if (power > 0) {
                    if (power > findPower) {
                        sources++;
                        if (axle != null) {
                            if (axle.getSignal() >= findSignal)
                                findPower = power;
                        } else {
                            findPower = power;
                            if (getBlock() == BWMBlocks.STEEL_AXLE && mech.getClass() == TileGearbox.class) {
                                findPower = Math.max(1, findPower / 2);
                            }
                        }
                    }
                    if (axle == null) {
                        findSignal = getMaximumSignal();
                    }
                }

            }
        }

        setPower(findPower);

        if (sources >= 2) {
            ((IOverpower) getBlockType()).overpower(world, pos);
            return;
        }
        byte newSignal = 0;
        if (findSignal > signal) {
            if (findSignal == 1) {
                ((IOverpower) getBlockType()).overpower(world, pos);
            }
            if (power > 0)
                newSignal = (byte) (findSignal - 1);
        } else {
            newSignal = 0;
            setPower(0);
        }
        if (newSignal != this.signal)
            setSignal(newSignal);
        ((BlockAxle) getBlockType()).setActive(world, pos, getPower() > 0);
        dirty = true;
    }


    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setByte("signal", signal);
        compound.setInteger("power", power);
        compound.setByte("maxSignal", maxSignal);
        compound.setInteger("maxPower", maxPower);
        compound.setInteger("minPower", minPower);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.signal = compound.getByte("signal");
        this.maxSignal = compound.getByte("maxSignal");

        this.power = compound.getInteger("power");
        this.maxPower = compound.getInteger("maxPower");
        this.minPower = compound.getInteger("minPower");
        super.readFromNBT(compound);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return super.hasCapability(capability, facing) || capability == CapabilityMechanicalPower.MECHANICAL_POWER || capability == CapabilityAxle.AXLE;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityMechanicalPower.MECHANICAL_POWER)
            return CapabilityMechanicalPower.MECHANICAL_POWER.cast(this);
        if (capability == CapabilityAxle.AXLE)
            return CapabilityAxle.AXLE.cast(this);
        return super.getCapability(capability, facing);
    }

    @Override
    public int getMechanicalOutput(EnumFacing facing) {
        if (facing.getAxis() == getAxis()) {
            IAxle axle = BWMAPI.IMPLEMENTATION.getAxle(world, pos.offset(facing), facing.getOpposite());
            if (axle != null && axle.getSignal() > this.getSignal())
                return 0;
            return power;
        }
        return 0;
    }

    @Override
    public int getMechanicalInput(EnumFacing facing) {
        return maxPower;
    }

    @Override
    public int getMaximumInput(EnumFacing facing) {
        return getMaximumInput();
    }

    @Override
    public int getMinimumInput(EnumFacing facing) {
        return 0;
    }

    @Override
    public byte getSignal() {
        return signal;
    }

    @Override
    public byte getMaximumSignal() {
        return maxSignal;
    }

    @Override
    public int getMaximumInput() {
        return maxPower;
    }

    @Override
    public int getMinimumInput() {
        return minPower;
    }

    public EnumFacing[] getDirections() {
        return ((BlockAxle) getBlockType()).getAxisDirections(world.getBlockState(pos));
    }

    @Override
    public EnumFacing.Axis getAxis() {
        if (getBlock() instanceof BlockAxle)
            return ((BlockAxle) getBlock()).getAxis(world.getBlockState(pos));
        return EnumFacing.Axis.Y;
    }

    public void setSignal(byte signal) {
        this.signal = signal;
    }

    public void setPower(int power) {
        this.power = Math.min(power, maxPower + 1);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        for (EnumFacing facing : getDirections()) {
            if (!BWMAPI.IMPLEMENTATION.isAxle(world, pos.offset(facing), facing.getOpposite())) {
                world.neighborChanged(pos.offset(facing), getBlockType(), pos);
            }
        }
        dirty = false;
    }

    public int getPower() {
        return power;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s", signal, power, pos);
    }

    @Override
    public World getBlockWorld() {
        return super.getWorld();
    }

    @Override
    public BlockPos getBlockPos() {
        return super.getPos();
    }

    @Override
    public Block getBlock() {
        return getBlockType();
    }

    @Override
    public void update() {
        if(!tickHandlers.isEmpty())
            tickHandlers.forEach(t -> t.tick(world, pos, this));
        if(dirty)
            markDirty();
    }
}

