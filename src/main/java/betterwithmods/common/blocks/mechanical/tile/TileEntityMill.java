package betterwithmods.common.blocks.mechanical.tile;

import betterwithmods.api.BWMAPI;
import betterwithmods.api.capabilities.CapabilityMechanicalPower;
import betterwithmods.api.tile.ICrankable;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.api.util.IProgressSource;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.blocks.mechanical.BlockMechMachines;
import betterwithmods.common.blocks.tile.TileBasicInventory;
import betterwithmods.util.DirUtils;
import betterwithmods.util.StackEjector;
import betterwithmods.util.VectorBuilder;
import com.google.common.collect.Lists;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class TileEntityMill extends TileBasicInventory implements ITickable, IMechanicalPower, ICrankable, IProgressSource {
    public boolean blocked;
    public int power;
    public int grindCounter;
    public int grindMax;

    private int increment;


    public TileEntityMill() {
        this.grindCounter = 0;
        this.increment = 1;
    }

    public int getIncrement() {
        return increment;
    }

    public void setIncrement(int increment) {
        this.increment = increment;
    }

    public boolean isActive() {
        return power > 0;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    public BlockMechMachines getBlock() {
        if (this.getBlockType() instanceof BlockMechMachines)
            return (BlockMechMachines) this.getBlockType();
        throw new IllegalStateException("This TileEntity does not have the correct block, something is severely wrong. Report to the mod author immediately");
    }

    private boolean findIfBlocked() {
        int count = 0;
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos offset = pos.offset(facing);
            IBlockState state = world.getBlockState(offset);
            Material material = state.getMaterial();
            if (world.isSideSolid(offset, facing.getOpposite()) || (!material.isReplaceable() && !material.isOpaque())) {
                count++;
            }
        }
        return count > 1;
    }

    public boolean isBlocked() {
        return blocked;
    }

    @Override
    public void update() {
        if (this.getBlockWorld().isRemote)
            return;

        this.power = calculateInput();
        this.blocked = findIfBlocked();
        getBlock().setActive(world, pos, isActive());

        if (isBlocked()) {
            return;
        }

        if (isActive()) {
            BWRegistry.MILLSTONE.craftRecipe(world, this, inventory);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("blocked"))
            this.blocked = tag.getBoolean("blocked");
        if (tag.hasKey("GrindCounter"))
            this.grindCounter = tag.getInteger("GrindCounter");
        if (tag.hasKey("GrindMax"))
            this.grindMax = tag.getInteger("GrindMax");
        if (tag.hasKey("increment"))
            this.increment = tag.getInteger("increment");
        this.power = tag.getInteger("power");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("GrindCounter", this.grindCounter);
        tag.setInteger("GrindMax", this.grindMax);
        tag.setInteger("power", power);
        tag.setInteger("increment", increment);
        tag.setBoolean("blocked", blocked);
        return tag;
    }

    @Override
    public int getInventorySize() {
        return 3;
    }

    private boolean canEject(EnumFacing facing) {
        if (world.isAirBlock(pos.offset(facing)))
            return true;
        return !world.isBlockFullCube(pos.offset(facing)) && !world.isSideSolid(pos.offset(facing), facing.getOpposite());
    }

    public static final StackEjector EJECTOR = new StackEjector(new VectorBuilder().rand(0.5f).offset(0.25f), new VectorBuilder().setGaussian(0.05f, 0, 0.05f));

    private void ejectStack(ItemStack stack) {
        List<EnumFacing> validDirections = Lists.newArrayList(EnumFacing.HORIZONTALS).stream().filter(this::canEject).collect(Collectors.toList());
        if (validDirections.isEmpty()) {
            blocked = true;
            return;
        }
        BlockPos offset = pos.offset(DirUtils.getRandomFacing(validDirections, getBlockWorld().rand));
        EJECTOR.setStack(stack).ejectStack(world, new Vec3d(offset), Vec3d.ZERO);
    }

    public void ejectRecipe(NonNullList<ItemStack> output) {
        if (!output.isEmpty()) {
            for (ItemStack anOutput : output) {
                ItemStack stack = anOutput.copy();
                if (!stack.isEmpty())
                    ejectStack(stack);
            }
        }
    }

    public boolean isGrinding() {
        return this.grindCounter > 0;
    }

    @Override
    public int getMechanicalOutput(EnumFacing facing) {
        return -1;
    }

    @Override
    public int getMechanicalInput(EnumFacing facing) {
        if (facing.getAxis().isVertical())
            return BWMAPI.IMPLEMENTATION.getPowerOutput(world, pos.offset(facing), facing.getOpposite());
        if (world.getTileEntity(pos.offset(facing)) instanceof TileCrank) {
            return BWMAPI.IMPLEMENTATION.getPowerOutput(world, pos.offset(facing), facing.getOpposite());
        }
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

    @Override
    public World getBlockWorld() {
        return super.getWorld();
    }

    @Override
    public BlockPos getBlockPos() {
        return getPos();
    }

    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.getBlockWorld().getTileEntity(this.pos) == this && player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public int getMax() {
        return grindMax;
    }

    @Override
    public int getProgress() {
        return this.grindCounter;
    }

}
