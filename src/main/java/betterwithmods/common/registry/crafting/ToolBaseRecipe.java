package betterwithmods.common.registry.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Predicate;

/**
 * Created by primetoxinz on 6/27/17.
 */
public abstract class ToolBaseRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    private ResourceLocation group;
    protected Predicate<ItemStack> isTool;
    protected ItemStack result;
    protected Ingredient input;

    public ToolBaseRecipe(ResourceLocation group, ItemStack result, Ingredient input, Predicate<ItemStack> isTool) {
        this.group = group;
        this.isTool = isTool;
        this.result = result;
        this.input = input;
    }

    public boolean isMatch(IInventory inv, World world) {
        boolean hasTool = false, hasInput = false;
        for (int x = 0; x < inv.getSizeInventory(); x++) {
            boolean inRecipe = false;
            ItemStack slot = inv.getStackInSlot(x);

            if (!slot.isEmpty()) {
                if (isTool.test(slot)) {
                    if (!hasTool) {
                        hasTool = true;
                        inRecipe = true;
                    } else {
                        return false;
                    }
                } else if (input.apply(slot)) {
                    if (!hasInput) {
                        hasInput = true;
                        inRecipe = true;
                    } else {
                        return false;
                    }
                }
                if (!inRecipe)
                    return false;
            }
        }
        return hasTool && hasInput;
    }

    public ItemStack getExampleStack() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        return isMatch(inv, worldIn);

    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return result.copy();
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return result.copy();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        playSound(inv);
        return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    }

    public String getGroup() {
        if (group != null)
            return group.toString();
        return "";
    }

    public abstract void playSound(InventoryCrafting inv);

    public abstract SoundEvent getSound();

    public abstract Pair<Float,Float> getSoundValues();

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(input);
        ingredients.add(new IngredientTool(isTool, getExampleStack()));
        return ingredients;
    }
}
