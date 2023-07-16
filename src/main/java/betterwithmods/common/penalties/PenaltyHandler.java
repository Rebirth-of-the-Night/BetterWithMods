package betterwithmods.common.penalties;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class PenaltyHandler<T extends Number & Comparable<T>, P extends Penalty<T>> {
    protected List<P> penalties = Lists.newArrayList();
    private P defaultPenalty;

    public PenaltyHandler() {
    }

    public void addDefault(P defaultPenalty) {
        this.defaultPenalty = defaultPenalty;
        this.addPenalty(defaultPenalty);
    }

    public void addPenalty(P penalty) {
        penalties.add(penalty);
    }

    @Nonnull
    public P getPenalty(T t) {
        return penalties.stream().filter(p -> p.inRange(t)).findFirst().orElse(defaultPenalty);
    }

    public abstract P getPenalty(EntityPlayer player);

    public P getMostSevere() {
        return penalties.stream().max((a,b) -> Float.compare(a.getSeverity(), b.getSeverity())).orElse(null);
    }

    public boolean isDisplayed() {
        return true;
    }
}
