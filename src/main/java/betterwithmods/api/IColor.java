package betterwithmods.api;

import net.minecraft.item.EnumDyeColor;

public interface IColor {
    boolean dye(EnumDyeColor color);

    int getColor(int index);
}
