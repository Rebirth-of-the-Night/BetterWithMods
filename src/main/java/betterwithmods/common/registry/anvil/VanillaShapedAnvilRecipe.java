package betterwithmods.common.registry.anvil;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VanillaShapedAnvilRecipe extends ShapedAnvilRecipe {
    public VanillaShapedAnvilRecipe(IRecipe recipe) {
        super(new ResourceLocation(recipe.getGroup()), recipe.getRecipeOutput(), buildIngredientList(recipe));
    }

    private static Object[] buildIngredientList(IRecipe recipe) {
        int width = recipe instanceof ShapedRecipes ? ((ShapedRecipes)recipe).getWidth() : ((ShapedOreRecipe)recipe).getRecipeWidth();
        int height = recipe instanceof ShapedRecipes ? ((ShapedRecipes)recipe).getHeight() : ((ShapedOreRecipe)recipe).getRecipeHeight();
        NonNullList<Ingredient> ings = recipe.getIngredients();
        char[] args = {'A', 'B', 'C', 'E', 'F', 'G', 'I', 'J', 'K'};
        List<Object> obj = new ArrayList<>();
        StringBuilder str = new StringBuilder();
        Map<Integer, Ingredient> ingredientMap = new HashMap<>();
        for (int i = 0; i < width * height; i++) {
            if (i != 0 && i % width == 0) {
                obj.add(str.toString());
                str = new StringBuilder();
            }
            if (ings.get(i) != Ingredient.EMPTY) {
                str.append(args[i]);
                ingredientMap.put(i, ings.get(i));
            }
            else {
                str.append(' ');
            }
        }
        while (str.toString().length() < width)
            str.append(' ');
        obj.add(str.toString());
        for (int i : ingredientMap.keySet()) {
            obj.add(args[i]);
            obj.add(ingredientMap.get(i));
        }
        return obj.toArray();
    }
}
