package betterwithmods.module.hardcore.world.saplings;

import betterwithmods.BWMod;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWMRecipes;
import betterwithmods.common.registry.block.recipe.BlockDropIngredient;
import betterwithmods.common.registry.block.recipe.BlockIngredient;
import betterwithmods.module.Feature;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class HCSapling extends Feature {

    public static List<SaplingConversion> SAPLING_CONVERSIONS = Lists.newArrayList();

    public static IBlockState getSapling(BlockPlanks.EnumType type) {
        return Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, type);
    }

    @Override
    public String getFeatureDescription() {
        return "Change saplings to grow in stages before becoming a tree";
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        for (BlockPlanks.EnumType type : BlockPlanks.EnumType.values()) {
            IBlockState sapling = getSapling(type);
            Block crop = new BlockSaplingCrop(sapling).setRegistryName(BWMod.MODID, String.format("sapling_crop_%s", type.getName()));
            BWMBlocks.registerBlock(crop, null);
            SAPLING_CONVERSIONS.add(new SaplingConversion(new BlockDropIngredient(BWMRecipes.getStackFromState(sapling)), crop));
        }
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.PlaceEvent event) {
        if (event.getPlacedBlock().getBlock() instanceof BlockSapling) {
            IBlockState state = event.getPlacedBlock();
            IBlockState replaced = event.getBlockSnapshot().getReplacedBlock();
            //getBlock().isReplaceable(event.getWorld(), event.getBlockSnapshot().getPos())
            if(replaced.getMaterial().isReplaceable()) {
                for (SaplingConversion conversion : SAPLING_CONVERSIONS) {
                    if (conversion.ingredient.apply(event.getWorld(), event.getPos(), state)) {
                        event.getWorld().setBlockState(event.getPos(), conversion.getReplacement().getDefaultState());
                    }
                }
            }
        }
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    public class SaplingConversion {
        private final BlockIngredient ingredient;
        private final Block replacement;

        public SaplingConversion(BlockIngredient ingredient, Block replacement) {
            this.ingredient = ingredient;
            this.replacement = replacement;
        }

        public BlockIngredient getIngredient() {
            return ingredient;
        }

        public Block getReplacement() {
            return replacement;
        }
    }
}
