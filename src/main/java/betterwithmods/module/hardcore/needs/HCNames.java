package betterwithmods.module.hardcore.needs;

import betterwithmods.module.Feature;
import betterwithmods.module.gameplay.PlayerDataHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by primetoxinz on 5/21/17.
 */
public class HCNames extends Feature {

    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent e) {
        if (e.getEntity() instanceof EntityPlayerMP) {
            EntityPlayer player = (EntityPlayer) e.getEntity();
            Team team = player.getTeam();
            Scoreboard scoreboard = e.getWorld().getScoreboard();
            if(team == null) {
                scoreboard.addPlayerToTeam(player.getName(), PlayerDataHandler.TEAM);
            }
        }
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @Override
    public String getFeatureDescription() {
        return "Disables Player Name Tags";
    }
}
