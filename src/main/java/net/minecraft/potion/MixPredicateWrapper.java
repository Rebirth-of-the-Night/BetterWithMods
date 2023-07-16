package net.minecraft.potion;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionHelper.MixPredicate;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.IRegistryDelegate;

public class MixPredicateWrapper<T extends IForgeRegistryEntry.Impl<T>> extends MixPredicate<T> {

    public MixPredicateWrapper(T input, Ingredient reagent, T output) {
        super(input, reagent, output);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IForgeRegistryEntry.Impl<T>> MixPredicateWrapper<T> from(Object in, Class<T> tClass) {
        if (in instanceof MixPredicate) {
            MixPredicate<T> mp = (MixPredicate<T>) in;
            return new MixPredicateWrapper<T>(mp.input.get(), mp.reagent, mp.output.get());
        }
        return null;
    }

    public static <T extends IForgeRegistryEntry.Impl<T>> MixPredicateWrapper<T> from(MixPredicate<T> in) {
        return new MixPredicateWrapper<T>(in.input.get(), in.reagent, in.output.get());
    }

    public IRegistryDelegate<T> getInput() {
        return input;
    }

    public IRegistryDelegate<T> getOutput() {
        return output;
    }

    public Ingredient getReagent() {
        return reagent;
    }

    public void setReagent(Ingredient reagent) {
        this.reagent = reagent;
    }
}