package betterwithmods.module.tweaks;

import betterwithmods.common.BWMRecipes;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.blocks.BlockRailDetectorBase;
import betterwithmods.common.items.ItemMaterial;
import betterwithmods.module.Feature;
import betterwithmods.module.ModuleLoader;
import betterwithmods.module.gameplay.MetalReclaming;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Objects;

import static betterwithmods.common.BWMBlocks.registerBlock;

public class DetectorRail extends Feature {

    public static final Block DETECTOR_RAIL_STONE = new BlockRailDetectorBase(cart -> !(cart instanceof EntityMinecartEmpty) || BlockRailDetectorBase.isRider(cart, Objects::nonNull)).setRegistryName("detector_rail_stone");
    public static final Block DETECTOR_RAIL_STEEL = new BlockRailDetectorBase(cart -> BlockRailDetectorBase.isRider(cart, rider -> rider instanceof EntityPlayer)).setRegistryName("detector_rail_steel");

    @Override
    public String getFeatureDescription() {
        return "Change what detector rails detect; Wooden:all minecarts; Stone: carts containing something, SFS: carts with players.";
    }

    @Override
    public void preInitClient(FMLPreInitializationEvent event) {
        overrideBlock("rail_detector");
        overrideBlock("rail_detector_powered");
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerBlock(DETECTOR_RAIL_STEEL);
        registerBlock(DETECTOR_RAIL_STONE);

        Blocks.DETECTOR_RAIL.setTranslationKey("detector_rail_wood");

        BWMRecipes.removeRecipe(Blocks.DETECTOR_RAIL.getRegistryName());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        if (ModuleLoader.isFeatureEnabled(MetalReclaming.class)) {
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(DETECTOR_RAIL_STONE, 6), new ItemStack(Items.IRON_INGOT, 6));
            BWRegistry.CRUCIBLE.addStokedRecipe(new ItemStack(DETECTOR_RAIL_STEEL, 6), Lists.newArrayList(new ItemStack(Items.IRON_INGOT, 6), ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.INGOT_STEEL, 2)));
        }
    }
}
