package betterwithmods.common.registry.crafting;

import betterwithmods.api.util.IColorProvider;
import betterwithmods.api.util.impl.BlockColorProvider;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.DyeUtils;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

public class RecipeShapedColor extends ShapedOreRecipe {

    public RecipeShapedColor(ResourceLocation group, Block result, Object... recipe) {
        super(group, result, recipe);
    }

    public RecipeShapedColor(ResourceLocation group, Item result, Object... recipe) {
        super(group, result, recipe);
    }

    public RecipeShapedColor(ResourceLocation group, @Nonnull ItemStack result, Object... recipe) {
        super(group, result, recipe);
    }
    public RecipeShapedColor(ResourceLocation group, @Nonnull ItemStack result, CraftingHelper.ShapedPrimer primer) {
        super(group, result, primer);
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting crafting) {
        int colorCount = 0;
        int[] components = new int[3];
        for (int i = 0; i < crafting.getSizeInventory(); i++) {
            ItemStack stack = crafting.getStackInSlot(i);

            int rgb = getColor(stack);
            if (rgb != 0) {
                components[0] += (rgb >> 16) & 0xff;
                components[1] += (rgb >> 8) & 0xff;
                components[2] += (rgb) & 0xff;
                colorCount++;
            }
        }

        components[0] /= colorCount;
        components[1] /= colorCount;
        components[2] /= colorCount;

        int rgb = ((components[0] & 0xff) << 16) | ((components[1] & 0x0ff) << 8) | (components[2] & 0xff);

        ItemStack output = this.output;
        if (output.getItem() instanceof ItemArmor) {
            ((ItemArmor) output.getItem()).setColor(output, rgb);
        }
        return output.copy();
    }

    public int getColor(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof IColorProvider) {
            return ((IColorProvider) item).getColor(stack);
        } else if (item instanceof ItemBlock) {
            return BlockColorProvider.INSTANCE.getColor(stack);
        }
        return DyeUtils.colorFromStack(stack).map(c -> c.colorValue).orElse(0);
    }

    public static class Factory implements IRecipeFactory {
        @Override
        public IRecipe parse(JsonContext context, JsonObject json) {

            String group = JsonUtils.getString(json, "group", "");

            Map<Character, Ingredient> ingMap = Maps.newHashMap();
            for (Map.Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "key").entrySet()) {
                if (entry.getKey().length() != 1)
                    throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
                if (" ".equals(entry.getKey()))
                    throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");

                ingMap.put(entry.getKey().toCharArray()[0], CraftingHelper.getIngredient(entry.getValue(), context));
            }

            ingMap.put(' ', Ingredient.EMPTY);

            JsonArray patternJ = JsonUtils.getJsonArray(json, "pattern");

            if (patternJ.size() == 0)
                throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");

            String[] pattern = new String[patternJ.size()];
            for (int x = 0; x < pattern.length; ++x) {
                String line = JsonUtils.getString(patternJ.get(x), "pattern[" + x + "]");
                if (x > 0 && pattern[0].length() != line.length())
                    throw new JsonSyntaxException("Invalid pattern: each row must  be the same width");
                pattern[x] = line;
            }

            CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
            primer.width = pattern[0].length();
            primer.height = pattern.length;
            primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
            primer.input = NonNullList.withSize(primer.width * primer.height, Ingredient.EMPTY);

            Set<Character> keys = Sets.newHashSet(ingMap.keySet());
            keys.remove(' ');

            int x = 0;
            for (String line : pattern) {
                for (char chr : line.toCharArray()) {
                    Ingredient ing = ingMap.get(chr);
                    if (ing == null)
                        throw new JsonSyntaxException("Pattern references symbol '" + chr + "' but it's not defined in the key");
                    primer.input.set(x++, ing);
                    keys.remove(chr);
                }
            }

            if (!keys.isEmpty())
                throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + keys);

            ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
            return new RecipeShapedColor(group.isEmpty() ? null : new ResourceLocation(group), result, primer);
        }
    }
}
