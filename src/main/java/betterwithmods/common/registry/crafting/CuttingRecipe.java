package betterwithmods.common.registry.crafting;

import com.google.gson.JsonObject;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by blueyu2 on 12/12/16.
 */
public class CuttingRecipe extends ToolDamageRecipe {
    public CuttingRecipe(ResourceLocation group, Ingredient input, ItemStack result) {
        super(group, result, input, stack -> stack.getItem() instanceof ItemShears);
    }

    @Override
    public ItemStack getExampleStack() {
        return new ItemStack(Items.SHEARS);
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.ENTITY_SHEEP_SHEAR;
    }

    @Override
    public Pair<Float, Float> getSoundValues() {
        return Pair.of(1.0f, 1.0f);
    }

    public static class Factory implements IRecipeFactory {
        @Override
        public IRecipe parse(JsonContext context, JsonObject json) {
            String group = JsonUtils.getString(json, "group", "");
            Ingredient cut = CraftingHelper.getIngredient(JsonUtils.getJsonObject(json, "cut"), context);
            ItemStack itemstack = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
            return new CuttingRecipe(new ResourceLocation(group), cut, itemstack);
        }
    }
}
