package betterwithmods.common.blocks.tile;

import betterwithmods.api.capabilities.SteamCapability;
import betterwithmods.api.tile.ISteamPower;
import betterwithmods.common.fluid.BWFluidRegistry;
import betterwithmods.common.fluid.FluidTankRestricted;
import betterwithmods.common.registry.heat.BWMHeatRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import java.util.ArrayList;
import java.util.List;

public class TileEntitySteamBoiler extends TileEntity implements ITickable, ISteamPower {

    private int heatUnits = 0;
    private boolean isActive = false;
    private byte update = 0;
    private FluidTankRestricted water = new FluidTankRestricted(new FluidStack(FluidRegistry.WATER, 4000), 4000);
    private FluidTankRestricted steam = new FluidTankRestricted(new FluidStack(BWFluidRegistry.STEAM, 4000), 4000);

    @Override
    public void update() {
        if (heatUnits > 0) {
            if (steam.getFluidAmount() < steam.getCapacity()) {
                if (water.getFluidAmount() > 0) {
                    FluidStack evaporate = water.getFluid();
                    if (evaporate != null && evaporate.isFluidEqual(new FluidStack(FluidRegistry.WATER, 1000))) {
                        int transfer = Math.min(heatUnits, steam.getCapacity() - steam.getFluidAmount());
                        if (transfer != 0) {
                            if (water.drain(transfer, false) != null) {
                                transfer = water.drain(transfer, true).amount;
                                steam.fill(new FluidStack(BWFluidRegistry.STEAM, transfer), true);
                            }
                        }
                    }
                }
            }
        }
        update++;
        if (update > 19) {
            update = 0;
            calculateSteamPower(null);
            calculateHeatUnits();
        }
    }

    @Override
    public void calculateSteamPower(EnumFacing facing) {
        boolean active = steam.getFluidAmount() > 0;
        if (isActive != active) {
            isActive = active;
            for (EnumFacing side : EnumFacing.VALUES) {
                if (side != EnumFacing.DOWN) {
                    TileEntity tile = getWorld().getTileEntity(pos.offset(side));
                    if (tile != null) {
                        if (tile.hasCapability(SteamCapability.STEAM_CAPABILITY, side)) {
                            tile.getCapability(SteamCapability.STEAM_CAPABILITY, side).setSteamUpdate(true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getSteamPower(EnumFacing facing) {
        return isActive ? 16 : 0;
    }

    @Override
    public void calculateHeatUnits() {
        BlockPos pos = this.pos.offset(EnumFacing.DOWN);
        int heat = 0;
        BWMHeatRegistry.HeatSource source = BWMHeatRegistry.get(world,pos);
        if(source != null)
            heat = source.getHeat();
        if (heat > 0) {
            for (int x = -1; x < 2; x++) {
                for (int z = -1; z < 2; z++) {
                    if (x == 0 && z == 0)
                        continue;
                    BlockPos check = pos.add(x, 0, z);
                    heat += BWMHeatRegistry.getHeat(world,check);
                }
            }
        }
        if (heat != heatUnits)
            heatUnits = heat;
    }

    @Override
    public int getHeatUnits(EnumFacing facing) {
        int exits = getExits().size();
        if (exits > 0)
            return this.heatUnits / exits;
        return this.heatUnits;
    }

    private List<EnumFacing> getExits() {
        List<EnumFacing> exits = new ArrayList<>();
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (facing != EnumFacing.DOWN) {
                TileEntity tile = getWorld().getTileEntity(pos.offset(facing));
                if (tile != null) {
                    if (tile.hasCapability(SteamCapability.STEAM_CAPABILITY, facing)) {
                        exits.add(facing);
                    }
                }
            }
        }
        return exits;
    }

    public FluidTank getFluidTankFromIndex(int index) {
        if (index == 1)
            return steam;
        return water;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        readSteamPower(tag);
    }

    @Override
    public void readSteamPower(NBTTagCompound tag) {
        if (tag.hasKey("Active"))
            isActive = tag.getBoolean("Active");
        if (tag.hasKey("Heat"))
            heatUnits = tag.getInteger("Heat");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagCompound t = super.writeToNBT(tag);
        writeSteamPower(t);
        return t;
    }

    @Override
    public NBTTagCompound writeSteamPower(NBTTagCompound tag) {
        tag.setBoolean("Active", isActive);
        tag.setInteger("Heat", heatUnits);
        return tag;
    }

    @Override
    public void setSteamUpdate(boolean update) {
    }

    @Override
    public boolean canTransferItem() {
        return false;
    }
}
