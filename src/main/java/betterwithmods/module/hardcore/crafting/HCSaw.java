package betterwithmods.module.hardcore.crafting;

import betterwithmods.common.BWMRecipes;
import betterwithmods.module.Feature;
import com.google.common.collect.Sets;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Set;

/**
 * Created by primetoxinz on 5/25/17.v
 */
public class HCSaw extends Feature {
    @Override
    public String getFeatureDescription() {
        return "Makes the Saw required to get complex wooden blocks, including Fences, Doors, Etc.";
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        Set<ResourceLocation> blocks = Sets.newHashSet(
                Blocks.OAK_FENCE.getRegistryName(),
                Blocks.ACACIA_FENCE.getRegistryName(),
                Blocks.BIRCH_FENCE.getRegistryName(),
                Blocks.SPRUCE_FENCE.getRegistryName(),
                Blocks.JUNGLE_FENCE.getRegistryName(),
                Blocks.OAK_FENCE.getRegistryName(),
                Blocks.DARK_OAK_FENCE.getRegistryName(),
                Blocks.OAK_FENCE_GATE.getRegistryName(),
                Blocks.BIRCH_FENCE_GATE.getRegistryName(),
                Blocks.SPRUCE_FENCE_GATE.getRegistryName(),
                Blocks.DARK_OAK_FENCE_GATE.getRegistryName(),
                Blocks.JUNGLE_FENCE_GATE.getRegistryName(),
                Blocks.OAK_FENCE_GATE.getRegistryName(),
                Blocks.ACACIA_FENCE_GATE.getRegistryName(),
                Blocks.TRAPDOOR.getRegistryName(),
                Items.OAK_DOOR.getRegistryName(),
                Items.ACACIA_DOOR.getRegistryName(),
                Items.BIRCH_DOOR.getRegistryName(),
                Items.SPRUCE_DOOR.getRegistryName(),
                Items.DARK_OAK_DOOR.getRegistryName(),
                Items.JUNGLE_DOOR.getRegistryName(),
                Items.BOAT.getRegistryName(),
                Items.ACACIA_BOAT.getRegistryName(),
                Items.BIRCH_BOAT.getRegistryName(),
                Items.SPRUCE_BOAT.getRegistryName(),
                Items.DARK_OAK_BOAT.getRegistryName(),
                Items.JUNGLE_BOAT.getRegistryName(),
                Items.ITEM_FRAME.getRegistryName(),
                Blocks.NOTEBLOCK.getRegistryName(),
                Items.SIGN.getRegistryName()
        );
        blocks.forEach(BWMRecipes::removeRecipe);
    }

}
