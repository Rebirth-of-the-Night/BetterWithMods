package betterwithmods.module.gameplay.miniblocks;

import betterwithmods.BWMod;
import betterwithmods.client.model.render.RenderUtils;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWMRecipes;
import betterwithmods.common.BWOreDictionary;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.items.ItemMaterial;
import betterwithmods.module.Feature;
import betterwithmods.module.gameplay.AnvilRecipes;
import betterwithmods.module.gameplay.miniblocks.blocks.BlockCorner;
import betterwithmods.module.gameplay.miniblocks.blocks.BlockMini;
import betterwithmods.module.gameplay.miniblocks.blocks.BlockMoulding;
import betterwithmods.module.gameplay.miniblocks.blocks.BlockSiding;
import betterwithmods.module.gameplay.miniblocks.client.MiniModel;
import betterwithmods.module.gameplay.miniblocks.tiles.TileCorner;
import betterwithmods.module.gameplay.miniblocks.tiles.TileMoulding;
import betterwithmods.module.gameplay.miniblocks.tiles.TileSiding;
import betterwithmods.util.InvUtils;
import betterwithmods.util.ReflectionHelperBlock;
import com.google.common.collect.*;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

public class MiniBlocks extends Feature {
    public static HashMap<MiniType, HashMap<Material, BlockMini>> MINI_MATERIAL_BLOCKS = Maps.newHashMap();
    public static HashMap<Material, BlockMini> SIDINGS = Maps.newHashMap();
    public static HashMap<Material, BlockMini> MOULDINGS = Maps.newHashMap();
    public static HashMap<Material, BlockMini> CORNERS = Maps.newHashMap();
    public static Multimap<Material, IBlockState> MATERIALS = HashMultimap.create();

    private static Map<Material, String> names = Maps.newHashMap();
    private static HashSet<String> whitelist = new HashSet<>();


    private static boolean addConversionRecipes;
    private static boolean autoGeneration;
    private static boolean requiresAnvil;

    static {
        MINI_MATERIAL_BLOCKS.put(MiniType.SIDING, SIDINGS);
        MINI_MATERIAL_BLOCKS.put(MiniType.MOULDING, MOULDINGS);
        MINI_MATERIAL_BLOCKS.put(MiniType.CORNER, CORNERS);
    }

    public MiniBlocks() {
        enabledByDefault = false;
    }

    public static boolean isValidMini(IBlockState state) {
        Material material = state.getMaterial();
        return names.containsKey(material) && MATERIALS.get(material).contains(state);
    }

    public static boolean isValidMini(IBlockState state, ItemStack stack) {
        ResourceLocation resloc = stack.getItem().getRegistryName();
        if (!autoGeneration && resloc != null && !whitelist.contains(resloc.toString()) && !whitelist.contains(resloc.toString() + ":" + stack.getMetadata()))
            return BWOreDictionary.hasPrefix(stack, "plankWood"); //Specifically planks are a-okay

        Block blk = state.getBlock();
        final ReflectionHelperBlock pb = new ReflectionHelperBlock();
        final Class<? extends Block> blkClass = blk.getClass();

        pb.onBlockActivated(null, null, null, null, null, null, 0, 0, 0);
        boolean noActivation = (getDeclaringClass(blkClass, pb.MethodName, World.class, BlockPos.class, IBlockState.class, EntityPlayer.class, EnumHand.class, EnumFacing.class, float.class, float.class, float.class) == Block.class);

        pb.updateTick(null, null, null, null);
        boolean noUpdate = getDeclaringClass(blkClass, pb.MethodName, World.class, BlockPos.class, IBlockState.class, Random.class) == Block.class;

        // ignore blocks with custom collision.
        pb.onEntityCollision(null, null, null, null);
        boolean noCustomCollision = getDeclaringClass(blkClass, pb.MethodName, World.class, BlockPos.class, IBlockState.class, Entity.class) == Block.class;
        final boolean isFullBlock = state.isFullBlock() || blkClass == BlockStainedGlass.class || blkClass == BlockGlass.class || blk == Blocks.SLIME_BLOCK || blk == Blocks.ICE;
        final boolean hasItem = Item.getItemFromBlock(blk) != Items.AIR;
        final boolean tickingBehavior = blk.getTickRandomly();
        final boolean isOre = BWOreDictionary.hasPrefix(stack, "ore") || BWOreDictionary.isOre(stack, "logWood");

        boolean hasBehavior = (blk.hasTileEntity(state) || tickingBehavior) && blkClass != BlockGrass.class && blkClass != BlockIce.class;

        return noUpdate && noActivation && noCustomCollision && isFullBlock && !hasBehavior && hasItem && !isOre;
    }

    private static Class<?> getDeclaringClass(
            final Class<?> blkClass,
            final String methodName,
            final Class<?>... args) {
        try {
            blkClass.getDeclaredMethod(methodName, args);
            return blkClass;
        } catch (final NoSuchMethodException | SecurityException e) {
            // nothing here...
        } catch (final NoClassDefFoundError e) {
            BWMod.logger.info("Unable to determine blocks eligibility for making a miniblock, " + blkClass.getName() + " attempted to load " + e.getMessage());
            return blkClass;
        } catch (final Throwable t) {
            return blkClass;
        }

        return getDeclaringClass(
                blkClass.getSuperclass(),
                methodName,
                args);
    }

    public static ItemStack fromParent(Block mini, IBlockState state) {
        return fromParent(mini, state, 1);
    }

    public static ItemStack fromParent(Block mini, IBlockState state, int count) {
        ItemStack stack = new ItemStack(mini, count);
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound texture = new NBTTagCompound();
        NBTUtil.writeBlockState(texture, state);
        tag.setTag("texture", texture);
        stack.setTagCompound(tag);
        return stack.copy();
    }

    public static void addWhitelistedBlock(ResourceLocation resloc) {
        whitelist.add(resloc.toString());
    }

    public static void addWhitelistedBlock(ResourceLocation resloc, int meta) { //Delete this in 1.13
        whitelist.add(resloc.toString() + ":" + meta);
    }

    public static void addMaterial(Material material, String name) {
        if (!names.containsKey(material)) //so addons don't overwrite our names, causing world breakage
            names.put(material, name);
    }

    private static void registerMiniSaw(IBlockState parent) {
        ItemStack mini = BWMRecipes.getStackFromState(parent);
        Material material = parent.getMaterial();
        MiniBlockIngredient siding = new MiniBlockIngredient("siding", mini);
        MiniBlockIngredient moulding = new MiniBlockIngredient("moulding", mini);
        MiniBlockIngredient corner = new MiniBlockIngredient("corner", mini);
        ItemStack sidingStack = MiniBlocks.fromParent(SIDINGS.get(material), parent, 2);
        ItemStack mouldingStack = MiniBlocks.fromParent(MOULDINGS.get(material), parent, 2);
        ItemStack cornerStack = MiniBlocks.fromParent(CORNERS.get(material), parent, 2);
        BWRegistry.WOOD_SAW.addRecipe(mini, sidingStack);
        BWRegistry.WOOD_SAW.addRecipe(siding, mouldingStack);
        BWRegistry.WOOD_SAW.addRecipe(moulding, cornerStack);
        if (BWOreDictionary.isOre(mini, "plankWood"))
            BWRegistry.WOOD_SAW.addRecipe(corner, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.GEAR, 2));
    }

    private static void registerMiniAnvil(IBlockState parent) {
        Material material = parent.getMaterial();
        ItemStack mini = BWMRecipes.getStackFromState(parent);
        MiniBlockIngredient siding = new MiniBlockIngredient("siding", mini);
        MiniBlockIngredient moulding = new MiniBlockIngredient("moulding", mini);
        ItemStack sidingStack = MiniBlocks.fromParent(SIDINGS.get(material), parent, 8);
        ItemStack mouldingStack = MiniBlocks.fromParent(MOULDINGS.get(material), parent, 8);
        ItemStack cornerStack = MiniBlocks.fromParent(CORNERS.get(material), parent, 8);

        AnvilRecipes.addSteelShapedRecipe(sidingStack.getItem().getRegistryName(), sidingStack, "XXXX", 'X', mini);
        AnvilRecipes.addSteelShapedRecipe(mouldingStack.getItem().getRegistryName(), mouldingStack, "XXXX", 'X', siding);
        AnvilRecipes.addSteelShapedRecipe(cornerStack.getItem().getRegistryName(), cornerStack, "XXXX", 'X', moulding);
    }

    @Override
    public void setupConfig() {
        autoGeneration = loadPropBool("Auto Generate Miniblocks", "Automatically add miniblocks for many blocks, based on heuristics and probably planetary alignments. WARNING: Exposure to this config option can kill pack developers.", false);
        addConversionRecipes = loadPropBool("Add Conversion Recipes", "Add recipes to convert the old, static, mini blocks to the new ones.", true);
        whitelist = loadPropStringHashSet("Whitelist", "Whitelist for blocks to generate miniblocks for (aside from the ones required by BWM)", new String[]{});
        whitelist.add("minecraft:stone:0");
        whitelist.add("minecraft:stonebrick");
        whitelist.add("minecraft:sandstone");
        whitelist.add("minecraft:brick_block");
        whitelist.add("minecraft:nether_brick");
        whitelist.add("minecraft:quartz_block");
        whitelist.add("betterwithmods:aesthetic:6");
        requiresAnvil = loadPropBool("Stone Miniblocks require Anvil recipe", "When enabled stone and metal miniblocks will require an anvil recipe, when disabled they will all be made with the saw", true);
    }

    public void addOldRecipeConversation(ItemStack old, Block mini, IBlockState base) {
        ItemStack output = fromParent(mini, base);
        String oldName = old.getItem().getTranslationKey(old).replace("tile.bwm:", "");
        addHardcoreRecipe(new ShapelessRecipes("mini_conversion", output, InvUtils.asNonnullList(Ingredient.fromStacks(old))).setRegistryName(new ResourceLocation(BWMod.MODID, oldName)));
    }

    @Override
    public String getFeatureDescription() {
        return "Dynamically generate Siding, Mouldings and Corners for many of the blocks in the game.";
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        names.put(Material.WOOD, "wood");
        names.put(Material.ROCK, "rock");
        names.put(Material.IRON, "iron");

        GameRegistry.registerTileEntity(TileSiding.class, new ResourceLocation("bwm.siding"));
        GameRegistry.registerTileEntity(TileMoulding.class, new ResourceLocation("bwm.moulding"));
        GameRegistry.registerTileEntity(TileCorner.class, new ResourceLocation("bwm.corner"));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void beforeBlockRegister(RegistryEvent.Register<Block> event) {
        for (Material material : names.keySet()) {
            String name = names.get(material);
            SIDINGS.put(material, (BlockMini) new BlockSiding(material).setRegistryName(String.format("%s_%s", "siding", name)));
            MOULDINGS.put(material, (BlockMini) new BlockMoulding(material).setRegistryName(String.format("%s_%s", "moulding", name)));
            CORNERS.put(material, (BlockMini) new BlockCorner(material).setRegistryName(String.format("%s_%s", "corner", name)));
        }

        SIDINGS.values().forEach(b -> BWMBlocks.registerBlock(b, new ItemMini(b)));
        MOULDINGS.values().forEach(b -> BWMBlocks.registerBlock(b, new ItemMini(b)));
        CORNERS.values().forEach(b -> BWMBlocks.registerBlock(b, new ItemMini(b)));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void beforeRecipes(RegistryEvent.Register<IRecipe> event) {
        final NonNullList<ItemStack> list = NonNullList.create();
        for (Item item : ForgeRegistries.ITEMS) {
            if (!(item instanceof ItemBlock))
                continue;
            try {
                item.getSubItems(item.getCreativeTab(), list);
                for (final ItemStack stack : list) {
                    if (!(stack.getItem() instanceof ItemBlock))
                        continue;
                    final IBlockState state = BWMRecipes.getStateFromStack(stack);
                    if (state != null && isValidMini(state, stack)) {
                        Material material = state.getMaterial();
                        if (names.containsKey(material)) {
                            MATERIALS.put(material, state);
                        }
                    }
                }
                list.clear();
            } catch (Throwable ignored) {
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void postInit(FMLPostInitializationEvent event) {

        for (Material material : names.keySet()) {
            BlockMini siding = SIDINGS.get(material);
            BlockMini moulding = MOULDINGS.get(material);
            BlockMini corner = CORNERS.get(material);

            addHardcoreRecipe(new MiniRecipe(siding, null));
            addHardcoreRecipe(new MiniRecipe(moulding, siding));
            addHardcoreRecipe(new MiniRecipe(corner, moulding));
        }

        List<IBlockState> states = Lists.newArrayList();

        if (requiresAnvil) {
            states.addAll(MATERIALS.get(Material.WOOD));
            for (IBlockState parent : Iterables.concat(MATERIALS.get(Material.ROCK), MATERIALS.get(Material.IRON))) {
                registerMiniAnvil(parent);
            }
        } else {
            states.addAll(MATERIALS.values());
        }

        for (IBlockState state : states) {
            registerMiniSaw(state);
        }

        if (addConversionRecipes) {
            for (BlockPlanks.EnumType type : BlockPlanks.EnumType.values()) {
                addOldRecipeConversation(new ItemStack(BWMBlocks.WOOD_SIDING, 1, type.getMetadata()), SIDINGS.get(Material.WOOD), Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, type));
                addOldRecipeConversation(new ItemStack(BWMBlocks.WOOD_MOULDING, 1, type.getMetadata()), MOULDINGS.get(Material.WOOD), Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, type));
                addOldRecipeConversation(new ItemStack(BWMBlocks.WOOD_CORNER, 1, type.getMetadata()), CORNERS.get(Material.WOOD), Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, type));
            }

            for (betterwithmods.common.blocks.mini.BlockMini.EnumType type : betterwithmods.common.blocks.mini.BlockMini.EnumType.VALUES) {
                addOldRecipeConversation(new ItemStack(BWMBlocks.STONE_SIDING, 1, type.getMetadata()), SIDINGS.get(Material.ROCK), type.getState());
                addOldRecipeConversation(new ItemStack(BWMBlocks.STONE_MOULDING, 1, type.getMetadata()), MOULDINGS.get(Material.ROCK), type.getState());
                addOldRecipeConversation(new ItemStack(BWMBlocks.STONE_CORNER, 1, type.getMetadata()), CORNERS.get(Material.ROCK), type.getState());
            }
        }
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }


    @SideOnly(Side.CLIENT)
    private void registerModel(IRegistry<ModelResourceLocation, IBakedModel> registry, String name, IBakedModel model) {
        registry.putObject(new ModelResourceLocation(BWMod.MODID + ":" + name, "normal"), model);
        registry.putObject(new ModelResourceLocation(BWMod.MODID + ":" + name, "inventory"), model);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPostBake(ModelBakeEvent event) {
        MiniModel.SIDING = new MiniModel(RenderUtils.getModel(new ResourceLocation(BWMod.MODID, "block/mini/siding")));
        MiniModel.MOULDING = new MiniModel(RenderUtils.getModel(new ResourceLocation(BWMod.MODID, "block/mini/moulding")));
        MiniModel.CORNER = new MiniModel(RenderUtils.getModel(new ResourceLocation(BWMod.MODID, "block/mini/corner")));
        for (Material material : names.keySet()) {
            String name = names.get(material);
            registerModel(event.getModelRegistry(), String.format("%s_%s", "siding", name), MiniModel.SIDING);
            registerModel(event.getModelRegistry(), String.format("%s_%s", "moulding", name), MiniModel.MOULDING);
            registerModel(event.getModelRegistry(), String.format("%s_%s", "corner", name), MiniModel.CORNER);
        }
    }

}
