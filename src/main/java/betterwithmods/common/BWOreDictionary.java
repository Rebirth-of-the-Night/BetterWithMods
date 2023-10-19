package betterwithmods.common;

import betterwithmods.api.util.IWood;
import betterwithmods.api.util.IWoodProvider;
import betterwithmods.common.blocks.BlockAesthetic;
import betterwithmods.common.blocks.BlockRawPastry;
import betterwithmods.common.blocks.BlockUrn;
import betterwithmods.common.items.ItemBark;
import betterwithmods.common.items.ItemMaterial;
import betterwithmods.common.registry.Wood;
import betterwithmods.util.InvUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.minecraft.init.Blocks.*;

/**
 * Created by primetoxinz on 5/10/17.
 */
public class BWOreDictionary {

    public static List<ItemStack> cropNames;
    public static List<Ore> nuggetNames;
    public static List<Ore> dustNames;
    public static List<Ore> oreNames;
    public static List<Ore> ingotNames;

    public static List<IWood> woods = new ArrayList<>();
    public static List<IWoodProvider> woodProviders = new ArrayList<>();


    public static List<ItemStack> planks;
    public static List<ItemStack> logs;
    public static List<IRecipe> logRecipes = new ArrayList<>();

    public static HashMultimap<String, String> toolEffectiveOre = HashMultimap.create();


    public static void registerOres() {

        toolEffectiveOre.putAll("axe", Lists.newArrayList("logWood", "plankWood"));
        toolEffectiveOre.putAll("mattock", Lists.newArrayList("stone", "cobblestone"));

        //TODO for 1.13
        registerOre("wool", new ItemStack(Blocks.WOOL, OreDictionary.WILDCARD_VALUE));
        registerOre("scroll", BWMItems.ARCANE_SCROLL);
        registerOre("book", BWMItems.MANUAL, Items.BOOK, Items.WRITTEN_BOOK);
        registerOre("dung", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.DUNG));
        registerOre("padding", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.PADDING));
        registerOre("soap", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SOAP));
        registerOre("archimedesScrew", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SCREW));
        registerOre("filament", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.FILAMENT));
        registerOre("latchRedstone", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.REDSTONE_LATCH));
        registerOre("plateSoulforgedSteel", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.PLATE_STEEL));
        registerOre("arrowSoulforgedSteel", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.BROADHEAD));
        registerOre("springSoulforgedSteel", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.STEEL_SPRING));
        registerOre("gearSoulforgedSteel", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.STEEL_GEAR));
        registerOre("gearWood", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.GEAR));
        registerOre("cropHemp", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.HEMP));
        registerOre("dyeBrown", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.DUNG));
        registerOre("dung", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.DUNG));
        registerOre("slimeball", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.GLUE));
        registerOre("glue", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.GLUE));
        registerOre("ingotSoulforgedSteel", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.INGOT_STEEL));
        registerOre("dustNetherrack", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.GROUND_NETHERRACK));
        registerOre("dustHellfire", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.HELLFIRE_DUST));
        registerOre("dustSoul", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SOUL_DUST));
        registerOre("dustWood", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SOUL_DUST));
        registerOre("ingotConcentratedHellfire", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.CONCENTRATED_HELLFIRE));
        registerOre("dustCoal", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.COAL_DUST));
        registerOre("dustPotash", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.POTASH));
        registerOre("dustWood", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SAWDUST));
        registerOre("dustSulfur", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.BRIMSTONE));
        registerOre("dustSaltpeter", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.NITER));
        registerOre("nuggetSoulforgedSteel", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.NUGGET_STEEL));
        registerOre("foodFlour", BlockRawPastry.getStack(BlockRawPastry.EnumType.BREAD));
        registerOre("dustCharcoal", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.CHARCOAL_DUST));
        registerOre("foodCocoapowder", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.COCOA_POWDER));
        registerOre("dustCarbon", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.COAL_DUST), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.CHARCOAL_DUST));
        registerOre("dustCoal", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.COAL_DUST));
        registerOre("dustCharcoal", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.CHARCOAL_DUST));
        registerOre("gemNetherCoal", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.NETHERCOAL));
        registerOre("materialNetherSludge", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.NETHER_SLUDGE));
        registerOre("foodChocolatebar", new ItemStack(BWMItems.CHOCOLATE));
        registerOre("chainmail", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.CHAIN_MAIL));

        registerOre("blockHardenedNetherClay", BlockAesthetic.getStack(BlockAesthetic.EnumType.NETHERCLAY));
        registerOre("blockConcentratedHellfire", BlockAesthetic.getStack(BlockAesthetic.EnumType.HELLFIRE));
        registerOre("blockPadding", BlockAesthetic.getStack(BlockAesthetic.EnumType.PADDING));
        registerOre("blockEnder", BlockAesthetic.getStack(BlockAesthetic.EnumType.ENDERBLOCK));
        registerOre("blockSoulforgedSteel", new ItemStack(BWMBlocks.STEEL_BLOCK, 1, 0));
        //Added bark subtype entries for Roots compatibility
        registerOre("barkWood", new ItemStack(BWMItems.BARK, 1, OreDictionary.WILDCARD_VALUE));
        registerOre("barkOak", new ItemStack(BWMItems.BARK, 1, 0));
        registerOre("barkSpruce", new ItemStack(BWMItems.BARK, 1, 1));
        registerOre("barkBirch", new ItemStack(BWMItems.BARK, 1, 2));
        registerOre("barkJungle", new ItemStack(BWMItems.BARK, 1, 3));
        registerOre("barkAcacia", new ItemStack(BWMItems.BARK, 1, 4));
        registerOre("barkDarkOak", new ItemStack(BWMItems.BARK, 1, 5));
        registerOre("barkBlood", new ItemStack(BWMItems.BARK, 1, 6));

        registerOre("hideTanned", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.TANNED_LEATHER));
        registerOre("hideTanned", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.TANNED_LEATHER_CUT));
        registerOre("hideBelt", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.LEATHER_BELT));
        registerOre("hideScoured", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SCOURED_LEATHER));
        registerOre("hideStrap", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.LEATHER_STRAP));
        registerOre("leather", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.LEATHER_CUT));


        registerOre("fiberHemp", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.HEMP_FIBERS));
        registerOre("fabricHemp", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.HEMP_CLOTH));

        registerOre("ingotDiamond", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.DIAMOND_INGOT));
        registerOre("nuggetDiamond", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.DIAMOND_NUGGET));

        registerOre("listAllmeat", Items.PORKCHOP, Items.BEEF, Items.CHICKEN, Items.FISH, Items.MUTTON, BWMItems.MYSTERY_MEAT);
        registerOre("listAllmeat", new ItemStack(Items.FISH, 1, ItemFishFood.FishType.SALMON.getMetadata()));
        registerOre("listAllmeatcooked", Items.COOKED_PORKCHOP, Items.COOKED_BEEF, Items.COOKED_CHICKEN, Items.COOKED_FISH, Items.COOKED_MUTTON, Items.COOKED_RABBIT, BWMItems.COOKED_MYSTERY_MEAT);
        registerOre("listAllmeatcooked", new ItemStack(Items.COOKED_FISH, 1, ItemFishFood.FishType.SALMON.getMetadata()));
        registerOre("foodStewMeat", Items.COOKED_PORKCHOP, Items.COOKED_BEEF, Items.COOKED_FISH, Items.COOKED_MUTTON, BWMItems.COOKED_MYSTERY_MEAT);
        registerOre("foodStewMeat", new ItemStack(Items.COOKED_FISH, 1, ItemFishFood.FishType.SALMON.getMetadata()));

        registerOre("tallow", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.TALLOW));

        registerOre("blockHellfire", new ItemStack(BWMBlocks.AESTHETIC, 1, BlockAesthetic.EnumType.HELLFIRE.getMeta()));

        registerOre("blockPlanter", new ItemStack(BWMBlocks.PLANTER, 1, OreDictionary.WILDCARD_VALUE));

        registerOre("pile", new ItemStack(BWMItems.DIRT_PILE), new ItemStack(BWMItems.SAND_PILE), new ItemStack(BWMItems.RED_SAND_PILE), new ItemStack(BWMItems.GRAVEL_PILE));
        registerOre("pileDirt", new ItemStack(BWMItems.DIRT_PILE));
        registerOre("pileSand", new ItemStack(BWMItems.SAND_PILE), new ItemStack(BWMItems.RED_SAND_PILE));
        registerOre("pileRedSand", new ItemStack(BWMItems.RED_SAND_PILE));
        registerOre("pileGravel", new ItemStack(BWMItems.GRAVEL_PILE));

        registerOre("blockVase", new ItemStack(BWMBlocks.VASE, 1, OreDictionary.WILDCARD_VALUE));
        registerOre("blockUrn", new ItemStack(BWMBlocks.URN, 1, OreDictionary.WILDCARD_VALUE));
        registerOre("treeSapling", new ItemStack(BWMBlocks.BLOOD_SAPLING));
        registerOre("treeLeaves", new ItemStack(BWMBlocks.BLOOD_LEAVES));
        registerOre("logWood", new ItemStack(BWMBlocks.BLOOD_LOG));
        registerOre("blockNetherSludge", new ItemStack(BWMBlocks.NETHER_CLAY));
        registerOre("cobblestone", new ItemStack(BWMBlocks.COBBLE, 1, OreDictionary.WILDCARD_VALUE));

        registerOre("slats", new ItemStack(BWMBlocks.SLATS, 1, OreDictionary.WILDCARD_VALUE));
        registerOre("grates", new ItemStack(BWMBlocks.GRATE, 1, OreDictionary.WILDCARD_VALUE));
        registerOre("wicker", new ItemStack(BWMBlocks.WICKER));

        registerOre("blockCandle", new ItemStack(BWMBlocks.CANDLE, 1, OreDictionary.WILDCARD_VALUE));
        registerOre("stickWood", new ItemStack(BWMBlocks.SHAFT));

        registerOre("blockWindChime", new ItemStack(BWMBlocks.BAMBOO_CHIME, 1, OreDictionary.WILDCARD_VALUE));
        registerOre("blockWindChime", new ItemStack(BWMBlocks.METAL_CHIME, 1, OreDictionary.WILDCARD_VALUE));
        registerOre("blockWoodTable", new ItemStack(BWMBlocks.WOOD_TABLE, 1, OreDictionary.WILDCARD_VALUE));
        registerOre("blockWoodBench", new ItemStack(BWMBlocks.WOOD_BENCH, 1, OreDictionary.WILDCARD_VALUE));

        registerOre("blockSoulUrn", BlockUrn.getStack(BlockUrn.EnumType.FULL, 1));
        registerOre("dustBlaze", new ItemStack(Items.BLAZE_POWDER));

        registerOre("foodDonut", BWMItems.DONUT);

        registerOre("meatPork", Items.PORKCHOP, Items.COOKED_PORKCHOP);
        registerOre("meatBeef", Items.BEEF, Items.COOKED_BEEF);
        registerOre("meatMutton", Items.MUTTON, Items.COOKED_MUTTON);
        registerOre("meatChicken", Items.CHICKEN, Items.COOKED_CHICKEN);
        registerOre("meatRotten", Items.ROTTEN_FLESH);
        registerOre("meatFish",
                new ItemStack(Items.FISH, 1, ItemFishFood.FishType.COD.getMetadata()),
                new ItemStack(Items.FISH, 1, ItemFishFood.FishType.SALMON.getMetadata()),
                new ItemStack(Items.COOKED_FISH, 1, ItemFishFood.FishType.COD.getMetadata()),
                new ItemStack(Items.COOKED_FISH, 1, ItemFishFood.FishType.SALMON.getMetadata())
        );

        registerOre("cookedPotato", Items.BAKED_POTATO);
        registerOre("cookedCarrot", Items.CARROT);

        registerOre("listAllExplosives", new ItemStack(Blocks.TNT));
        registerOre("listAllExplosives", new ItemStack(Items.GUNPOWDER));
        registerOre("listAllExplosives", new ItemStack(BWMItems.DYNAMITE));
        registerOre("listAllExplosives", new ItemStack(BWMBlocks.MINING_CHARGE));
        registerOre("listAllExplosives", BlockAesthetic.getStack(BlockAesthetic.EnumType.HELLFIRE));
        registerOre("listAllExplosives", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.BLASTING_OIL));
        registerOre("listAllExplosives", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.HELLFIRE_DUST));
        registerOre("listAllExplosives", ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.CONCENTRATED_HELLFIRE));


        registerOre("seed", new ItemStack(BWMBlocks.HEMP), new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Items.MELON_SEEDS), new ItemStack(Items.PUMPKIN_SEEDS), new ItemStack(Items.BEETROOT_SEEDS));

        registerOre("concrete", new ItemStack(Blocks.CONCRETE, 1, OreDictionary.WILDCARD_VALUE));
        registerOre("wool", new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE));
        registerOre("terracotta", STAINED_HARDENED_CLAY, WHITE_GLAZED_TERRACOTTA, ORANGE_GLAZED_TERRACOTTA, MAGENTA_GLAZED_TERRACOTTA, LIGHT_BLUE_GLAZED_TERRACOTTA, YELLOW_GLAZED_TERRACOTTA, LIME_GLAZED_TERRACOTTA, PINK_GLAZED_TERRACOTTA, GRAY_GLAZED_TERRACOTTA, SILVER_GLAZED_TERRACOTTA, CYAN_GLAZED_TERRACOTTA, PURPLE_GLAZED_TERRACOTTA, BLUE_GLAZED_TERRACOTTA, BROWN_GLAZED_TERRACOTTA, GREEN_GLAZED_TERRACOTTA, RED_GLAZED_TERRACOTTA, BLACK_GLAZED_TERRACOTTA);


    }

    private static ItemStack getPlankOutput(ItemStack log) {
        Iterator<IRecipe> it = CraftingManager.REGISTRY.iterator();
        ItemStack stack = ItemStack.EMPTY;
        while (it.hasNext() && stack.isEmpty()) {
            IRecipe recipe = it.next();
            if (isPlank(recipe.getRecipeOutput())) {
                NonNullList<Ingredient> ing = recipe.getIngredients();
                for (Ingredient in : ing) {
                    for (ItemStack check : in.getMatchingStacks()) {
                        if (check.isItemEqual(log)) {
                            stack = recipe.getRecipeOutput().copy();
                            logRecipes.add(recipe);
                            break;
                        }
                    }
                    if (!stack.isEmpty())
                        break;
                }
            }

        }
        return stack;
    }

    private static boolean isPlank(ItemStack output) {
        return BWOreDictionary.listContains(output, OreDictionary.getOres("plankWood"));
    }

    public static void registerOre(String ore, ItemStack... items) {
        for (ItemStack i : items)
            OreDictionary.registerOre(ore, i);
    }

    public static void registerOre(String ore, Block... blocks) {
        for (Block block : blocks)
            registerOre(ore, new ItemStack(block));
    }

    public static void registerOre(String ore, Item... items) {
        for (Item item : items)
            registerOre(ore, new ItemStack(item));
    }

    public static void postInitOreDictGathering() {
        nuggetNames = getOreIngredients("nugget");
        dustNames = getOreIngredients("dust");
        oreNames = getOreIngredients("ore");
        ingotNames = getOreIngredients("ingot");
        cropNames = getOreNames("crop");
        woods.addAll(Lists.newArrayList(
                new Wood(new ItemStack(Blocks.LOG, 1, 0), new ItemStack(Blocks.PLANKS, 1, 0), ItemBark.getStack("oak", 1)),
                new Wood(new ItemStack(Blocks.LOG, 1, 1), new ItemStack(Blocks.PLANKS, 1, 1), ItemBark.getStack("spruce", 1)),
                new Wood(new ItemStack(Blocks.LOG, 1, 2), new ItemStack(Blocks.PLANKS, 1, 2), ItemBark.getStack("birch", 1)),
                new Wood(new ItemStack(Blocks.LOG, 1, 3), new ItemStack(Blocks.PLANKS, 1, 3), ItemBark.getStack("jungle", 1)),
                new Wood(new ItemStack(Blocks.LOG2, 1, 0), new ItemStack(Blocks.PLANKS, 1, 4), ItemBark.getStack("acacia", 1)),
                new Wood(new ItemStack(Blocks.LOG2, 1, 1), new ItemStack(Blocks.PLANKS, 1, 5), ItemBark.getStack("dark_oak", 1)),
                new Wood(new ItemStack(BWMBlocks.BLOOD_LOG), new ItemStack(Blocks.PLANKS, 1, 3), ItemBark.getStack("bloody", 1), true)
        ));
        woods.forEach(w -> getPlankOutput(w.getLog(1)));
        logs = OreDictionary.getOres("logWood").stream().filter(stack -> !stack.getItem().getRegistryName().getNamespace().equalsIgnoreCase("minecraft") && !stack.getItem().getRegistryName().getNamespace().equalsIgnoreCase("betterwithmods")).collect(Collectors.toList());
        for (ItemStack log : logs) {
            if (log.getMetadata() == OreDictionary.WILDCARD_VALUE) {//Probably the most common instance of OreDict use for logs.
                for (int i = 0; i <= 4; i++) {//Terraqueous's logs go up to 4 for some reason. Should we look for up to 15?
                    ItemStack subLog = new ItemStack(log.getItem(), 1, i);
                    ItemStack plank = getPlankOutput(subLog);
                    if (!plank.isEmpty() && !isWoodRegistered(subLog)) {
                        Wood wood = new Wood(subLog, plank);
                        woods.add(wood);
                    }
                }
            } else {
                ItemStack plank = getPlankOutput(log);
                if (!plank.isEmpty() && !isWoodRegistered(log)) {
                    Wood wood = new Wood(log, plank);
                    woods.add(wood);
                }
            }
        }

    }

    public static boolean isWoodRegistered(ItemStack stack) {
        return woods.stream().anyMatch(wood -> wood.getLog(1).isItemEqual(stack));
    }

    public static List<ItemStack> getOreNames(String prefix) {
        return Arrays.stream(OreDictionary.getOreNames()).filter(Objects::nonNull).filter(n -> n.startsWith(prefix)).map(OreDictionary::getOres).filter(o -> !o.isEmpty()).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static List<ItemStack> getItems(List<Ore> ores) {
        return ores.stream().flatMap(o -> o.getOres().stream()).collect(Collectors.toList());
    }

    public static List<Ore> getOreIngredients(String prefix) {
        return Arrays.stream(OreDictionary.getOreNames()).filter(Objects::nonNull).filter(n -> n.startsWith(prefix)).map(n -> new Ore(prefix, n)).collect(Collectors.toList());
    }

    public static int listContains(Object obj, List<Object> list) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (obj instanceof ItemStack && list.get(i) instanceof ItemStack) {
                    ItemStack stack = (ItemStack) obj;
                    ItemStack toCheck = (ItemStack) list.get(i);
                    if (ItemStack.areItemsEqual(stack, toCheck)) {
                        if (toCheck.hasTagCompound()) {
                            if (ItemStack.areItemStackTagsEqual(stack, toCheck))
                                return i;
                        } else if (stack.hasTagCompound()) {
                            return -1;
                        } else
                            return i;
                    }
                }
            }
        }
        return -1;
    }

    public static boolean isOre(ItemStack stack, String ore) {
        return listContains(stack, OreDictionary.getOres(ore));
    }

    public static boolean listContains(ItemStack check, List<ItemStack> list) {
        if (list != null) {
            if (list.isEmpty()) return false;
            for (ItemStack item : list) {
                if (ItemStack.areItemsEqual(check, item) || (check.getItem() == item.getItem() && item.getItemDamage() == OreDictionary.WILDCARD_VALUE)) {
                    return !item.hasTagCompound() || ItemStack.areItemStackTagsEqual(check, item);
                }
            }
        }
        return false;
    }

    public static List<String> getOres(ItemStack stack) {
        return IntStream.of(OreDictionary.getOreIDs(stack)).mapToObj(OreDictionary::getOreName).collect(Collectors.toList());
    }

    public static boolean hasPrefix(ItemStack stack, String suffix) {
        return listContains(stack, getOreNames(suffix));
    }

    public static boolean isToolForOre(String tool, ItemStack stack) {
        return toolEffectiveOre.get(tool).stream().anyMatch(getOres(stack)::contains);
    }

    public static IWood getWoodFromState(IBlockState state) {

        ItemStack stack = BWMRecipes.getStackFromState(state);
        IWood wood = null;
        if (!stack.isEmpty()) {
            wood = woods.stream().filter(w -> InvUtils.matches(w.getLog(1), stack)).findFirst().orElse(null);
        }
        if (wood == null) {
            for (IWoodProvider provider : woodProviders) {
                if (provider.match(state)) {
                    wood = provider.getWood(state);
                    break;
                }
            }
        }
        return wood;
    }

    public static List<Ingredient> fromOres(String... ores) {
        return Arrays.stream(ores).map(OreIngredient::new).collect(Collectors.toList());
    }

    public static class Ore extends OreIngredient {
        private String prefix;
        private String ore;

        public Ore(String prefix, String ore) {
            super(ore);
            this.prefix = prefix;
            this.ore = ore;
        }

        public List<ItemStack> getOres() {
            return OreDictionary.getOres(ore);
        }

        public String getOre() {
            return ore;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getSuffix() {
            return ore.substring(getPrefix().length());
        }

    }
}
