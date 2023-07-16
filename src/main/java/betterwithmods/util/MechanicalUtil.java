package betterwithmods.util;

import betterwithmods.api.capabilities.CapabilityAxle;
import betterwithmods.api.capabilities.CapabilityMechanicalPower;
import betterwithmods.api.tile.IAxle;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.api.util.IMechanicalUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class MechanicalUtil implements IMechanicalUtil {

    public boolean isRedstonePowered(World world, BlockPos pos) {
        return world.getRedstonePowerFromNeighbors(pos) > 0;
    }

    public IMechanicalPower getMechanicalPower(World world, BlockPos pos, EnumFacing facing) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile.hasCapability(CapabilityMechanicalPower.MECHANICAL_POWER, facing)) {
            return tile.getCapability(CapabilityMechanicalPower.MECHANICAL_POWER, facing);
        }
        return null;
    }

    public boolean canInput(World world, BlockPos pos, EnumFacing facing) {
        IMechanicalPower power = getMechanicalPower(world, pos, facing);
        return power != null && power.getMaximumInput(facing) > 0;
    }

    public boolean isAxle(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        IAxle axle = getAxle(world, pos, facing);
        return axle != null && axle.getAxis() == facing.getAxis();
    }

    public IAxle getAxle(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile.hasCapability(CapabilityAxle.AXLE, facing)) {
            return tile.getCapability(CapabilityAxle.AXLE, facing);
        }
        return null;
    }

    public int getPowerOutput(World world, BlockPos pos, EnumFacing facing) {
        IMechanicalPower power = getMechanicalPower(world, pos, facing);
        if (power != null) {
            return power.getMechanicalOutput(facing);
        }
        return 0;
    }

}
