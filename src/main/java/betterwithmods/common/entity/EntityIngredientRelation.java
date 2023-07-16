package betterwithmods.common.entity;


import net.minecraft.entity.Entity;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public interface EntityIngredientRelation {
    ResourceLocation getName();

    Ingredient getIngredient(Entity entity);
}
