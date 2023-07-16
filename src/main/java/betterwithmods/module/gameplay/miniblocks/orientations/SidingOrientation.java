package betterwithmods.module.gameplay.miniblocks.orientations;

import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static betterwithmods.module.gameplay.miniblocks.orientations.OrientationUtils.inCenter;
import static betterwithmods.module.gameplay.miniblocks.orientations.OrientationUtils.isMax;

public enum SidingOrientation implements BaseOrientation {
    UP("up", EnumFacing.UP, 180, 0, new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D)),
    DOWN("down", EnumFacing.DOWN, 0, 0, new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D)),
    NORTH("north", EnumFacing.NORTH, 90, 0, new AxisAlignedBB(0.0D, 0.0D, 0.5D, 1.0D, 1.0D, 1.0D)),
    SOUTH("south", EnumFacing.SOUTH, 270, 0, new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.5D)),
    WEST("west", EnumFacing.WEST, 270, 90, new AxisAlignedBB(0.5D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)),
    EAST("east", EnumFacing.EAST, 270, 270, new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5D, 1.0D, 1.0D));

    public static final SidingOrientation[] VALUES = values();

    private String name;
    private EnumFacing facing;
    private AxisAlignedBB bounds;
    private int x, y;

    SidingOrientation(String name, EnumFacing facing, int x, int y, AxisAlignedBB bounds) {
        this.name = name;
        this.facing = facing;
        this.x = x;
        this.y = y;
        this.bounds = bounds;
    }

    public static BaseOrientation fromFace(EnumFacing facing) {
        if (facing != null)
            return SidingOrientation.VALUES[facing.getIndex()];
        return BaseOrientation.DEFAULT;
    }

    public static BaseOrientation getFromVec(Vec3d hit, EnumFacing facing) {
        float hitXFromCenter = (float) (hit.x - 0.5F);
        float hitYFromCenter = (float) (hit.y - 0.5F);
        float hitZFromCenter = (float) (hit.z - 0.5F);
        switch (facing.getAxis()) {
            case Y:
                if (inCenter(hitXFromCenter, hitZFromCenter, 0.25f)) {
                    return fromFace(facing);
                } else if (isMax(hitXFromCenter, hitZFromCenter)) {
                    return hitXFromCenter < 0 ? fromFace(EnumFacing.EAST) : fromFace(EnumFacing.WEST);
                } else {
                    return hitZFromCenter < 0 ? fromFace(EnumFacing.SOUTH) : fromFace(EnumFacing.NORTH);
                }
            case X:
                if (inCenter(hitYFromCenter, hitZFromCenter, 0.25f)) {
                    return fromFace(facing);
                } else if (isMax(hitYFromCenter, hitZFromCenter)) {
                    return hitYFromCenter < 0 ? fromFace(EnumFacing.UP) : fromFace(EnumFacing.DOWN);

                } else {
                    return hitZFromCenter < 0 ? fromFace(EnumFacing.SOUTH) : fromFace(EnumFacing.NORTH);
                }
            case Z:
                if (inCenter(hitYFromCenter, hitXFromCenter, 0.25f)) {
                    return fromFace(facing);
                } else if (isMax(hitYFromCenter, hitXFromCenter)) {
                    return hitYFromCenter < 0 ? fromFace(EnumFacing.UP) : fromFace(EnumFacing.DOWN);
                } else {
                    return hitXFromCenter < 0 ? fromFace(EnumFacing.EAST) : fromFace(EnumFacing.WEST);
                }
            default:
                return fromFace(facing);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public EnumFacing getFacing() {
        return facing;
    }

    @Override
    public AxisAlignedBB getBounds() {
        return bounds;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public TRSRTransformation toTransformation() {
        return TRSRTransformation.from(ModelRotation.getModelRotation(x, y));
    }

    @Override
    public BaseOrientation next() {
        return VALUES[(this.ordinal() + 1) % (VALUES.length)];
    }
}

