package betterwithmods.common.registry;

import betterwithmods.api.util.IWood;
import betterwithmods.common.items.ItemBark;
import betterwithmods.common.items.ItemMaterial;
import net.minecraft.item.ItemStack;

public  class Wood implements IWood {
    private ItemStack log, plank, bark;
    private boolean isSoulDust = false;

    public Wood(ItemStack log, ItemStack plank) {
        this.log = log;
        this.plank = plank;
        this.bark = ItemBark.getStack("oak", 1);
    }

    public Wood(ItemStack log, ItemStack plank, ItemStack bark) {
        this.log = log;
        this.plank = plank;
        this.bark = bark;
    }

    public Wood(ItemStack log, ItemStack plank, ItemStack bark, boolean isSoulDust) {
        this(log, plank, bark);
        this.isSoulDust = isSoulDust;
    }

    public ItemStack getLog(int count) {
        ItemStack copy = log.copy();
        copy.setCount(count);
        return copy;
    }

    public ItemStack getPlank(int count) {
        ItemStack copy = plank.copy();
        copy.setCount(count);
        return copy;
    }

    public ItemStack getBark(int count) {
        ItemStack copy = bark.copy();
        copy.setCount(count);
        return copy;
    }

    public ItemStack getSawdust(int count) {
        return isSoulDust ? ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SOUL_DUST, count) : ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SAWDUST, count);
    }

}