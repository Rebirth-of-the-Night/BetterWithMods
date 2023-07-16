package betterwithmods.common.registry.block.recipe;

import betterwithmods.common.BWMRecipes;
import betterwithmods.util.InvUtils;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BlockIngredient extends Ingredient {
    private NonNullList<ItemStack> stacks;
    private IntList itemIds = null;
    private ItemStack[] array = null;
    private int lastSizeA = -1, lastSizeL = -1;

    private Set<IBlockState> states;

    public BlockIngredient(String ore) {
        super(0);
        this.stacks = OreDictionary.getOres(ore);
    }

    public BlockIngredient(List<ItemStack> stacks) {
        super(0);
        this.stacks = InvUtils.asNonnullList(stacks);
    }

    public BlockIngredient(ItemStack... stacks) {
        super(0);
        this.stacks = InvUtils.asNonnullList(stacks);
    }

    public BlockIngredient(Ingredient ingredient) {
        super(0);
        this.stacks = InvUtils.asNonnullList(ingredient.getMatchingStacks());
    }


    @Override
    @Nonnull
    public ItemStack[] getMatchingStacks() {
        if (array == null || this.lastSizeA != stacks.size()) {
            if (states == null) states = Sets.newHashSet();
            NonNullList<ItemStack> lst = NonNullList.create();
            Iterator<ItemStack> iter = this.stacks.iterator();
            while(iter.hasNext()) {
                ItemStack itemstack = iter.next();
                Set<IBlockState> s = BWMRecipes.getStatesFromStack(itemstack);
                if (s.isEmpty()) {
                    iter.remove();
                    continue;
                }
                states.addAll(s);
                if (itemstack.getMetadata() == OreDictionary.WILDCARD_VALUE) {
                    itemstack.getItem().getSubItems(CreativeTabs.SEARCH, lst);
                } else {
                    lst.add(itemstack);
                }
            }
            this.array = lst.toArray(new ItemStack[lst.size()]);
            this.lastSizeA = stacks.size();
        }
        return this.array;
    }


    @Override
    @Nonnull
    public IntList getValidItemStacksPacked() {
        if (this.itemIds == null || this.lastSizeL != stacks.size()) {
            this.itemIds = new IntArrayList(this.stacks.size());
            for (ItemStack itemstack : this.stacks) {
                if (itemstack.getMetadata() == OreDictionary.WILDCARD_VALUE) {
                    NonNullList<ItemStack> lst = NonNullList.create();
                    itemstack.getItem().getSubItems(CreativeTabs.SEARCH, lst);
                    for (ItemStack item : lst)
                        this.itemIds.add(RecipeItemHelper.pack(item));
                } else {
                    this.itemIds.add(RecipeItemHelper.pack(itemstack));
                }
            }
            this.itemIds.sort(IntComparators.NATURAL_COMPARATOR);
            this.lastSizeL = stacks.size();
        }

        return this.itemIds;
    }


    @Override
    public boolean apply(@Nullable ItemStack input) {
        return input != null && this.stacks.stream().anyMatch(t -> InvUtils.matches(t, input));
    }

    public boolean apply(World world, BlockPos pos, @Nullable IBlockState state) {
        return state != null && getStates().contains(state);
    }

    @Override
    protected void invalidate() {
        this.itemIds = null;
        this.array = null;
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    public Set<IBlockState> getStates() {
        if (states == null) {
            getMatchingStacks();
        }
        return states;
    }


}
