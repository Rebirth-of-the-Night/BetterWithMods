package betterwithmods.client.container.inventory;

import betterwithmods.common.blocks.tile.TileEntitySteelAnvil;
import betterwithmods.common.registry.anvil.AnvilCraftingManager;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraftforge.items.ItemStackHandler;

public class ItemCraftHandler extends ItemStackHandler {

    public InventoryCrafting crafting;
    private TileEntitySteelAnvil te;

    public ItemCraftHandler(int size, TileEntitySteelAnvil te) {
        super(size);
        this.te = te;
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (crafting != null)
            te.setResult(AnvilCraftingManager.findMatchingResult(this.crafting, te.getWorld()));
        super.onContentsChanged(slot);
    }
}