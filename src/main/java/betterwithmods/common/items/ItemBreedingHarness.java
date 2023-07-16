package betterwithmods.common.items;

import betterwithmods.client.BWCreativeTabs;
import betterwithmods.module.gameplay.Gameplay;

/**
 * Purpose:
 *
 * @author primetoxinz
 * @version 11/15/16
 */
public class ItemBreedingHarness extends ItemAltName {
    public ItemBreedingHarness() {
        setCreativeTab(BWCreativeTabs.BWTAB);
        setMaxStackSize(1);
    }

    @Override
    public String[] getLocations() {
        if (Gameplay.kidFriendly)
            return new String[]{"breeding_harness_kf"};
        else
            return new String[]{"breeding_harness"};
    }
}
