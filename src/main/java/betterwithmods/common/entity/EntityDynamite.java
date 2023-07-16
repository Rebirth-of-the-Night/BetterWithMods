package betterwithmods.common.entity;

import betterwithmods.common.BWMItems;
import betterwithmods.common.items.ItemDynamite;
import betterwithmods.util.FluidUtils;
import betterwithmods.util.InvUtils;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.MoverType;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.Nonnull;
import java.util.List;

public class EntityDynamite extends Entity implements IProjectile {
    private static final float pi = 3.141593F;

    private static final DataParameter<ItemStack> DYNAMITE_TYPE = EntityDataManager.createKey(EntityDynamite.class, DataSerializers.ITEM_STACK);
    private static final DataParameter<Integer> FUSE = EntityDataManager.createKey(EntityDynamite.class, DataSerializers.VARINT);

    public EntityDynamite(World world) {
        this(world, 0, 0, 0, 0);
    }

    public EntityDynamite(World world, double xPos, double yPos, double zPos, int fuse) {
        super(world);
        this.setSize(0.25F, 0.4F);
        this.setPosition(xPos, yPos, zPos);
        setFuse(fuse);
        //this.preventEntitySpawning = true;
        //this.isImmuneToFire = true;
    }

    public EntityDynamite(World world, EntityLivingBase owner, int fuse) {
        this(world);
        this.setLocationAndAngles(owner.posX, owner.posY + owner.getEyeHeight(), owner.posZ, owner.rotationYaw, owner.rotationPitch);
        this.posX -= MathHelper.cos(this.rotationYaw / 180.0F * pi) * 0.16F;
        this.posY -= 0.1D;
        this.posZ -= MathHelper.sin(this.rotationYaw / 180.0F * pi) * 0.16F;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.motionX = (-MathHelper.sin(this.rotationYaw / 180.0F * pi) * MathHelper.cos(this.rotationPitch / 180.0F * pi) * 0.4F);
        this.motionZ = (MathHelper.cos(this.rotationYaw / 180.0F * pi) * MathHelper.cos(this.rotationPitch / 180.0F * pi) * 0.4F);
        this.motionY = (-MathHelper.sin(this.rotationPitch / 180.0F * pi) * 0.4F);
        this.shoot(this.motionX, this.motionY, this.motionZ, 0.75F, 1.0F);
        setFuse(fuse);
    }

    public void setDynamiteStack(ItemStack stack) {
        stack = stack.copy();
        stack.setCount(1);
        dataManager.set(DYNAMITE_TYPE, stack);
    }

    public ItemStack getDynamiteStack() {
        return dataManager.get(DYNAMITE_TYPE);
    }

    public float getFuseSlide() {
        float maxFuse = getMaxFuse();
        return (maxFuse - getFuse()) / maxFuse;
    }

    public int getMaxFuse() {
        Item item = getDynamiteStack().getItem();
        if(item instanceof ItemDynamite)
            return ((ItemDynamite) item).getFuseTime();
        return 1;
    }

    public int getFuse() {
        return dataManager.get(FUSE);
    }

    public void setFuse(int fuse) {
        dataManager.set(FUSE, fuse);
    }

    /*@SideOnly(Side.CLIENT)
    @Override
    public void handleStatusUpdate(byte id) {
        if(id == 100) {
            this.fuse = 100;
        }
    }*/

    @Override
    protected void dealFireDamage(int amount) {
        super.dealFireDamage(amount);
        if(getFuse() <= 0)
            setFuse(50);
    }

    @Override
    public void setFire(int seconds) { //Both? Both is good.
        super.setFire(seconds);
        if(getFuse() <= 0)
            setFuse(50);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        Fluid fluid = FluidUtils.getFluidFromBlock(world, getPosition(), EnumFacing.UP);
        if (fluid != null && fluid.getTemperature() >= FluidRegistry.LAVA.getTemperature()) {
            setFuse(1);
        }

        boolean flag1 = this.isWet();

        if (this.world.isFlammableWithin(this.getEntityBoundingBox().shrink(0.001D)))
        {
            this.dealFireDamage(1);

            if (!flag1)
            {
                this.setFire(8);
            }
        }

        /*if (world.getBlockState(getPosition()).getBlock() == Blocks.FIRE) {
            this.fuse = 1;
            this.getEntityWorld().playSound(null, new BlockPos(this.posX, this.posY, this.posZ), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        }*/

        int fuse = getFuse();
        if (fuse > 0) {
            if (!world.isRemote) {

                //Send up to the client
                world.setEntityState(this, (byte) 100);

                //Play sounds
                if (fuse % 20 == 0) {
                    world.playSound(null, getPosition(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                }

            }

            //Spawn particles
            float smokeOffset = 0.25F;
            if (fluid != null && fluid == FluidRegistry.WATER) {
                world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * smokeOffset, this.posY - this.motionY * smokeOffset, this.posZ - this.motionZ * smokeOffset, this.motionX, this.motionY, this.motionZ);
            } else {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX - this.motionX * smokeOffset, this.posY - this.motionY * smokeOffset, this.posZ - this.motionZ * smokeOffset, this.motionX, this.motionY, this.motionZ);
            }

            fuse--;

            if (fuse <= 0) {
                if (!this.getEntityWorld().isRemote) {
                    explode();
                }
                this.setDead();
            }
            else {
                setFuse(fuse);
            }
        } else {
            if (onGround) {
                double speed = motionX * motionX + motionY * motionY + motionZ * motionZ;
                if (speed < 0.1 * 0.1) {
                    convertToItem();
                }
            }
        }

        Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d1, vec3d, false, true, false);
        vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
        vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

        if (raytraceresult != null)
        {
            vec3d = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
        }

        if (raytraceresult != null && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult))
        {
            this.onImpact(raytraceresult);
        }

        move(MoverType.SELF, motionX, motionY, motionZ);

        float f1 = 0.999F;

        if (this.isInWater())
        {
            f1 = 0.6F;
        }

        this.motionX *= (double)f1;
        this.motionY *= (double)f1;
        this.motionZ *= (double)f1;

        if (!this.hasNoGravity())
        {
            this.motionY -= 0.05000000074505806D;
        }

        this.doBlockCollisions();

        if (this.onGround) {
            this.motionX *= 0.7D;
            this.motionZ *= 0.7D;
            //this.motionY *= -0.5D;
        }

        this.extinguish();
    }

    protected void onImpact(RayTraceResult result) {
        if(result.typeOfHit == RayTraceResult.Type.BLOCK) {
            double speed = motionX * motionX + motionY * motionY + motionZ * motionZ;
            if(speed > 0.1) {
                switch (result.sideHit) {
                    case DOWN:
                    case UP:
                        motionX *= 0.6;
                        motionZ *= 0.6;
                        motionY *= -0.5;
                        break;
                    case NORTH:
                    case SOUTH:
                        motionZ *= -0.7;
                        break;
                    case WEST:
                    case EAST:
                        motionX *= -0.7;
                        break;
                }
            }
        }
    }

    @Override
    public void shoot(double dX, double dY,
                      double dZ, float angle, float f) {
        float sqrt = MathHelper.sqrt(dX * dX + dY * dY + dZ * dZ);
        dX /= sqrt;
        dY /= sqrt;
        dZ /= sqrt;
        dX += this.rand.nextGaussian() * 0.0075D * f;
        dY += this.rand.nextGaussian() * 0.0075D * f;
        dZ += this.rand.nextGaussian() * 0.0075D * f;
        dX *= angle;
        dY *= angle;
        dZ *= angle;
        this.motionX = dX;
        this.motionY = dY;
        this.motionZ = dZ;
        float pitch = MathHelper.sqrt(dX * dX + dZ * dZ);
        this.prevRotationYaw = (this.rotationYaw = (float) (Math.atan2(dX, dZ) * 180.0D / pi));
        this.prevRotationPitch = (this.rotationPitch = (float) (Math.atan2(dY, pitch) * 180.0D / pi));
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(DYNAMITE_TYPE, new ItemStack(BWMItems.DYNAMITE));
        this.dataManager.register(FUSE, 0);
    }

    @Override
    public void readEntityFromNBT(@Nonnull NBTTagCompound tag) {
        if (tag.hasKey("Fuse"))
            setFuse(tag.getInteger("Fuse"));
        setDynamiteStack(new ItemStack(tag.getCompoundTag("Dynamite")));
    }

    @Override
    public void writeEntityToNBT(@Nonnull NBTTagCompound tag) {
        if (getFuse() > 0) {
            tag.setInteger("Fuse", getFuse());
        }
        tag.setTag("Dynamite", getDynamiteStack().serializeNBT());
    }

    public void explode() {
        Item item = getDynamiteStack().getItem();
        if(item instanceof ItemDynamite) {
            ((ItemDynamite) item).explode(this);
        }

    }

    public void redneckFishing(BlockPos center, Iterable<BlockPos> set, float chance) {
        if(!isWaterBlock(center))
            return;
        for (BlockPos pos : set) {
            if (isWaterBlock(pos) && this.rand.nextFloat() < chance) {
                spawnDeadFish(pos);
            }
        }
    }

    @Deprecated
    private boolean isWaterBlock(BlockPos pos) {
        Block block = this.getEntityWorld().getBlockState(pos).getBlock();
        return block instanceof BlockLiquid && this.getEntityWorld().getBlockState(pos).getMaterial() == Material.WATER;
    }

    private void spawnDeadFish(BlockPos pos) {
        LootContext.Builder build = new LootContext.Builder((WorldServer) world);
        List<ItemStack> fish = world.getLootTableManager().getLootTableFromLocation(LootTableList.GAMEPLAY_FISHING_FISH).generateLootForPools(world.rand, build.build());
        for (ItemStack stack : fish) {
            InvUtils.spawnStack(world, pos, Lists.newArrayList(stack));
        }
    }

    private void convertToItem() {
        if (!world.isRemote)
            InvUtils.spawnStack(world, posX, posY, posZ, 20, getDynamiteStack());
        this.setDead();
    }

}
