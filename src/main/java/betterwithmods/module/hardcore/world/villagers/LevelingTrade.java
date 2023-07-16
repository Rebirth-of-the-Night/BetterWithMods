package betterwithmods.module.hardcore.world.villagers;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.village.MerchantRecipe;

public class LevelingTrade extends MerchantRecipe {
    public boolean levels;

    public LevelingTrade(NBTTagCompound tagCompound) {
        super(tagCompound);
    }

    public LevelingTrade(ItemStack buy1, ItemStack buy2, ItemStack sell) {
        this(buy1, buy2, sell, 0, 1);
    }

    public LevelingTrade(ItemStack buy1, ItemStack buy2, ItemStack sell, int toolUsesIn, int maxTradeUsesIn) {
        super(buy1, buy2, sell, toolUsesIn, maxTradeUsesIn);
    }

    public LevelingTrade(ItemStack buy1, ItemStack sell) {
        this(buy1, ItemStack.EMPTY, sell);
    }

    public LevelingTrade(ItemStack buy1, Item sellItem) {
        super(buy1, sellItem);
    }

    public boolean shouldLevel() {
        return levels;
    }

    public LevelingTrade leveling() {
        this.levels = true;
        return this;
    }
}
