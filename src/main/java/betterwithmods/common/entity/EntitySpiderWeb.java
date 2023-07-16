package betterwithmods.common.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/**
 * Created by primetoxinz on 4/22/17.
 */
public class EntitySpiderWeb extends EntityThrowable {
    public EntitySpiderWeb(World worldIn) {
        super(worldIn);
    }

    public EntitySpiderWeb(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public EntitySpiderWeb(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        BlockPos pos = result.getBlockPos();
        if(pos != null && (pos.getY() < 0 || pos.getY() >= 256))
            return;

        IBlockState state;
        switch (result.typeOfHit) {
            case BLOCK:
                state = world.getBlockState(pos);
                if (state.getMaterial().isReplaceable() || state.getBlock().equals(Blocks.WEB)) {
                    return;
                }
                BlockPos offset = pos.offset(result.sideHit);
                if(world.getBlockState(offset).getMaterial().isReplaceable()) {
                    world.setBlockState(offset, Blocks.WEB.getDefaultState());
                }
                setDead();
                break;

            case ENTITY:
                if (result.entityHit instanceof EntitySpider)
                    return;
                pos = result.entityHit.getPosition();
                state = world.getBlockState(pos);
                if (state.getMaterial().isReplaceable()) {
                    world.setBlockState(pos, Blocks.WEB.getDefaultState());
                }
                setDead();
                break;
            default:
                break;
        }
    }
}
