package betterwithmods.network.handler;

import betterwithmods.BWMod;
import betterwithmods.network.messages.MessageHarness;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageHarnessHandler extends BWMessageHandler<MessageHarness> {
    @Override
    public void handleMessage(MessageHarness message, MessageContext context) {
        BWMod.proxy.syncHarness(message.entity,message.stack);
    }
}
