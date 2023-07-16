package betterwithmods.module.tweaks;

import betterwithmods.module.Feature;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Created by primetoxinz on 5/6/17.
 */
public class Sinkholes extends Feature {
    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            return;

        EntityPlayer player = event.player;
        if (player.onGround) {
            int j6 = MathHelper.floor(player.posX);
            int i1 = MathHelper.floor(player.posY - 0.20000000298023224D);
            int k6 = MathHelper.floor(player.posZ);
            BlockPos pos = new BlockPos(j6, i1, k6);
            World world = player.world;
            IBlockState standing = world.getBlockState(pos);
            if(world.isAirBlock(pos.down()) && standing.getBlock() instanceof BlockFalling) {
                world.scheduleBlockUpdate(pos,standing.getBlock(),0,5);
            }

        }
    }

    @Override
    public String getFeatureDescription() {
        return "Falling blocks update when players stand on them, causing them to fall if the blocks are not supported by non-falling blocks. ";
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }
}
