package betterwithmods.common.blocks.mechanical.tile;

import betterwithmods.BWMod;
import betterwithmods.api.BWMAPI;
import betterwithmods.api.capabilities.CapabilityMechanicalPower;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.api.tile.IRopeConnector;
import betterwithmods.api.util.IProgressSource;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.BlockRope;
import betterwithmods.common.blocks.mechanical.BlockMechMachines;
import betterwithmods.common.blocks.tile.SimpleStackHandler;
import betterwithmods.common.blocks.tile.TileEntityVisibleInventory;
import betterwithmods.common.entity.EntityExtendingRope;
import betterwithmods.common.registry.PulleyStructureManager;
import betterwithmods.module.GlobalConfig;
import betterwithmods.util.InvUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class TileEntityPulley extends TileEntityVisibleInventory implements IMechanicalPower, IProgressSource {

    private EntityExtendingRope rope;
    private NBTTagCompound ropeTag = null;
    private int power;


    public boolean isPowered() {
        return power > 0;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    public boolean isRaising() {
        return !BWMAPI.IMPLEMENTATION.isRedstonePowered(world, pos) && isPowered();
    }

    public boolean isLowering() {
        return !BWMAPI.IMPLEMENTATION.isRedstonePowered(world, pos) && !isPowered();
    }

    @Override
    public int getInventorySize() {
        return 4;
    }

    @Override
    public SimpleStackHandler createItemStackHandler() {
        return super.createItemStackHandler();
    }

    @Override
    public String getName() {
        return "inv.pulley.name";
    }

    @Override
    public int getMaxVisibleSlots() {
        return 4;
    }

    public boolean isUseableByPlayer(EntityPlayer player) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        return player.getDistanceSq(x + 0.5D, y + 0.5D, z + 0.5D) <= 64.0D;
    }

    @Override
    public void update() {
        if (this.getBlockWorld().isRemote)
            return;
        tryNextOperation();
    }

    private void tryNextOperation() {
        this.power = calculateInput();

        if (!activeOperation() && this.getBlockWorld().getBlockState(this.pos).getBlock() instanceof BlockMechMachines) {
            if (canGoDown(false)) {
                goDown();
            } else if (canGoUp()) {
                goUp();
            }
        }
    }

    private boolean validRopeConnector(BlockPos pos) {
        IBlockState state = getBlockWorld().getBlockState(pos);
        return state.getBlock() instanceof IRopeConnector && ((IRopeConnector) state.getBlock()).getFacing(state) == EnumFacing.UP;
    }

    private boolean canGoUp() {
        if (isRaising()) {
            if (putRope(false)) {
                BlockPos lowest = BlockRope.getLowestRopeBlock(getBlockWorld(), pos);
                if (!lowest.equals(pos)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canGoDown(boolean isMoving) {
        if (isLowering()) {
            if (takeRope(false)) {
                BlockPos newPos = BlockRope.getLowestRopeBlock(getBlockWorld(), pos).down();
                IBlockState state = getBlockWorld().getBlockState(newPos);
                boolean flag = !isMoving && validRopeConnector(newPos);
                if (newPos.getY() > 0 && (getBlockWorld().isAirBlock(newPos) || state.getBlock().isReplaceable(getBlockWorld(), newPos) || flag)
                        && newPos.up().getY() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private void goUp() {
        BlockPos lowest = BlockRope.getLowestRopeBlock(getBlockWorld(), pos);
        boolean flag = validRopeConnector(lowest.down());
        rope = new EntityExtendingRope(getBlockWorld(), pos, lowest, lowest.up().getY());
        if (!flag || movePlatform(lowest.down(), true)) {
            getBlockWorld().playSound(null, pos.down(), SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS,
                    0.4F + (getBlockWorld().rand.nextFloat() * 0.1F), 1.0F);
            getBlockWorld().spawnEntity(rope);
            getBlockWorld().setBlockToAir(lowest);
            putRope(true);
        } else {
            rope = null;
        }
    }

    private void goDown() {
        BlockPos newPos = BlockRope.getLowestRopeBlock(getBlockWorld(), pos).down();
        boolean flag = validRopeConnector(newPos);
        rope = new EntityExtendingRope(getBlockWorld(), pos, newPos.up(), newPos.getY());
        if (!flag || movePlatform(newPos, false)) {
            getBlockWorld().spawnEntity(rope);
        } else {
            rope = null;
        }
    }

    /**
     * Turns the platform into entities and moves them with the rope
     */

    private boolean movePlatform(BlockPos anchor, boolean up) {
        IBlockState state = getBlockWorld().getBlockState(anchor);
        if (!(state.getBlock() instanceof IRopeConnector))
            return false;

        HashSet<BlockPos> platformBlocks = new HashSet<>();
        platformBlocks.add(anchor);
        boolean success;
        BlockPos below = anchor.down();
        if (isPlatform(below) && ((IRopeConnector) state.getBlock()).canMovePlatforms(world, anchor)) {
            success = addToList(platformBlocks, below, up);
        } else {
            success = up || isIgnoreable(below);
        }
        if (!success) {
            return false;
        }

        for (BlockPos blockPos : platformBlocks) {
            Arrays.asList(new BlockPos[]{blockPos.north(), blockPos.south()}).forEach(p -> {
                if (!platformBlocks.contains(p)) {
                    fixRail(p, EnumRailDirection.ASCENDING_NORTH, EnumRailDirection.ASCENDING_SOUTH);
                }
            });
            Arrays.asList(new BlockPos[]{blockPos.east(), blockPos.west()}).forEach(p -> {
                if (!platformBlocks.contains(p)) {
                    fixRail(p, EnumRailDirection.ASCENDING_EAST, EnumRailDirection.ASCENDING_WEST);
                }
            });
        }

        if (!getBlockWorld().isRemote) {
            for (BlockPos blockPos : platformBlocks) {
                Vec3i offset = blockPos.subtract(anchor.up());
                rope.addBlock(offset, getBlockWorld(), blockPos);
                if (isMoveableBlock(blockPos.up())) {
                    rope.addBlock(new Vec3i(offset.getX(), offset.getY() + 1, offset.getZ()), getBlockWorld(), blockPos.up());
                    getBlockWorld().setBlockToAir(blockPos.up());
                }
                getBlockWorld().setBlockToAir(blockPos);
            }
        }

        return true;
    }

    public boolean isIgnoreable(BlockPos pos) {
        return world.isAirBlock(pos) || world.getBlockState(pos).getMaterial().isReplaceable();
    }

    public boolean isMoveableBlock(BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() == Blocks.REDSTONE_WIRE || state.getBlock() instanceof BlockRailBase;
    }

    public boolean isPlatform(BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return PulleyStructureManager.isPulleyBlock(state);
    }

    @SuppressWarnings("unchecked")
    private void fixRail(BlockPos rail, EnumRailDirection... directions) {
        List<EnumRailDirection> list = Arrays.asList(directions);
        IBlockState state = getBlockWorld().getBlockState(rail);
        if (getBlockWorld().getBlockState(rail).getBlock() instanceof BlockRailBase) {
            PropertyEnum<EnumRailDirection> shape = null;
            for (IProperty<?> p : state.getPropertyKeys()) {
                if ("shape".equals(p.getName()) && p instanceof PropertyEnum<?>) {
                    shape = (PropertyEnum<EnumRailDirection>) p;
                    break;
                }
            }

            if (shape != null) {
                EnumRailDirection currentShape = state.getValue(shape);
                if (list.contains(currentShape)) {
                    getBlockWorld().setBlockState(rail, state.withProperty(shape, flatten(currentShape)), 6);
                }
            } else {
                BWMod.logger.warn(String.format("Rail at %s has no shape?", rail));
            }
        }
    }

    private EnumRailDirection flatten(EnumRailDirection old) {
        switch (old) {
            case ASCENDING_EAST:
            case ASCENDING_WEST:
                return EnumRailDirection.EAST_WEST;
            case ASCENDING_NORTH:
            case ASCENDING_SOUTH:
                return EnumRailDirection.NORTH_SOUTH;
            default:
                return old;
        }
    }

    private boolean addToList(HashSet<BlockPos> set, BlockPos p, boolean up) {
        if (set.size() > GlobalConfig.maxPlatformBlocks)
            return false;
        if (!isPlatform(p)) {
            return true;
        }

        BlockPos blockCheck = up ? p.up() : p.down();
        if (!(isIgnoreable(blockCheck) || isMoveableBlock(blockCheck) || isPlatform(blockCheck)) && !set.contains(blockCheck))
            return false;

        set.add(p);

        List<BlockPos> fails = new ArrayList<>();

        Arrays.asList(p.up(), p.down(), p.north(), p.south(), p.east(), p.west()).forEach(q -> {
            if (fails.isEmpty() && !set.contains(q)) {
                if (!addToList(set, q, up))
                    fails.add(q);
            }
        });

        return fails.isEmpty();
    }

    private boolean activeOperation() {
        return rope != null && rope.isEntityAlive();
    }

    private boolean takeRope(boolean flag) {
        return InvUtils.consumeItemsInInventory(inventory, new ItemStack(BWMBlocks.ROPE), 1, !flag);
    }

    private boolean putRope(boolean flag) {
        return InvUtils.insert(inventory, new ItemStack(BWMBlocks.ROPE, 1), !flag).isEmpty();
    }

    public boolean onJobCompleted(boolean up, int targetY, EntityExtendingRope theRope) {
        BlockPos ropePos = new BlockPos(pos.getX(), targetY - (up ? 1 : 0), pos.getZ());
        IBlockState state = getBlockWorld().getBlockState(ropePos);
        if (!up) {
            if ((getBlockWorld().isAirBlock(ropePos) || state.getBlock().isReplaceable(getBlockWorld(), ropePos)) && BWMBlocks.ROPE.canPlaceBlockAt(getBlockWorld(), ropePos) && takeRope(true)) {
                getBlockWorld().playSound(null, pos.down(), SoundEvents.BLOCK_GRASS_PLACE, SoundCategory.BLOCKS, 0.4F, 1.0F);
                getBlockWorld().setBlockState(ropePos, BWMBlocks.ROPE.getDefaultState());
            } else {
                tryNextOperation();
                theRope.setDead();
                return false;
            }
        }
        if ((theRope.getUp() ? canGoUp() : canGoDown(true)) && !theRope.isPathBlocked()) {
            theRope.setTargetY(targetY + (theRope.getUp() ? 1 : -1));
            if (up) {
                if (!getBlockWorld().isAirBlock(ropePos.up())) {
                    getBlockWorld().playSound(null, pos.down(), SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS,
                            0.4F + (getBlockWorld().rand.nextFloat() * 0.1F), 1.0F);
                    getBlockWorld().setBlockToAir(ropePos.up());
                    putRope(true);
                }
            }
            return true;
        } else {
            tryNextOperation();
            theRope.setDead();
            return false;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagCompound ropetag = new NBTTagCompound();
        if (rope != null)
            rope.writeToNBTAtomically(ropetag);
        tag.setTag("Rope", ropetag);

        tag.setInteger("power", power);
        return super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.ropeTag = (NBTTagCompound) tag.getTag("Rope");
        this.power = tag.getInteger("power");
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (rope == null && !getWorld().isRemote && ropeTag != null && !ropeTag.isEmpty()) {
            NBTTagList pos = (NBTTagList) ropeTag.getTag("Pos");
            if (pos != null) {
                rope = (EntityExtendingRope) AnvilChunkLoader.readWorldEntityPos(ropeTag, getBlockWorld(), pos.getDoubleAt(0),
                        pos.getDoubleAt(1), pos.getDoubleAt(2), true);
            }
        }
    }

    @Override
    public int getMechanicalOutput(EnumFacing facing) {
        return -1;
    }

    @Override
    public int getMechanicalInput(EnumFacing facing) {
        if (facing.getAxis().isHorizontal())
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

    @Override
    public int getMax() {
        return 1;
    }

    @Override
    public int getProgress() {
        return Math.min(power, 1);
    }


}
