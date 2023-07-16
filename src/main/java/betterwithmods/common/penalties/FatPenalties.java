package betterwithmods.common.penalties;

import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.Range;

public class FatPenalties extends PenaltyHandler<Float, BasicPenalty<Float>> {
    private static final String category = "hardcore.hchunger.hungerPenalties.fat";

    public FatPenalties() {
        super();
        addDefault(new BasicPenalty<>(true, true, true, true, true, false, 1f, 0f, "none", "bwm.fat_penalty.none", category, Range.between(0f, 36f)));
        addPenalty(new BasicPenalty<>(true, true, true, false, true, false, 1f, 1 / 4f, "plump", "bwm.fat_penalty.plump", category, Range.between(36f, 42f)));
        addPenalty(new BasicPenalty<>(true, true, true, false, true, false, 1f, 2 / 4f, "chubby", "bwm.fat_penalty.chubby", category, Range.between(42f, 48f)));
        addPenalty(new BasicPenalty<>(false, false, false, false, true, false, 0.5f, 3 / 4f, "fat", "bwm.fat_penalty.fat", category, Range.between(48f, 52f)));
        addPenalty(new BasicPenalty<>(false, false, false, false, true, false, 0.25f, 1f, "obese", "bwm.fat_penalty.obese", category, Range.between(52f, 60f)));
    }

    @Override
    public BasicPenalty<Float> getPenalty(EntityPlayer player) {
        float level = player.getFoodStats().getSaturationLevel();
        return getPenalty(level);
    }
}
