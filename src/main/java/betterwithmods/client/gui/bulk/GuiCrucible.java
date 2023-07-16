package betterwithmods.client.gui.bulk;

import betterwithmods.common.blocks.mechanical.tile.TileEntityCrucible;
import net.minecraft.entity.player.EntityPlayer;

public class GuiCrucible extends GuiCookingPot {
    public GuiCrucible(EntityPlayer player, TileEntityCrucible crucible) {
        super(player, crucible);
    }
}
