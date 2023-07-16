package betterwithmods.common.registry.bulk.recipes;

import betterwithmods.api.recipe.IRecipeOutputs;
import betterwithmods.api.recipe.impl.ListOutputs;
import betterwithmods.util.InvUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class BulkRecipe implements Comparable<BulkRecipe> {

    protected NonNullList<Ingredient> inputs;
    protected IRecipeOutputs recipeOutput;
    protected int priority;

    public BulkRecipe(List<Ingredient> inputs, IRecipeOutputs outputs, int priority) {
        this.inputs = InvUtils.asNonnullList(inputs);
        this.recipeOutput = outputs;
        this.priority = priority;
    }

    public BulkRecipe(@Nonnull List<Ingredient> inputs, @Nonnull List<ItemStack> outputs) {
        this(inputs, outputs, 0);
    }

    public BulkRecipe(List<Ingredient> inputs, IRecipeOutputs outputs) {
        this.inputs = InvUtils.asNonnullList(inputs);
        this.recipeOutput = outputs;
    }

    public BulkRecipe(List<Ingredient> inputs, @Nonnull List<ItemStack> outputs, int priority) {
        this(inputs, new ListOutputs(outputs), priority);
    }

    public NonNullList<ItemStack> onCraft(World world, TileEntity tile, ItemStackHandler inv) {
        NonNullList<ItemStack> items = NonNullList.create();
        if (consumeIngredients(inv, items)) {
            items.addAll(getOutputs());
            return BulkCraftEvent.fireOnCraft(tile, world, inv, this, items);
        }
        return NonNullList.create();
    }

    public IRecipeOutputs getRecipeOutput() {
        return recipeOutput;
    }

    public List<ItemStack> getOutputs() {
        return recipeOutput.getOutputs();
    }

    public List<Ingredient> getInputs() {
        return inputs;
    }

    protected boolean consumeIngredients(ItemStackHandler inventory, NonNullList<ItemStack> containItems) {
        HashSet<Ingredient> toConsume = new HashSet<>(inputs);
        for (Ingredient ingredient : toConsume) {
            if (!InvUtils.consumeItemsInInventory(inventory, ingredient, false, containItems))
                return false;
        }
        return true;
    }

    public boolean isInvalid() {
        return (getInputs().isEmpty() || getInputs().stream().noneMatch(InvUtils::isIngredientValid) || recipeOutput.isInvalid());
    }

    @Override
    public String toString() {
        return String.format("%s: %s -> %s", getClass().getSimpleName(), this.inputs, this.recipeOutput);
    }

    /**
     * Recipes with higher priority will be crafted first.
     *
     * @return sorting priority for Comparable
     */
    public int getPriority() {
        return priority;
    }

    public BulkRecipe setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    @Override
    public int compareTo(@Nonnull BulkRecipe bulkRecipe) {
        return Comparator.comparingInt(BulkRecipe::getPriority).reversed().compare(this, bulkRecipe);
    }

    public int matches(ItemStackHandler inventory) {
        int index = Integer.MAX_VALUE;
        for (Ingredient ingredient : inputs) {
            if ((index = Math.min(index, InvUtils.getFirstOccupiedStackOfItem(inventory, ingredient))) == -1)
                return -1;
        }
        return index;
    }

    public boolean isHidden() {
        return false;
    }
}

