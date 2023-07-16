package betterwithmods.util.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class HealthCommand extends CommandBase {
    @Override
    public String getName() {
        return "health";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/health <health points>";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 4;
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            if (args.length > 0) {
                float health = Float.parseFloat(args[0]);
                ((EntityPlayer) sender).setHealth(health);
            }
        }
    }
}
