package betterwithmods.util;

import com.google.common.collect.Sets;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.StreamSupport;

import static net.minecraft.world.chunk.Chunk.NULL_BLOCK_STORAGE;

/**
 * @author Koward
 */
public final class WorldUtils {

    private static final HashSet<Material> SOLID_MATERIALS = Sets.newHashSet(
            Material.ROCK,
            Material.ANVIL,
            Material.GLASS,
            Material.IRON,
            Material.ICE,
            Material.PACKED_ICE,
            Material.REDSTONE_LIGHT,
            Material.PISTON
    );

    private WorldUtils() {
    }

    public static void playBroadcast(World world, @Nullable SoundEvent event) {
        playBroadcast(world, event, 1, 1);
    }

    public static void playBroadcast(World world, @Nullable SoundEvent event, float volume, float pitch) {
        if (event == null)
            return;
        world.getPlayers(EntityPlayer.class, (T) -> true).forEach(player -> world.playSound(null, player.getPosition(), event, SoundCategory.BLOCKS, volume, pitch));
    }

    public static void removeTask(EntityLiving entity, Class<? extends EntityAIBase> clazz) {
        entity.tasks.taskEntries.removeIf(task -> clazz.isAssignableFrom(task.action.getClass()));
    }

    public static boolean isSolid(World world, BlockPos pos, EnumFacing facing, IBlockState state) {
        return SOLID_MATERIALS.contains(state.getMaterial()) && state.getBlockFaceShape(world, pos, facing.getOpposite()) == BlockFaceShape.SOLID;
    }

    /**
     * Based on {@link World#getLightFromNeighbors(BlockPos)} build 2185
     */
    public static int getNaturalLightFromNeighbors(World worldIn, BlockPos pos) {
        return getNaturalLight(worldIn, pos, true, 0);
    }

    /**
     * Based on {@link World#getLight(BlockPos, boolean)} build 2185
     */
    private static int getNaturalLight(World worldIn, BlockPos pos, boolean checkNeighbors, int amount) {
        if (pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000) {
            if (checkNeighbors && worldIn.getBlockState(pos).useNeighborBrightness()) {
                int i1 = getNaturalLight(worldIn, pos.up(), false, 0);
                int i = getNaturalLight(worldIn, pos.east(), false, 0);
                int j = getNaturalLight(worldIn, pos.west(), false, 0);
                int k = getNaturalLight(worldIn, pos.south(), false, 0);
                int l = getNaturalLight(worldIn, pos.north(), false, 0);

                if (i > i1) {
                    i1 = i;
                }

                if (j > i1) {
                    i1 = j;
                }

                if (k > i1) {
                    i1 = k;
                }

                if (l > i1) {
                    i1 = l;
                }

                return i1;
            } else if (pos.getY() < 0) {
                return 0;
            } else {
                if (pos.getY() >= 256) {
                    pos = new BlockPos(pos.getX(), 255, pos.getZ());
                }

                Chunk chunk = worldIn.getChunk(pos);
                return getNaturalLightSubtracted(chunk, pos, amount);
            }
        } else {
            return 15;
        }
    }

    /**
     * Based on {@link Chunk#getLightSubtracted(BlockPos, int)} build 2185
     */
    private static int getNaturalLightSubtracted(Chunk chunkIn, BlockPos pos, int amount) {
        int i = pos.getX() & 15;
        int j = pos.getY();
        int k = pos.getZ() & 15;
        ExtendedBlockStorage extendedblockstorage = chunkIn.getBlockStorageArray()[j >> 4];

        if (extendedblockstorage == NULL_BLOCK_STORAGE) {
            return chunkIn.getWorld().provider.hasSkyLight() && amount < EnumSkyBlock.SKY.defaultLightValue ? EnumSkyBlock.SKY.defaultLightValue - amount : 0;
        } else {
            int l = !chunkIn.getWorld().provider.hasSkyLight() ? 0 : extendedblockstorage.getSkyLight(i, j & 15, k);
            l = l - amount;

            if (l < 0) {
                l = 0;
            }

            return l;
        }
    }

    public static double getDistance(BlockPos pos1, BlockPos pos2) {
        assert (pos1 != null);
        assert (pos2 != null);
        return new Vec3d(pos1).distanceTo(new Vec3d(pos2));
    }

    public static boolean spawnGhast(World world, BlockPos pos) {
        EntityGhast ghast = new EntityGhast(world);
        double failures = 1;

        int i = 0;

        double xPos = pos.getX(), yPos = pos.getY(), zPos = pos.getZ();
        do {
            ghast.setLocationAndAngles(xPos, yPos, zPos, world.rand.nextFloat() * 360.0F, 0.0F);
            AxisAlignedBB box = ghast.getEntityBoundingBox().offset(ghast.getPosition().up(5));
            boolean blocked = StreamSupport.stream(BlockPos.MutableBlockPos.getAllInBox(getMin(box), getMax(box)).spliterator(), false).anyMatch(p -> !world.isAirBlock(p));
            if (!blocked) {
                return world.spawnEntity(ghast);
            } else {
                failures++;
            }

            xPos = pos.getX() + (world.rand.nextDouble() - world.rand.nextDouble()) * Math.max(20, failures);
            yPos = pos.getY() + failures;
            zPos = pos.getZ() + (world.rand.nextDouble() - world.rand.nextDouble()) * Math.max(20, failures);
            i++;
        }
        while (i < 200);
        return false;
    }

    public static boolean isWater(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.FLOWING_WATER;
    }

    public static boolean isWaterSource(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() == Blocks.WATER && state.getValue(BlockLiquid.LEVEL) == 0;
    }

    public static AxisAlignedBB toInts(AxisAlignedBB box) {
        return new AxisAlignedBB((int) box.minX, (int) box.minY, (int) box.minZ, (int) box.maxX, (int) box.maxY, (int) box.maxZ);
    }

    public static Set<BlockPos> getPosAround(BlockPos pos, EnumFacing.Axis axis) {
        Set<BlockPos> posSet = Sets.newHashSet();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                switch (axis) {
                    case X:
                        posSet.add(pos.add(0, i, j));
                        break;
                    case Y:
                        posSet.add(pos.add(i, 0, j));
                        break;
                    case Z:
                        posSet.add(pos.add(i, j, 0));
                        break;
                }
            }
        }
        return posSet;
    }

    public static BlockPos getMin(AxisAlignedBB box) {
        return new BlockPos(box.minX, box.minY, box.minZ);
    }

    public static BlockPos getMax(AxisAlignedBB box) {
        return new BlockPos(box.maxX, box.maxY, box.maxZ);
    }

    public static boolean matches(IBlockState a, IBlockState b) {
        return b == null || a.equals(b);
    }

    public static void addDrop(LivingDropsEvent evt, ItemStack drop) {
        EntityItem item = new EntityItem(evt.getEntityLiving().getEntityWorld(), evt.getEntityLiving().posX, evt.getEntityLiving().posY, evt.getEntityLiving().posZ, drop);
        item.setDefaultPickupDelay();
        evt.getDrops().add(item);
    }

    public static boolean isPast(World world, TimeFrame frame) {
        return frame.start < getDayTicks(world);
    }

    public static boolean isTimeFrame(World world, TimeFrame frame) {
        return frame.isBetween((int) getDayTicks(world));
    }

    public static boolean isMoonPhase(World world, MoonPhase phase) {
        return phase.ordinal() == world.provider.getMoonPhase(world.getWorldTime());
    }

    public static int getDayTicks(World world) {
        return (int) (world.getWorldTime() % Time.DAY.getTicks());
    }

    public static boolean isPrecipitationAt(World world, BlockPos pos) {
        if (world.isRaining()) {
            if (world.canSeeSky(pos)) {
                return world.getPrecipitationHeight(pos).getY() <= pos.getY();
            }
        }
        return false;
    }

    public static void setWeatherCleared(MinecraftServer server) {
        for (int i = 0; i < server.worlds.length; ++i) {
            World world = server.worlds[i];
            WorldInfo info = world.getWorldInfo();
            info.setCleanWeatherTime((int) Time.DAY.ticks);
            info.setRainTime(0);
            info.setThunderTime(0);
            info.setRaining(false);
            info.setThundering(false);
        }
    }

    public static void setAllWorldTimes(MinecraftServer server, TimeFrame time) {
        for (int i = 0; i < server.worlds.length; ++i) {
            server.worlds[i].setWorldTime((long) time.start);
        }
    }


    public enum MoonPhase {
        Full,
        WaningGibbous,
        LastQuarter,
        WaningCrescent,
        New,
        WaxingCrescent,
        FirstQuarter,
        WaxingGibbous
    }

    public enum Time {
        SECOND(0.27),
        MINUTE(16.6),
        HOUR(1000),
        DAY(24000);
        private double ticks;

        Time(double ticks) {
            this.ticks = ticks;
        }

        public double getTicks() {
            return ticks;
        }
    }

    public enum TimeFrame {
        DAWN(0, 3600),
        MORNING(1000),
        NOON(5000, 7000),
        DUSK(10200, 12700),
        MIDNIGHT(17000, 19000),
        NIGHT(13001, 24000),
        DAY(0, 13000);
        private static final Random rand = new Random();
        private int start, end;

        TimeFrame(int start, int end) {
            this.start = start;
            this.end = end;
        }

        TimeFrame(int time) {
            this(time, time);
        }

        public boolean isBetween(int time) {
            return time >= start && time <= end;
        }

        public int randomBetween() {
            return rand.nextInt((end - start) + 1) + start;
        }
    }

}
