package betterwithmods.network.handler;

import betterwithmods.network.messages.MessageRotate;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageRotateHandler extends BWMessageHandler<MessageRotate> {
    @Override
    public void handleMessage(MessageRotate message, MessageContext context) {
//        BWMod.proxy.rotateEntity(message.entity,message.yaw,message.pitch);
    }
}
