package betterwithmods.common.entity;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.function.Predicate;

public class EntityIngredientRelationRegistry {

    public static List<EntityIngredientRelation> REGISTRY = Lists.newArrayList();

    public EntityIngredientRelation addBreedingEntry(EntityIngredientRelation entry) {
        REGISTRY.add(entry);
        return entry;
    }

    public PredicateEntityIngredientRelation addPredicateEntry(ResourceLocation name, Predicate<Entity> predicate) {
        PredicateEntityIngredientRelation entry = new PredicateEntityIngredientRelation(name, predicate);
        REGISTRY.add(entry);
        return entry;
    }

    public EntityIngredientRelation getBreedingEntry(ResourceLocation name) {
        return REGISTRY.stream().filter(e -> e.getName().equals(name)).findFirst().orElse(null);
    }

    public Ingredient findIngredient(Entity entity) {
        for(EntityIngredientRelation e: REGISTRY) {
            Ingredient i = e.getIngredient(entity);
            if(i != null)
                return i;
        }
        return null;
    }

}
