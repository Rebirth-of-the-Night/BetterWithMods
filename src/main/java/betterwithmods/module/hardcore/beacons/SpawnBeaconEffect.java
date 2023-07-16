package betterwithmods.module.hardcore.beacons;

import betterwithmods.BWMod;
import betterwithmods.common.blocks.tile.TileEntityBeacon;
import betterwithmods.common.registry.block.recipe.BlockIngredient;
import betterwithmods.module.hardcore.world.spawn.HCSpawn;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by primetoxinz on 7/17/17.
 */
@Mod.EventBusSubscriber(modid = BWMod.MODID)
public class SpawnBeaconEffect extends BeaconEffect {

    public static final HashMap<BlockPos, HashSet<BindingPoint>> SPAWN_LIST = Maps.newHashMap();

    public SpawnBeaconEffect() {
        super("spawn", new BlockIngredient("blockSoulforgedSteel"), EntityPlayer.class);
        this.setBaseBeamColor(Color.GRAY);
    }


    public static void removeAll(BlockPos pos) {
        SPAWN_LIST.remove(pos);
    }

    public static void addPoint(BlockPos pos, BindingPoint point) {
        if (SPAWN_LIST.containsKey(pos)) {
            HashSet<BindingPoint> points = SPAWN_LIST.get(pos);
            if (!containsEntry(pos, point))
                points.add(point);
            SPAWN_LIST.put(pos, points);
        } else {
            SPAWN_LIST.put(pos, Sets.newHashSet(point));
        }
    }

    public static boolean containsEntry(BlockPos pos, BindingPoint point) {
        if (SPAWN_LIST.containsKey(pos)) {
            HashSet<BindingPoint> points = SPAWN_LIST.get(pos);
            return points.contains(point);
        }
        return false;
    }

    public static BlockPos findSpawn(EntityPlayer player, World world) {
        for (BlockPos pos : SPAWN_LIST.keySet()) {
            if (shouldSpawnHere(pos, player, world)) {
                return pos;
            }
        }
        return null;
    }

    public static boolean shouldSpawnHere(BlockPos beacon, EntityPlayer player, World world) {
        if (SPAWN_LIST.containsKey(beacon)) {
            Set<BindingPoint> points = SPAWN_LIST.get(beacon);
            for (BindingPoint point : points) {
                if (point.canSpawn(beacon, player, world))
                    return true;
            }
        }
        return false;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void findSpawn(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof EntityPlayerMP))
            return;
        EntityPlayerMP player = (EntityPlayerMP) event.getEntity();

        BlockPos pos = findSpawn(player, player.world);
        if (pos != null) {
            //Set spawn up one block so you don't spawn in the beacon.
            HCSpawn.setSpawn(player, pos.up());
        }
    }

    @Override
    public boolean onPlayerInteracted(World world, BlockPos pos, int level, EntityPlayer player, EnumHand hand, ItemStack stack) {
        BindingPoint point = new BindingPoint(player, level);
        //TODO improve this in 1.13
        String message = "bwm.message.spawn_beacon.already_set";
        if (!containsEntry(pos, point)) {
            addPoint(pos, point);
            message = "bwm.message.spawn_beacon.set";
        }
        player.sendStatusMessage(new TextComponentTranslation(message), true);
        return true;
    }

    @Override
    public void onBeaconCreate(@Nonnull World world, @Nonnull BlockPos pos, int beaconLevel) {
    }

    @Override
    public void apply(NonNullList<EntityLivingBase> entitiesInRange, @Nonnull World world, @Nonnull BlockPos pos, int beaconLevel) {
    }

    @Override
    public void onBeaconBreak(World world, BlockPos pos, int level) {
    }

    public enum SpawnType {

        LEVEL1(40),
        LEVEL2(160),
        LEVEL3(-1),
        LEVEL4(-2);

        public static SpawnType[] VALUES = values();
        int range;

        SpawnType(int range) {
            this.range = range;
        }

        public boolean inRange(BlockPos beacon, EntityPlayer player, World world) {
            switch (this.range) {
                case -1:
                    return player.dimension == world.provider.getDimension();
                case -2:
                    return true;
                default:
                    double d = player.getDistance(beacon.getX(), beacon.getY(), beacon.getZ());
                    return d <= this.range;
            }
        }
    }

    public static class BindingPoint implements INBTSerializable<NBTTagCompound> {

        private UUID uuid;
        private SpawnType type;


        public BindingPoint(EntityPlayer player, int level) {
            this(player.getGameProfile().getId(), SpawnType.VALUES[level]);
        }

        public BindingPoint(UUID uuid, SpawnType type) {
            this.uuid = uuid;
            this.type = type;
        }

        public BindingPoint(NBTTagCompound compound) {
            deserializeNBT(compound);
        }

        public boolean canSpawn(BlockPos beacon, EntityPlayer player, World world) {
            TileEntity tile = world.getTileEntity(beacon);
            if (isPlayer(player) && (tile instanceof TileEntityBeacon) && (((TileEntityBeacon) tile).getLevels() - 1) == type.ordinal())
                return type.inRange(beacon, player, world);
            return false;
        }

        public boolean isPlayer(EntityPlayer player) {
            return player.getGameProfile() != null && player.getGameProfile().getId().equals(uuid);
        }

        @Override
        public boolean equals(Object o) {
            return this.hashCode() == o.hashCode();
        }

        @Override
        public int hashCode() {
            return uuid.hashCode() ^ type.hashCode();
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("type", type.ordinal());
            tag.setString("uuid", uuid.toString());
            return tag;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            this.type = SpawnType.VALUES[nbt.getInteger("type")];
            this.uuid = UUID.fromString(nbt.getString("uuid"));
        }
    }
}
