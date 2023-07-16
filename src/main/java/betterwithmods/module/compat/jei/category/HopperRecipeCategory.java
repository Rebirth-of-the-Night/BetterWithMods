package betterwithmods.module.compat.jei.category;

import betterwithmods.BWMod;
import betterwithmods.api.recipe.IOutput;
import betterwithmods.common.blocks.mechanical.BlockMechMachines;
import betterwithmods.module.compat.jei.wrapper.HopperRecipeWrapper;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Purpose:
 *
 * @author primetoxinz
 * @version 11/20/16
 */
public class HopperRecipeCategory extends BWMRecipeCategory<HopperRecipeWrapper> {
    public static final int width = 145;
    public static final int height = 80;
    public static final String UID = "bwm.hopper";
    public static final ResourceLocation location = new ResourceLocation(BWMod.MODID, "textures/gui/jei/hopper.png");
    int outputSlot = 3;
    int secondaryOutputSlot = 5;

    public HopperRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper.createDrawable(location, 0, 0, width, height), UID, Translator.translateToLocal("inv.hopper.name"));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawExtras(Minecraft minecraft) {
        String throwText = Translator.translateToLocal("inv.hopper.throw");
        int l = minecraft.fontRenderer.getStringWidth(throwText);
        int textColor = 0x808080;
        minecraft.fontRenderer.drawString(throwText, width / 2 - l + 5, -11, textColor);
        minecraft.fontRenderer.drawString(Translator.translateToLocal("inv.hopper.filter"), width / 2 - 50, 16, textColor);
        minecraft.fontRenderer.drawString(Translator.translateToLocal("inv.hopper.outputs"), width / 2 + 10, -11, textColor);
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout layout, @Nonnull HopperRecipeWrapper wrapper, @Nonnull IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = layout.getItemStacks();
        IGuiIngredientGroup<IOutput> outputs = layout.getIngredientsGroup(IOutput.class);


        guiItemStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            if (slotIndex == 2 && !tooltip.isEmpty())
                tooltip.add(1, TextFormatting.LIGHT_PURPLE + "" + TextFormatting.BOLD + Translator.translateToLocal("inv.hopper.place"));
        });
        int x = width / 2 - 18, y = 0;
        guiItemStacks.init(0, true, x, y); //inputs item
        guiItemStacks.init(1, true, x - 27, y + 27); //filter
        guiItemStacks.init(2, true, x, y + 45); //urn

        createSlotsHorizontal(outputs, false, 2, outputSlot, x + 29, y + 1);
        createSlotsHorizontal(outputs, false, 2, secondaryOutputSlot, x + 29, y + 28);

        guiItemStacks.init(7, false, x, y + 27); //hopper
        guiItemStacks.init(8, false, x + 27, y + 45); //urn


        guiItemStacks.set(ingredients);
        guiItemStacks.set(7, BlockMechMachines.getStack(BlockMechMachines.EnumType.HOPPER));

        List<List<ItemStack>> containers = ingredients.getOutputs(ItemStack.class);
        if(!containers.isEmpty()) {
            List<ItemStack> container = containers.get(0);
            if (container != null)
                guiItemStacks.set(8, container);
        }

        outputs.set(ingredients);
        List<List<IOutput>> o = ingredients.getOutputs(IOutput.class);
        for (int i = 0; i < 4; i++) {
            List<IOutput> output = o.get(i);
            if (output != null)
                outputs.set(i + 3, output);
        }
    }
}

