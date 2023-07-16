package betterwithmods.common.penalties;

import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.Range;

public class HungerPenalties extends PenaltyHandler<Integer, BasicPenalty<Integer>> {
    private static final String category = "hardcore.hchunger.penalties.hunger";

    public HungerPenalties() {
        super();
        addDefault(new BasicPenalty<>(true, true, true, true, true, false, 1f, 0f, "none", "bwm.hunger_penalty.none", category, Range.between(60, 25)));
        addPenalty(new BasicPenalty<>(true, true, false, true, true, false, 0.75f, 1 / 5f, "peckish", "bwm.hunger_penalty.peckish", category, Range.between(24, 18)));
        addPenalty(new BasicPenalty<>(true, true, false, false, true, false, 0.75f, 2 / 5f, "hungry", "bwm.hunger_penalty.hungry", category, Range.between(17, 13)));
        addPenalty(new BasicPenalty<>(false, false, false, false, true, false, 0.5f, 3 / 5f, "famished", "bwm.hunger_penalty.famished", category, Range.between(12, 7)));
        addPenalty(new BasicPenalty<>(false, false, false, false, true, false, 0.25f, 4 / 5f, "starving", "bwm.hunger_penalty.starving", category, Range.between(6, 1)));
        addPenalty(new BasicPenalty<>(false, false, false, false, true, false, 0.25f, 1, "dying", "bwm.hunger_penalty.dying", category, Range.between(0, -1)));
    }

    @Override
    public BasicPenalty<Integer> getPenalty(EntityPlayer player) {
        int level = player.getFoodStats().getFoodLevel();
        return getPenalty(level);
    }
}
