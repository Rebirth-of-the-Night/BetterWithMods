package betterwithaddons.interaction.jei.category;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nonnull;

@SuppressWarnings("rawtypes")
public abstract class BWARecipeCategory implements IRecipeCategory
{
    @Nonnull
    private final IDrawable background;

    @Nonnull
    private final String localizedName;

    public BWARecipeCategory(@Nonnull IDrawable background, String unlocalizedName)
    {
        this.background = background;
        this.localizedName = I18n.translateToLocal(unlocalizedName);
    }

    @Nonnull
    @Override
    public String getTitle()
    {
        return localizedName;
    }

    @Nonnull
    @Override
    public IDrawable getBackground()
    {
        return background;
    }
}