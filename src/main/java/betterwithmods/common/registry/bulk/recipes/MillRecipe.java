package betterwithmods.common.registry.bulk.recipes;

import betterwithmods.api.recipe.IRecipeOutputs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by primetoxinz on 5/16/17.
 */
public class MillRecipe extends BulkRecipe {
    private SoundEvent sound;

    private int ticks;

    public MillRecipe(List<Ingredient> inputs, IRecipeOutputs outputs, int priority, SoundEvent sound, int ticks) {
        super(inputs, outputs, priority);
        this.sound = sound;
        this.ticks = ticks;
    }

    public MillRecipe(@Nonnull List<Ingredient> inputs, @Nonnull List<ItemStack> outputs, int ticks) {
        super(inputs, outputs);
        this.ticks = ticks;
    }

    public MillRecipe(@Nonnull List<Ingredient> inputs, @Nonnull List<ItemStack> outputs) {
        this(inputs, outputs, 200);

    }

    public SoundEvent getSound() {
        return sound;
    }

    public MillRecipe setSound(SoundEvent sound) {
        this.sound = sound;
        return this;
    }

    public MillRecipe setSound(String sound) {
        SoundEvent s = null;
        if (sound != null && !sound.isEmpty()) {
            try {
                s = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(sound));
            } catch (Throwable ignore) {
            }
        }
        return setSound(s);
    }

    @Override
    public MillRecipe setPriority(int priority) {
        return (MillRecipe) super.setPriority(priority);
    }

    public int getTicks() {
        return ticks;
    }

    public MillRecipe setTicks(int ticks) {
        this.ticks = ticks;
        if (this.ticks <= 0)
            this.ticks = 200;
        return this;
    }

}
