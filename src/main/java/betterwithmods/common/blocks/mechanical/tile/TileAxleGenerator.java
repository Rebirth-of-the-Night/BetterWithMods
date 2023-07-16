package betterwithmods.common.blocks.mechanical.tile;

import betterwithmods.api.capabilities.CapabilityMechanicalPower;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.common.BWSounds;
import betterwithmods.common.blocks.mechanical.BlockAxleGenerator;
import betterwithmods.common.blocks.mechanical.IBlockActive;
import betterwithmods.common.blocks.tile.TileBasic;
import betterwithmods.module.gameplay.Gameplay;
import betterwithmods.util.DirUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileAxleGenerator extends TileBasic implements ITickable, IMechanicalPower {
    //Every generator will take up a single block with no extended bounding box
    protected byte power = 0;
    public byte dyeIndex = 0;
    protected float runningSpeed = 0.4F;
    public float currentRotation = 0.0F;
    public float previousRotation = 0.0F;
    public float waterMod = 1;
    protected boolean isValid;

    protected int tick;

    public abstract void calculatePower();

    public abstract void verifyIntegrity();

    public abstract int getRadius();

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("CurrentRotation"))
            currentRotation = tag.getFloat("CurrentRotation");
        if (tag.hasKey("RotationSpeed"))
            previousRotation = tag.getFloat("RotationSpeed");
        if (tag.hasKey("power"))
            power = tag.getByte("power");
        if (tag.hasKey("DyeIndex"))
            dyeIndex = tag.getByte("DyeIndex");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagCompound t = super.writeToNBT(tag);
        t.setByte("power", power);
        t.setFloat("CurrentRotation", currentRotation);
        t.setFloat("RotationSpeed", previousRotation);
        return t;
    }

    public float getCurrentRotation() {
        return this.currentRotation;
    }

    public float getPrevRotation() {
        return this.previousRotation;
    }

    @Override
    public void update() {
        tick++;
        if (tick % 20 == 0 && getBlockWorld().getBlockState(pos).getBlock() instanceof BlockAxleGenerator) {
            verifyIntegrity();
            calculatePower();
        }

        if (isValid()) {
            if (power != 0) {
                this.previousRotation = this.power * runningSpeed * waterMod;
                this.currentRotation += (this.power * this.power) * runningSpeed * waterMod;
                this.currentRotation %= 360;
                if (this.getBlockWorld().rand.nextInt(100) == 0)
                    this.getBlockWorld().playSound(null, pos, BWSounds.WOODCREAK, SoundCategory.BLOCKS, 0.5F, getBlockWorld().rand.nextFloat() * 0.25F + 0.25F);
            } else {
                previousRotation = 0;
                currentRotation = 0;
            }
        }
    }

    public boolean isOverworld() {
        return world.provider.isSurfaceWorld();
    }

    public boolean isEnd() {
        return world.provider.getDimensionType() == DimensionType.THE_END;
    }

    public boolean isNether() {
        return world.provider.isNether();
    }

    public void setPower(byte power) {
        this.power = power;
        world.setBlockState(pos, world.getBlockState(pos).withProperty(IBlockActive.ACTIVE, power > 0));
    }

    public boolean isValid() {
        return isValid;
    }


    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    //Unless you increase this, expect to see the TESR to pop in as you approach.
    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
       return Gameplay.generatorRenderDistance * Gameplay.generatorRenderDistance;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityMechanicalPower.MECHANICAL_POWER || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityMechanicalPower.MECHANICAL_POWER) {
            return CapabilityMechanicalPower.MECHANICAL_POWER.cast(this);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public int getMechanicalOutput(EnumFacing facing) {
        return power;
    }

    @Override
    public int getMechanicalInput(EnumFacing facing) {
        return 0;
    }

    @Override
    public int getMaximumInput(EnumFacing facing) {
        return 0;
    }

    public EnumFacing getOrientation() {
        if (world.getBlockState(pos).getBlock() instanceof BlockAxleGenerator) {
            EnumFacing.Axis axis = world.getBlockState(pos).getValue(DirUtils.AXIS);
            switch (axis) {
                case X:
                    return EnumFacing.EAST;
                case Z:
                    return EnumFacing.SOUTH;
                default:
                    return EnumFacing.UP;
            }
        } else
            return EnumFacing.UP;
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


}
