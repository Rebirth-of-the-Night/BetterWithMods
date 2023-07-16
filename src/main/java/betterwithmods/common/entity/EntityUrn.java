package betterwithmods.common.entity;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.BlockBloodSapling;
import betterwithmods.common.blocks.BlockNetherGrowth;
import betterwithmods.common.blocks.BlockUrn;
import betterwithmods.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by primetoxinz on 6/13/17.
 */
public class EntityUrn extends EntitySnowball {

    public EntityUrn(World worldIn) {
        super(worldIn);
    }

    public EntityUrn(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    public EntityUrn(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 3)
        {
            for (int i = 0; i < 8; ++i)
            {
                this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX, this.posY, this.posZ, 0.1D, 0.5D, 0.1D, Block.getStateId(BWMBlocks.URN.getDefaultState().withProperty(BlockUrn.TYPE, BlockUrn.EnumType.FULL)));
            }
        }
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote) {


            if (result.typeOfHit != RayTraceResult.Type.MISS) {
                BlockPos pos = result.typeOfHit == RayTraceResult.Type.ENTITY ? result.entityHit.getPosition() : result.getBlockPos();
                world.playSound(null, getPosition(), SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.PLAYERS, 0.5F, 0.4F / (rand.nextFloat() * 0.4F + 0.8F));

                Iterable<BlockPos> pool = BlockPos.PooledMutableBlockPos.getAllInBox(pos.add(-3, -3, -3), pos.add(3, 3, 3));
                boolean grew = false;
                for (BlockPos p : pool) {
                    IBlockState state = world.getBlockState(p);
                    if (state.getBlock() == BWMBlocks.NETHER_GROWTH) {
                        BlockNetherGrowth b = (BlockNetherGrowth) state.getBlock();
                        for(int i =0;i<10;i++)
                            b.grow(world, p, state, rand);
                        grew = true;
                    }
                    if (state.getBlock() == BWMBlocks.BLOOD_SAPLING) {
                        BlockBloodSapling sapling = (BlockBloodSapling)state.getBlock();
                        if (sapling.generateTree(world, p, world.getBlockState(p), rand))
                            grew = true;
                    }
                }
                if (!grew) {
                    WorldUtils.spawnGhast(world, pos);
                }
            }
            this.world.setEntityState(this, (byte) 3);
            this.setDead();
        }
    }
}
