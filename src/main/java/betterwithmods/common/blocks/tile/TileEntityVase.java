package betterwithmods.common.blocks.tile;

import betterwithmods.common.items.ItemMaterial;
import net.minecraft.item.ItemStack;

/**
 * Created by Christian on 24.09.2016.
 */
public class TileEntityVase extends TileBasicInventory {
    public TileEntityVase() {
    }


    @Override
    public void onBreak() {
        ItemStack vaseitem = inventory.getStackInSlot(0);
        if (vaseitem.isItemEqual(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.BLASTING_OIL))) {
            float intensity = 1.5f;
            getWorld().createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), intensity, true);
        } else  {
            super.onBreak();
        }
    }


    @Override
    public int getInventorySize() {
        return 1;
    }

}
