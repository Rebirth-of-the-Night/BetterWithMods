package betterwithmods.event;

import betterwithmods.BWMod;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.BWSounds;
import betterwithmods.util.player.PlayerHelper;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = BWMod.MODID)
public class PenaltyEventHandler {

    @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent event) {

        //This has to fun on clientside and serverside
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            //Whether the player can jump.
            if (!BWRegistry.PENALTY_HANDLERS.canJump(player)) {
                event.getEntityLiving().motionX = 0;
                event.getEntityLiving().motionY = 0;
                event.getEntityLiving().motionZ = 0;
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            return;

        EntityPlayer player = event.player;
        //Don't run on client side
        if (player.world.isRemote)
            return;

        if (!PlayerHelper.isSurvival(player))
            return;

        //Handle whether the player can sprint
        if (!BWRegistry.PENALTY_HANDLERS.canSprint(player)) {
            player.setSprinting(false);
        }

        //Swimming
        if (player.isInWater() && !BWRegistry.PENALTY_HANDLERS.canSwim(player)) {
            if (!PlayerHelper.isNearBottom(player)) {
                player.motionY -= 0.04;
            }
        }
    }


    private static Object2IntMap<UUID> painTimers = new Object2IntOpenHashMap<>();

    private static boolean inPain(EntityPlayer player) {
        UUID uuid = player.getUniqueID();
        if(uuid == null)
            return false;
        if(painTimers.getOrDefault(uuid,0) > 60) {
            painTimers.put(uuid, 0);
            return true;
        }
        painTimers.put(uuid, painTimers.getOrDefault(uuid, 0) + 1);
        return false;
    }

    @SubscribeEvent
    public static void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            World world = player.world;

            if (!PlayerHelper.isSurvival(player) || player.isRiding()) {
                //Remove the modifier when gamemode changes.
                PlayerHelper.removeModifier(player, SharedMonsterAttributes.MOVEMENT_SPEED, PlayerHelper.PENALTY_SPEED_UUID);
                return;
            }
            //Speed
            double speed = BWRegistry.PENALTY_HANDLERS.getSpeedModifier(player);
            if (speed != 0) {
                PlayerHelper.changeSpeed(player, "Penalty Speed Modifier", speed, PlayerHelper.PENALTY_SPEED_UUID);
            }

            //Pain

            if (!world.isRemote && BWRegistry.PENALTY_HANDLERS.inPain(player)) {
                if (PlayerHelper.isMoving(player) && inPain(player)) {
                    world.playSound(null, player.getPosition(), BWSounds.OOF, SoundCategory.BLOCKS, 0.75f, 1f);
                }
            }

        }
    }

    @SubscribeEvent
    public void onPlayerAttack(LivingAttackEvent event) {
        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            if (PlayerHelper.isSurvival(player)) {
                if (!BWRegistry.PENALTY_HANDLERS.canAttack(player)) {
                    player.playSound(BWSounds.OOF, 0.75f, 1f);
                    event.setCanceled(true);
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }
}
