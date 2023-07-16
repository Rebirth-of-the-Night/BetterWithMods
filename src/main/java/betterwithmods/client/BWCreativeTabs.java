package betterwithmods.client;

import betterwithmods.common.BWMItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BWCreativeTabs {
    public static final CreativeTabs BWTAB = new CreativeTabs("bwm:bwTab") {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(BWMItems.AXLE_GENERATOR);
        }
    };
}
