package betterwithmods.common.items;

import betterwithmods.api.IMultiLocations;
import betterwithmods.module.gameplay.Gameplay;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class ItemAltName extends Item implements IMultiLocations {

    @Override
    public String getTranslationKey(ItemStack stack) {
        if (Gameplay.kidFriendly)
            return super.getTranslationKey(stack) + "_kf";
        return super.getTranslationKey(stack);
    }
}
