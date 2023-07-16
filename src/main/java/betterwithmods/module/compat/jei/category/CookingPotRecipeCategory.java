package betterwithmods.module.compat.jei.category;

import betterwithmods.BWMod;
import betterwithmods.api.recipe.IOutput;
import betterwithmods.common.registry.bulk.recipes.CookingPotRecipe;
import betterwithmods.common.registry.heat.BWMHeatRegistry;
import betterwithmods.module.compat.jei.wrapper.BulkRecipeWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;

public class CookingPotRecipeCategory extends BWMRecipeCategory<BulkRecipeWrapper<CookingPotRecipe>> {
    public static final String CAULDRON_UID = "bwm.cauldron";
    public static final String CRUCIBLE_UID = "bwm.crucible";

    private static final int width = 165;
    private static final int height = 57;
    private static final int inputSlots = 9;
    private static final int outputSlot = 0;

    private static final ResourceLocation guiTexture = new ResourceLocation(BWMod.MODID, "textures/gui/jei/cooking.png");
    @Nonnull
    private final ICraftingGridHelper craftingGrid;
    @Nonnull
    private IDrawableAnimated flame;
    private IGuiHelper helper;

    public CookingPotRecipeCategory(IGuiHelper helper, String uid) {
        super(helper.createDrawable(guiTexture, 0, 0, width, height), uid, String.format("inv.%s.name", uid.substring(4)));
        this.helper = helper;
        this.craftingGrid = helper.createCraftingGridHelper(inputSlots, outputSlot);
    }

    @Override
    public void drawExtras(@Nonnull Minecraft minecraft) {
        flame.draw(minecraft, 77, 22);
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout layout, @Nonnull BulkRecipeWrapper<CookingPotRecipe> wrapper, @Nonnull IIngredients ingredients) {

        IDrawableStatic flameDrawable = helper.createDrawable(guiTexture, 166, wrapper.getRecipe().getHeat() > 1 ? 14 : 0, 14, 14);
        this.flame = helper.createAnimatedDrawable(flameDrawable, 200, IDrawableAnimated.StartDirection.BOTTOM, false);

        IGuiItemStackGroup stacks = layout.getItemStacks();
        IGuiIngredientGroup<IOutput> outputs = layout.getIngredientsGroup(IOutput.class);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int index = i + (j * 3);
                stacks.init(inputSlots + index, true, 7 + i * 18, 2 + j * 18);
                outputs.init(outputSlot + index, false, 106 + i * 18, 3 + j * 18);
            }
        }
        outputs.set(ingredients);

        int heat = wrapper.getRecipe().getHeat();
        List<ItemStack> heatSources = BWMHeatRegistry.getStacks(heat);
        if (!heatSources.isEmpty()) {
            stacks.init(19, true, 75, 38);
            stacks.set(19, heatSources);
        }

        List<List<ItemStack>> inputList = ingredients.getInputs(ItemStack.class);
        craftingGrid.setInputs(stacks, inputList);
    }
}
