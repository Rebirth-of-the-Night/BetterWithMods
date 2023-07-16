package betterwithmods.common.blocks;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import net.minecraft.block.BlockRailDetector;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class BlockRailDetectorBase extends BlockRailDetector {

    private Predicate<Entity> filter;

    public BlockRailDetectorBase(Predicate<Entity> cart) {
        this.filter = cart;
        setHardness(0.7F);
        setSoundType(SoundType.METAL);
    }

    public static boolean isRider(Entity entity, Predicate<Entity> rider) {
        if (entity instanceof EntityMinecart) {
            EntityMinecart cart = (EntityMinecart) entity;
            Optional<Entity> riding = cart.getPassengers().stream().findFirst();
            if(riding.isPresent()) {
                return rider.apply(riding.orElse(null));
            }
        }
        return false;
    }

    @SuppressWarnings("all")
    @Override
    protected <T extends EntityMinecart> List<T> findMinecarts(World worldIn, BlockPos pos, Class<T> clazz, Predicate<Entity>... filters) {
        AxisAlignedBB axisalignedbb = this.getDectectionBox(pos);
        Set<Predicate<Entity>> p = Sets.newHashSet(filters);
        p.add(this.filter);
        return worldIn.getEntitiesWithinAABB(clazz, axisalignedbb, entity -> p.stream().anyMatch(f -> f.apply(entity)));
    }

    private AxisAlignedBB getDectectionBox(BlockPos pos) {
        return new AxisAlignedBB((double) ((float) pos.getX() + 0.2F), (double) pos.getY(), (double) ((float) pos.getZ() + 0.2F), (double) ((float) (pos.getX() + 1) - 0.2F), (double) ((float) (pos.getY() + 1) - 0.2F), (double) ((float) (pos.getZ() + 1) - 0.2F));
    }
}
