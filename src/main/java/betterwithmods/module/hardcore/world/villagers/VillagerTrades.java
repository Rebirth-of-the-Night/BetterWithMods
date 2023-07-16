package betterwithmods.module.hardcore.world.villagers;

import betterwithmods.common.items.ItemMaterial;
import com.google.common.collect.Lists;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import java.util.List;
import java.util.Random;

public class VillagerTrades {

    public static List<EntityVillager.ITradeList> CLERIC_1 = Lists.newArrayList(
            new EntityVillager.ListItemForEmeralds(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.HEMP), new EntityVillager.PriceInfo(-18, -22)),
            new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.RED_MUSHROOM), new EntityVillager.PriceInfo(-14, -16)),
            new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.CACTUS), new EntityVillager.PriceInfo(-35, -62)),
            new EntityVillager.ListItemForEmeralds(new ItemStack(Items.FLINT_AND_STEEL), new EntityVillager.PriceInfo(1, 1)),
            new EntityVillager.ListItemForEmeralds(new ItemStack(Items.PAINTING), new EntityVillager.PriceInfo(2, 3)),
            new ItemForLevel(new ItemStack(Blocks.ENCHANTING_TABLE))
    );

    public static class ItemForLevel implements EntityVillager.ITradeList {
        public ItemStack input;

        public ItemForLevel(ItemStack input) {
            this.input = input;
        }

        @Override
        public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
            recipeList.add(new MerchantRecipe(input, ItemStack.EMPTY));
        }
    }


}
