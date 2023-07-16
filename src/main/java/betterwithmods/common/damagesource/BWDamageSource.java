package betterwithmods.common.damagesource;

import betterwithmods.event.FakePlayerHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;

public class BWDamageSource extends DamageSource {
    public static final BWDamageSource gloom = new BWDamageSource("gloom", true);
    public static final BWDamageSource growth = new BWDamageSource("growth", false);
    private static FakeDamageSource saw = null;
    private static MultiFakeSource steel_saw = null;
    private static FakeDamageSource choppingBlock = null;
    public static final BWDamageSource acidRain = new BWDamageSource("acid_rain", true);
	public static final BWDamageSource squid = new BWDamageSource("squid", false);

    protected BWDamageSource(String name, boolean ignoreArmor) {
        super(name);
        if (ignoreArmor)
            setDamageBypassesArmor();
    }

    public static MultiFakeSource getSteelSawDamage() {
        if (steel_saw != null)
            return steel_saw;
        if (FakePlayerHandler.getPlayer() != null)
            return steel_saw = new MultiFakeSource("steel_saw", FakePlayerHandler.getPlayer(), 1);
        return null;
    }

    public static FakeDamageSource getSawDamage() {
        if (saw != null)
            return saw;
        if (FakePlayerHandler.getPlayer() != null)
            return saw = new FakeDamageSource("saw", FakePlayerHandler.getPlayer());
        return null;
    }

    public static FakeDamageSource getChoppingBlockDamage() {
        if (choppingBlock != null)
            return choppingBlock;
        if (FakePlayerHandler.getPlayer() != null)
            return choppingBlock = new FakeDamageSource("chopping_block", FakePlayerHandler.getPlayer());
        return null;
    }

    public static class FakeDamageSource extends EntityDamageSource {
        public String message;

        public FakeDamageSource(String message, EntityPlayer player) {
            super("player", player);
            this.message = message;
        }

        @Override
        public boolean isDifficultyScaled() {
            return false;
        }

        @Override
        public boolean isUnblockable() {
            return false;
        }

        @Override
        public boolean getIsThornsDamage() {
            return false;
        }

        @Override
        public ITextComponent getDeathMessage(EntityLivingBase killed) {
            return new TextComponentTranslation("death.attack." + message, killed.getDisplayName());
        }

        @Nullable
        @Override
        public Entity getTrueSource() {
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof FakeDamageSource)
                return ((FakeDamageSource) o).message.equals(this.message);
            return false;
        }
    }

    public static class MultiFakeSource extends FakeDamageSource {

        private final int choices;

        public MultiFakeSource(String message, EntityPlayer player, int choices) {
            super(message, player);
            this.choices = choices;
        }

        @Override
        public ITextComponent getDeathMessage(EntityLivingBase killed) {
            return new TextComponentTranslation("death.attack." + message + "." + killed.getRNG().nextInt(choices), killed.getDisplayName());
        }

    }
}
