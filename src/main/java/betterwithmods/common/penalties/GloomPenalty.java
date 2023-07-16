package betterwithmods.common.penalties;

import betterwithmods.common.penalties.attribute.BWMAttributes;
import betterwithmods.common.penalties.attribute.PotionTemplate;
import org.apache.commons.lang3.Range;

public class GloomPenalty extends Penalty<Integer> {
    public GloomPenalty(boolean grue, boolean jump, float spooked, String sound, String sound_spooked, float damage, String name, String lang, String category, float severity, Range<Integer> range) {
        super(lang, severity, BWMAttributes.getRange(category, name, "Numeric range for whether this penalty it active", range),
                BWMAttributes.GRUE.fromConfig(category,name,grue),
                BWMAttributes.JUMP.fromConfig(category,name,jump),
                BWMAttributes.SPOOKED.fromConfig(category,name,spooked),
                BWMAttributes.SOUND.fromConfig(category,name,sound),
                BWMAttributes.SOUND_SPOOKED.fromConfig(category,name,sound_spooked),
                BWMAttributes.DAMAGE.fromConfig(category,name,damage),
                BWMAttributes.POTION.fromConfig(category,name,new PotionTemplate[0])
        );
    }


}
