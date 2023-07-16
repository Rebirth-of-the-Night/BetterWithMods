package betterwithmods.module.hardcore.world.spawn;

import betterwithmods.BWMod;
import betterwithmods.module.Feature;
import betterwithmods.module.GlobalConfig;
import betterwithmods.module.gameplay.PlayerDataHandler;
import betterwithmods.util.WorldUtils;
import betterwithmods.util.player.PlayerHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Random;

import static net.minecraft.world.WorldType.FLAT;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class HCSpawn extends Feature {

    public static final Random RANDOM = new Random();
    public static final String GAMERULE_CHANGETIME = "doChangeTimeOnDeath";
    public static int HARDCORE_SPAWN_INTERNAL_RADIUS;
    public static int HARDCORE_SPAWN_RADIUS;
    public static int HARDCORE_SPAWN_COOLDOWN_RADIUS;
    public static int HARDCORE_SPAWN_COOLDOWN; //20 min
    public static int HARDCORE_SPAWN_MAX_ATTEMPTS = 20;
    public static ResourceLocation PLAYER_SPAWN_POSITION = new ResourceLocation(BWMod.MODID, "spawn_position");

    public static void setSpawn(EntityPlayer player, BlockPos pos) {
        SpawnSaving.getCapability(player).ifPresent(cap -> cap.setPos(pos));
        player.setSpawnPoint(pos, true);
    }

    public static BlockPos getSpawn(EntityPlayer player) {
        return SpawnSaving.getCapability(player).map(SpawnSaving::getPos).orElse(player.world.getSpawnPoint());
    }

    public static BlockPos getRandomPoint(World world, BlockPos origin, int min, int max) {
        BlockPos ret = origin;
        double length = MathHelper.getInt(RANDOM, min, max);
        double angle = MathHelper.nextDouble(RANDOM, 0, 360);
        double customX = -Math.sin(angle) * length;
        double customZ = Math.cos(angle) * length;
        ret = ret.add(MathHelper.floor(customX) + 0.5D, 1.5D, MathHelper.floor(customZ) + 0.5D);
        ret = world.getTopSolidOrLiquidBlock(ret);
        return ret;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(SpawnSaving.class, new SpawnSaving.Storage(), SpawnSaving::new);
    }

    @Override
    public void setupConfig() {
        HARDCORE_SPAWN_RADIUS = loadPropInt("Hardcore Spawn Radius", "Radius from original spawn which you will be randomly spawned", 2000);
        HARDCORE_SPAWN_COOLDOWN_RADIUS = loadPropInt("Hardcore Spawn Cooldown Radius", "Radius from your previous spawn you will spawn if you die during a cooldown period", 100);
        HARDCORE_SPAWN_COOLDOWN = loadPropInt("Hardcore Spawn Cooldown Ticks", "Amount of time after a HCSpawn which you will continue to spawn in the same area", 12000);
        HARDCORE_SPAWN_INTERNAL_RADIUS = loadPropInt("Hardcore Spawn Internal Radius", "This internal radius will stop the player from spawning too close to the original spawn", 125);
    }

    @Override
    public String getFeatureDescription() {
        return "Makes it so death is an actual issue as you will spawn randomly within a 2000 block radius of your original spawn. Compasses still point to original spawn.";
    }

    @Override
    public boolean requiresMinecraftRestartToEnable() {
        return true;
    }

    /**
     * Random Respawn. Less intense when there is a short time since death.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void randomRespawn(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof EntityPlayerMP)) return;
        if (event.getEntity().getEntityWorld().getWorldType() == FLAT)
            return;
        EntityPlayerMP player = (EntityPlayerMP) event.getEntity();

        PlayerDataHandler.PlayerInfo info = PlayerDataHandler.getPlayerInfo(player);

        if (PlayerHelper.isSurvival(player)) {
            int timeSinceDeath = info.getTicksSinceDeath();
            boolean isNew = timeSinceDeath >= HARDCORE_SPAWN_COOLDOWN;
            if (isNew) {
                //Only reset the death timer when the cooldown is met, so you can't prolong a spawn are by intentionally dying to reset the timer.
                info.setTicksSinceDeath(0);
            }
            BlockPos currentSpawn = isNew ? player.world.getSpawnPoint() : getSpawn(player);
            int radius = isNew ? HARDCORE_SPAWN_RADIUS : HARDCORE_SPAWN_COOLDOWN_RADIUS;

            int internalRadius = isNew ? HARDCORE_SPAWN_INTERNAL_RADIUS : 0;

            if (GlobalConfig.debug)
                player.sendMessage(new TextComponentString(String.format("Spawn: %s, %s, %s, %s", isNew, currentSpawn, radius, timeSinceDeath)));
            BlockPos newPos = getRespawnPoint(player, currentSpawn, internalRadius, radius);
            setSpawn(player, newPos);
        }
    }

    /**
     * Find a random position to respawn. Tries 20 times maximum to find a
     * suitable place. Else, the previous SP will remain unchanged.
     *
     * @return The new BlockPos
     */
    private BlockPos getRespawnPoint(EntityPlayer player, BlockPos spawnPoint, int min, int max) {
        World world = player.getEntityWorld();
        BlockPos ret = spawnPoint;
        if (!world.provider.isNether()) {
            boolean found = false;
            for (int tryCounter = 0; tryCounter < HARDCORE_SPAWN_MAX_ATTEMPTS; tryCounter++) {

                ret = getRandomPoint(world, spawnPoint, min, max);
                // Check if the position is correct
                int cmp = ret.getY() - world.provider.getAverageGroundLevel();
                Material check = world.getBlockState(ret).getMaterial();
                IBlockState state = world.getBlockState(ret.up());
                if (cmp >= 0 && !check.isLiquid() && state.getBlock().canSpawnInBlock()) {
                    found = true;
                    break;
                }
            }
            if (!found)
                BWMod.logger.info("New respawn point could not be found.");
        }

        return ret;
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(PLAYER_SPAWN_POSITION, new SpawnSaving((EntityPlayer) event.getObject()));
        }
    }

    @SubscribeEvent
    public void clone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            setSpawn(event.getEntityPlayer(), getSpawn(event.getOriginal()));
        } else {
            setSpawn(event.getEntityPlayer(), event.getEntityPlayer().getEntityWorld().getSpawnPoint());
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        event.getWorld().getGameRules().addGameRule(GAMERULE_CHANGETIME, "false", GameRules.ValueType.BOOLEAN_VALUE);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        EntityPlayer player = event.player;
        World world = player.getEntityWorld();
        if (!world.getGameRules().getBoolean(GAMERULE_CHANGETIME))
            return;
        if (!PlayerHelper.isSurvival(player) && !(player instanceof EntityPlayerMP))
            return;

        MinecraftServer server = player.getServer();
        if (server != null && server.getPlayerList().getPlayers().size() == 1) {
            PlayerDataHandler.PlayerInfo info = PlayerDataHandler.getPlayerInfo(player);

            int timeSinceDeath = info.getTicksSinceDeath();
            boolean isNew = timeSinceDeath >= HARDCORE_SPAWN_COOLDOWN;
            if (isNew) {
                WorldUtils.setWeatherCleared(server);
                WorldUtils.setAllWorldTimes(server, WorldUtils.TimeFrame.DAWN);
            }
        }

    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            return;
        if (event.player instanceof EntityPlayerMP) {
            PlayerDataHandler.PlayerInfo info = PlayerDataHandler.getPlayerInfo(event.player);
            info.incrementTicksSinceDeath(1);
        }
    }
}

