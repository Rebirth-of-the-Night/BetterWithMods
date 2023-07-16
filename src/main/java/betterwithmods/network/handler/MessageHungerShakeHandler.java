package betterwithmods.network.handler;

import betterwithmods.client.gui.GuiHunger;
import betterwithmods.network.messages.MessageHungerShake;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageHungerShakeHandler extends BWMessageHandler<MessageHungerShake> {
    @Override
    public void handleMessage(MessageHungerShake message, MessageContext context) {
        GuiHunger.INSTANCE.shake();
    }
}
