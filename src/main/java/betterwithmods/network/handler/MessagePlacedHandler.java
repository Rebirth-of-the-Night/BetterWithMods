package betterwithmods.network.handler;

import betterwithmods.BWMod;
import betterwithmods.network.messages.MessagePlaced;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePlacedHandler extends BWMessageHandler<MessagePlaced> {
    @Override
    public void handleMessage(MessagePlaced message, MessageContext context) {
        if (message.pos != null)
            BWMod.proxy.syncPlaced(message.pos);
    }
}
