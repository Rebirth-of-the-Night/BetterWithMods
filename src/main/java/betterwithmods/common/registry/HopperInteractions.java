package betterwithmods.common.registry;

import betterwithmods.api.recipe.IRecipeOutputs;
import betterwithmods.api.recipe.impl.ListOutputs;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.blocks.BlockUrn;
import betterwithmods.common.blocks.mechanical.tile.TileEntityFilteredHopper;
import betterwithmods.common.blocks.tile.SimpleStackHandler;
import betterwithmods.util.InvUtils;
import betterwithmods.util.StackIngredient;
import com.google.common.collect.Lists;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Purpose:
 *
 * @author primetoxinz
 * @version 11/13/16
 */

//TODO move to a singleton in 1.13
public class HopperInteractions {
    public static final ArrayList<HopperRecipe> RECIPES = Lists.newArrayList();

    public static List<HopperRecipe> getDisplayRecipes() {
        List<HopperRecipe> display = Lists.newArrayList();
        for (HopperRecipe recipe : RECIPES) {
            display.add(recipe);
            if (recipe instanceof SoulUrnRecipe) {
                display.add(new DummySoulUrnRecipe((SoulUrnRecipe) recipe));
            }
        }
        return display;
    }


    public static void addHopperRecipe(HopperRecipe recipe) {
        RECIPES.add(recipe);
    }

    public static boolean remove(List<ItemStack> outputs, List<ItemStack> secondary) {
        return RECIPES.removeAll(findRecipe(outputs, secondary));
    }

    public static boolean removeFuzzy(List<ItemStack> outputs, List<ItemStack> secondary) {
        return RECIPES.removeAll(findRecipeFuzzy(outputs, secondary));
    }

    public static boolean removeExact(List<ItemStack> outputs, List<ItemStack> secondary) {
        return RECIPES.removeAll(findRecipeExact(outputs, secondary));
    }

    public static boolean removeByInput(ItemStack input) {
        return RECIPES.removeAll(findRecipeByInput(input));
    }

    protected static List<HopperRecipe> findRecipe(List<ItemStack> outputs, List<ItemStack> secondary) {
        List<HopperRecipe> recipes = findRecipeExact(outputs, secondary);
        if (recipes.isEmpty())
            recipes = findRecipeFuzzy(outputs, secondary);
        return recipes;
    }

    protected static List<HopperRecipe> findRecipeFuzzy(List<ItemStack> outputs, List<ItemStack> secondary) {
        return RECIPES.stream().filter(recipe -> recipe.getRecipeOutputInsert().matchesFuzzy(outputs) && recipe.getRecipeOutputWorld().matchesFuzzy(secondary)).collect(Collectors.toList());
    }

    protected static List<HopperRecipe> findRecipeExact(List<ItemStack> outputs, List<ItemStack> secondary) {
        return RECIPES.stream().filter(recipe -> recipe.getRecipeOutputInsert().matches(outputs) && recipe.getRecipeOutputWorld().matches(secondary)).collect(Collectors.toList());
    }

    protected static List<HopperRecipe> findRecipeByInput(ItemStack input) {
        return RECIPES.stream().filter(r -> r.input.apply(input)).collect(Collectors.toList());
    }

    public static boolean attemptToCraft(String filterName, World world, BlockPos pos, EntityItem input, TileEntityFilteredHopper tile) {
        for (HopperRecipe recipe : RECIPES) {
            if (recipe.isRecipe(filterName, input)) {
                if (recipe.canCraft(world, pos)) {
                    recipe.craft(input, world, pos, tile);
                    return true;
                }
            }
        }
        return false;
    }

    public static class DummySoulUrnRecipe extends SoulUrnRecipe {

        public DummySoulUrnRecipe(SoulUrnRecipe parent) {
            super(StackIngredient.fromIngredient(8, parent.input), parent.getOutputs(), parent.getSecondaryOutputs().stream().map(s -> InvUtils.setCount(s, 8)).collect(Collectors.toList()));
        }

        @Override
        public List<ItemStack> getInputContainer() {
            return Lists.newArrayList(BlockUrn.getStack(BlockUrn.EnumType.EMPTY, 1));
        }

        @Override
        public List<ItemStack> getOutputContainer() {
            return Lists.newArrayList(BlockUrn.getStack(BlockUrn.EnumType.FULL, 1));
        }
    }


    public static class SoulUrnRecipe extends HopperRecipe {
        public SoulUrnRecipe(Ingredient input, ItemStack output, ItemStack... secondaryOutput) {
            super("betterwithmods:soul_sand", input, output, secondaryOutput);
        }

        public SoulUrnRecipe(Ingredient input, List<ItemStack> output, List<ItemStack> secondaryOutput) {
            super("betterwithmods:soul_sand", input, output, secondaryOutput);
        }

        @Override
        public void onCraft(World world, BlockPos pos, EntityItem item, TileEntityFilteredHopper tile) {
            tile.increaseSoulCount(1);
            if (!world.isRemote) {
                world.playSound(null, pos, SoundEvents.ENTITY_GHAST_AMBIENT, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.1F + 0.45F);
            }
            super.onCraft(world, pos, item, tile);
        }
    }


    //TODO this should not be an internal class anymore
    public static class HopperRecipe {
        protected final String filterName;
        protected final Ingredient input;

        protected IRecipeOutputs recipeOutputWorld, recipeOutputInsert;

        public HopperRecipe(String filterName, Ingredient input, ItemStack output, ItemStack... secondaryOutput) {
            this(filterName, input, Lists.newArrayList(output), Lists.newArrayList(secondaryOutput));
        }

        public HopperRecipe(String filterName, Ingredient input, IRecipeOutputs recipeOutputInsert, IRecipeOutputs recipeOutputWorld) {
            this.filterName = filterName;
            this.input = input;
            this.recipeOutputWorld = recipeOutputWorld;
            this.recipeOutputInsert = recipeOutputInsert;
        }

        public HopperRecipe(String filterName, Ingredient input, List<ItemStack> output, List<ItemStack> secondaryOutput) {
            this(filterName, input, new ListOutputs(output), new ListOutputs(secondaryOutput));
        }

        public boolean isRecipe(String filterName, EntityItem entity) {
            if (filterName.equals(this.filterName)) {
                if (entity != null) {
                    ItemStack stack = entity.getItem();
                    return input.apply(stack);
                }
                return false;
            }
            return false;
        }

        public void craft(EntityItem inputStack, World world, BlockPos pos, TileEntityFilteredHopper tile) {
            SimpleStackHandler inventory = tile.inventory;
            for (ItemStack output : getOutputs()) {
                ItemStack remainder = InvUtils.insert(inventory, output, false);
                if (!remainder.isEmpty())
                    InvUtils.ejectStackWithOffset(world, inputStack.getPosition(), remainder);
            }
            InvUtils.ejectStackWithOffset(world, inputStack.getPosition(), getSecondaryOutputs());
            onCraft(world, pos, inputStack, tile);
        }

        public void onCraft(World world, BlockPos pos, EntityItem item, TileEntityFilteredHopper tile) {
            int count = input instanceof StackIngredient ? ((StackIngredient) input).getCount(item.getItem()) : 1;
            item.getItem().shrink(count);
            if (item.getItem().getCount() <= 0)
                item.setDead();
        }

        public String getFilterType() {
            return filterName;
        }

        public List<ItemStack> getFilters() {
            return Lists.newArrayList(BWRegistry.HOPPER_FILTERS.getFilter(getFilterType()).getFilter().getMatchingStacks());
        }

        public List<ItemStack> getInputContainer() {
            return Lists.newArrayList();
        } //For showing that it needs urns

        public List<ItemStack> getOutputContainer() {
            return Lists.newArrayList();
        } //For showing that it needs urns

        public Ingredient getInputs() {
            return input;
        }

        public List<ItemStack> getOutputs() {
            return recipeOutputInsert.getOutputs();
        }

        public List<ItemStack> getSecondaryOutputs() {
            return recipeOutputWorld.getOutputs();
        }

        public IRecipeOutputs getRecipeOutputWorld() {
            return recipeOutputWorld;
        }

        public IRecipeOutputs getRecipeOutputInsert() {
            return recipeOutputInsert;
        }

        public boolean canCraft(World world, BlockPos pos) {
            TileEntityFilteredHopper tile = (TileEntityFilteredHopper) world.getTileEntity(pos);
            if (tile != null) {
                ItemStackHandler inventory = tile.inventory;
                List<ItemStack> outputs = getOutputs();
                if (outputs.isEmpty())
                    return true;
                return outputs.stream().allMatch(stack -> InvUtils.insert(inventory, stack, true).isEmpty());
            }
            return true;
        }
    }
}
