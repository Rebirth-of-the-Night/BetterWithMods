package betterwithmods.common;

import betterwithmods.util.InvUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.ForgeRegistry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public final class BWMRecipes {
    private static final boolean GENERATE_RECIPES = false;
    private static final List<IRecipe> RECIPES = new ArrayList<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Set<String> USED_OD_NAMES = new TreeSet<>();
    private static File RECIPE_DIR = null;
    private static final Map<String, List<IRecipe>> HARDCORE_RECIPES = new HashMap<>();
    public static final List<ItemStack> REMOVE_RECIPE_BY_OUTPUT = Lists.newArrayList();
    public static final List<List<Ingredient>> REMOVE_RECIPE_BY_INPUT = Lists.newArrayList();
    public static final List<ResourceLocation> REMOVE_RECIPE_BY_RL = Lists.newArrayList();
    public static final List<Pattern> REMOVE_BY_REGEX = Lists.newArrayList();

    public static List<IRecipe> getHardcoreRecipes(String ID) {
        if (HARDCORE_RECIPES.containsKey(ID))
            return Collections.unmodifiableList(HARDCORE_RECIPES.get(ID));
        return null;
    }

    public static IRecipe addHardcoreRecipe(String ID, IRecipe recipe) {
        if (!HARDCORE_RECIPES.containsKey(ID)) {
            HARDCORE_RECIPES.put(ID, new ArrayList<>());
        }
        HARDCORE_RECIPES.get(ID).add(recipe);
        return recipe;
    }

    public static List<IRecipe> getRecipes() {
        return Collections.unmodifiableList(RECIPES);
    }

    public static IRecipe addRecipe(IRecipe recipe) {
        RECIPES.add(recipe);
        return recipe;
    }

    public static ShapedOreRecipe addOreRecipe(ItemStack output, Object... inputs) {
        addShapedRecipe(output, inputs);
        return null;
    }

    public static ShapelessOreRecipe addShapelessOreRecipe(ItemStack output, Object... inputs) {
        addShapelessRecipe(output, inputs);
        return null;
    }

    public static void removeRecipe(Ingredient... inputs) {
        REMOVE_RECIPE_BY_INPUT.add(Lists.newArrayList(inputs));
    }

    public static void removeRecipe(IRecipe recipe) {
        ForgeRegistry<IRecipe> reg = (ForgeRegistry<IRecipe>) ForgeRegistries.RECIPES;
        reg.remove(recipe.getRegistryName());
    }

    public static void removeRecipe(ItemStack output) {
        REMOVE_RECIPE_BY_OUTPUT.add(output);
    }

    public static void removeRecipe(ResourceLocation loc) {
        REMOVE_RECIPE_BY_RL.add(loc);
    }

    public static void removeRecipe(String loc) {
        removeRecipe(new ResourceLocation(loc));
    }

    public static void removeRecipe(Pattern pattern) {
        REMOVE_BY_REGEX.add(pattern);
    }

    // Replace calls to GameRegistry.addShapeless/ShapedRecipe with these methods, which will dump it to a json in your dir of choice
// Also works with OD, replace GameRegistry.addRecipe(new ShapedOreRecipe/ShapelessOreRecipe with the same calls

    public static void addFurnaceRecipe(ItemStack input, ItemStack output) {
        FurnaceRecipes.instance().getSmeltingList().put(input, output);
    }

    public static void removeFurnaceRecipe(Item input) {
        removeFurnaceRecipe(new ItemStack(input));
    }

    public static boolean removeFurnaceRecipe(ItemStack input) {
        //for some reason mojang put fucking wildcard for their ore meta
        return FurnaceRecipes.instance().getSmeltingList().entrySet().removeIf(next -> InvUtils.matches(next.getKey(), input));
    }

    public static Set<IBlockState> getStatesFromStack(ItemStack stack) {
        if (stack.getItem() instanceof ItemBlock) {
            if (stack.getMetadata() == OreDictionary.WILDCARD_VALUE) {
                return Sets.newHashSet(((ItemBlock) stack.getItem()).getBlock().getBlockState().getValidStates());
            }
            return Sets.newHashSet(getStateFromStack(stack));
        }
        return Sets.newHashSet();
    }

    @SuppressWarnings("deprecation")
    public static IBlockState getStateFromStack(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemBlock) {
            final ItemBlock itemBlock = ((ItemBlock) stack.getItem());
            return itemBlock.getBlock().getStateFromMeta(itemBlock.getMetadata(stack));
        }
        return null;
    }

    public static ItemStack getStackFromState(IBlockState state) {
        if (state == null)
            return ItemStack.EMPTY;
        Block block = state.getBlock();
        int meta = block.damageDropped(state);
        return new ItemStack(block, 1, meta);
    }

    private static void setupDir() {
        if (RECIPE_DIR == null) {
            RECIPE_DIR = new File("/home/primetoxinz/Programming/BetterWithMods-1.12/src/main/resources/assets/betterwithmods/recipes/output");
        }

        if (!RECIPE_DIR.exists()) {
            RECIPE_DIR.mkdir();
        }
    }

    private static void addShapedRecipe(ItemStack result, Object... components) {
        if (!GENERATE_RECIPES)
            return;
        setupDir();

        // GameRegistry.addShaped
        // Recipe(result, components);

        Map<String, Object> json = new HashMap<>();

        List<String> pattern = new ArrayList<>();
        int i = 0;
        while (i < components.length && components[i] instanceof String) {
            pattern.add((String) components[i]);
            i++;
        }
        json.put("pattern", pattern);

        boolean isOreDict = false;
        Map<String, Map<String, Object>> key = new HashMap<>();
        Character curKey = null;
        for (; i < components.length; i++) {
            Object o = components[i];
            if (o instanceof Character) {
                if (curKey != null)
                    throw new IllegalArgumentException("Provided two char keys in a row");
                curKey = (Character) o;
            } else {
                if (curKey == null)
                    throw new IllegalArgumentException("Providing object without a char key");
                if (o instanceof String)
                    isOreDict = true;
                key.put(Character.toString(curKey), serializeItem(o));
                curKey = null;
            }
        }
        json.put("key", key);
        json.put("type", isOreDict ? "forge:ore_shaped" : "minecraft:crafting_shaped");
        json.put("result", serializeItem(result));

        // names the json the same name as the output's registry name
        // repeatedly adds _alt if a file already exists
        // janky I know but it works
        String suffix = result.getItem().getHasSubtypes() ? "_" + result.getItemDamage() : "";

        String name = result.getItem().getRegistryName().getPath() + suffix;
        if (name.contains("material") || name.contains("aesthetic"))
            name = result.getTranslationKey().replace("item.bwm:", "").replace("tile.bwm:", "");

        File f = new File(RECIPE_DIR, name + ".json");

        while (f.exists()) {
            name += "_alt";
            f = new File(RECIPE_DIR, name + ".json");
        }
        try (FileWriter w = new FileWriter(f)) {
            GSON.toJson(json, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addShapelessRecipe(ItemStack result, Object... components) {
        if (!GENERATE_RECIPES)
            return;
        setupDir();

        // addShapelessRecipe(result, components);

        Map<String, Object> json = new HashMap<>();

        boolean isOreDict = false;
        List<Map<String, Object>> ingredients = new ArrayList<>();
        for (Object o : components) {
            if (o instanceof String)
                isOreDict = true;
            ingredients.add(serializeItem(o));
        }
        json.put("ingredients", ingredients);
        json.put("type", isOreDict ? "forge:ore_shapeless" : "minecraft:crafting_shapeless");
        json.put("result", serializeItem(result));

        // names the json the same name as the output's registry name
        // repeatedly adds _alt if a file already exists
        // janky I know but it works
        String suffix = result.getItem().getHasSubtypes() ? "_" + result.getItemDamage() : "";

        String name = result.getItem().getRegistryName().getPath() + suffix;
        if (name.contains("material"))
            name = result.getTranslationKey().replace("item.bwm:", "");


        File f = new File(RECIPE_DIR, name + ".json");

        while (f.exists()) {
            name += "_alt";
            f = new File(RECIPE_DIR, name + ".json");
        }


        try (FileWriter w = new FileWriter(f)) {
            GSON.toJson(json, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Object> serializeItem(Object thing) {
        if (thing instanceof Item) {
            return serializeItem(new ItemStack((Item) thing));
        }
        if (thing instanceof Block) {
            return serializeItem(new ItemStack((Block) thing));
        }
        if (thing instanceof ItemStack) {
            ItemStack stack = (ItemStack) thing;
            Map<String, Object> ret = new HashMap<>();
            ret.put("item", stack.getItem().getRegistryName().toString());
            if (stack.getItem().getHasSubtypes() || stack.getItemDamage() != 0) {
                ret.put("data", stack.getItemDamage());
            }
            if (stack.getCount() > 1) {
                ret.put("count", stack.getCount());
            }

            if (stack.hasTagCompound()) {
                throw new IllegalArgumentException("nbt not implemented");
            }

            return ret;
        }
        if (thing instanceof String) {
            Map<String, Object> ret = new HashMap<>();
            USED_OD_NAMES.add((String) thing);
            ret.put("ore", "" + thing);
            ret.put("type", "forge:ore_dict");
            return ret;
        }

        throw new IllegalArgumentException("Not a block, item, stack, or od name");
    }
}
