package betterwithmods.common.penalties;

import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.Range;

public class HealthPenalities extends PenaltyHandler<Float, BasicPenalty<Float>> {

    private static final String category = "hardcore.hcinjury.penalties";

    public HealthPenalities() {
        super();
        
        addDefault(new BasicPenalty<>(true, true, true, true, true, false, 1f, 0f, "none", "bwm.health_penalty.none", category, Range.between(20f, 11f)));
        addPenalty(new BasicPenalty<>(true, true, true, true, true, false, 0.75f, 1 / 5f, "hurt", "bwm.health_penalty.hurt", category, Range.between(11f, 9f)));
        addPenalty(new BasicPenalty<>(true, true, true, true, false, true, 0.75f, 2 / 5f, "injured", "bwm.health_penalty.injured", category, Range.between(9f, 7f)));
        addPenalty(new BasicPenalty<>(true, true, true, true, false, true, 0.5f, 3 / 5f, "wounded", "bwm.health_penalty.wounded", category, Range.between(7f, 5f)));
        addPenalty(new BasicPenalty<>(false, false, true, false, false, true, 0.25f, 4 / 5f, "crippled", "bwm.health_penalty.crippled", category, Range.between(5f, 3f)));
        addPenalty(new BasicPenalty<>(false, false, true, false, false, true, 0.25f, 1, "dying", "bwm.health_penalty.dying", category, Range.between(3f, -1f)));
    }

    @Override
    public BasicPenalty<Float> getPenalty(EntityPlayer player) {
        float level = player.getHealth();
        return getPenalty(level);
    }

}
