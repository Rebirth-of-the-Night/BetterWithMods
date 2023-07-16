package betterwithmods.module.compat.jei.category;

import betterwithmods.BWMod;
import betterwithmods.api.recipe.IOutput;
import betterwithmods.common.blocks.mechanical.BlockMechMachines;
import betterwithmods.module.compat.jei.wrapper.TurntableRecipeWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.util.Translator;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * Purpose:
 *
 * @author primetoxinz
 * @version 11/11/16
 */
public class TurntableRecipeCategory extends BWMRecipeCategory<TurntableRecipeWrapper> {
    public static final int width = 76;
    public static final int height = 50;
    public static final String UID = "bwm.turntable";
    private static final ResourceLocation location = new ResourceLocation(BWMod.MODID, "textures/gui/jei/turntable.png");


    public TurntableRecipeCategory(IGuiHelper helper) {
        super(helper.createDrawable(location, 0, 0, width, height), UID, Translator.translateToLocal("inv.turntable.name"));
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout layout, @Nonnull TurntableRecipeWrapper wrapper, @Nonnull IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = layout.getItemStacks();
        IGuiIngredientGroup<IOutput> guiOutputs = layout.getIngredientsGroup(IOutput.class);

        guiItemStacks.init(0, true, 5, 9);
        guiItemStacks.init(1, false, 32, 9);
        guiItemStacks.init(4, false, 5, 27);

        createSlotsHorizontal(guiOutputs, false, 2, 2, 33, 30);

        guiItemStacks.set(ingredients);
        guiOutputs.set(ingredients);

        guiItemStacks.set(4, BlockMechMachines.getStack(BlockMechMachines.EnumType.TURNTABLE));
    }
}

