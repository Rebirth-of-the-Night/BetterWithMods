package betterwithmods.client.tesr;

import betterwithmods.client.model.render.FluidRenderUtils;
import betterwithmods.common.blocks.tile.TileFluid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import javax.annotation.Nonnull;

public class TESRBucket extends TileEntitySpecialRenderer<TileFluid> {
    protected static Minecraft mc = Minecraft.getMinecraft();

    private static float renderOffset = 12 / 16f;

    @Override
    public void render(@Nonnull TileFluid tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        FluidTank tank = tile.getTank();
        render(tank, tile.getPos(), x, y, z, partialTicks, destroyStage, alpha);
    }

    public static void render(FluidTank tank, BlockPos pos, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        FluidStack liquid = tank.getFluid();

        if (liquid != null) {
            float height = liquid.amount / ((float) tank.getCapacity());

            if (renderOffset > 1.2f || renderOffset < -1.2f) {
                renderOffset -= (renderOffset / 12f + 0.1f) * partialTicks;
            } else {
                renderOffset = 0;
            }

            float d = FluidRenderUtils.FLUID_OFFSET;
            FluidRenderUtils.renderFluidCuboid(liquid, pos, x + 3 / 16d, y + 2 / 16D, z + 3 / 16d, d, d, d, 10 / 16D - d, height * (9 / 16D), 10 / 16D - d);
        }
    }
}
