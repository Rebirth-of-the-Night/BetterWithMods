package betterwithmods.common.entity;

import betterwithmods.BWMod;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class EntityJungleSpider extends EntityCaveSpider {
    public static final ResourceLocation LOOT = LootTableList.register(new ResourceLocation(BWMod.MODID, "entity/jungle_spider"));

    public EntityJungleSpider(World worldIn) {
        super(worldIn);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        if (super.attackEntityAsMob(entityIn))
        {
            if (entityIn instanceof EntityLivingBase)
            {
                int duration = 0;

                if (this.world.getDifficulty() == EnumDifficulty.NORMAL)
                {
                    duration = 7;
                }
                else if (this.world.getDifficulty() == EnumDifficulty.HARD)
                {
                    duration = 15;
                }

                if (duration > 0)
                {
                    ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(MobEffects.HUNGER, duration * 20, 0));
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return LOOT;
    }

    @Override
    protected boolean isValidLightLevel() {
        BlockPos blockpos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);

        if (this.world.getLightFor(EnumSkyBlock.BLOCK, blockpos) > 7)
        {
            return false;
        }

        return true;
    }

    @Override
    public boolean getCanSpawnHere() {
        BlockPos pos = new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ);
        BlockPos topPos = world.getHeight(pos).down();
        IBlockState topBlock = world.getBlockState(topPos);

        //On the ground, search up a little, just so we can also spawn in caves
        ArrayList<Integer> possible_spawns = getPossibleSpawnHeights(new BlockPos.MutableBlockPos(pos.up(32)),32);
        //Otherwise, if there's a leaf canopy search from there
        if(possible_spawns.isEmpty() && topBlock.getBlock() == Blocks.LEAVES && topBlock.getValue(BlockLeaves.DECAYABLE)) {
            possible_spawns = getPossibleSpawnHeights(new BlockPos.MutableBlockPos(topPos), 16);
        }
        if(possible_spawns.isEmpty())
            return false;
        this.setPosition(posX, possible_spawns.get(rand.nextInt(possible_spawns.size())), posZ);
        return super.getCanSpawnHere();
    }

    private ArrayList<Integer> getPossibleSpawnHeights(BlockPos.MutableBlockPos pos, int limit)
    {
        ArrayList<Integer> heights = new ArrayList<>();
        int leaves = 0;
        for(int i = 0; i < limit; i++)
        {
            IBlockState state = world.getBlockState(pos);
            if(isJungleLeaves(state))
                leaves += 1;
            else
                leaves = 0;
            if(leaves == 2) //Only on top of leaves that are two blocks thick.
                heights.add(pos.getY()+2);
            pos.move(EnumFacing.DOWN);
        }
        return heights;
    }

    private boolean isJungleLeaves(IBlockState state)
    {
        return state.getBlock() == Blocks.LEAVES && state.getValue(BlockLeaves.DECAYABLE);
    }
}
