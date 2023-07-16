package betterwithmods.common.blocks.mechanical.tile;

import betterwithmods.api.BWMAPI;
import betterwithmods.api.capabilities.CapabilityMechanicalPower;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.common.blocks.EnumTier;
import betterwithmods.common.blocks.mechanical.BlockBellows;
import betterwithmods.common.blocks.tile.TileBasic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;

/**
 * Purpose:
 *
 * @author primetoxinz
 * @version 3/16/17
 */
public class TileBellows extends TileBasic implements IMechanicalPower {

    private int tick;
    private boolean continuous = false;
    private int power;

    public void onChange() {

        if (getBlock().getTier(world,pos) == EnumTier.STEEL)
            continuous = true;
        int power = calculateInput();
        if (continuous) {
            if (power != this.power)
                this.power = power;
            if (this.power > 0) {
                if (tick >= 37) {
                    getBlock().setActive(world, pos, !getBlock().isActive(world.getBlockState(pos)));
                    tick = 0;
                }
                tick++;
            }
        } else {
            if (power != this.power) {
                this.power = power;
                getBlock().setActive(world, pos, this.power > 0);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("power", power);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        power = compound.getInteger("power");
        super.readFromNBT(compound);
    }

    @Override
    public int getMechanicalOutput(EnumFacing facing) {
        return -1;
    }

    @Override
    public int getMechanicalInput(EnumFacing facing) {
        if (facing != EnumFacing.UP && facing != getBlock().getFacing(world.getBlockState(pos)))
            return BWMAPI.IMPLEMENTATION.getPowerOutput(world, pos.offset(facing), facing.getOpposite());
        return 0;
    }

    @Override
    public int getMaximumInput(EnumFacing facing) {
        if (getBlock().getTier(world,pos) == EnumTier.STEEL)
            return 3;
        return 1;
    }

    @Override
    public int getMinimumInput(EnumFacing facing) {
        return 0;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nonnull EnumFacing facing) {
        if (capability == CapabilityMechanicalPower.MECHANICAL_POWER)
            return true;
        return super.hasCapability(capability, facing);
    }

    @Nonnull
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nonnull EnumFacing facing) {
        if (capability == CapabilityMechanicalPower.MECHANICAL_POWER)
            return CapabilityMechanicalPower.MECHANICAL_POWER.cast(this);
        return super.getCapability(capability, facing);
    }

    public BlockBellows getBlock() {
        if(this.getBlockType() instanceof BlockBellows)
            return (BlockBellows) getBlockType();
        throw new IllegalStateException("This TileEntity does not have the correct block, something is severely wrong. Report to the mod author immediately");
    }

    @Override
    public World getBlockWorld() {
        return super.getWorld();
    }

    @Override
    public BlockPos getBlockPos() {
        return super.getPos();
    }

}
