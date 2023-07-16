package betterwithmods.network.handler;

import betterwithmods.BWMod;
import betterwithmods.network.messages.MessageFXDynamite;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageFXDynamiteHandler extends BWMessageHandler<MessageFXDynamite> {
    @Override
    public void handleMessage(MessageFXDynamite message, MessageContext context) {
        BWMod.proxy.createExplosionParticles(message.getCenter(),message.size,message.affectedPositions);
    }
}
