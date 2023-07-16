package betterwithmods.client.tesr;

import betterwithmods.client.model.render.RenderUtils;
import betterwithmods.common.blocks.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TESRFurnaceContent extends TileEntitySpecialRenderer<TileEntityFurnace> {

    public static final ResourceLocation FULL = new ResourceLocation("betterwithmods:blocks/furnace_full");

    @Override
    public void render(TileEntityFurnace te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te.getStackInSlot(0).isEmpty())
            return;

        IBlockState state = te.getWorld().getBlockState(te.getPos());

        if(state.getBlock() instanceof BlockFurnace) { //Listen, stop making ghost TEs and our mod won't crash it's simple shit really.
            EnumFacing facing = state.getValue(BlockFurnace.FACING);

            double x1 = x, z1 = z;
            if (facing.getAxis() == EnumFacing.Axis.X) {
                x1 += facing.getXOffset() * 0.00025;
            } else {
                z1 += facing.getZOffset() * 0.00025;
            }
            RenderUtils.renderFill(FULL, te.getPos(), x1, y, z1, 0, 0, 0, 1, 1, 1, new EnumFacing[]{facing});
        }
    }


}
