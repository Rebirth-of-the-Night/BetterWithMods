package betterwithmods.client.gui;

import betterwithmods.BWMod;
import betterwithmods.client.container.anvil.ContainerSteelAnvil;
import betterwithmods.common.blocks.tile.TileEntitySteelAnvil;
import net.minecraft.util.ResourceLocation;

public class GuiSteelAnvil extends GuiBase {

    private static final ResourceLocation tex = new ResourceLocation(BWMod.MODID, "textures/gui/steel_anvil.png");
    private TileEntitySteelAnvil tile;

    public GuiSteelAnvil(TileEntitySteelAnvil tileEntity, ContainerSteelAnvil container) {
        super(container, tex);
        this.ySize = 183;
        tile = tileEntity;
    }

    @Override
    public String getTitle() {
        return tile.getName();
    }

    @Override
    public int getTitleY() {
        return 6;
    }
}