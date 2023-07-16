package betterwithmods.client.gui.bulk;

import betterwithmods.BWMod;
import betterwithmods.client.container.bulk.ContainerCookingPot;
import betterwithmods.client.gui.GuiProgress;
import betterwithmods.common.blocks.mechanical.tile.TileEntityCookingPot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiCookingPot extends GuiProgress {
    private static final ResourceLocation TEXTURE = new ResourceLocation(BWMod.MODID, "textures/gui/cooking_pot.png");

    private final TileEntityCookingPot tile;
    private final ContainerCookingPot container;

    public GuiCookingPot(EntityPlayer player, TileEntityCookingPot tile) {
        super(new ContainerCookingPot(player, tile), TEXTURE);
        this.container = (ContainerCookingPot) this.inventorySlots;
        this.ySize = 193;
        this.tile = tile;
    }


    @Override
    public String getTitle() {
        return tile.getName();
    }

    @Override
    public int getTitleY() {
        return 6;
    }


    @Override
    public int getX() {
        return 81;
    }

    @Override
    public int getY() {
        return 19;
    }

    @Override
    public int getTextureX() {
        return 176 + (container.getHeat() > 1 ? getWidth(): 0);
    }

    @Override
    public int getTextureY() {
        return 14;
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public int getWidth() {
        return 14;
    }

    @Override
    protected int toPixels() {
        return (int) (14 * getPercentage());
    }
}
