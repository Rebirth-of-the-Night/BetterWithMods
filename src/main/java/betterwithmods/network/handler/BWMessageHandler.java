package betterwithmods.network.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BWMessageHandler<REQ extends IMessage> implements IMessageHandler<REQ, IMessage> {

    @SideOnly(Side.CLIENT)
    public World getClientWorld() {
        return Minecraft.getMinecraft().world;
    }

    public abstract void handleMessage(REQ message, MessageContext context);

    @Override
    public final IMessage onMessage(REQ message, MessageContext context) {
        FMLCommonHandler.instance().getWorldThread(context.netHandler).addScheduledTask(() -> this.handleMessage(message, context));
        return null;
    }

}
