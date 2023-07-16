package betterwithmods.module.compat.jei;

import betterwithmods.api.recipe.IOutput;
import betterwithmods.api.recipe.impl.ChanceOutput;
import betterwithmods.api.recipe.impl.RandomOutput;
import betterwithmods.api.recipe.impl.StackOutput;
import betterwithmods.client.container.anvil.ContainerSteelAnvil;
import betterwithmods.client.gui.GuiSteelAnvil;
import betterwithmods.client.gui.bulk.GuiCauldron;
import betterwithmods.client.gui.bulk.GuiCrucible;
import betterwithmods.client.gui.bulk.GuiFilteredHopper;
import betterwithmods.client.gui.bulk.GuiMill;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWMItems;
import betterwithmods.common.BWMRecipes;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.blocks.mechanical.BlockCookingPot;
import betterwithmods.common.blocks.mechanical.BlockMechMachines;
import betterwithmods.common.items.ItemMaterial;
import betterwithmods.common.registry.HopperInteractions;
import betterwithmods.common.registry.HopperInteractions.HopperRecipe;
import betterwithmods.common.registry.KilnStructureManager;
import betterwithmods.common.registry.anvil.AnvilCraftingManager;
import betterwithmods.common.registry.anvil.ShapedAnvilRecipe;
import betterwithmods.common.registry.anvil.ShapelessAnvilRecipe;
import betterwithmods.common.registry.block.recipe.KilnRecipe;
import betterwithmods.common.registry.block.recipe.SawRecipe;
import betterwithmods.common.registry.block.recipe.TurntableRecipe;
import betterwithmods.common.registry.bulk.recipes.CookingPotRecipe;
import betterwithmods.common.registry.bulk.recipes.MillRecipe;
import betterwithmods.common.registry.crafting.ToolBaseRecipe;
import betterwithmods.common.registry.crafting.ToolDamageRecipe;
import betterwithmods.common.registry.heat.BWMHeatRegistry;
import betterwithmods.module.compat.jei.category.*;
import betterwithmods.module.compat.jei.ingredient.OutputHelper;
import betterwithmods.module.compat.jei.ingredient.OutputRenderer;
import betterwithmods.module.compat.jei.wrapper.*;
import betterwithmods.module.gameplay.miniblocks.MiniBlocks;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import mezz.jei.Internal;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IVanillaRecipeFactory;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import mezz.jei.gui.Focus;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;
import mezz.jei.startup.StackHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static betterwithmods.common.blocks.mechanical.BlockCookingPot.EnumType.CAULDRON;
import static betterwithmods.common.blocks.mechanical.BlockCookingPot.EnumType.CRUCIBLE;
import static betterwithmods.common.blocks.mechanical.BlockMechMachines.EnumType.*;

@mezz.jei.api.JEIPlugin
public class JEI implements IModPlugin {
    public static IJeiHelpers HELPER;

    public static IJeiRuntime JEI_RUNTIME;

    public static List<Class<? extends IOutput>> ALL_OUTPUTS = Lists.newArrayList();

    static {
        ALL_OUTPUTS.add(IOutput.class);
        ALL_OUTPUTS.add(StackOutput.class);
        ALL_OUTPUTS.add(RandomOutput.class);
        ALL_OUTPUTS.add(ChanceOutput.class);
    }

    public static void doAllOutputs(Consumer<Class<? extends IOutput>> consumer) {
        ALL_OUTPUTS.forEach(consumer);
    }

    public static void showRecipe(Ingredient ingredient) {
        ItemStack stack = Lists.newArrayList(ingredient.getMatchingStacks()).stream().findFirst().orElse(ItemStack.EMPTY);
        if (stack.isEmpty())
            return;
        IFocus<?> focus = new Focus<Object>(IFocus.Mode.OUTPUT, stack);
        JEI.JEI_RUNTIME.getRecipesGui().show(focus);
    }

    private static String getHeatUID(String base, int heat) {
        if (heat == BWMHeatRegistry.UNSTOKED_HEAT) {
            return base;
        } else if (heat == BWMHeatRegistry.STOKED_HEAT) {
            return String.format("%s.%s", base, "stoked");
        } else {
            return String.format("%s.%s", base, heat);
        }
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        if (JEI_RUNTIME == null) {
            JEI_RUNTIME = jeiRuntime;
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration reg) {
        final IJeiHelpers helpers = reg.getJeiHelpers();
        final IGuiHelper guiHelper = helpers.getGuiHelper();

        for (int heat : BWMHeatRegistry.allHeatLevels()) {
            reg.addRecipeCategories(
                    new CookingPotRecipeCategory(guiHelper, getHeatUID(CookingPotRecipeCategory.CAULDRON_UID, heat)),
                    new CookingPotRecipeCategory(guiHelper, getHeatUID(CookingPotRecipeCategory.CRUCIBLE_UID, heat)),
                    new KilnRecipeCategory(guiHelper, getHeatUID(KilnRecipeCategory.UID, heat))
            );
        }

        reg.addRecipeCategories(
                new MillRecipeCategory(guiHelper),
                new SawRecipeCategory(guiHelper),
                new SteelSawRecipeCategory(guiHelper),
                new TurntableRecipeCategory(guiHelper),
                new HopperRecipeCategory(guiHelper),
                new SteelAnvilRecipeCategory(guiHelper)
        );
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
        getAllMiniBlocks().forEach(item -> subtypeRegistry.registerSubtypeInterpreter(item, itemStack -> {
            NBTTagCompound compound = itemStack.getSubCompound("texture");
            return compound != null ? compound.toString() : "";
        }));
    }

    private List<Item> getAllMiniBlocks() {
        ArrayList<Item> list = new ArrayList<>();
        MiniBlocks.SIDINGS.values().stream().map(Item::getItemFromBlock).forEach(list::add);
        MiniBlocks.MOULDINGS.values().stream().map(Item::getItemFromBlock).forEach(list::add);
        MiniBlocks.CORNERS.values().stream().map(Item::getItemFromBlock).forEach(list::add);
        return list;
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {
        StackHelper stackHelper = Internal.getStackHelper();
        doAllOutputs(clazz -> registry.register(clazz, Collections.emptySet(), new OutputHelper<>(stackHelper), new OutputRenderer<>()));
    }

    @Override
    public void register(@Nonnull IModRegistry reg) {
        HELPER = reg.getJeiHelpers();

        registerHeatBasedRecipes(reg);

        reg.handleRecipes(MillRecipe.class, r -> new BulkRecipeWrapper<>(HELPER, r, 3), MillRecipeCategory.UID);
        reg.handleRecipes(SawRecipe.class, r -> new BlockRecipeWrapper<>(HELPER, r, 3), SawRecipeCategory.UID);
        reg.handleRecipes(SawRecipe.class, r -> new BlockRecipeWrapper<>(HELPER, r, 3), SteelSawRecipeCategory.UID);
        reg.handleRecipes(TurntableRecipe.class, recipe -> new TurntableRecipeWrapper(HELPER, recipe), TurntableRecipeCategory.UID);
        reg.handleRecipes(HopperRecipe.class, recipe -> new HopperRecipeWrapper(HELPER, recipe), HopperRecipeCategory.UID);
        reg.handleRecipes(ShapedAnvilRecipe.class, recipe -> new ShapedAnvilRecipeWrapper(HELPER, recipe), SteelAnvilRecipeCategory.UID);
        reg.handleRecipes(ShapelessAnvilRecipe.class, recipe -> new ShapelessRecipeWrapper<>(HELPER, recipe), SteelAnvilRecipeCategory.UID);
        reg.handleRecipes(ShapelessOreRecipe.class, recipe -> new ShapelessRecipeWrapper<>(HELPER, recipe), SteelAnvilRecipeCategory.UID);
        reg.handleRecipes(ShapelessRecipes.class, recipe -> new ShapelessRecipeWrapper<>(HELPER, recipe), SteelAnvilRecipeCategory.UID);
        reg.handleRecipes(ToolBaseRecipe.class, recipe -> new ShapelessRecipeWrapper<>(HELPER, recipe), SteelAnvilRecipeCategory.UID);
        reg.handleRecipes(ToolBaseRecipe.class, recipe -> new ShapelessRecipeWrapper<>(HELPER, recipe), VanillaRecipeCategoryUid.CRAFTING);
        reg.handleRecipes(ToolDamageRecipe.class, recipe -> new ShapelessRecipeWrapper<>(HELPER, recipe), SteelAnvilRecipeCategory.UID);
        reg.handleRecipes(ToolDamageRecipe.class, recipe -> new ShapelessRecipeWrapper<>(HELPER, recipe), "minecraft.crafting");

        reg.addRecipes(BWRegistry.MILLSTONE.getRecipes(), MillRecipeCategory.UID);
        reg.addRecipes(BWRegistry.WOOD_SAW.getDisplayRecipes(), SawRecipeCategory.UID);
        reg.addRecipes(BWRegistry.TURNTABLE.getDisplayRecipes(), TurntableRecipeCategory.UID);

        reg.addRecipes(HopperInteractions.getDisplayRecipes(), HopperRecipeCategory.UID);
        reg.addRecipes(AnvilCraftingManager.ANVIL_CRAFTING, SteelAnvilRecipeCategory.UID);


        reg.addRecipeCatalyst(BlockMechMachines.getStack(MILL), MillRecipeCategory.UID);
        reg.addRecipeCatalyst(BlockMechMachines.getStack(HOPPER), HopperRecipeCategory.UID);
        reg.addRecipeCatalyst(BlockMechMachines.getStack(TURNTABLE), TurntableRecipeCategory.UID);

        reg.addRecipeCatalyst(new ItemStack(BWMBlocks.SAW), SawRecipeCategory.UID);
        reg.addRecipeCatalyst(new ItemStack(BWMBlocks.STEEL_SAW), SteelSawRecipeCategory.UID);
        reg.addRecipeCatalyst(new ItemStack(BWMBlocks.STEEL_ANVIL), SteelAnvilRecipeCategory.UID);

        reg.addRecipeClickArea(GuiMill.class, 81, 19, 14, 14, MillRecipeCategory.UID);
        reg.addRecipeClickArea(GuiSteelAnvil.class, 88, 41, 28, 23, SteelAnvilRecipeCategory.UID);
        reg.addRecipeClickArea(GuiFilteredHopper.class, 81, 19, 14, 14, HopperRecipeCategory.UID);

        registerAnvil(reg);

        IRecipeTransferRegistry recipeTransferRegistry = reg.getRecipeTransferRegistry();
        recipeTransferRegistry.addRecipeTransferHandler(ContainerSteelAnvil.class, SteelAnvilRecipeCategory.UID, 1, 16, 17, 36);
        recipeTransferRegistry.addRecipeTransferHandler(new AnvilCraftingTransfer());

    }

    private void registerAnvil(IModRegistry reg) {
        List<ItemStack> tools = Lists.newArrayList(new ItemStack(BWMItems.STEEL_AXE), new ItemStack(BWMItems.STEEL_BATTLEAXE), new ItemStack(BWMItems.STEEL_BOOTS), new ItemStack(BWMItems.STEEL_CHEST), new ItemStack(BWMItems.STEEL_HELMET), new ItemStack(BWMItems.STEEL_HOE), new ItemStack(BWMItems.STEEL_MATTOCK),
                new ItemStack(BWMItems.STEEL_PANTS), new ItemStack(BWMItems.STEEL_PICKAXE), new ItemStack(BWMItems.STEEL_SHOVEL), new ItemStack(BWMItems.STEEL_SWORD));
        IVanillaRecipeFactory v = reg.getJeiHelpers().getVanillaRecipeFactory();
        for (ItemStack stack : tools) {
            ItemStack dam1 = stack.copy();
            dam1.setItemDamage(dam1.getMaxDamage());
            ItemStack dam2 = stack.copy();
            dam2.setItemDamage(dam2.getMaxDamage() * 3 / 4);
            ItemStack dam3 = stack.copy();
            dam3.setItemDamage(dam3.getMaxDamage() * 2 / 4);

            v.createAnvilRecipe(dam1, Collections.singletonList(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.INGOT_STEEL)), Collections.singletonList(dam2));
            v.createAnvilRecipe(dam2, Collections.singletonList(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.INGOT_STEEL)), Collections.singletonList(dam3));
        }

    }

    private void registerHeatBasedRecipes(@Nonnull IModRegistry reg) {
        Set<String> cauldron = Sets.newHashSet(), crucible = Sets.newHashSet(), kiln = Sets.newHashSet();
        for (int heat : BWMHeatRegistry.allHeatLevels()) {
            String cauldronUID = getHeatUID(CookingPotRecipeCategory.CAULDRON_UID, heat);
            String crucibleUID = getHeatUID(CookingPotRecipeCategory.CRUCIBLE_UID, heat);
            String kilnUID = getHeatUID(KilnRecipeCategory.UID, heat);
            cauldron.add(cauldronUID);
            crucible.add(crucibleUID);
            kiln.add(kilnUID);

            reg.handleRecipes(CookingPotRecipe.class, recipe -> new BulkRecipeWrapper<>(HELPER, recipe, 9), cauldronUID);
            reg.handleRecipes(CookingPotRecipe.class, recipe -> new BulkRecipeWrapper<>(HELPER, recipe, 9), crucibleUID);
            reg.handleRecipes(KilnRecipe.class, recipe -> new KilnRecipeWrapper(HELPER, recipe), kilnUID);

            reg.addRecipes(BWRegistry.CAULDRON.getRecipesForHeat(heat), cauldronUID);
            reg.addRecipes(BWRegistry.CRUCIBLE.getRecipesForHeat(heat), crucibleUID);
            reg.addRecipes(BWRegistry.KILN.getRecipesForHeat(heat), kilnUID);

        }

        reg.addRecipeCatalyst(BlockCookingPot.getStack(CAULDRON), cauldron.stream().toArray(String[]::new));
        reg.addRecipeCatalyst(BlockCookingPot.getStack(CRUCIBLE), crucible.stream().toArray(String[]::new));
        for(IBlockState state: KilnStructureManager.KILN_BLOCKS)
            reg.addRecipeCatalyst(BWMRecipes.getStackFromState(state), kiln.stream().toArray(String[]::new));

        reg.addRecipeClickArea(GuiCauldron.class, 81, 19, 14, 14, cauldron.stream().toArray(String[]::new));
        reg.addRecipeClickArea(GuiCrucible.class, 81, 19, 14, 14, crucible.stream().toArray(String[]::new));

    }


}

