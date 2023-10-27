package betterwithmods.module.compat.jei.category;

import betterwithmods.BWMod;
import betterwithmods.api.recipe.IOutput;
import betterwithmods.common.registry.bulk.recipes.MillRecipe;
import betterwithmods.module.compat.jei.IngredientTypes;
import betterwithmods.module.compat.jei.wrapper.BulkRecipeWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class MillRecipeCategory extends BWMRecipeCategory<BulkRecipeWrapper<MillRecipe>> {
    public static final String UID = "bwm.mill";
    private static final int width = 149;
    private static final int height = 32;
    private static final int inputSlots = 0;
    private static final int outputSlot = 3;
    private static final ResourceLocation guiTexture = new ResourceLocation(BWMod.MODID, "textures/gui/jei/mill.png");

    @Nonnull
    private final IDrawableAnimated gear;

    public MillRecipeCategory(IGuiHelper helper) {
        super(helper.createDrawable(guiTexture, 0, 0, width, height), UID, "inv.mill.name");
        IDrawableStatic flameDrawable = helper.createDrawable(guiTexture, 150, 0, 14, 14);
        this.gear = helper.createAnimatedDrawable(flameDrawable, 200, IDrawableAnimated.StartDirection.BOTTOM, false);
    }

    @Override
    public void drawExtras(@Nonnull Minecraft minecraft) {
        gear.draw(minecraft, 68, 8);
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout layout, @Nonnull BulkRecipeWrapper<MillRecipe> wrapper, @Nonnull IIngredients ingredients) {
        IGuiItemStackGroup guiItemStackGroup = layout.getItemStacks();
        IGuiIngredientGroup<IOutput> guiOutputs = layout.getIngredientsGroup(IngredientTypes.OUTPUT_GENERIC);

        createSlotsHorizontal(guiItemStackGroup, true, 3, inputSlots, 7, 7);
        createSlotsHorizontal(guiOutputs, false, 3, outputSlot, 90, 8);

        guiItemStackGroup.set(ingredients);
        guiOutputs.set(ingredients);


    }
}
