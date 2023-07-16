package betterwithmods.common.blocks.mechanical.tile;

import betterwithmods.api.BWMAPI;
import betterwithmods.api.capabilities.CapabilityMechanicalPower;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.BWSounds;
import betterwithmods.common.blocks.mechanical.BlockSaw;
import betterwithmods.common.blocks.tile.TileBasic;
import betterwithmods.common.registry.block.recipe.SawRecipe;
import betterwithmods.util.WorldUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Created by primetoxinz on 7/24/17.
 */
public class TileSaw extends TileBasic implements IMechanicalPower {
    private int power;


    public void cut(World world, BlockPos pos, Random rand) {
        if (!(getBlockType() instanceof BlockSaw))
            return;
        EnumFacing facing = getBlock().getFacing(world, pos);
        final BlockPos blockPos = pos.offset(facing);
        final IBlockState state = world.getBlockState(blockPos);
        if(world.isAirBlock(blockPos))
            return;
        SawRecipe recipe = BWRegistry.WOOD_SAW.findRecipe(world, blockPos, state).orElse(null);
        boolean tryScreech = false;

        if (recipe != null) {
            if (!recipe.craftRecipe(world, blockPos, rand, state)) {
                tryScreech = true;
            }
        } else {
            tryScreech = true;
        }

        if (tryScreech && !getBlock().isChoppingBlock(state) && WorldUtils.isSolid(world, blockPos, facing, state)) {
            world.playSound(null, blockPos, BWSounds.METAL_HACKSAW, SoundCategory.BLOCKS, 1.0f, 0.80f);
        }
    }

    public void onChanged() {
        int power = calculateInput();
        if (this.power != power) {
            this.power = power;
            getBlock().setActive(world, pos, power > 0);
        }
    }

    @Override
    public boolean overpowerChance() {
        return true;
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
        if (facing != getBlock().getFacing(world, pos))
            return BWMAPI.IMPLEMENTATION.getPowerOutput(world, pos.offset(facing), facing.getOpposite());
        return 0;
    }

    @Override
    public int getMaximumInput(EnumFacing facing) {
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

    public BlockSaw getBlock() {
        if (getBlockType() instanceof BlockSaw)
            return (BlockSaw) getBlockType();
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
