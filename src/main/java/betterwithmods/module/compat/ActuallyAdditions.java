package betterwithmods.module.compat;

import betterwithmods.module.CompatFeature;
import betterwithmods.module.hardcore.needs.hunger.FoodHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/**
 * Created by primetoxinz on 5/13/17.
 */
public class ActuallyAdditions extends CompatFeature {
    public ActuallyAdditions() {
        super("actuallyadditions");
    }

    @Override
    public void init(FMLInitializationEvent event) {
        FoodHelper.setAlwaysEdible(new ItemStack(getItemFood(new ResourceLocation(this.modid, "item_food"))));
    }

}
