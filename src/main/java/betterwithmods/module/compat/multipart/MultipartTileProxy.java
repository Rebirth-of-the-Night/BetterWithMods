package betterwithmods.module.compat.multipart;

import mcmultipart.api.multipart.IMultipartTile;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.tileentity.TileEntity;

public class MultipartTileProxy implements IMultipartTile {

    private final TileEntity tile;

    protected MultipartTileProxy(TileEntity tile) {
        this.tile = tile;
    }

    public static MultipartTileProxy proxy(TileEntity tile) {
        if (tile instanceof ITickable) {
            return new MultipartTileProxy.Ticking(tile);
        }
        return new MultipartTileProxy(tile);
    }

    @Override
    public TileEntity getTileEntity() {
        return tile;
    }

    private static class Ticking extends MultipartTileProxy implements ITickable {

        protected Ticking(TileEntity tile) {
            super(tile);
        }

        @Override
        public void tick() {
            ((ITickable) getTileEntity()).tick();
        }
    }

}
