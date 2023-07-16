package betterwithmods.client.gui.bulk;

import betterwithmods.common.blocks.mechanical.tile.TileEntityCauldron;
import net.minecraft.entity.player.EntityPlayer;

public class GuiCauldron extends GuiCookingPot {
    public GuiCauldron(EntityPlayer player, TileEntityCauldron cauldron) {
        super(player, cauldron);
    }
}
