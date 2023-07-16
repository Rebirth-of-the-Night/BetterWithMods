package betterwithmods.network.handler;

import betterwithmods.BWMod;
import betterwithmods.network.messages.MessageGloom;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageGloomHandler extends BWMessageHandler<MessageGloom> {
    @Override
    public void handleMessage(MessageGloom message, MessageContext context) {
        if (message.uuid != null)
            BWMod.proxy.syncGloom(message.uuid, message.gloom);
    }
}
