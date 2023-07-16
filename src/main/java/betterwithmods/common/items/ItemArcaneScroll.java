package betterwithmods.common.items;

import betterwithmods.common.BWMItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by primetoxinz on 9/11/16.
 */
public class ItemArcaneScroll extends Item {

    public ItemArcaneScroll() {
        super();
        setHasSubtypes(true);
    }

    public static ItemStack getScrollWithEnchant(Enchantment enchantment) {
        ItemStack stack = new ItemStack(BWMItems.ARCANE_SCROLL);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("enchant", Enchantment.getEnchantmentID(enchantment));
        stack.setTagCompound(tag);
        return stack;
    }

    public static Enchantment getEnchantment(ItemStack scroll) {
        NBTTagCompound tag = scroll.getTagCompound();
        if (tag.hasKey("enchant")) {
            int e = tag.getInteger("enchant");
            return Enchantment.getEnchantmentByID(e);

        }
        return null;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (Enchantment enchantment : Enchantment.REGISTRY) {
                items.add(getScrollWithEnchant(enchantment));
            }
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    /**
     * Return an item rarity from EnumRarity
     */
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        int id = stack.getTagCompound() != null ? stack.getTagCompound().getInteger("enchant") : 0;
        Enchantment enchantment = Enchantment.getEnchantmentByID(id);
        if (enchantment != null) {
            tooltip.add(enchantment.getTranslatedName(-1));
        }
    }

}
