package betterwithmods.module.compat.jei.category;

import betterwithmods.BWMod;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.registry.block.recipe.SawRecipe;
import betterwithmods.module.compat.jei.wrapper.BlockRecipeWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.util.Translator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by primetoxinz on 9/5/16.
 */
public class SteelSawRecipeCategory extends BWMRecipeCategory<BlockRecipeWrapper<SawRecipe>> {
    public static final int WIDTH = 82;
    public static final int HEIGHT = 50;
    public static final String UID = "bwm.saw.steel";

    public static final ResourceLocation LOCATION =  new ResourceLocation(BWMod.MODID, "textures/gui/jei/saw.png");

    public SteelSawRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper.createDrawable(LOCATION, 0, 0, WIDTH, HEIGHT), UID,Translator.translateToLocal("inv.saw.name"));
    }


    @Override
    public void setRecipe(@Nonnull IRecipeLayout layout, @Nonnull BlockRecipeWrapper wrapper, @Nonnull IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = layout.getItemStacks();
        guiItemStacks.init(0, true, 8, 9);
        guiItemStacks.init(1, false, 57, 9);
        guiItemStacks.init(2, false, 32, 27);
        guiItemStacks.set(0, ingredients.getInputs(ItemStack.class).get(0));
        guiItemStacks.set(1, ingredients.getOutputs(ItemStack.class).stream().flatMap(List::stream).collect(Collectors.toList()));
        guiItemStacks.set(2, new ItemStack(BWMBlocks.SAW));
    }
}
