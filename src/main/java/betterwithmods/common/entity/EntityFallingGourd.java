package betterwithmods.common.entity;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EntityFallingGourd extends EntityFallingBlock {
    private static final DataParameter<Integer> FALLBLOCK = EntityDataManager.createKey(EntityFallingGourd.class, DataSerializers.VARINT);
    private ItemStack seedStack = ItemStack.EMPTY;
    private SoundEvent shatterSound = SoundEvents.ENTITY_SLIME_SQUISH;
    private boolean smashOnImpact = false;

    public EntityFallingGourd(World worldIn) {
        super(worldIn);
    }

    public EntityFallingGourd(World worldIn, double x, double y, double z, IBlockState fallingBlockState) {
        super(worldIn, x, y, z, fallingBlockState);
        setBlock(fallingBlockState);
    }

    @Override
    public void onUpdate() {
        IBlockState fallblock = getBlock();
        Block block = fallblock.getBlock();
        if (fallblock.getMaterial() == Material.AIR) {
            this.setDead();
        } else {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            BlockPos blockpos1;
            if (this.fallTime++ == 0) {
                blockpos1 = new BlockPos(this);
                if (this.getEntityWorld().getBlockState(blockpos1).getBlock() == block) {
                    this.getEntityWorld().setBlockToAir(blockpos1);
                } else if (!this.getEntityWorld().isRemote) {
                    this.setDead();
                    return;
                }
            }

            if (!this.hasNoGravity()) {
                this.motionY -= 0.03999999910593033D;
            }

            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.9800000190734863D;
            this.motionY *= 0.9800000190734863D;
            this.motionZ *= 0.9800000190734863D;
            if (!this.getEntityWorld().isRemote) {
                blockpos1 = new BlockPos(this);
                if (onGround) {
                    IBlockState iblockstate = world.getBlockState(blockpos1);
                    if (world.isAirBlock(new BlockPos(this.posX, this.posY - 0.009999999776482582D, this.posZ)) && BlockFalling.canFallThrough(this.getEntityWorld().getBlockState(new BlockPos(this.posX, this.posY - 0.01D, this.posZ)))) {
                        onGround = false;
                        return;
                    }

                    this.motionX *= 0.7D;
                    this.motionZ *= 0.7D;
                    this.motionY *= -0.5D;
                    if (10 + rand.nextInt(7) <= this.fallTime)
                        smashOnImpact = true;
                    if (iblockstate.getBlock() != Blocks.PISTON_EXTENSION) {
                        if (!smashOnImpact && this.getEntityWorld().mayPlace(block, blockpos1, true, EnumFacing.UP, null) && !BlockFalling.canFallThrough(this.getEntityWorld().getBlockState(blockpos1.down())) && this.getEntityWorld().setBlockState(blockpos1, fallblock, 3)) {
                            this.setDead();
                            if (block instanceof BlockFalling) {
                                ((BlockFalling) block).onEndFalling(this.getEntityWorld(), blockpos1, fallblock, iblockstate);
                            }
                        } else {
                            this.shatter();
                        }
                    }
                } /*else if (fallTime > 100 && !world.isRemote && (blockpos1.getY() < 1 || blockpos1.getY() > 256) || fallTime > 600) {
                    this.shatter();
                }*/
            }
        }
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
        int i = MathHelper.ceil(distance - 1.0F);

        if (i > 0 && !world.isRemote)
        {
            List<Entity> list = Lists.newArrayList(this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox()));

            for (Entity entity : list)
            {
                entity.attackEntityFrom(DamageSource.FALLING_BLOCK, 1);
                smashOnImpact = true;
            }
        }
    }

    public void shatter() {
        if (!getEntityWorld().isRemote) {
            playSound(shatterSound, 0.8F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F); //slime sound i guess
            IBlockState fallblock = getBlock();
            if (fallblock != null)
                getEntityWorld().playEvent(2001, new BlockPos(this), Block.getStateId(fallblock));

            if (!seedStack.isEmpty()) {
                ItemStack seeds = seedStack.copy();
                seeds.setCount(rand.nextInt(3) + 1);
                if (this.shouldDropItem && this.getEntityWorld().getGameRules().getBoolean("doEntityDrops")) {
                    this.entityDropItem(seeds, 0.0F);
                }
            }
            this.setDead();
        }
    }

    @Nullable
    @Override
    public IBlockState getBlock() {
        return Block.getStateById(dataManager.get(FALLBLOCK));
    }

    public void setBlock(IBlockState blockstate) {
        dataManager.set(FALLBLOCK, Block.getStateId(blockstate));
    }

    public ItemStack getSeedStack() {
        return seedStack;
    }

    public void setSeedStack(ItemStack stack) {
        seedStack = stack;
    }

    public void setShatterSound(SoundEvent sound) {
        shatterSound = sound;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(FALLBLOCK, 0);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        setBlock(fallTile);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        fallTile = getBlock();
        super.writeEntityToNBT(compound);
    }
}
