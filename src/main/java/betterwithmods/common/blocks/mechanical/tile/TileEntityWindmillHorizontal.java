package betterwithmods.common.blocks.mechanical.tile;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.mechanical.BlockWindmill;
import betterwithmods.util.DirUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityWindmillHorizontal extends TileEntityBaseWindmill {

    public TileEntityWindmillHorizontal() {
        super();
        this.bladeMeta = new int[]{0, 0, 0, 0};
    }

    @Override
    public void verifyIntegrity() {
        boolean valid = true;
        if (getBlockWorld().getBlockState(pos).getBlock() == BWMBlocks.WINDMILL) {
            EnumFacing.Axis axis = getBlockWorld().getBlockState(pos).getValue(DirUtils.AXIS);
            for (int vert = -6; vert <= 6; vert++) {
                for (int i = -6; i <= 6; i++) {
                    int xP = (axis == EnumFacing.Axis.Z ? i : 0);
                    int zP = (axis == EnumFacing.Axis.X ? i : 0);
                    BlockPos offset = pos.add(xP, vert, zP);
                    if (xP == 0 && vert == 0 && zP == 0)
                        continue;
                    else {
                        IBlockState state = world.getBlockState(offset);
                        valid = state.getBlock().isReplaceable(world, offset);
                    }
                    if (!valid)
                        break;
                }
                if (!valid)
                    break;
            }
        }
        isValid = valid && this.getBlockWorld().canBlockSeeSky(pos) && !isNether() && !isEnd();
    }

    @Override
    public int getRadius() {
        return 7;
    }

    //Extend the bounding box if the TESR is bigger than the occupying block.
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        IBlockState state = getBlockWorld().getBlockState(pos);
        if (!(state.getBlock() instanceof BlockWindmill))
            return Block.FULL_BLOCK_AABB;

        EnumFacing.Axis axis = getBlockWorld().getBlockState(pos).getValue(DirUtils.AXIS);
        EnumFacing facing = (axis == EnumFacing.Axis.Z) ? EnumFacing.SOUTH : EnumFacing.EAST;
        Vec3i vec = facing.getDirectionVec();
        int xP = axis == EnumFacing.Axis.Z ? getRadius() : 0;
        int yP = getRadius();
        int zP = axis == EnumFacing.Axis.X ? getRadius() : 0;
        return new AxisAlignedBB(-xP, -yP, -zP, xP, yP, zP).offset(0.5, 0.5, 0).offset(pos).expand(vec.getX(), vec.getY(), vec.getZ());
    }
}
