package betterwithmods.common.entity;

import betterwithmods.event.FakePlayerHandler;
import betterwithmods.module.tweaks.ExplosionTracker;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.List;

import static net.minecraft.util.EnumFacing.UP;

/**
 * Created by primetoxinz on 9/5/16.
 */
public class EntityMiningCharge extends Entity {
    private static final DataParameter<Integer> FUSE = EntityDataManager.createKey(EntityMiningCharge.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> FACING = EntityDataManager.createKey(EntityMiningCharge.class, DataSerializers.VARINT);
    private EntityLivingBase igniter;
    /**
     * How long the fuse is
     */
    private int fuse;
    private EnumFacing facing;
    private HashMap<Block, IBlockState> dropMap = new HashMap<Block, IBlockState>() {
        private static final long serialVersionUID = -3447689178833578930L;

        {
            put(Blocks.COBBLESTONE, Blocks.GRAVEL.getDefaultState());
            put(Blocks.GRAVEL, Blocks.SAND.getDefaultState());
            put(Blocks.GRASS, Blocks.DIRT.getDefaultState());
            put(Blocks.DIRT, Blocks.DIRT.getDefaultState());
            put(Blocks.SAND, Blocks.SAND.getDefaultState());
        }
    };

    public EntityMiningCharge(World worldIn) {
        super(worldIn);
        this.fuse = 80;
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.98F);
    }

    public EntityMiningCharge(World worldIn, double x, double y, double z, EntityLivingBase igniter, EnumFacing facing) {
        this(worldIn);
        setFacing(facing);
        this.setPosition(x, y, z);
        this.setFuse(80);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.igniter = igniter;
        setNoGravity(facing != UP);
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(FUSE, 80);
        this.dataManager.register(FACING, EnumFacing.NORTH.getIndex());
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (!this.hasNoGravity()) {
            this.motionY -= 0.03999999910593033D;
        }
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);


        --this.fuse;

        if (this.fuse <= 0) {
            this.setDead();

            if (!this.getEntityWorld().isRemote) {
                this.explode();
            }
        } else {
            this.handleWaterMovement();
            this.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
        }
    }

    private void explode() {
        world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
        world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX, this.posY, this.posZ, 1.0D, 0.0D, 0.0D);

        BlockPos pos = getPosition();
        EnumFacing facing = getFacing().getOpposite();

        BlockPos center = pos.offset(facing);

        AxisAlignedBB area = new AxisAlignedBB(center.getX(), center.getY(), center.getZ(), center.getX(), center.getY(), center.getZ()).grow(1);
        Iterable<BlockPos> positions = BlockPos.getAllInBox((int) area.minX, (int) area.minY, (int) area.minZ, (int) area.maxX, (int) area.maxY, (int) area.maxZ);
        for (BlockPos b : positions) {
            explodeBlock(world, b);
        }
        explodeBlock(world, pos.offset(facing, 3));

        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, area);
        entities.forEach(entity -> entity.attackEntityFrom(DamageSource.causeExplosionDamage(igniter), 45f));

        MinecraftForge.EVENT_BUS.post(new ExplosionTracker.ExplosionTrackingEvent(new Vec3d(pos), this.igniter, world));
    }


    private void explodeBlock(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        float resistance = state.getBlock().getExplosionResistance(world, pos, null, null);
        if (resistance < 100) {
            Explosion explosion = new Explosion(world, igniter, posX, posY, posZ, 0, false, false);
            if (state.getBlock().canDropFromExplosion(explosion)) {
                IBlockState drop = dropMap.getOrDefault(state.getBlock(), state);
                drop.getBlock().harvestBlock(world, FakePlayerHandler.getShoveler(), pos, drop, null, FakePlayerHandler.getShoveler().getHeldItemMainhand());
            }
            state.getBlock().onBlockExploded(world, pos, explosion);
            world.setBlockToAir(pos);
        }
    }

    /**
     * (abstract) Protected HELPER method to write subclass entity data to NBT.
     */
    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setShort("Fuse", (short) this.getFuse());
        compound.setByte("Facing", (byte) this.getFacing().getIndex());
    }

    /**
     * (abstract) Protected HELPER method to read subclass entity data from NBT.
     */
    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.setFuse(compound.getShort("Fuse"));
        this.setFacing(compound.getByte("Facing"));
    }

    /**
     * returns null or the entityliving it was placed or ignited by
     */
    public EntityLivingBase getTntPlacedBy() {
        return this.igniter;
    }

    @Override
    public float getEyeHeight() {
        return 0.0F;
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (FUSE.equals(key)) {
            this.fuse = this.getFuseDM();
        }
        if (FACING.equals(key)) {
            this.facing = EnumFacing.byIndex(getFacingDM());
        }
    }

    public void setFacing(EnumFacing facing) {
        this.dataManager.set(FACING, facing.getIndex());
        this.facing = facing;
    }

    public int getFuseDM() {
        return this.dataManager.get(FUSE);
    }

    public int getFacingDM() {
        return this.dataManager.get(FACING);
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public void setFacing(int facing) {
        setFacing(EnumFacing.byIndex(facing));
    }

    public int getFuse() {
        return this.fuse;
    }

    public void setFuse(int fuseIn) {
        this.dataManager.set(FUSE, fuseIn);
        this.fuse = fuseIn;
    }
}