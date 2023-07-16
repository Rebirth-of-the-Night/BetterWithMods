package betterwithmods.network;

import betterwithmods.BWMod;
import betterwithmods.network.handler.*;
import betterwithmods.network.messages.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class BWNetwork {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(BWMod.MODID);
    private static int i = 0;

    public static void registerNetworking() {
        INSTANCE.registerMessage(MessageHungerShakeHandler.class, MessageHungerShake.class, i++, Side.CLIENT);
        INSTANCE.registerMessage(MessageHarnessHandler.class, MessageHarness.class, i++, Side.CLIENT);
        INSTANCE.registerMessage(MessageRotateHandler.class, MessageRotate.class, i++, Side.CLIENT);
        INSTANCE.registerMessage(MessageGloomHandler.class, MessageGloom.class, i++, Side.CLIENT);
        INSTANCE.registerMessage(MessagePlacedHandler.class, MessagePlaced.class, i++, Side.CLIENT);
        INSTANCE.registerMessage(MessageFXDynamiteHandler.class, MessageFXDynamite.class, i++, Side.CLIENT);
    }

    public static void sendPacket(Entity player, Packet<?> packet) {
        if (player instanceof EntityPlayerMP && ((EntityPlayerMP) player).connection != null) {
            ((EntityPlayerMP) player).connection.sendPacket(packet);
        }
    }

    public static void sendTo(IMessage message, EntityPlayerMP player) {
        INSTANCE.sendTo(message, player);
    }

    public static void sendToAllAround(IMessage message, World world, BlockPos pos) {
        INSTANCE.sendToAllAround(message, new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 128));
    }

}
