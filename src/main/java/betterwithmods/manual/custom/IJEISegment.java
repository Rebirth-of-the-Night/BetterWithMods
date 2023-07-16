package betterwithmods.manual.custom;

import betterwithmods.util.StackIngredient;
import com.google.common.base.Strings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreIngredient;

public interface IJEISegment {

    default Ingredient getIngredient(String data) {
        if (data.startsWith("oredict")) {
            return new OreIngredient(data.substring("oredict:".length()));
        }
        data = data.substring(data.indexOf(":") + 1);
        final int splitIndex = data.lastIndexOf('@');
        final String name, optMeta;
        if (splitIndex > 0) {
            name = data.substring(0, splitIndex);
            optMeta = data.substring(splitIndex);
        } else {
            name = data;
            optMeta = "";
        }
        final int meta = (Strings.isNullOrEmpty(optMeta)) ? 0 : Integer.parseInt(optMeta.substring(1));
        final Item item = Item.REGISTRY.getObject(new ResourceLocation(name));
        if (item == null)
            return Ingredient.EMPTY;
        return StackIngredient.fromStacks(new ItemStack(item, 1, meta));
    }
}
