package betterwithmods.common.world.gen.village.field;

import betterwithmods.common.world.gen.village.AbandonedVillagePiece;
import net.minecraft.world.gen.structure.StructureVillagePieces;

/**
 * Created by primetoxinz on 6/30/17.
 */
public abstract class BWFieldBase extends AbandonedVillagePiece {
    public BWFieldBase() {
    }

    public BWFieldBase(StructureVillagePieces.Start start, int type) {
        super(start, type);
    }
}
