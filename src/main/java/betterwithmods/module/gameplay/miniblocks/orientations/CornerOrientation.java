package betterwithmods.module.gameplay.miniblocks.orientations;

import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static betterwithmods.module.gameplay.miniblocks.orientations.OrientationUtils.getCorner;

public enum CornerOrientation implements BaseOrientation {

    DOWN_NORTH("down_north", 0, 0, new AxisAlignedBB(0.0D, 0.0D, 0.5D, 0.5D, 0.5D, 1.0D)),
    DOWN_SOUTH("down_south", 0, 90, new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5D, 0.5D, 0.5D)),
    DOWN_EAST("down_east", 0, 270, new AxisAlignedBB(0.5D, 0.0D, 0.5D, 1.0D, 0.5D, 1.0D)),
    DOWN_WEST("down_west", 0,180, new AxisAlignedBB(0.5D, 0.0D, 0.0D, 1.0D, 0.5D, 0.5D)),
    UP_NORTH("up_north", 90,0, new AxisAlignedBB(0.0D, 0.5D, 0.5D, 0.5D, 1.0D, 1.0D)),
    UP_SOUTH("up_south", 90,90, new AxisAlignedBB(0.0D, 0.5D, 0.0D, 0.5D, 1.0D, 0.5D)),
    UP_EAST("up_east",90,270, new AxisAlignedBB(0.5D, 0.5D, 0.5D, 1.0D, 1.0D, 1.0D)),
    UP_WEST("up_west", 90,180, new AxisAlignedBB(0.5D, 0.5D, 0.0D, 1.0D, 1.0D, 0.5D));

    public static final CornerOrientation[] VALUES = values();

    private String name;
    private AxisAlignedBB bounds;
    private int x, y;

    CornerOrientation(String name, int x, int y,AxisAlignedBB bounds) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.bounds = bounds;
    }

    public static BaseOrientation fromFace(EnumFacing facing) {
        if (facing != null)
            return CornerOrientation.VALUES[facing.getIndex()];
        return BaseOrientation.DEFAULT;
    }

    public static BaseOrientation getFromVec(Vec3d hit, EnumFacing facing) {
        float hitXFromCenter = (float) (hit.x - 0.5F);
        float hitYFromCenter = (float) (hit.y - 0.5F);
        float hitZFromCenter = (float) (hit.z - 0.5F);
        switch (facing.getAxis()) {
            case Y:
                int corner = getCorner(hitXFromCenter, hitZFromCenter, 0);
                if (corner != -1) {
                    int[] corners = hitYFromCenter > 0 ? new int[]{2, 3, 1, 0} : new int[]{6, 7, 5, 4};
                    return CornerOrientation.VALUES[corners[corner]];
                }
            case X:
                corner = getCorner(hitYFromCenter, hitZFromCenter, 0);
                if (corner != -1) {
                    int[] corners = hitXFromCenter > 0 ? new int[]{4, 5, 1, 0} : new int[]{6, 7, 3, 2};
                    return CornerOrientation.VALUES[corners[corner]];
                }
            case Z:
                corner = getCorner(hitYFromCenter, hitXFromCenter, 0);
                if (corner != -1) {
                    int[] corners = hitZFromCenter > 0 ? new int[]{7, 5, 1, 3} : new int[]{6, 4, 0, 2};
                    return CornerOrientation.VALUES[corners[corner]];
                }
            default:
                return fromFace(facing.getOpposite());
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AxisAlignedBB getBounds() {
        return bounds;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public TRSRTransformation toTransformation() {
        return TRSRTransformation.from(ModelRotation.getModelRotation(x,y));
    }

    @Override
    public BaseOrientation next() {
        return VALUES[(this.ordinal() + 1) % (VALUES.length)];
    }
}

