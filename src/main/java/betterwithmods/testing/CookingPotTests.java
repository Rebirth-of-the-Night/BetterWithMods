package betterwithmods.testing;

import betterwithmods.common.registry.bulk.manager.CookingPotManager;
import betterwithmods.common.registry.bulk.recipes.CookingPotRecipe;
import betterwithmods.testing.base.BaseTest;
import betterwithmods.testing.base.Before;
import betterwithmods.testing.base.Test;
import betterwithmods.util.StackIngredient;
import com.google.common.collect.Lists;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.fest.assertions.Assertions;

import java.util.List;


public class CookingPotTests extends BaseTest {

    private CookingPotManager TEST_MANAGER;

    private List<Ingredient> inputs = Lists.newArrayList(StackIngredient.fromStacks(new ItemStack(Blocks.COBBLESTONE)));
    private List<ItemStack> outputs = Lists.newArrayList(new ItemStack(Items.DIAMOND));
    private CookingPotRecipe recipe = new CookingPotRecipe(inputs, outputs, 1);

    @Before
    public void beforeTest() {
        TEST_MANAGER = new CookingPotManager();
    }

    @Test
    public void testRecipeAddition() {
        Assertions.assertThat(TEST_MANAGER.getRecipes()).isEmpty();
        TEST_MANAGER.addRecipe(recipe);
        Assertions.assertThat(TEST_MANAGER.getRecipes()).hasSize(1);
    }

    @Test
    public void testRecipeRemoval() {
        Assertions.assertThat(recipe.isInvalid()).isFalse();

        Assertions.assertThat(TEST_MANAGER.getRecipes()).isEmpty();
        TEST_MANAGER.addRecipe(recipe);
        Assertions.assertThat(TEST_MANAGER.getRecipes()).isNotEmpty();
        TEST_MANAGER.remove(recipe);
    }

}
