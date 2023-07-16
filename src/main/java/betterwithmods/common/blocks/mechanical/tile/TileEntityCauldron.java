package betterwithmods.common.blocks.mechanical.tile;

import betterwithmods.common.BWRegistry;

public class TileEntityCauldron extends TileEntityCookingPot {
    public TileEntityCauldron() {
        super(BWRegistry.CAULDRON);
    }

    @Override
    public String getName() {
        return "inv.cauldron.name";
    }

}
