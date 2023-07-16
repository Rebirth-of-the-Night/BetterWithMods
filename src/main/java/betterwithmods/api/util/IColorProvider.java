package betterwithmods.api.util;

import net.minecraft.item.ItemStack;

public interface IColorProvider {
    int getColor(ItemStack stack);

    default float[] getColorComponents(ItemStack stack) {
        int color = getColor(stack);
        float r = ((color >> 16) & 0xff) / 255.0f;
        float g = ((color >> 8) & 0xff) / 255.0f;
        float b = ((color) & 0xff) / 255.0f;
        return new float[]{r, g, b};
    }

}
