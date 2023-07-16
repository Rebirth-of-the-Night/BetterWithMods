package betterwithmods.util;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Function;

/**
 * Hack class to allow multiple bounding boxes per entity.
 *
 * @author mrebhan
 */

public class AABBArray extends AxisAlignedBB {

    private AxisAlignedBB[] boundingBoxes;

    public AABBArray(AxisAlignedBB... boundingBoxes) {
        this(join(boundingBoxes));
        this.boundingBoxes = boundingBoxes;
        this.boundingBoxes = getParts(this);
    }

    private AABBArray(AxisAlignedBB full) {
        super(full.minX, full.minY, full.minZ, full.maxX, full.maxY, full.maxZ);
    }

    private static AxisAlignedBB join(AxisAlignedBB[] in) {
        if (in.length == 0) {
            throw new NullPointerException();
        }

        AxisAlignedBB aabb1 = in[0];
        for (int i = 1; i < in.length; i++) {
            aabb1 = aabb1.union(in[i]);
        }
        return aabb1;
    }

    public static AxisAlignedBB[] getParts(AxisAlignedBB source) {
        if (source instanceof AABBArray) {
            HashSet<AxisAlignedBB> bbs = new HashSet<>();
            Arrays.asList(((AABBArray) source).boundingBoxes).forEach(aabb -> bbs.addAll(Arrays.asList(getParts(aabb))));
            return bbs.toArray(new AxisAlignedBB[0]);
        }
        return new AxisAlignedBB[]{source};
    }

    @Override
    public boolean intersects(Vec3d p_189973_1_, Vec3d p_189973_2_) {
        boolean flag = false;
        for (AxisAlignedBB axisAlignedBB : boundingBoxes) {
            flag |= axisAlignedBB.intersects(p_189973_1_, p_189973_2_);
        }
        return flag;
    }

    @Override
    public boolean intersects(double x1, double y1, double z1, double x2, double y2, double z2) {
        boolean flag = false;
        for (AxisAlignedBB axisAlignedBB : getParts(this)) {
            flag |= axisAlignedBB.intersects(x1, y1, z1, x2, y2, z2);
        }
        return flag;
    }


    @Override
    public AABBArray offset(double x, double y, double z) {
        AxisAlignedBB[] parts = getParts(this);
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].offset(x, y, z);
        }
        return new AABBArray(parts);
    }

    @Override
    public double calculateXOffset(AxisAlignedBB other, double offsetX) {
        for (AxisAlignedBB axisAlignedBB : boundingBoxes) {
            offsetX = axisAlignedBB.calculateXOffset(other, offsetX);
        }
        return offsetX;
    }

    @Override
    public double calculateYOffset(AxisAlignedBB other, double offsetY) {
        for (AxisAlignedBB axisAlignedBB : boundingBoxes) {
            offsetY = axisAlignedBB.calculateYOffset(other, offsetY);
        }
        return offsetY;
    }

    @Override
    public double calculateZOffset(AxisAlignedBB other, double offsetZ) {
        for (AxisAlignedBB axisAlignedBB : boundingBoxes) {
            offsetZ = axisAlignedBB.calculateZOffset(other, offsetZ);
        }
        return offsetZ;
    }

    @Override
    public boolean intersects(AxisAlignedBB other) {
        boolean flag = false;
        for (AxisAlignedBB axisAlignedBB : boundingBoxes) {
            flag |= axisAlignedBB.intersects(other);
        }
        return flag;
    }

    @Override
    public boolean contains(Vec3d vec) {
        boolean flag = false;
        for (AxisAlignedBB axisAlignedBB : getParts(this)) {
            flag |= axisAlignedBB.contains(vec);
        }
        return flag;
    }

    @Override
    public boolean intersectsWithXY(Vec3d vec) {
        boolean flag = false;
        for (AxisAlignedBB axisAlignedBB : getParts(this)) {
            flag |= axisAlignedBB.intersectsWithXY(vec);
        }
        return flag;
    }

    @Override
    public boolean intersectsWithXZ(Vec3d vec) {
        boolean flag = false;
        for (AxisAlignedBB axisAlignedBB : getParts(this)) {
            flag |= axisAlignedBB.intersectsWithXZ(vec);
        }
        return flag;
    }

    @Override
    public boolean intersectsWithYZ(Vec3d vec) {
        boolean flag = false;
        for (AxisAlignedBB axisAlignedBB : getParts(this)) {
            flag |= axisAlignedBB.intersectsWithYZ(vec);
        }
        return flag;
    }

    @Nonnull
    @Override
    public AABBArray grow(double x, double y, double z) {
        AABBArray aabbArray = new AABBArray(boundingBoxes);
        for (int i = 0; i < aabbArray.boundingBoxes.length; i++) {
            aabbArray.boundingBoxes[i] = aabbArray.boundingBoxes[i].grow(x, y, z);
        }
        return aabbArray;
    }

    @Nonnull
    @Override
    public AABBArray grow(double value) {
        return this.grow(value, value, value);
    }

    @Override
    public RayTraceResult calculateIntercept(Vec3d vecA, Vec3d vecB) {
        RayTraceResult result = null;
        for (AxisAlignedBB axisAlignedBB : getParts(this)) {
            result = axisAlignedBB.calculateIntercept(vecA, vecB);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    public AABBArray forEach(Function<AxisAlignedBB, AxisAlignedBB> consumer) {
        AxisAlignedBB[] var1 = getParts(this);
        for (int i = 0; i < var1.length; i++) {
            var1[i] = consumer.apply(var1[i]);
        }

        return new AABBArray(var1);
    }

    @Override
    public AxisAlignedBB expand(double x, double y, double z) {
        return super.expand(x, y, z);
    }

    @Override
    public AxisAlignedBB shrink(double value) {
        return super.shrink(value);
    }

    @Override
    public AxisAlignedBB offset(BlockPos pos) {
        return super.offset(pos);
    }

    @Override
    public AxisAlignedBB setMaxY(double y2) {
        return super.setMaxY(y2);
    }

    @Override
    public AxisAlignedBB union(AxisAlignedBB other) {
        return super.union(other);
    }

    public AxisAlignedBB[] getBoundingBoxes() {
        return this.boundingBoxes;
    }

}
