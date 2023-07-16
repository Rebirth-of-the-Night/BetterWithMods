package betterwithmods.common.items.tools;

import betterwithmods.common.BWMItems;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

import java.util.Set;

public class ItemHacksaw extends ItemTool {

    public static final Set<Block> EFFECTIVE = Sets.newHashSet();

    public ItemHacksaw() {
        super(1.0F, -2.8F, BWMItems.SOULFORGED_STEEL, EFFECTIVE);

    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return Sets.newHashSet("axe");
    }
}
