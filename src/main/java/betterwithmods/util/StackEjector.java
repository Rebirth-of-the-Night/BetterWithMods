package betterwithmods.util;

import com.google.common.base.Preconditions;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class StackEjector {

    private ItemStack stack;
    private VectorBuilder positionBuilder, motionBuilder;
    private int pickupDelay;


    public StackEjector(VectorBuilder position, VectorBuilder motion) {
        this.positionBuilder = position;
        this.motionBuilder = motion;
    }

    public StackEjector setStack(@Nonnull ItemStack stack) {
        this.stack = stack;
        return this;
    }

    public VectorBuilder getPositionBuilder() {
        return positionBuilder;
    }

    public VectorBuilder getMotionBuilder() {
        return motionBuilder;
    }


    public void ejectStack(@Nonnull World world, @Nonnull Vec3d p, @Nonnull Vec3d m) {
        Preconditions.checkNotNull(stack, "stack");

        if (world.isRemote)
            return;
        Vec3d position = getPositionBuilder().build(p);
        Vec3d motion = getMotionBuilder().build(m);
        if (position == null)
            return;
        EntityItem item = new EntityItem(world, position.x, position.y, position.z, stack);
        item.setPickupDelay(pickupDelay);
        if (motion != null) {
            item.motionX = motion.x;
            item.motionY = motion.y;
            item.motionZ = motion.z;
        }
        world.spawnEntity(item);
    }

    public StackEjector setPickupDelay(int pickupDelay) {
        this.pickupDelay = pickupDelay;
        return this;
    }


}
