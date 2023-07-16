package betterwithmods.module.compat.jei.category;

import betterwithmods.BWMod;
import betterwithmods.api.recipe.IOutput;
import betterwithmods.common.registry.block.recipe.SawRecipe;
import betterwithmods.module.compat.jei.wrapper.BlockRecipeWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.util.Translator;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * Created by primetoxinz on 9/5/16.
 */
public class SawRecipeCategory extends BWMRecipeCategory<BlockRecipeWrapper<SawRecipe>> {
    public static final int WIDTH = 117;
    public static final int HEIGHT = 36;
    public static final String UID = "bwm.saw";

    public static final ResourceLocation LOCATION =  new ResourceLocation(BWMod.MODID, "textures/gui/jei/saw.png");

    public SawRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper.createDrawable(LOCATION, 0, 0, WIDTH, HEIGHT), UID,Translator.translateToLocal("inv.saw.name"));
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout layout, @Nonnull BlockRecipeWrapper wrapper, @Nonnull IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = layout.getItemStacks();
        IGuiIngredientGroup<IOutput> guiOutputs = layout.getIngredientsGroup(IOutput.class);
        guiItemStacks.init(0, true, 8, 9);
        createSlotsHorizontal(guiOutputs, false, 3, 1, 58, 10);
        guiItemStacks.set(ingredients);
        guiOutputs.set(ingredients);
    }
}
