package betterwithmods.common.entity;

import betterwithmods.util.SetIngredient;
import net.minecraft.entity.Entity;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import java.util.function.Predicate;

public class PredicateEntityIngredientRelation implements EntityIngredientRelation {
    private Predicate<Entity> predicate;
    private SetIngredient ingredients = new SetIngredient();
    private ResourceLocation name;

    public PredicateEntityIngredientRelation(ResourceLocation name, Predicate<Entity> predicate) {
        this.name = name;
        this.predicate = predicate;
    }

    public PredicateEntityIngredientRelation addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    public Ingredient getIngredient(Entity entity) {
        if (this.predicate.test(entity)) {
            return this.ingredients;
        }
        return null;
    }

    public ResourceLocation getName() {
        return name;
    }
}
