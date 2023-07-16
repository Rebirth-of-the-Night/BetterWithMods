package betterwithmods.common.entity;

import betterwithmods.module.hardcore.crafting.HCFishing;
import betterwithmods.util.WorldUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

/**
 * Created by primetoxinz on 7/23/17.
 */
public class EntityHCFishHook extends EntityFishHook implements IEntityAdditionalSpawnData {

    public EntityHCFishHook(World world) {
        super(world, null);
    }

    public EntityHCFishHook(World worldIn, EntityPlayer fishingPlayer) {
        super(worldIn, fishingPlayer);
    }

    @Override
    public void setDead() {
        super.setDead();
    }

    @Override
    public void shoot() {
        if (angler == null)
            return;
        super.shoot();
    }

    @Override
    public void init(EntityPlayer angler) {
        this.setSize(0.25F, 0.25F);
        this.ignoreFrustumCheck = true;
        this.angler = angler;
        if (this.angler != null)
            this.angler.fishEntity = this;
    }

    @Override
    public void catchingFish(BlockPos pos) {
        WorldServer worldserver = (WorldServer) this.world;

        //minutes
        double initialTime = HCFishing.configuration.initialTime;
        if (WorldUtils.isTimeFrame(world, WorldUtils.TimeFrame.NIGHT))
            initialTime *= HCFishing.configuration.nightModifier;
        if (worldserver.isRainingAt(pos.up()))
            initialTime *= HCFishing.configuration.rainModifier;
        if (WorldUtils.isMoonPhase(world, WorldUtils.MoonPhase.Full))
            initialTime *= HCFishing.configuration.fullMoonModifier;
        if (WorldUtils.isTimeFrame(worldserver, WorldUtils.TimeFrame.DAWN))
            initialTime *= HCFishing.configuration.dawnModifier;
        else if (WorldUtils.isTimeFrame(worldserver, WorldUtils.TimeFrame.DUSK))
            initialTime *= HCFishing.configuration.duskModifier;

        if (this.ticksCatchable > 0) {
            --this.ticksCatchable;

            if (this.ticksCatchable <= 0) {
                this.ticksCaughtDelay = 0;
                this.ticksCatchableDelay = 0;
            } else {
                this.motionY -= 0.2D * (double) this.rand.nextFloat() * (double) this.rand.nextFloat();
            }
        } else if (this.ticksCatchableDelay > 0) {
            this.ticksCatchableDelay -= 1;

            if (this.ticksCatchableDelay > 0) {
                this.fishApproachAngle = (float) ((double) this.fishApproachAngle + this.rand.nextGaussian() * 4.0D);
                float f = this.fishApproachAngle * 0.017453292F;
                float f1 = MathHelper.sin(f);
                float f2 = MathHelper.cos(f);
                double d0 = this.posX + (double) (f1 * (float) this.ticksCatchableDelay * 0.1F);
                double d1 = (double) ((float) MathHelper.floor(this.getEntityBoundingBox().minY) + 1.0F);
                double d2 = this.posZ + (double) (f2 * (float) this.ticksCatchableDelay * 0.1F);
                Block block = worldserver.getBlockState(new BlockPos(d0, d1 - 1.0D, d2)).getBlock();

                if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
                    if (this.rand.nextFloat() < 0.15F) {
                        worldserver.spawnParticle(EnumParticleTypes.WATER_BUBBLE, d0, d1 - 0.10000000149011612D, d2, 1, (double) f1, 0.1D, (double) f2, 0.0D);
                    }

                    float f3 = f1 * 0.04F;
                    float f4 = f2 * 0.04F;
                    worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, d0, d1, d2, 0, (double) f4, 0.01D, (double) (-f3), 1.0D);
                    worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, d0, d1, d2, 0, (double) (-f4), 0.01D, (double) f3, 1.0D);
                }
            } else {
                this.motionY = (double) (-0.4F * MathHelper.nextFloat(this.rand, 0.6F, 1.0F));
                this.playSound(SoundEvents.ENTITY_BOBBER_SPLASH, 0.25F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                double d3 = this.getEntityBoundingBox().minY + 0.5D;
                worldserver.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX, d3, this.posZ, (int) (1.0F + this.width * 20.0F), (double) this.width, 0.0D, (double) this.width, 0.20000000298023224D);
                worldserver.spawnParticle(EnumParticleTypes.WATER_WAKE, this.posX, d3, this.posZ, (int) (1.0F + this.width * 20.0F), (double) this.width, 0.0D, (double) this.width, 0.20000000298023224D);
                this.ticksCatchable = MathHelper.getInt(this.rand, 20, 40);
            }
        } else if (this.ticksCaughtDelay > 0) {
            this.ticksCaughtDelay -= 1;
            float f5 = 0.15F;

            if (this.ticksCaughtDelay < 20) {
                f5 = (float) ((double) f5 + (double) (20 - this.ticksCaughtDelay) * 0.05D);
            } else if (this.ticksCaughtDelay < 40) {
                f5 = (float) ((double) f5 + (double) (40 - this.ticksCaughtDelay) * 0.02D);
            } else if (this.ticksCaughtDelay < 60) {
                f5 = (float) ((double) f5 + (double) (60 - this.ticksCaughtDelay) * 0.01D);
            }

            if (this.rand.nextFloat() < f5) {
                float f6 = MathHelper.nextFloat(this.rand, 0.0F, 360.0F) * 0.017453292F;
                float f7 = MathHelper.nextFloat(this.rand, 25.0F, 60.0F);
                double d4 = this.posX + (double) (MathHelper.sin(f6) * f7 * 0.1F);
                double d5 = (double) ((float) MathHelper.floor(this.getEntityBoundingBox().minY) + 1.0F);
                double d6 = this.posZ + (double) (MathHelper.cos(f6) * f7 * 0.1F);
                Block block1 = worldserver.getBlockState(new BlockPos((int) d4, (int) d5 - 1, (int) d6)).getBlock();

                if (block1 == Blocks.WATER || block1 == Blocks.FLOWING_WATER) {
                    worldserver.spawnParticle(EnumParticleTypes.WATER_SPLASH, d4, d5, d6, 2 + this.rand.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D);
                }
            }

            if (this.ticksCaughtDelay <= 0) {
                this.fishApproachAngle = MathHelper.nextFloat(this.rand, 0.0F, 360.0F);
                this.ticksCatchableDelay = MathHelper.getInt(this.rand, 20, 80);
            }
        } else {
            if (angler.isCreative()) {
                this.ticksCaughtDelay = 1;
            } else {
                int minute  = 20 * 60;
                this.ticksCaughtDelay = MathHelper.getInt(this.rand, (int) (initialTime * (minute)), (int) ((initialTime + 2) * (minute)));
                this.ticksCaughtDelay = Math.max(HCFishing.configuration.minimumTime, this.ticksCaughtDelay - (this.lureSpeed * minute));
            }

        }
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        if (angler != null)
            buffer.writeInt(angler.getEntityId());
        else
            buffer.writeInt(0);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        angler = (EntityPlayer) Minecraft.getMinecraft().world.getEntityByID(additionalData.readInt());
    }
}

