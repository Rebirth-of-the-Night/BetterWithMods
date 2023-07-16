package betterwithmods.common.blocks.mechanical.tile;

import betterwithmods.common.BWRegistry;

public class TileEntityCrucible extends TileEntityCookingPot {
    public TileEntityCrucible() {
        super(BWRegistry.CRUCIBLE);
    }

    @Override
    public String getName() {
        return "inv.crucible.name";
    }

}
