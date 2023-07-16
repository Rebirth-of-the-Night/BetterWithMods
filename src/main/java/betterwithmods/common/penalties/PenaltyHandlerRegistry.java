package betterwithmods.common.penalties;

import betterwithmods.common.penalties.attribute.Attribute;
import betterwithmods.common.penalties.attribute.BWMAttributes;
import betterwithmods.common.penalties.attribute.IAttributeInstance;
import betterwithmods.common.penalties.attribute.PotionTemplate;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PenaltyHandlerRegistry extends HashSet<PenaltyHandler<?, ?>> {

    private static final long serialVersionUID = 2747709985667138767L;
    
    private final Predicate<Boolean> p = Boolean::booleanValue;

    public boolean canHeal(EntityPlayer player) {
        return booleanAttributes(player, BWMAttributes.HEAL).allMatch(p);
    }

    public boolean canJump(EntityPlayer player) {
        return booleanAttributes(player, BWMAttributes.JUMP).allMatch(p);
    }

    public boolean canSprint(EntityPlayer player) {
        return booleanAttributes(player, BWMAttributes.SPRINT).allMatch(p);
    }

    public boolean canSwim(EntityPlayer player) {
        return booleanAttributes(player, BWMAttributes.SWIM).allMatch(p);
    }


    public boolean canAttack(EntityPlayer player) {
        return booleanAttributes(player, BWMAttributes.ATTACK).allMatch(p);
    }

    public boolean inPain(EntityPlayer player) {
        return booleanAttributes(player, BWMAttributes.PAIN).anyMatch(p);
    }

    public float getSpooked(EntityPlayer player) {
        return floatAttributes(player, BWMAttributes.SPOOKED).reduce((a, b) -> a * b).orElse(0f);
    }

    public float getSpeedModifier(@Nonnull EntityPlayer player) {
        return floatAttributes(player, BWMAttributes.SPEED).reduce((a, b) -> a * b).orElse(0f);
    }

    public boolean attackedByGrue(EntityPlayer player) {
        return booleanAttributes(player, BWMAttributes.GRUE).anyMatch(p);
    }

    public float getDamage(@Nonnull EntityPlayer player) {
        return floatAttributes(player, BWMAttributes.DAMAGE).reduce((a, b) -> a + b).orElse(0f);
    }

    public Iterable<PotionTemplate> getPotionEffects(@Nonnull EntityPlayer player) {
        return potionAttributes(player, BWMAttributes.POTION).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private Stream<Penalty<?>> handlers(@Nonnull EntityPlayer player) {
        return (Stream<Penalty<?>>) stream().map(handler -> handler.getPenalty(player)).filter(Objects::nonNull);
    }

    public Stream<Boolean> booleanAttributes(EntityPlayer player, Attribute<Boolean> attribute) {
        return handlers(player).map(penalty -> penalty.getBoolean(attribute)).filter(Objects::nonNull).map(IAttributeInstance::getValue);
    }

    public Stream<Float> floatAttributes(EntityPlayer player, Attribute<Float> attribute) {
        return handlers(player).map(penalty -> penalty.getFloat(attribute)).filter(Objects::nonNull).map(IAttributeInstance::getValue);
    }

    public Stream<PotionTemplate> potionAttributes(EntityPlayer player, Attribute<PotionTemplate[]> attribute) {
        return handlers(player).map(penalty -> penalty.getPotion(attribute)).filter(Objects::nonNull).map(IAttributeInstance::getValue).flatMap(Arrays::stream);
    }

}
