package betterwithmods.module;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.util.ResourceLocation;

/**
 * Created by primetoxinz on 5/24/17.
 */
public class CompatFeature extends Feature {
    protected final String modid;

    public CompatFeature(String modid) {
        this.modid = modid;
    }

    @Override
    public String getFeatureDescription() {
        return "Compatibility for " + modid;
    }

    public Item getItem(ResourceLocation location) {
        return Item.REGISTRY.getObject(location);
    }

    public Item getItem(String location) {
        return Item.REGISTRY.getObject(new ResourceLocation(location));
    }

    public Block getBlock(ResourceLocation location) {
        return Block.REGISTRY.getObject(location);
    }

    public Block getBlock(String location) {
        return Block.REGISTRY.getObject(new ResourceLocation(location));
    }


    public ItemFood getItemFood(ResourceLocation location) {
        Item item = getItem(location);
        if (item instanceof ItemFood)
            return (ItemFood) item;
        return null;
    }


}
