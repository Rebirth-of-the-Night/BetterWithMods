package betterwithmods.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class VectorBuilder {

    private Random random = new Random();

    private Vec3d vec;

    private List<Function<Vec3d, Vec3d>> operations = Lists.newArrayList();

    public VectorBuilder addOperation(Function<Vec3d, Vec3d> operation) {
        this.operations.add(operation);
        return this;
    }

    public VectorBuilder offset(double offset) {
        return this.offset(offset, offset, offset);
    }

    public VectorBuilder offset(double x, double y, double z) {
        return addOperation(vec -> {
            Preconditions.checkNotNull(vec, "vec");
            return vec.add(x, y, z);
        });

    }

    public VectorBuilder rand(double multiplier) {
        return rand(multiplier, multiplier, multiplier);
    }

    public VectorBuilder rand(double multiplierX, double multiplierY, double multiplierZ) {
        return addOperation(vec -> {
            Preconditions.checkNotNull(vec, "vec");
            return vec.add(random.nextDouble() * multiplierX, random.nextDouble() * multiplierY, random.nextDouble() * multiplierZ);
        });
    }


    public VectorBuilder setGaussian(double multiplierX, double multiplierY, double multiplierZ) {
        return addOperation(vec -> {
            Preconditions.checkNotNull(vec, "vec");
            return vec.add(random.nextGaussian() * multiplierX, random.nextGaussian() * multiplierY, random.nextGaussian() * multiplierZ);
        });
    }

    public VectorBuilder setGaussian(double multiplier) {
        return setGaussian(multiplier, multiplier, multiplier);
    }

    public Vec3d build(Vec3d initial) {
        this.vec = initial;
        operations.forEach(c -> this.vec = c.apply(this.vec));
        return this.vec;
    }


}
