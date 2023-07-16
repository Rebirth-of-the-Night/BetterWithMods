package betterwithmods.common.blocks.mechanical.tile;

import betterwithmods.api.IColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.DimensionType;

public abstract class TileEntityBaseWindmill extends TileAxleGenerator implements IColor {
    protected int[] bladeMeta;

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        for (int i = 0; i < bladeMeta.length; i++) {
            if (tag.hasKey("Color_" + i))
                bladeMeta[i] = tag.getInteger("Color_" + i);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagCompound t = super.writeToNBT(tag);
        for (int i = 0; i < bladeMeta.length; i++) {
            t.setInteger("Color_" + i, bladeMeta[i]);
        }
        t.setByte("DyeIndex", this.dyeIndex);
        return t;
    }

    @Override
    public boolean dye(EnumDyeColor color) {
        boolean dyed = false;
        if (bladeMeta[dyeIndex] != color.getMetadata()) {
            bladeMeta[dyeIndex] = color.getMetadata();
            dyed = true;
            IBlockState state = getBlockWorld().getBlockState(this.pos);
            this.getBlockWorld().notifyBlockUpdate(this.pos, state, state, 2);
            this.markDirty();
        }
        dyeIndex++;
        if (dyeIndex > (bladeMeta.length - 1))
            dyeIndex = 0;
        return dyed;
    }


    public int getBladeColor(int blade) {
        return bladeMeta[blade];
    }

    @Override
    public int getColor(int index) {
        return bladeMeta[index];
    }

    @Override
    public void calculatePower() {
        byte power = 0;
        if (isValid()) {
            if (world.provider.getDimensionType() == DimensionType.OVERWORLD) {
                if (world.isThundering())
                    power = 3;
                else if (world.isRaining())
                    power = 2;
                else
                    power = 1;
            } else {
                power = 1;
            }
        }

        if (power != this.power) {
            setPower(power);
        }
    }


    @Override
    public int getMinimumInput(EnumFacing facing) {
        return 0;
    }

}
