package betterwithmods.common.items;

import betterwithmods.api.IMultiLocations;
import betterwithmods.client.BWCreativeTabs;
import betterwithmods.common.BWMItems;
import com.google.common.collect.Lists;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.ArrayList;
import java.util.List;

public class ItemBark extends Item implements IMultiLocations {

    public static List<String> barks = Lists.newArrayList("oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "bloody");

    public ItemBark() {
        super();
        this.setCreativeTab(BWCreativeTabs.BWTAB);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    public int getItemBurnTime(ItemStack itemStack) {
        return 25;
    }

    public static ItemStack getStack(String wood, int amount) {
        return new ItemStack(BWMItems.BARK, amount, barks.indexOf(wood));
    }

    @Override
    public String[] getLocations() {
        ArrayList<String> locations = new ArrayList<>();
        for (int i = 0; i < barks.size(); i++) {
            locations.add("bark_" + barks.get(i));
        }
        return locations.toArray(new String[locations.size()]);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab))
            for (int i = 0; i < barks.size(); i++) {
                items.add(new ItemStack(this, 1, i));
            }
    }


    @Override
    public String getTranslationKey(ItemStack stack) {
        if (stack.getMetadata() > barks.size())
            return super.getTranslationKey();
        return super.getTranslationKey() + "." + barks.get(stack.getItemDamage());
    }
}
