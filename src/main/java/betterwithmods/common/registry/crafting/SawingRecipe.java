package betterwithmods.common.registry.crafting;

import betterwithmods.BWMod;
import betterwithmods.common.BWMItems;
import betterwithmods.common.BWSounds;
import betterwithmods.common.items.tools.ItemHacksaw;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import org.apache.commons.lang3.tuple.Pair;

public class SawingRecipe extends ToolDamageRecipe {
    public SawingRecipe(ItemStack result, Ingredient input) {
        super(new ResourceLocation(BWMod.MODID, "hacksaw"), result, input, stack -> stack.getItem() instanceof ItemHacksaw);
    }

    @Override
    public SoundEvent getSound() {
        return BWSounds.METAL_HACKSAW;
    }

    @Override
    public Pair<Float, Float> getSoundValues() {
        return Pair.of(1.0f,0.8f);
    }

    @Override
    public ItemStack getExampleStack() {
        return new ItemStack(BWMItems.STEEL_HACKSAW);
    }
}
