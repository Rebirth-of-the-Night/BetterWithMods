/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [18/03/2016, 22:16:30 (GMT)]
 */
package betterwithmods.module;

import betterwithmods.util.StackIngredient;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

public class ConfigHelper {

    public static boolean needsRestart;
    public static boolean allNeedRestart = false;


    public static HashMap<String, Boolean> CONDITIONS = Maps.newHashMap();

    public static void setDescription(String category, String comment) {
        ModuleLoader.config.setCategoryComment(category, comment);
    }

    public static boolean loadRecipeCondition(String jsonString, String propName, String category, String desc, boolean default_) {
        boolean value = loadPropBool(propName, category, desc, default_);
        CONDITIONS.put(jsonString, value);
        return value;
    }

    public static int[] loadPropIntList(String propName, String category, String comment, int[] default_) {
        Property prop = ModuleLoader.config.get(category, propName, default_, comment);
        setNeedsRestart(prop);
        return prop.getIntList();
    }

    public static int loadPropInt(String propName, String category, String desc, String comment, int default_, int min, int max) {
        Property prop = ModuleLoader.config.get(category, propName, default_, comment, min, max);
        prop.setComment(desc);
        setNeedsRestart(prop);

        return prop.getInt(default_);
    }

    public static int loadPropInt(String propName, String category, String desc, int default_) {
        Property prop = ModuleLoader.config.get(category, propName, default_);
        prop.setComment(desc);
        setNeedsRestart(prop);

        return prop.getInt(default_);
    }

    public static double loadPropDouble(String propName, String category, String desc, double default_) {
        Property prop = ModuleLoader.config.get(category, propName, default_);
        prop.setComment(desc);
        setNeedsRestart(prop);

        return prop.getDouble(default_);
    }

    public static double loadPropDouble(String propName, String category, String desc, double default_, double min, double max) {
        Property prop = ModuleLoader.config.get(category, propName, default_, desc, min, max);
        prop.setComment(desc);
        setNeedsRestart(prop);

        return prop.getDouble(default_);
    }

    public static boolean loadPropBool(String propName, String category, String desc, boolean default_) {
        Property prop = ModuleLoader.config.get(category, propName, default_);
        prop.setComment(desc);
        setNeedsRestart(prop);

        return prop.getBoolean(default_);
    }

    public static String loadPropString(String propName, String category, String desc, String default_) {
        Property prop = ModuleLoader.config.get(category, propName, default_);
        prop.setComment(desc);
        setNeedsRestart(prop);

        return prop.getString();
    }

    public static String[] loadPropStringList(String propName, String category, String desc, String[] default_) {
        Property prop = ModuleLoader.config.get(category, propName, default_);
        prop.setComment(desc);
        setNeedsRestart(prop);
        return prop.getStringList();
    }

    public static List<ResourceLocation> loadPropRLList(String propName, String category, String desc, String[] default_) {
        String[] l = loadPropStringList(propName, category, desc, default_);
        return Arrays.stream(l).map(ConfigHelper::rlFromString).collect(Collectors.toList());
    }

    public static Set<ResourceLocation> loadPropRLSet(String propName, String category, String desc, String[] default_) {
        String[] l = loadPropStringList(propName, category, desc, default_);
        return Arrays.stream(l).map(ConfigHelper::rlFromString).collect(Collectors.toSet());
    }

    public static ResourceLocation rlFromString(String loc) {
        String[] split = loc.split(":");
        if (split.length > 1) {
            return new ResourceLocation(split[0], split[1]);
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public static Collection<IBlockState> statesFromString(String name) {
        String[] split = name.split(":");

        if (split.length > 1) {
            int meta = 0;
            if (split.length > 2) {
                if (split[2].equalsIgnoreCase("*"))
                    meta = OreDictionary.WILDCARD_VALUE;
                else
                    meta = Integer.parseInt(split[2]);
            }
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(split[0], split[1]));
            if (block != null) {
                if(meta == OreDictionary.WILDCARD_VALUE) {
                    return block.getBlockState().getValidStates();
                } else {
                    return Collections.singleton(block.getStateFromMeta(meta));
                }
            }
        }
        return Collections.emptySet();
    }

    public static ItemStack stackFromString(String name) {
        String[] split = name.split(":");
        if (split.length > 1) {
            int meta = 0;
            if (split.length > 2) {
                if (split[2].equalsIgnoreCase("*"))
                    meta = OreDictionary.WILDCARD_VALUE;
                else
                    meta = Integer.parseInt(split[2]);
            }
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(split[0], split[1]));
            if (item != null) {
                return new ItemStack(item, 1, meta);
            }
        }
        return ItemStack.EMPTY;
    }

    private static Ingredient ingredientfromString(String name) {
        if (name.startsWith("ore:"))
            return new OreIngredient(name.substring(4));
        String[] split = name.split(":");
        if (split.length > 1) {
            int meta = 0;
            if (split.length > 2) {
                if (split[2].equalsIgnoreCase("*"))
                    meta = OreDictionary.WILDCARD_VALUE;
                else
                    meta = Integer.parseInt(split[2]);
            }
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(split[0], split[1]));
            if (item != null) {
                return StackIngredient.fromStacks(new ItemStack(item, 1, meta));
            }
        }
        return Ingredient.EMPTY;
    }

    private static String fromStack(ItemStack stack) {
        if (stack.getMetadata() == OreDictionary.WILDCARD_VALUE) {
            return String.format("%s:*", stack.getItem().getRegistryName());
        } else if (stack.getMetadata() == 0) {
            return stack.getItem().getRegistryName().toString();
        } else {
            return String.format("%s:%s", stack.getItem().getRegistryName(), stack.getMetadata());
        }
    }

    public static List<ItemStack> loadItemStackList(String propName, String category, String desc, String[] default_) {
        return Arrays.stream(loadPropStringList(propName, category, desc, default_)).map(ConfigHelper::stackFromString).collect(Collectors.toList());
    }

    public static List<ItemStack> loadItemStackList(String propName, String category, String desc, ItemStack[] default_) {
        String[] strings_ = new String[default_.length];
        Arrays.stream(default_).map(ConfigHelper::fromStack).collect(Collectors.toList()).toArray(strings_);
        return loadItemStackList(propName, category, desc, strings_);
    }

    public static ItemStack[] loadItemStackArray(String propName, String category, String desc, String[] default_) {
        return Arrays.stream(loadPropStringList(propName, category, desc, default_)).map(ConfigHelper::stackFromString).toArray(ItemStack[]::new);
    }

    public static ItemStack[] loadItemStackArray(String propName, String category, String desc, ItemStack[] default_) {
        String[] strings_ = new String[default_.length];
        Arrays.stream(default_).map(ConfigHelper::fromStack).collect(Collectors.toList()).toArray(strings_);
        return loadItemStackArray(propName, category, desc, strings_);
    }


    public static HashMap<Ingredient, Integer> loadIngredientIntMap(String propName, String category, String desc, String[] _default) {
        HashMap<Ingredient, Integer> map = Maps.newHashMap();
        String[] l = loadPropStringList(propName, category, desc, _default);
        for (String s : l) {
            String[] a = s.split("=");
            if (a.length == 2) {
                map.put(ConfigHelper.ingredientfromString(a[0]), Integer.parseInt(a[1]));
            }
        }
        return map;
    }

    public static HashMap<Integer, Integer> loadIntIntMap(String propName, String category, String desc, String[] _default) {
        HashMap<Integer, Integer> map = Maps.newHashMap();
        String[] l = loadPropStringList(propName, category, desc, _default);
        for (String s : l) {
            String[] a = s.split("=");
            if (a.length == 2) {
                map.put(Integer.parseInt(a[0]), Integer.parseInt(a[1]));
            }
        }
        return map;
    }

    private static void setNeedsRestart(Property prop) {
        if (needsRestart)
            prop.setRequiresMcRestart(needsRestart);
        needsRestart = allNeedRestart;
    }


    public static class ConditionConfig implements IConditionFactory {
        @Override
        public BooleanSupplier parse(JsonContext context, JsonObject json) {
            String enabled = JsonUtils.getString(json, "enabled");
            return () -> CONDITIONS.getOrDefault(enabled, false);
        }
    }
}
