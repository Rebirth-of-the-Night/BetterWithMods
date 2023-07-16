package betterwithmods.api.client;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IColorable {
    @SideOnly(Side.CLIENT)
    IItemColor getColorHandler();

}

