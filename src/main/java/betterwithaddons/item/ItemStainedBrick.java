package betterwithaddons.item;

import betterwithaddons.util.IHasVariants;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.ArrayList;
import java.util.List;

public class ItemStainedBrick extends Item implements IHasVariants {

    public ItemStainedBrick() {
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    public String getTranslationKey(ItemStack p_getTranslationKey_1_) {
        int i = p_getTranslationKey_1_.getMetadata();
        return super.getTranslationKey() + "." + EnumDyeColor.byMetadata(i).getTranslationKey();
    }

    @Override
    public List<ModelResourceLocation> getVariantModels() {
        ArrayList<ModelResourceLocation> rlist = new ArrayList<ModelResourceLocation>();

        EnumDyeColor[] dyes = EnumDyeColor.values();
        int len = dyes.length;

        for(int i = 0; i < len; ++i) {
            EnumDyeColor dye = dyes[i];
            rlist.add(new ModelResourceLocation(getRegistryName()+"_"+dye.getName(), "inventory"));
        }

        return rlist;
    }

    @Override
    public String getVariantName(int meta) {
        return null;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if(this.isInCreativeTab(tab))
            for(int i = 0; i < 16; ++i) {
                items.add(new ItemStack(this, 1, i));
            }
    }
}
