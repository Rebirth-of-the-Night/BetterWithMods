package betterwithmods.common.blocks.mechanical.tile;

import betterwithmods.api.BWMAPI;
import betterwithmods.api.capabilities.CapabilityMechanicalPower;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.common.blocks.mechanical.BlockGearbox;
import betterwithmods.common.blocks.tile.TileBasic;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;

public class TileGearbox extends TileBasic implements IMechanicalPower {
    protected int power;
    protected int maxPower;
    protected int tick;
    protected int unchanged;

    public TileGearbox() {
    }

    public TileGearbox(int maxPower) {
        this.maxPower = maxPower;
    }

    public void onChanged() {
        tick++;
        if (tick < 20)
            return;
        tick = 0;

        if (BWMAPI.IMPLEMENTATION.isRedstonePowered(world, pos)) {
            setPower(0);
            markDirty();
            return;
        }
        int power = this.getMechanicalInput(getFacing());


        if (power != this.power) {
            setPower(power);
            unchanged = 0;
        } else {
            unchanged++;
        }
        if (isOverpowered() && unchanged > 30) {
            getBlock().overpower(world, pos);
        }
        markDirty();
    }

    @Override
    public int getMinimumInput(EnumFacing facing) {
        return 1;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nonnull EnumFacing facing) {
        return capability == CapabilityMechanicalPower.MECHANICAL_POWER
                || super.hasCapability(capability, facing);
    }

    @Nonnull
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nonnull EnumFacing facing) {
        if (capability == CapabilityMechanicalPower.MECHANICAL_POWER)
            return CapabilityMechanicalPower.MECHANICAL_POWER.cast(this);
        return super.getCapability(capability, facing);
    }

    @Override
    public int getMechanicalOutput(EnumFacing facing) {
        if (facing != getFacing() && BWMAPI.IMPLEMENTATION.isAxle(world, pos.offset(facing), facing.getOpposite()))
            return Math.min(power, maxPower);
        return -1;
    }

    @Override
    public int getMechanicalInput(EnumFacing facing) {
        BlockPos pos = getBlockPos().offset(facing);
        if (BWMAPI.IMPLEMENTATION.getMechanicalPower(world, pos, facing.getOpposite()) != null && !(BWMAPI.IMPLEMENTATION.getMechanicalPower(world, pos, facing.getOpposite()) instanceof TileGearbox))
            return BWMAPI.IMPLEMENTATION.getPowerOutput(world, pos, facing.getOpposite());
        return 0;
    }

    @Override
    public int getMaximumInput(EnumFacing facing) {
        return this.maxPower;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        power = tag.getInteger("power");
        maxPower = tag.getInteger("maxPower");
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagCompound t = super.writeToNBT(tag);
        tag.setInteger("power", power);
        tag.setInteger("maxPower", maxPower);
        return t;
    }


    public BlockGearbox getBlock() {
        return (BlockGearbox) getBlockType();
    }

    public EnumFacing getFacing() {
        return getBlock().getFacing(world, pos);
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        getBlock().setActive(world, pos, power > 0);
    }

    @Override
    public String toString() {
        return String.format("%s", power);
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
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    public boolean isOverpowered() {
        return this.power > getMaximumInput(getFacing());
    }
}
