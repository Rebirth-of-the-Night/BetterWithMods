package betterwithmods.module.hardcore.beacons;

import betterwithmods.common.registry.block.recipe.BlockIngredient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class PotionBeaconEffect extends BeaconEffect {

    private Map<PotionEffect, Amplification> potionEffects;
    private Predicate<EntityLivingBase> canApply;

    public PotionBeaconEffect(String name, BlockIngredient structureBlock, Class<? extends EntityLivingBase> validEntityType) {
        super(name, structureBlock, validEntityType);
        this.potionEffects = new HashMap<>();
        this.canApply = (T) -> true;
    }

    public PotionBeaconEffect addPotionEffect(Potion potion, int duration, Amplification amplification) {
        this.potionEffects.put(new PotionEffect(potion, duration, 1), amplification);
        return this;
    }

    public PotionBeaconEffect setCanApply(Predicate<EntityLivingBase> canApply) {
        this.canApply = canApply;
        return this;
    }

    @Override
    public void onBeaconCreate(@Nonnull World world, @Nonnull BlockPos pos, int beaconLevel) {

    }

    @Override
    public void apply(NonNullList<EntityLivingBase> entitiesInRange, @Nonnull World world, @Nonnull BlockPos pos, int beaconLevel) {
        List<PotionEffect> amplifiedPotionEffects = new ArrayList<>();
        potionEffects.forEach(((potionEffect, amplification) -> amplifiedPotionEffects.add(new PotionEffect(potionEffect.getPotion(), potionEffect.getDuration(), amplification.getForLevel(beaconLevel)))));

        for (EntityLivingBase entity : entitiesInRange) {
            for (PotionEffect potionEffect : amplifiedPotionEffects) {
                if (entity.isPotionApplicable(potionEffect) && canApply.test(entity)) {
                    entity.addPotionEffect(potionEffect);
                }
            }
        }
    }

    @Override
    public boolean onPlayerInteracted(World world, BlockPos pos, int level, EntityPlayer player, EnumHand hand, ItemStack stack) {
        return false;
    }

    @Override
    public void onBeaconBreak(World world, BlockPos pos, int level) {

    }


    protected enum Amplification {
        ZERO(beaconLevel -> 0),
        NONE((beaconLevel) -> 1),
        LEVEL((beaconLevel) -> beaconLevel),
        LEVEL_REDUCED((beaconLevel) -> beaconLevel - 1);

        private Function<Integer, Integer> amplifier;

        Amplification(Function<Integer, Integer> amplifier) {
            this.amplifier = amplifier;
        }

        public int getForLevel(int beaconLevel) {
            return amplifier.apply(beaconLevel);
        }
    }
}
