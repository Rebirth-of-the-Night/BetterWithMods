package betterwithmods.common.blocks.tile;

import betterwithmods.api.capabilities.SteamCapability;
import betterwithmods.api.tile.ISteamPower;
import betterwithmods.common.blocks.mechanical.tile.TileEntityFilteredHopper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TileEntitySteamPipe extends TileEntity implements ITickable, ISteamPower {
    private int heatUnits = 0;
    private int steamPower = 0;
    private boolean update = false;
    private Random rand = new Random();

    @Override
    public void update() {
        if (update) {
            calculateSteamPower(null);
            update = false;
        }
        List<EnumFacing> low = findLowestTransfer(false);
        if (!low.isEmpty()) {
            int exits = low.size();
            EnumFacing facing = low.get(rand.nextInt(exits));
            TileEntity tile = getWorld().getTileEntity(pos.offset(facing));
            if (tile != null) {
                if (tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing)) {
                    //Insert item transfer code here.
                }
            }
        }
    }

    private List<EnumFacing> findLowestTransfer(boolean heat) {
        List<EnumFacing> dirs = new ArrayList<>();
        if (!heat) {
            for (EnumFacing facing : EnumFacing.VALUES) {
                TileEntity tile = getWorld().getTileEntity(pos.offset(facing));
                if (tile != null) {
                    if (isPipelineExit(tile, facing)) {
                        dirs.add(facing);
                    } else if (tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing) && tile.hasCapability(SteamCapability.STEAM_CAPABILITY, facing)) {
                        if (tile.getCapability(SteamCapability.STEAM_CAPABILITY, facing).canTransferItem()) {
                            if (facing == EnumFacing.DOWN) {
                                if (tile.getCapability(SteamCapability.STEAM_CAPABILITY, facing).getSteamPower(facing.getOpposite()) == 0)
                                    dirs.add(facing);
                            } else if (tile.getCapability(SteamCapability.STEAM_CAPABILITY, facing).getSteamPower(facing.getOpposite()) < steamPower)
                                dirs.add(facing);
                        }
                    }
                }
            }
        } else {
            for (EnumFacing facing : EnumFacing.VALUES) {
                TileEntity tile = getWorld().getTileEntity(pos.offset(facing));
                if (tile != null) {
                    if (tile.hasCapability(SteamCapability.STEAM_CAPABILITY, facing)) {
                        if (facing != EnumFacing.DOWN && tile.getCapability(SteamCapability.STEAM_CAPABILITY, facing).getSteamPower(facing.getOpposite()) < steamPower) {
                            dirs.add(facing);
                        }
                    }
                }
            }
        }
        return dirs;
    }

    @Override
    public void calculateSteamPower(EnumFacing facing) {
        int currentPower = steamPower;
        int highestNeighbor = 0;
        for (EnumFacing side : EnumFacing.VALUES) {
            TileEntity tile = getWorld().getTileEntity(pos.offset(side));
            if (side == EnumFacing.UP)
                continue;
            if (tile != null) {
                if (tile.hasCapability(SteamCapability.STEAM_CAPABILITY, side)) {
                    if (tile.getCapability(SteamCapability.STEAM_CAPABILITY, side).getSteamPower(side.getOpposite()) > highestNeighbor)
                        highestNeighbor = tile.getCapability(SteamCapability.STEAM_CAPABILITY, side).getSteamPower(side.getOpposite());
                }
            }
        }

        if (highestNeighbor > currentPower) {
            currentPower = highestNeighbor - 1;
        } else if (currentPower > 0) {
            --currentPower;
        } else {
            currentPower = 0;
        }

        if (steamPower != currentPower) {
            steamPower = currentPower;

            for (EnumFacing side : EnumFacing.VALUES) {
                TileEntity tile = getWorld().getTileEntity(pos.offset(side));
                if (tile != null) {
                    if (tile.hasCapability(SteamCapability.STEAM_CAPABILITY, side)) {
                        tile.getCapability(SteamCapability.STEAM_CAPABILITY, side).setSteamUpdate(true);
                    }
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        readSteamPower(tag);
    }

    @Override
    public void readSteamPower(NBTTagCompound tag) {
        if (tag.hasKey("Steam"))
            this.steamPower = tag.getInteger("Steam");
        if (tag.hasKey("Heat"))
            this.heatUnits = tag.getInteger("Heat");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagCompound t = super.writeToNBT(tag);
        writeSteamPower(t);
        return t;
    }

    @Override
    public NBTTagCompound writeSteamPower(NBTTagCompound tag) {
        tag.setInteger("Steam", this.steamPower);
        tag.setInteger("Heat", this.heatUnits);
        return tag;
    }

    @Override
    public int getHeatUnits(EnumFacing facing) {
        int transfer = findLowestTransfer(true).size();
        if (transfer > 0)
            return heatUnits / transfer;
        return heatUnits;
    }

    @Override
    public int getSteamPower(EnumFacing facing) {
        return steamPower;
    }

    @Override
    public void calculateHeatUnits() {
        List<EnumFacing> low = findLowestTransfer(true);
        heatUnits = 0;
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (!low.contains(facing)) {
                TileEntity tile = getWorld().getTileEntity(pos.offset(facing));
                if (tile != null) {
                    if (tile.hasCapability(SteamCapability.STEAM_CAPABILITY, facing)) {
                        heatUnits = tile.getCapability(SteamCapability.STEAM_CAPABILITY, facing).getHeatUnits(facing.getOpposite());
                    }
                }
            }
        }
    }

    @Override
    public void setSteamUpdate(boolean update) {
        if (this.update != update)
            this.update = update;
    }

    private boolean isPipelineExit(TileEntity tile, EnumFacing facing) {
        return tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing) && !tile.hasCapability(SteamCapability.STEAM_CAPABILITY, facing) && !(tile instanceof TileEntityHopper) && !(tile instanceof TileEntityFilteredHopper);
    }

    @Override
    public boolean canTransferItem() {
        return true;
    }
}
