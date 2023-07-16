package betterwithmods.common.blocks.mechanical.tile;

import betterwithmods.api.BWMAPI;
import betterwithmods.api.capabilities.CapabilityMechanicalPower;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.blocks.tile.IMechSubtype;
import betterwithmods.common.blocks.tile.TileBasic;
import betterwithmods.common.registry.TurntableRotationManager;
import betterwithmods.common.registry.block.recipe.TurntableRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;

public class TileEntityTurntable extends TileBasic implements IMechSubtype, ITickable, IMechanicalPower {

    private static final int height = 3;
    private static final int[] ticksToRotate = {10, 20, 40, 80};
    public byte timerPos = 0;
    private int potteryRotation = 0;
    private double[] offsets = {0.25D, 0.375D, 0.5D, 0.625D};
    private boolean asynchronous = false;
    private int rotationTime = 0;
    private int power;

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("SwitchSetting")) {
            this.timerPos = tag.getByte("SwitchSetting");
            if (this.timerPos > 3)
                this.timerPos = 3;
        }
        if (tag.hasKey("PotteryRotation"))
            this.potteryRotation = tag.getInteger("PotteryRotation");
        if (tag.hasKey("Asynchronous"))
            this.asynchronous = tag.getBoolean("Asynchronous");
        if (tag.hasKey("RotationTime"))
            this.rotationTime = tag.getInteger("RotationTime");
        this.power = tag.getInteger("power");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("PotteryRotation", this.potteryRotation);
        tag.setByte("SwitchSetting", this.timerPos);
        tag.setBoolean("Asynchronous", this.asynchronous);
        if (this.asynchronous || this.rotationTime != 0)
            tag.setInteger("RotationTime", this.rotationTime);
        tag.setInteger("power", power);
        return tag;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public void update() {
        if (!this.getBlockWorld().isRemote) {
            this.power = calculateInput();
            if (power > 0) {
                if (!asynchronous && getBlockWorld().getTotalWorldTime() % (long) ticksToRotate[timerPos] == 0) {
                    this.getBlockWorld().playSound(null, pos, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.05F, 1.0F);
                    rotateTurntable();
                } else if (asynchronous) {
                    rotationTime++;
                    if (rotationTime >= ticksToRotate[timerPos]) {
                        rotationTime = 0;
                        this.getBlockWorld().playSound(null, pos, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.05F, 1.0F);
                        rotateTurntable();
                    }
                }
            }
        }
    }

    public boolean processRightClick(EntityPlayer player) {
        if (!player.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
            if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.CLOCK) {
                toggleAsynchronous(player);
                return true;
            }
        } else if (player.getHeldItemMainhand().isEmpty()) {
            advanceTimerPos();
            getBlockWorld().scheduleBlockUpdate(pos, this.getBlockType(), this.getBlockType().tickRate(getBlockWorld()), 5);
            getBlockWorld().playSound(null, pos, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
            return true;
        }
        return false;
    }

    public void toggleAsynchronous(EntityPlayer player) {
        if (!this.getBlockWorld().getGameRules().getBoolean("doDaylightCycle")) {
            if (!asynchronous) {
                this.asynchronous = true;
            } else if (player != null) {
                player.sendStatusMessage(new TextComponentTranslation("message.bwm:async.unavailable"), false);
            }
        } else {
            boolean isSneaking = player.isSneaking();
            String isOn = "enabled";
            boolean async = !this.asynchronous;
            if ((!async && !isSneaking) || (async && isSneaking))
                isOn = "disabled";
            player.sendStatusMessage(new TextComponentTranslation("message.bwm:async." + isOn), false);
            if (!isSneaking) {
                this.getBlockWorld().playSound(null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.05F, 1.0F);
                this.asynchronous = async;
            }
        }
    }

    public void rotateTurntable() {
        Rotation rotation = BWMAPI.IMPLEMENTATION.isRedstonePowered(world, pos) ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(getPos());

        for (int i = 1; i < height; i++) {
            pos.setY(pos.getY() + 1);

            IBlockState input = getBlockWorld().getBlockState(pos);
            TurntableRecipe recipe = rotateCraftable(world, pos, input);

            TurntableRotationManager.IRotation handler = rotateBlock(pos, rotation);
            if (handler == null)
                break;
            if (handler.canTransmitHorizontally(world, pos))
                TurntableRotationManager.rotateAttachments(world, pos, rotation);
            if (!handler.canTransmitVertically(world, pos,recipe))
                break;
        }

        getBlockWorld().neighborChanged(pos, BWMBlocks.SINGLE_MACHINES, pos);
    }

    public byte getTimerPos() {
        return timerPos;
    }

    public double getOffset() {
        return offsets[this.timerPos];
    }

    public void advanceTimerPos() {
        timerPos++;
        if (timerPos > 3)
            timerPos = 0;
        IBlockState state = getBlockWorld().getBlockState(pos);
        getBlockWorld().notifyBlockUpdate(pos, state, state, 3);
    }

    private TurntableRotationManager.IRotation rotateBlock(BlockPos pos, Rotation rotation) {
        return TurntableRotationManager.rotate(world, pos, rotation);
    }

    private void spawnParticlesAndSound(IBlockState state) {
        if (state.getMaterial() != Material.AIR) {
            world.playSound(null, pos, state.getBlock().getSoundType(state, world, pos, null).getPlaceSound(), SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.8F);
            ((WorldServer) this.world).spawnParticle(EnumParticleTypes.BLOCK_DUST, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 30, 0.0D, 0.5D, 0.0D, 0.15000000596046448D, Block.getStateId(state));
        }
    }

    private TurntableRecipe rotateCraftable(World world, BlockPos pos, IBlockState input) {
        TurntableRecipe recipe = BWRegistry.TURNTABLE.findRecipe(world, pos, input).orElse(null);
        if (recipe != null) {
            this.potteryRotation++;
            spawnParticlesAndSound(input);
            if (recipe.craftRecipe(world, pos, world.rand, input))
                this.potteryRotation = 0;
        } else {
            this.potteryRotation = 0;
        }
        return recipe;
    }

    @Override
    public int getSubtype() {
        return this.timerPos + 8;
    }

    @Override
    public void setSubtype(int type) {
        this.timerPos = (byte) Math.min(type, 3);
    }


    @Override
    public int getMechanicalOutput(EnumFacing facing) {
        return -1;
    }

    @Override
    public int getMechanicalInput(EnumFacing facing) {
        if (facing == EnumFacing.DOWN)
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
        return super.getPos();
    }

    @Override
    public Block getBlock() {
        return getBlockType();
    }

    public int getPotteryRotation() {
        return potteryRotation;
    }
}
