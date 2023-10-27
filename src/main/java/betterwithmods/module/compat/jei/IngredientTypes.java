package betterwithmods.module.compat.jei;

import java.util.Collections;

import betterwithmods.api.recipe.IOutput;
import betterwithmods.api.recipe.impl.ChanceOutput;
import betterwithmods.api.recipe.impl.RandomOutput;
import betterwithmods.api.recipe.impl.StackOutput;
import betterwithmods.module.compat.jei.ingredient.OutputHelper;
import betterwithmods.module.compat.jei.ingredient.OutputRenderer;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.startup.StackHelper;

public class IngredientTypes {

    public static IIngredientType<IOutput> OUTPUT_GENERIC = () -> IOutput.class;
    public static IIngredientType<StackOutput> OUTPUT_STACK = () -> StackOutput.class;
    public static IIngredientType<RandomOutput> OUTPUT_RANDOM = () -> RandomOutput.class;
    public static IIngredientType<ChanceOutput> OUTPUT_CHANCE = () -> ChanceOutput.class;

    public static void registerTypes(IModIngredientRegistration registry, StackHelper stackHelper) {
        registry.register(OUTPUT_GENERIC, Collections.emptySet(), new OutputHelper<IOutput>(stackHelper), new OutputRenderer<IOutput>());
        registry.register(OUTPUT_STACK, Collections.emptySet(), new OutputHelper<StackOutput>(stackHelper), new OutputRenderer<StackOutput>());
        registry.register(OUTPUT_RANDOM, Collections.emptySet(), new OutputHelper<RandomOutput>(stackHelper), new OutputRenderer<RandomOutput>());
        registry.register(OUTPUT_CHANCE, Collections.emptySet(), new OutputHelper<ChanceOutput>(stackHelper), new OutputRenderer<ChanceOutput>());
    }
}
