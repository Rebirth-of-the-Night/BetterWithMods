package betterwithmods.common.blocks.mechanical.tile;

import betterwithmods.api.BWMAPI;
import betterwithmods.api.capabilities.CapabilityMechanicalPower;
import betterwithmods.api.tile.ICrankable;
import betterwithmods.api.tile.IHeated;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.api.util.IProgressSource;
import betterwithmods.common.blocks.mechanical.BlockCookingPot;
import betterwithmods.common.blocks.tile.TileEntityVisibleInventory;
import betterwithmods.common.registry.bulk.manager.CraftingManagerBulk;
import betterwithmods.common.registry.bulk.recipes.CookingPotRecipe;
import betterwithmods.common.registry.heat.BWMHeatRegistry;
import betterwithmods.util.DirUtils;
import betterwithmods.util.InvUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import java.util.HashMap;
import java.util.List;
import java.util.Random;


public abstract class TileEntityCookingPot extends TileEntityVisibleInventory implements IMechanicalPower, IHeated, ICrankable, IProgressSource {
    private static final int MAX_TIME = 1000;
    public int cookProgress, cookTime;
    public EnumFacing facing;
    public int heat;
    protected CraftingManagerBulk<CookingPotRecipe> manager;

    public TileEntityCookingPot(CraftingManagerBulk<CookingPotRecipe> manager) {
        this.manager = manager;
        this.cookProgress = 0;
        this.cookTime = 0;
        this.occupiedSlots = 0;
        this.facing = EnumFacing.UP;
        this.hasCapability = f -> f == facing;
    }

    @Override
    public boolean hasFastRenderer() {
        return false;
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

    private boolean isInputtingPower(EnumFacing facing) {
        return this.getMechanicalInput(facing) > 0;
    }

    private EnumFacing getPoweredSide() {
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            if (isInputtingPower(facing))
                return facing;
        }
        return EnumFacing.UP;
    }

    private boolean isPowered() {
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            if (isInputtingPower(facing))
                return true;
        }
        return false;
    }

    @Override
    public int getInventorySize() {
        return 27;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.facing = EnumFacing.byIndex(value(tag, "facing", EnumFacing.UP.getIndex()));
        this.cookProgress = value(tag, "progress", 0);
        this.cookTime = value(tag, "time", 4000);
        this.heat = value(tag, "heat", 0);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagCompound t = super.writeToNBT(tag);
        t.setInteger("facing", facing.getIndex());
        t.setInteger("progress", this.cookProgress);
        t.setInteger("heat", this.heat);
        t.setInteger("time", this.cookTime);
        return t;
    }

    @Override
    public void update() {

        if (getBlock() instanceof BlockCookingPot) {
            IBlockState state = this.getBlockWorld().getBlockState(this.pos);
            if (isPowered()) {
                this.cookProgress = 0;
                this.facing = getPoweredSide();

                ejectInventory(DirUtils.rotateFacingAroundY(this.facing, false));
            } else {
                if (this.facing != EnumFacing.UP)
                    this.facing = EnumFacing.UP;

                spawnParticles();

                entityCollision();

                //Only do crafting on the server
                if(!world.isRemote && !InvUtils.isEmpty(inventory)) {
                    int heat = findHeat(getPos());
                    if (this.heat != heat) {
                        this.heat = heat;
                        this.cookProgress = 0;
                    }
                    int time = findCookTime();
                    if (this.cookTime != time) {
                        this.cookTime = time;
                    }
                    manager.craftRecipe(world, this, inventory);
                }
            }
            if (facing != state.getValue(DirUtils.TILTING)) {
                world.setBlockState(pos, state.withProperty(DirUtils.TILTING, facing));
            }
        }
    }

    private int findCookTime() {
        int divisor = -heat;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                divisor += findHeat(pos.add(x, 0, z));
            }
        }
        if (divisor != 0)
            return MAX_TIME / divisor;
        return MAX_TIME;
    }


    @Override
    public int getHeat(World world, BlockPos pos) {
        return heat;
    }

    private int findHeat(BlockPos pos) {
        return getHeatCached(pos.down());
    }

    private HashMap<BlockPos, BWMHeatRegistry.HeatSource> heatCache = new HashMap<>();
    private int getHeatCached(BlockPos pos){
        BWMHeatRegistry.HeatSource src = heatCache.get(pos);
        if (src != null && src.matches(world, pos)){
            return src.getHeat();
        } else if (src!=null){
            heatCache.remove(pos);
        }
        src = BWMHeatRegistry.get(world, pos);
        if (src != null){
            heatCache.put(pos, src);
            return src.getHeat();
        }
        return 0;
    }

    private void spawnParticles() {
        Random random = this.getBlockWorld().rand;
        if (heat >= BWMHeatRegistry.STOKED_HEAT && random.nextDouble() < 0.2) {
            double xOffset = 0.25 + random.nextDouble() * 0.5;
            double zOffset = 0.25 + random.nextDouble() * 0.5;
            this.getBlockWorld().spawnParticle(EnumParticleTypes.CLOUD, pos.getX() + xOffset, pos.getY() + 0.75F, pos.getZ() + zOffset, 0, 0.05 + random.nextDouble() * 0.05, 0);
        }
    }

    //TODO move this to Block#onEntityCollision by lowering the bounding box a bit, like the filtered hopper
    private void entityCollision() {
        if (captureDroppedItems()) {
            getBlockWorld().scheduleBlockUpdate(pos, this.getBlockType(), this.getBlockType().tickRate(getBlockWorld()), 5);
            this.markDirty();
        }
    }


    public List<EntityItem> getCaptureItems(World worldIn, BlockPos pos) {
        return worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1D, pos.getY() + 1.5D, pos.getZ() + 1D), EntitySelectors.IS_ALIVE);
    }

    private boolean captureDroppedItems() {
        boolean insert = false;
        if (!InvUtils.isFull(inventory)) {
            List<EntityItem> items = this.getCaptureItems(getBlockWorld(), getPos());
            for (EntityItem item : items)
                insert |= InvUtils.insertFromWorld(inventory, item, 0, 27, false);
        }
        if (insert) {
            this.getBlockWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((getBlockWorld().rand.nextFloat() - getBlockWorld().rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            return true;
        }
        return false;
    }

    public void ejectInventory(EnumFacing facing) {
        int index = InvUtils.getFirstOccupiedStackNotOfItem(inventory, Items.BRICK);
        if (index >= 0 && index < inventory.getSlots()) {
            ItemStack stack = inventory.getStackInSlot(index);
            int ejectStackSize = 8;
            if (8 > stack.getCount()) {
                ejectStackSize = stack.getCount();
            }
            BlockPos target = pos.offset(facing);
            ItemStack eject = new ItemStack(stack.getItem(), ejectStackSize, stack.getItemDamage());
            InvUtils.copyTags(eject, stack);
            IBlockState targetState = getBlockWorld().getBlockState(target);
            boolean ejectIntoWorld = getBlockWorld().isAirBlock(target) ||
                    targetState.getBlock().isReplaceable(getBlockWorld(), target) ||
                    !targetState.getMaterial().isSolid() ||
                    targetState.getBoundingBox(getBlockWorld(), target).maxY < 0.5d;
            if (ejectIntoWorld) {
                this.getBlockWorld().playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2F, ((getBlockWorld().rand.nextFloat() - getBlockWorld().rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                ejectStack(getBlockWorld(), target, facing, eject);
                inventory.extractItem(index, ejectStackSize, false);
            }
        }
    }

    public void ejectStack(World world, BlockPos pos, EnumFacing facing, ItemStack stack) {
        if (world.isRemote)
            return;
        Vec3i vec = new BlockPos(0, 0, 0).offset(facing);
        EntityItem item = new EntityItem(world, pos.getX() + 0.5F - (vec.getX() / 4d), pos.getY() + 0.25D, pos.getZ() + 0.5D - (vec.getZ() / 4d), stack);
        float velocity = 0.05F;
        item.motionX = (double) (vec.getX() * velocity);
        item.motionY = vec.getY() * velocity * 0.1;
        item.motionZ = (double) (vec.getZ() * velocity);
        item.setDefaultPickupDelay();
        world.spawnEntity(item);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.getBlockWorld() != null) {
            IBlockState state = getBlockWorld().getBlockState(pos);
            getBlockWorld().notifyBlockUpdate(pos, state, state, 3);
        }
    }

    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.getBlockWorld().getTileEntity(this.pos) == this && player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public int getMaxVisibleSlots() {
        return 27;
    }

    @Override
    public int getMechanicalOutput(EnumFacing facing) {
        return 0;
    }

    @Override
    public int getMechanicalInput(EnumFacing facing) {
        return BWMAPI.IMPLEMENTATION.getPowerOutput(world, pos.offset(facing), facing.getOpposite());
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
    public World getBlockWorld() {
        return super.getWorld();
    }

    @Override
    public BlockPos getBlockPos() {
        return getPos();
    }

    @Override
    public Block getBlock() {
        return getBlockType();
    }

    @Override
    public int getProgress() {
        return cookProgress;
    }

    @Override
    public int getMax() {
        return cookTime;
    }

}
