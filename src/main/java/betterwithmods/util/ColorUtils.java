package betterwithmods.util;

import betterwithmods.api.util.impl.BlockColorProvider;
import betterwithmods.common.BWMRecipes;
import betterwithmods.common.BWOreDictionary;
import betterwithmods.common.registry.block.recipe.BlockIngredient;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;

public class ColorUtils {
    public static final PropertyEnum<EnumDyeColor> COLOR = BlockColored.COLOR;

    private static final EnumDyeColor[] DYES = EnumDyeColor.values();
    public static HashMap<BlockIngredient, DyeAmount> FLOWER_TO_DYES = Maps.newHashMap();
    private static HashMap<String, EnumDyeColor> DYE_CACHE = Maps.newHashMap();

    static {
        addFlower(BlockFlower.EnumFlowerType.DANDELION, new DyeAmount(EnumDyeColor.YELLOW, 2));
        addFlower(BlockFlower.EnumFlowerType.POPPY, new DyeAmount(EnumDyeColor.RED, 2));
        addFlower(BlockFlower.EnumFlowerType.BLUE_ORCHID, new DyeAmount(EnumDyeColor.LIGHT_BLUE, 2));
        addFlower(BlockFlower.EnumFlowerType.ALLIUM, new DyeAmount(EnumDyeColor.MAGENTA, 2));
        addFlower(BlockFlower.EnumFlowerType.HOUSTONIA, new DyeAmount(EnumDyeColor.SILVER, 2));
        addFlower(BlockFlower.EnumFlowerType.RED_TULIP, new DyeAmount(EnumDyeColor.RED, 2));
        addFlower(BlockFlower.EnumFlowerType.ORANGE_TULIP, new DyeAmount(EnumDyeColor.ORANGE, 2));
        addFlower(BlockFlower.EnumFlowerType.WHITE_TULIP, new DyeAmount(EnumDyeColor.SILVER, 2));
        addFlower(BlockFlower.EnumFlowerType.PINK_TULIP, new DyeAmount(EnumDyeColor.PINK, 2));
        addFlower(BlockFlower.EnumFlowerType.OXEYE_DAISY, new DyeAmount(EnumDyeColor.SILVER, 2));
        addFlower(BlockDoublePlant.EnumPlantType.PAEONIA, new DyeAmount(EnumDyeColor.PINK, 4));
        addFlower(BlockDoublePlant.EnumPlantType.ROSE, new DyeAmount(EnumDyeColor.RED, 4));
        addFlower(BlockDoublePlant.EnumPlantType.SYRINGA, new DyeAmount(EnumDyeColor.MAGENTA, 4));
        addFlower(BlockDoublePlant.EnumPlantType.SUNFLOWER, new DyeAmount(EnumDyeColor.YELLOW, 4));
    }

    private static void addFlower(BlockIngredient ingredient, DyeAmount dyeAmount) {
        FLOWER_TO_DYES.put(ingredient, dyeAmount);
    }

    private static void addFlower(BlockDoublePlant.EnumPlantType type, DyeAmount dyeAmount) {
        ItemStack flower = new ItemStack(Blocks.DOUBLE_PLANT, 1, type.getMeta());
        addFlower(new BlockIngredient(flower), dyeAmount);
    }


    private static void addFlower(BlockFlower.EnumFlowerType type, DyeAmount dyeAmount) {
        ItemStack flower = new ItemStack(type.getBlockType() == BlockFlower.EnumFlowerColor.YELLOW ? Blocks.YELLOW_FLOWER : Blocks.RED_FLOWER, 1, type.getMeta());
        addFlower(new BlockIngredient(flower), dyeAmount);
    }

    public static ItemStack getDye(EnumDyeColor color, int count) {
        return new ItemStack(Items.DYE, count, color.getDyeDamage());
    }

    @Nullable
    private static EnumDyeColor getDye(String dyeOredict) {
        if (!DYE_CACHE.containsKey(dyeOredict)) {
            for (EnumDyeColor dye : DYES) {
                String oredict = String.format("dye%s", CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, dye.getTranslationKey()));
                if (oredict.matches(dyeOredict)) {
                    DYE_CACHE.put(dyeOredict, dye);
                    break;
                }
            }
        }
        return DYE_CACHE.get(dyeOredict);
    }


    public static float[] average(float[]... arrays) {
        int divisor = arrays.length;
        float[] output = new float[arrays[0].length];
        for (float[] array : arrays) {
            for (int j = 0; j < array.length; j++) {
                output[j] += array[j];
            }
        }
        for (int i = 0; i < output.length; i++) {
            output[i] = output[i] / divisor;
        }
        return output;
    }

    @Nullable
    public static EnumDyeColor getColor(ItemStack stack) {
        if (stack != ItemStack.EMPTY && BWOreDictionary.hasPrefix(stack, "dye")) {
            for (String ore : BWOreDictionary.getOres(stack)) {
                EnumDyeColor dye = getDye(ore);
                if (dye != null)
                    return dye;
            }
        }
        return null;
    }


    public static float[] getColorFromBlock(World world, BlockPos pos, BlockPos beacon) {
        if (world.isAirBlock(pos)) {
            return new float[]{1, 1, 1};
        }
        IBlockState state = world.getBlockState(pos);
        float[] color = state.getBlock().getBeaconColorMultiplier(state, world, pos, beacon);
        if (color == null) {
            color = BlockColorProvider.INSTANCE.getColorComponents(BWMRecipes.getStackFromState(state));
        }
        return color != null ? color : new float[]{1, 1, 1};
    }


    public static class DyeAmount {
        private EnumDyeColor dye;
        private int count;

        public DyeAmount(EnumDyeColor dye, int count) {
            this.dye = dye;
            this.count = count;
        }

        public ItemStack getStack() {
            return getDye(dye, count);
        }
    }
}
