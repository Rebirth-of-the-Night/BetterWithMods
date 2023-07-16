package betterwithmods.common.blocks.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Created by primetoxinz on 5/14/17.
 */
public class SimpleStackHandler extends ItemStackHandler {
    private TileEntity tile;
    public SimpleStackHandler(int size, TileEntity tile) {
        super(size);
        this.tile = tile;
    }
    @Override
    public void onContentsChanged(int slot) {
        tile.markDirty();
    }

    @Override
    public String toString() {
        return stacks.toString();
    }
}
