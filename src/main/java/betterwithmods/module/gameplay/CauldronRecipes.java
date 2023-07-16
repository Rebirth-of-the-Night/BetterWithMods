package betterwithmods.module.gameplay;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWMItems;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.blocks.BlockAesthetic;
import betterwithmods.common.items.ItemMaterial;
import betterwithmods.common.registry.block.recipe.IngredientSpecial;
import betterwithmods.common.registry.heat.BWMHeatRegistry;
import betterwithmods.module.Feature;
import betterwithmods.module.gameplay.miniblocks.MiniBlockIngredient;
import betterwithmods.util.StackIngredient;
import com.google.common.collect.Lists;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;

import java.util.Map;

/**
 * Created by primetoxinz on 5/16/17.
 */
public class CauldronRecipes extends Feature {
    public CauldronRecipes() {
        canDisable = false;
    }

    @Override
    public void init(FMLInitializationEvent event) {
        unstoked();
        stoked();
    }

    private void stoked() {

        Ingredient dung = new OreIngredient("dung");
        Ingredient food = StackIngredient.fromIngredient(8, new IngredientSpecial(stack -> stack.getItem() instanceof ItemFood));
        BWRegistry.CAULDRON.addUnstokedRecipe(Lists.newArrayList(food, dung), Lists.newArrayList(new ItemStack(BWMItems.FERTILIZER, 8)));

        StackIngredient meat = StackIngredient.mergeStacked(Lists.newArrayList(
                StackIngredient.fromOre(1, "meatPork"),
                StackIngredient.fromOre(4, "meatBeef"),
                StackIngredient.fromOre(4, "meatMutton"),
                StackIngredient.fromOre(10, "meatRotten")
        ));
        BWRegistry.CAULDRON.addStokedRecipe(meat, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.TALLOW));

        StackIngredient leather = StackIngredient.mergeStacked(Lists.newArrayList(
                StackIngredient.fromStacks(new ItemStack(Items.LEATHER)),
                StackIngredient.fromStacks(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SCOURED_LEATHER)),
                StackIngredient.fromStacks(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.LEATHER_STRAP, 8)),
                StackIngredient.fromStacks(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.LEATHER_CUT, 2)),
                StackIngredient.fromStacks(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SCOURED_LEATHER_CUT, 2)),
                StackIngredient.fromStacks(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.LEATHER_CUT, 2)),
                StackIngredient.fromStacks(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.TANNED_LEATHER_CUT, 2)),
                StackIngredient.fromStacks(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SCOURED_LEATHER_CUT, 2)),
                StackIngredient.fromOre(2, "book")
        ));
        BWRegistry.CAULDRON.addStokedRecipe(leather, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.GLUE));

        StackIngredient wood = StackIngredient.mergeStacked(Lists.newArrayList(
                StackIngredient.fromOre("logWood"),
                StackIngredient.fromOre(6, "plankWood"),
                StackIngredient.fromIngredient(12, new MiniBlockIngredient("siding", new OreIngredient("plankWood"))),
                StackIngredient.fromIngredient(24, new MiniBlockIngredient("moulding", new OreIngredient("plankWood"))),
                StackIngredient.fromIngredient(48, new MiniBlockIngredient("corner", new OreIngredient("plankWood"))),
                StackIngredient.fromOre(16, "dustWood")
        ));
        BWRegistry.CAULDRON.addStokedRecipe(wood, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.POTASH));

        BWRegistry.CAULDRON.addStokedRecipe(new ItemStack(Items.BOW, 1, OreDictionary.WILDCARD_VALUE), Lists.newArrayList(new ItemStack(Items.STRING), new ItemStack(Items.STICK)));
        BWRegistry.CAULDRON.addStokedRecipe(new ItemStack(BWMItems.COMPOSITE_BOW, 1, OreDictionary.WILDCARD_VALUE), Lists.newArrayList(new ItemStack(Items.STRING), new ItemStack(Items.BONE)));

        BWRegistry.CAULDRON.addStokedRecipe(Lists.newArrayList(ItemMaterial.getIngredient(ItemMaterial.EnumMaterial.TALLOW), ItemMaterial.getIngredient(ItemMaterial.EnumMaterial.POTASH)), Lists.newArrayList(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SOAP)));

        BWRegistry.CAULDRON.addStokedRecipe(new ItemStack(Items.LEATHER_HELMET, 1, OreDictionary.WILDCARD_VALUE), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.GLUE, 2));
        BWRegistry.CAULDRON.addStokedRecipe(new ItemStack(Items.LEATHER_CHESTPLATE, 1, OreDictionary.WILDCARD_VALUE), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.GLUE, 4));
        BWRegistry.CAULDRON.addStokedRecipe(new ItemStack(Items.LEATHER_LEGGINGS, 1, OreDictionary.WILDCARD_VALUE), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.GLUE, 3));
        BWRegistry.CAULDRON.addStokedRecipe(new ItemStack(Items.LEATHER_BOOTS, 1, OreDictionary.WILDCARD_VALUE), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.GLUE, 2));

        BWRegistry.CAULDRON.addStokedRecipe(new ItemStack(BWMItems.LEATHER_TANNED_HELMET, 1, OreDictionary.WILDCARD_VALUE), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.GLUE, 2));
        BWRegistry.CAULDRON.addStokedRecipe(new ItemStack(BWMItems.LEATHER_TANNED_CHEST, 1, OreDictionary.WILDCARD_VALUE), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.GLUE, 4));
        BWRegistry.CAULDRON.addStokedRecipe(new ItemStack(BWMItems.LEATHER_TANNED_PANTS, 1, OreDictionary.WILDCARD_VALUE), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.GLUE, 3));
        BWRegistry.CAULDRON.addStokedRecipe(new ItemStack(BWMItems.LEATHER_TANNED_BOOTS, 1, OreDictionary.WILDCARD_VALUE), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.GLUE, 2));

        BWRegistry.CAULDRON.addStokedRecipe(Lists.newArrayList(
                StackIngredient.fromItem(Items.SUGAR),
                StackIngredient.fromOre(4, "meatRotten"),
                StackIngredient.fromStacks(new ItemStack(Items.DYE, 4, EnumDyeColor.WHITE.getDyeDamage()))
        ), Lists.newArrayList(new ItemStack(BWMItems.KIBBLE, 2)));
    }

    private void unstoked() {
        StackIngredient cord = StackIngredient.mergeStacked(Lists.newArrayList(
                StackIngredient.fromOre(1, "string"),
                StackIngredient.fromOre(1, "fiberHemp")
        ));

        BWRegistry.CAULDRON.addHeatlessRecipe(Lists.newArrayList(new OreIngredient("dustPotash"), StackIngredient.fromOre(4, "dustHellfire")), Lists.newArrayList(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.NETHER_SLUDGE, 8)), BWMHeatRegistry.UNSTOKED_HEAT);
        BWRegistry.CAULDRON.addHeatlessRecipe(Lists.newArrayList(new OreIngredient("dustHellfire"), new OreIngredient("dustCarbon")), Lists.newArrayList(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.NETHERCOAL, 4)), BWMHeatRegistry.UNSTOKED_HEAT);
        BWRegistry.CAULDRON.addHeatlessRecipe(Lists.newArrayList(new OreIngredient("dustHellfire"), StackIngredient.fromStacks(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.TALLOW))), Lists.newArrayList(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.BLASTING_OIL, 2)), BWMHeatRegistry.UNSTOKED_HEAT);
        BWRegistry.CAULDRON.addHeatlessRecipe(Lists.newArrayList(StackIngredient.fromOre(8, "dustHellfire")), Lists.newArrayList(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.CONCENTRATED_HELLFIRE)), BWMHeatRegistry.UNSTOKED_HEAT);

        BWRegistry.CAULDRON.addUnstokedRecipe(Lists.newArrayList(new OreIngredient("foodFlour"), StackIngredient.fromItem(Items.SUGAR)), Lists.newArrayList(new ItemStack(BWMItems.DONUT, 4)));
        BWRegistry.CAULDRON.addUnstokedRecipe(new OreIngredient("blockCactus"), new ItemStack(Items.DYE, 1, 2));
        BWRegistry.CAULDRON.addUnstokedRecipe(Lists.newArrayList(cord, new OreIngredient("dustGlowstone"), new OreIngredient("dustRedstone")), Lists.newArrayList(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.FILAMENT)));
        BWRegistry.CAULDRON.addUnstokedRecipe(Lists.newArrayList(cord, new OreIngredient("dustBlaze"), new OreIngredient("dustRedstone")), Lists.newArrayList(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.ELEMENT)));

        StackIngredient bark = StackIngredient.mergeStacked(Lists.newArrayList(
                StackIngredient.fromOre(5, "barkOak"),
                StackIngredient.fromOre(3, "barkSpruce"),
                StackIngredient.fromOre(2, "barkBirch"),
                StackIngredient.fromOre(4, "barkJungle"),
                StackIngredient.fromOre(8, "barkAcacia"),
                StackIngredient.fromOre(8, "barkDarkOak"),
                StackIngredient.fromOre(8, "barkBlood")
        ));
        BWRegistry.CAULDRON.addUnstokedRecipe(Lists.newArrayList(
                StackIngredient.fromStacks(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SCOURED_LEATHER)),
                bark
        ), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.TANNED_LEATHER));

        BWRegistry.CAULDRON.addUnstokedRecipe(Lists.newArrayList(
                StackIngredient.fromStacks(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SCOURED_LEATHER_CUT, 2)),
                bark
        ), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.TANNED_LEATHER_CUT, 2));


        BWRegistry.CAULDRON.addUnstokedRecipe(Lists.newArrayList(
                new OreIngredient("dustSulfur"),
                new OreIngredient("dustSaltpeter"),
                new OreIngredient("dustCarbon")),
                new ItemStack(Items.GUNPOWDER, 2));
        BWRegistry.CAULDRON.addUnstokedRecipe(Lists.newArrayList(
                new OreIngredient("gunpowder"),
                cord),
                ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.FUSE));
        BWRegistry.CAULDRON.addUnstokedRecipe(Lists.newArrayList(
                StackIngredient.fromStacks(BlockAesthetic.getStack(BlockAesthetic.EnumType.CHOPBLOCKBLOOD, 4)),
                new OreIngredient("soap")),
                BlockAesthetic.getStack(BlockAesthetic.EnumType.CHOPBLOCK, 4));
        BWRegistry.CAULDRON.addUnstokedRecipe(Lists.newArrayList(
                StackIngredient.fromStacks(new ItemStack(Blocks.STICKY_PISTON, 4)),
                new OreIngredient("soap")),
                new ItemStack(Blocks.PISTON, 4));
        BWRegistry.CAULDRON.addUnstokedRecipe(Lists.newArrayList(
                new OreIngredient("meatFish"),
                StackIngredient.fromItem(Items.MILK_BUCKET),
                StackIngredient.fromStacks(new ItemStack(Items.BOWL, 2))),
                new ItemStack(BWMItems.CHOWDER, 2));
        BWRegistry.CAULDRON.addUnstokedRecipe(Lists.newArrayList(
                new OreIngredient("meatChicken"),
                new OreIngredient("cookedCarrot"),
                new OreIngredient("cookedPotato"),
                StackIngredient.fromStacks(new ItemStack(Items.BOWL, 3))),
                new ItemStack(BWMItems.CHICKEN_SOUP, 3));
        BWRegistry.CAULDRON.addUnstokedRecipe(Lists.newArrayList(
                new OreIngredient("foodCocoapowder"),
                StackIngredient.fromItem(Items.SUGAR),
                StackIngredient.fromItem(Items.MILK_BUCKET)),
                new ItemStack(BWMItems.CHOCOLATE, 2)
        );

        Ingredient stewMeats = StackIngredient.mergeStacked(Lists.newArrayList(
                StackIngredient.fromOre("meatPork"),
                StackIngredient.fromOre("meatBeef"),
                StackIngredient.fromOre("meatMutton")
        ));

        BWRegistry.CAULDRON.addUnstokedRecipe(Lists.newArrayList(
                stewMeats,
                new OreIngredient("foodFlour"),
                new OreIngredient("cookedCarrot"),
                new OreIngredient("cookedPotato"),
                StackIngredient.fromStacks(new ItemStack(Items.BOWL, 5)),
                StackIngredient.fromStacks(new ItemStack(Blocks.BROWN_MUSHROOM, 3))
        ), new ItemStack(BWMItems.HEARTY_STEW, 5));

        BWRegistry.CAULDRON.addUnstokedRecipe(Lists.newArrayList(
                StackIngredient.fromItem(Items.MILK_BUCKET),
                StackIngredient.fromItem(Items.BOWL),
                StackIngredient.fromStacks(new ItemStack(Blocks.BROWN_MUSHROOM, 3))
        ), new ItemStack(Items.MUSHROOM_STEW));

        BWRegistry.CAULDRON.addUnstokedRecipe(Lists.newArrayList(
                StackIngredient.fromItem(Items.BOWL),
                StackIngredient.fromStacks(new ItemStack(Items.BEETROOT, 6))
        ), new ItemStack(Items.BEETROOT_SOUP));

        BWRegistry.CAULDRON.addUnstokedRecipe(Lists.newArrayList(
                StackIngredient.fromItem(Items.COOKED_RABBIT),
                new OreIngredient("cookedCarrot"),
                new OreIngredient("cookedPotato"),
                StackIngredient.fromOre("foodFlour"),
                StackIngredient.fromStacks(new ItemStack(Blocks.BROWN_MUSHROOM, 3)),
                StackIngredient.fromStacks(new ItemStack(Items.BOWL, 5))
        ), new ItemStack(Items.RABBIT_STEW, 5));

        BWRegistry.CAULDRON.addUnstokedRecipe(Lists.newArrayList(
                StackIngredient.fromStacks(new ItemStack(Blocks.SAPLING)),
                StackIngredient.fromStacks(new ItemStack(Blocks.SAPLING, 1, 1)),
                StackIngredient.fromStacks(new ItemStack(Blocks.SAPLING, 1, 2)),
                StackIngredient.fromStacks(new ItemStack(Blocks.SAPLING, 1, 3)),
                StackIngredient.fromStacks(new ItemStack(Blocks.SAPLING, 1, 4)),
                StackIngredient.fromStacks(new ItemStack(Blocks.SAPLING, 1, 5)),
                StackIngredient.fromStacks(new ItemStack(Items.NETHER_WART)),
                StackIngredient.fromOre(8, "blockSoulUrn")
        ), new ItemStack(BWMBlocks.BLOOD_SAPLING));

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        //Add all food recipes
        Map<ItemStack, ItemStack> furnace = FurnaceRecipes.instance().getSmeltingList();
        for (ItemStack input : furnace.keySet()) {
            if (input != null) {
                if (input.getItem() instanceof ItemFood && input.getItem() != Items.BREAD) {
                    ItemStack output = FurnaceRecipes.instance().getSmeltingResult(input);
                    if (!output.isEmpty()) {
                        BWRegistry.CAULDRON.addUnstokedRecipe(input, output);
                    }
                }
            }
        }
    }


}

