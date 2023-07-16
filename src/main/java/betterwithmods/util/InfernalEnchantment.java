package betterwithmods.util;

import net.minecraft.enchantment.Enchantment;

import javax.annotation.Nonnull;

public class InfernalEnchantment extends Enchantment {

    private int minLevel, maxLevel;
    private int id;

    public InfernalEnchantment(@Nonnull Enchantment enchantment) {
        super(enchantment.getRarity(), enchantment.type, enchantment.applicableEquipmentTypes);
        this.maxLevel = enchantment.getMaxLevel();
        this.minLevel = enchantment.getMinLevel();
        this.id = Enchantment.getEnchantmentID(enchantment);
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public int getMinLevel() {
        return minLevel;
    }

    public InfernalEnchantment setMinLevel(int minLevel) {
        this.minLevel = minLevel;
        return this;
    }

    public InfernalEnchantment setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
        return this;
    }

    public int getId() {
        return id;
    }
}
