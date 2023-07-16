package betterwithmods.common.penalties;

import betterwithmods.module.hardcore.needs.HCGloom;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.Range;

public class GloomPenalties extends PenaltyHandler<Integer, GloomPenalty> {

    private static final String category = "hardcore.hcgloom.penalties";

    public GloomPenalties() {
        super();
        addDefault(new GloomPenalty(false, true, 0, "", "", 0,"none","bwm.gloom_penalty.none", category, 0, Range.is(0)));
        addPenalty(new GloomPenalty(false, true, 0.01f, "betterwithmods:ambient.gloom", "betterwithmods:ambient.spook", 0,"gloom", "bwm.gloom_penalty.gloom", category, 1, Range.between(1, 1200)));
        addPenalty(new GloomPenalty(false, true, 0.05f, "betterwithmods:ambient.gloom", "betterwithmods:ambient.spook", 0,"dread", "bwm.gloom_penalty.dread", category, 2, Range.between(1201, 2400)));
        addPenalty(new GloomPenalty(true, false, 0.10f, "betterwithmods:ambient.gloom", "betterwithmods:ambient.spook", 2, "terror", "bwm.gloom_penalty.terror", category, 2, Range.between(2401, 100000)));
    }

    @Override
    public GloomPenalty getPenalty(EntityPlayer player) {
        return getPenalty(HCGloom.getGloomTime(player));
    }

    public int getMaxTime() {
        return penalties.stream().mapToInt(p -> p.getRange().getMaximum()).max().orElse(0);
    }
}
