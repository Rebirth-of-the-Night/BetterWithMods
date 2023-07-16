package betterwithmods.common.potion;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;

public class PotionTruesight extends BWPotion {
    public PotionTruesight(String name, boolean b, int potionColor) {
        super(name, b, potionColor);
    }


    //TODO caching of mob spawning


    @Override
    public void tick(EntityLivingBase entity) {
        World world = entity.getEntityWorld();
        if (world.isRemote) {
            Minecraft mc = Minecraft.getMinecraft();
            if (entity != mc.player)
                return;
            int var3 = mc.gameSettings.particleSetting;
            if (!mc.isGamePaused() && (world.provider.getDimension() == 0 || world.provider.getDimension() == 1)) {

                int entityX = MathHelper.floor(entity.posX);
                int var5 = MathHelper.floor(entity.posY);
                int var6 = MathHelper.floor(entity.posZ);
                int radius = 10;
                for (int x = entityX - radius; x <= entityX + radius; ++x) {
                    for (int y = var5 - radius; y <= var5 + radius; ++y) {
                        for (int z = var6 - radius; z <= var6 + radius; ++z) {
                            if (canSpawnMobsHere(world, new BlockPos(x, y, z)) && (var3 == 0 || world.rand.nextInt(12) <= 2 - var3 << 1)) {

                                double i = (double) x + world.rand.nextDouble();
                                double j = (double) y + world.rand.nextDouble() * 0.25D;
                                double k = (double) z + world.rand.nextDouble();
                                world.spawnParticle(EnumParticleTypes.SPELL_MOB, i, j, k, 0, 0, 0);
                            }
                        }
                    }
                }
            }
        }

    }

    //Borrowed from MoreOverlays
    private static boolean canSpawnMobsHere(World world, BlockPos pos) {
        if (world.getLightFor(EnumSkyBlock.BLOCK, pos) >= 8)
            return false;

        if (world.getBiome(pos).getSpawnableList(EnumCreatureType.MONSTER).isEmpty())
            return false;

        if(!WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType.ON_GROUND, world,pos))
            return false;

        if (!checkCollision(pos, world))
            return false;
        return true;
    }


    public static boolean ignoreLayer = false;

    private final static AxisAlignedBB TEST_BB = new AxisAlignedBB(0.6D / 2D, 0, 0.6D / 2D, 1D - 0.6D / 2D, 1D, 1D - 0.6D / 2D);

    private static boolean checkCollision(BlockPos pos, World world) {
        IBlockState block1 = world.getBlockState(pos);

        if (block1.isNormalCube() || (!ignoreLayer && world.getBlockState(pos.up()).isNormalCube())) //Don't check because a check on normal Cubes will/should return false ( 99% collide ).
            return false;
        else if (world.isAirBlock(pos) && (ignoreLayer || world.isAirBlock(pos.up())))  //Don't check because Air has no Collision Box
            return true;

        AxisAlignedBB bb = TEST_BB.offset(pos.getX(), pos.getY(), pos.getZ());
        if (world.getCollisionBoxes(null, bb).isEmpty() && !world.containsAnyLiquid(bb)) {
            if (ignoreLayer)
                return true;
            else {
                AxisAlignedBB bb2 = bb.offset(0, 1, 0);
                return world.getCollisionBoxes(null, bb2).isEmpty() && !world.containsAnyLiquid(bb2);
            }
        }
        return false;
    }


}
