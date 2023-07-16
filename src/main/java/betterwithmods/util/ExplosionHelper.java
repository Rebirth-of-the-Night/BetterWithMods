package betterwithmods.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.List;
import java.util.Set;

public class ExplosionHelper {
    Explosion explosion;

    private final List<BlockPos> affectedBlockPositions;
    private final List<Entity> affectedEntities;

    public ExplosionHelper(Explosion explosion) {
        this.explosion = explosion;
        this.affectedBlockPositions = Lists.newArrayList();
        this.affectedEntities = Lists.newArrayList();
    }

    public World getWorld() {
        return explosion.world;
    }

    public Vec3d getPosition() {
        return explosion.getPosition();
    }

    public Entity getExploder() {
        return explosion.exploder;
    }

    public float getSize() {
        return explosion.size;
    }

    public List<BlockPos> getAffectedBlocks() {
        return affectedBlockPositions;
    }

    public List<Entity> getAffectedEntities() {
        return affectedEntities;
    }

    public void calculateBlocks(float size, boolean ignoreLiquids) {
        Set<BlockPos> set = Sets.newHashSet();
        int i = 16;

        affectedBlockPositions.clear();

        World world = getWorld();
        Vec3d pos = getPosition();
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;
        Entity exploder = getExploder();

        for (int rx = 0; rx < i; ++rx) {
            for (int ry = 0; ry < i; ++ry) {
                for (int rz = 0; rz < i; ++rz) {
                    if (rx == 0 || rx == i - 1 || ry == 0 || ry == i - 1 || rz == 0 || rz == i - 1) {
                        double dx = (double) ((float) rx / (i - 1) * 2.0F - 1.0F);
                        double dy = (double) ((float) ry / (i - 1) * 2.0F - 1.0F);
                        double dz = (double) ((float) rz / (i - 1) * 2.0F - 1.0F);
                        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
                        dx = dx / length;
                        dy = dy / length;
                        dz = dz / length;
                        float rayDistance = size * (0.7F + world.rand.nextFloat() * 0.6F);
                        double xRay = x;
                        double yRay = y;
                        double zRay = z;

                        float f1 = 0.3F;
                        float rayWaterDistance = rayDistance;
                        while (rayDistance > 0.0F) {
                            BlockPos blockpos = new BlockPos(xRay, yRay, zRay);
                            IBlockState iblockstate = world.getBlockState(blockpos);

                            boolean isLiquid = iblockstate.getMaterial().isLiquid();
                            if (iblockstate.getMaterial() != Material.AIR) {
                                float resistance = exploder != null ? exploder.getExplosionResistance(explosion, world, blockpos, iblockstate) : iblockstate.getBlock().getExplosionResistance(world, blockpos, null, explosion);
                                float cost = (resistance + f1) * f1;
                                rayWaterDistance -= cost;
                            }

                            if(!ignoreLiquids || !isLiquid)
                                rayDistance = rayWaterDistance;

                            if (rayDistance > 0.0F && (exploder == null || exploder.canExplosionDestroyBlock(explosion, world, blockpos, iblockstate, rayDistance))) {
                                set.add(blockpos);
                            }

                            xRay += dx * f1;
                            yRay += dy * f1;
                            zRay += dz * f1;
                            rayDistance -= 0.225F;
                            rayWaterDistance -= 0.225F;
                        }
                    }
                }
            }
        }

        affectedBlockPositions.addAll(set);
    }

    public void calculateEntities(float size) {
        affectedEntities.clear();

        World world = getWorld();
        Vec3d pos = getPosition();
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;
        Entity exploder = getExploder();

        float doubleSize = size * 2.0F;
        AxisAlignedBB aabb = new AxisAlignedBB(x, y, z, x, y, z).grow(doubleSize + 1.0);
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(exploder, aabb);

        for (Entity entity : list) {
            if (!entity.isImmuneToExplosions()) {
                double explosionRatio = entity.getDistance(x, y, z) / (double) doubleSize;

                if (explosionRatio <= 1.0D) {
                    double dx = entity.posX - x;
                    double dy = entity.posY + (double) entity.getEyeHeight() - y;
                    double dz = entity.posZ - z;
                    double length = (double) MathHelper.sqrt(dx * dx + dy * dy + dz * dz);

                    if (length != 0.0D) {
                        affectedEntities.add(entity);
                    }
                }
            }
        }
    }

    public void createExplosion() {
        World world = getWorld();
        Vec3d pos = getPosition();
        world.newExplosion(getExploder(), pos.x, pos.y, pos.z, getSize(), causesFire(), damagesTerrain());
    }

    private boolean damagesTerrain() {
        return explosion.damagesTerrain;
    }

    private boolean causesFire() {
        return explosion.causesFire;
    }
}
