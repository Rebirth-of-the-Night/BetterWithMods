package betterwithmods.common.blocks.tile;

import betterwithmods.api.BWMAPI;
import betterwithmods.api.capabilities.CapabilityMechanicalPower;
import betterwithmods.api.tile.ICrankable;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.common.blocks.mechanical.BlockCookingPot;
import betterwithmods.util.DirUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Purpose:
 *
 * @author primetoxinz
 * @version 3/20/17
 */
public class TileEntityDragonVessel extends TileBasic implements ITickable, IMechanicalPower, ICrankable {
    private final int MAX_EXPERIENCE = 1395;
    private int experience;
    private int maxDist = 5;
    private EnumFacing facing = EnumFacing.UP;
    public TileEntityDragonVessel() {
    }

    public int getExperience() {
        return experience;
    }

    public int getMaxExperience() {
        return MAX_EXPERIENCE;
    }

    private int addExperience(int xp)  {
        if(this.experience >= MAX_EXPERIENCE)
            return xp;
        if(this.experience < MAX_EXPERIENCE-xp) {
            this.experience += xp;
            return 0;
        } else if( this.experience >= MAX_EXPERIENCE-xp) {
            int newXp = xp - (MAX_EXPERIENCE-experience);
            this.experience += newXp;
            return xp - newXp;
        }
        return xp;
    }
    private void hooverXP(EntityXPOrb entity) {
        if (!world.isRemote && !entity.isDead) {
            int xpValue = entity.getXpValue();
            int xpDrained = MathHelper.clamp(MAX_EXPERIENCE - experience,0,xpValue);
            addExperience(xpValue);
            xpValue -= xpDrained;
            markDirty();
            if (xpValue > 0) {
                entity.xpValue = xpValue;
                release(entity);
            } else {
                entity.setDead();
            }
        }
    }

    @Override
    public void update() {
        if (this.getBlockWorld().isRemote)
            return;
        if (this.getBlockWorld().getBlockState(this.pos).getBlock() instanceof BlockCookingPot) {
            IBlockState state = this.getBlockWorld().getBlockState(this.pos);

            if (!isPowered()) {
                this.facing = EnumFacing.UP;
                AxisAlignedBB box = new AxisAlignedBB(pos).grow(maxDist);
                List<EntityXPOrb> xp = world.getEntitiesWithinAABB(EntityXPOrb.class, box);
                for (EntityXPOrb entity : xp) {
                    double xDist = (getPos().getX() + 0.5D - entity.posX);
                    double yDist = (getPos().getY() + 0.5D - entity.posY);
                    double zDist = (getPos().getZ() + 0.5D - entity.posZ);

                    double totalDistance = Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);

                    if (totalDistance < 1.5) {
                        hooverXP(entity);
                    } else if (shouldAttract(getPos(), entity)) {
                        double d = 1 - (Math.max(0.1, totalDistance) / maxDist);
                        double speed = 0.01 + (d * 0.02);

                        entity.motionX += xDist / totalDistance * speed;
                        entity.motionZ += zDist / totalDistance * speed;
                        entity.motionY += yDist / totalDistance * speed;
                        if (yDist > 0.5) {
                            entity.motionY = 0.12;
                        }

                        // force client sync because this movement is server-side only
                        boolean silent = entity.isSilent();
                        entity.setSilent(!silent);
                        entity.setSilent(silent);
                    }
                }
            } else {
                this.facing = getPoweredSide();
                ejectExperience(DirUtils.rotateFacingAroundY(this.facing, false));
            }

            if (facing != state.getValue(DirUtils.TILTING)) {
                world.setBlockState(pos, state.withProperty(DirUtils.TILTING, facing));
            }
        }
    }

    private void ejectExperience(EnumFacing facing) {
        if(experience > 0) {
            BlockPos target = pos.offset(facing);
            IBlockState targetState = getBlockWorld().getBlockState(target);
            boolean ejectIntoWorld = getBlockWorld().isAirBlock(target) || targetState.getBlock().isReplaceable(getBlockWorld(), target) || !targetState.getMaterial().isSolid() || targetState.getBoundingBox(getBlockWorld(), target).maxY < 0.5d;
            if (ejectIntoWorld) {
                Vec3i vec = new BlockPos(0, 0, 0).offset(facing);
                int xp = EntityXPOrb.getXPSplit(experience);
                experience -= xp;
                markDirty();
                EntityXPOrb orb = new EntityXPOrb(world, pos.getX() + 0.5F - (vec.getX() / 4d), pos.getY() + 0.25D, pos.getZ() + 0.5D - (vec.getZ() / 4d), xp);
                orb.motionX = 0.0D;
                orb.motionY = 0.0D;
                orb.motionZ = 0.0D;
                world.spawnEntity(orb);
            }
        }
    }

    private static final String PREVENT_REMOTE_MOVEMENT = "PreventRemoteMovement";
    public static final String BWM_PULLER_TAG = "BWMpuller";

    public static boolean shouldAttract(@Nullable BlockPos pullerPos, @Nullable Entity entity) {

        if (entity == null || entity.isDead) {
            return false;
        }
        if (entity instanceof IProjectile && entity.motionY > 0.01) {
            return false;
        }

        NBTTagCompound data = entity.getEntityData();

        if (isReservedByOthers(data)) {
            return false;
        }

        if (!isReservedByBWM(data)) {
            // if it is not being pulled already, pull it
            if (pullerPos != null) {
                data.setLong(BWM_PULLER_TAG, pullerPos.toLong());
            }
            return true;
        }

        if (pullerPos == null) {
            // it is already being pulled, so with no further info we are done
            return false;
        }

        long posL = data.getLong(BWM_PULLER_TAG);
        if (posL == pullerPos.toLong()) {
            // item already pulled from pullerPos so done
            return true;
        }

        // it is being pulled by something else, so check to see if we are closer
        BlockPos curOwner = BlockPos.fromLong(posL);
        double distToCur = curOwner.distanceSqToCenter(entity.posX, entity.posY, entity.posZ);
        double distToMe = pullerPos.distanceSqToCenter(entity.posX, entity.posY, entity.posZ);
        if (distToMe + 1 < distToCur) {
            // only take over if it is clearly nearer to us
            data.setLong(BWM_PULLER_TAG, pullerPos.toLong());
            return true;
        }
        return false;
    }

    public static void release(@Nullable Entity entity) {
        if (entity != null && !entity.isDead) {
            NBTTagCompound data = entity.getEntityData();
            data.removeTag(BWM_PULLER_TAG);
        }
    }

    public static boolean isReserved(Entity entity) {
        return isReservedByBWM(entity.getEntityData()) || isReservedByOthers(entity.getEntityData());
    }

    public static boolean isReservedByBWM(NBTTagCompound data) {
        return data.hasKey(BWM_PULLER_TAG);
    }


    public static boolean isReservedByOthers(NBTTagCompound data) {
        return data.hasKey(PREVENT_REMOTE_MOVEMENT);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.facing = tag.hasKey("facing") ? EnumFacing.byIndex(tag.getInteger("facing")) : EnumFacing.UP;
        this.experience = tag.hasKey("Experience") ? tag.getInteger("Experience") : 0;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagCompound t = super.writeToNBT(tag);
        t.setInteger("facing", facing.getIndex());
        t.setInteger("Experience", this.experience);
        return t;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
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
        return null;
    }

    private boolean isPowered() {
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            if (isInputtingPower(facing))
                return true;
        }
        return false;
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
}
