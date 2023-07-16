package betterwithmods.util.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.*;

import java.util.Collections;

/**
 * Set of methods dealing with ItemTools.
 *
 * @author Koward
 */
public final class ToolsManager {
    private ToolsManager() {
    }


    public static Item.ToolMaterial getToolMaterial(ItemStack tool) {
        Item item = tool.getItem();
        if (item instanceof ItemTool)
            return Item.ToolMaterial.valueOf(((ItemTool) item).getToolMaterialName());
        return null;
    }

    public static void setAxesAsEffectiveAgainst(Block... blocks) {
        for (Item item : Item.REGISTRY) {
            if (!(item instanceof ItemAxe)) continue;
            ItemAxe tool = (ItemAxe) item;
            setToolAsEffectiveAgainst(tool, blocks);
        }
    }

    public static void setPickaxesAsEffectiveAgainst(Block... blocks) {
        for (Item item : Item.REGISTRY) {
            if (!(item instanceof ItemPickaxe)) continue;
            ItemPickaxe tool = (ItemPickaxe) item;
            setToolAsEffectiveAgainst(tool, blocks);
        }
    }

    public static void setToolAsEffectiveAgainst(ItemTool tool, Block... blocks) {
        Collections.addAll(tool.effectiveBlocks, blocks);
    }

    //Potential to crash if tool material is improperly assigned?
    public static float getSpeed(ItemStack stack) {
        Item.ToolMaterial material = getToolMaterial(stack);
        if (material != null)
            return material.getEfficiency();
        return 1;
    }

    public static float getSpeed(ItemStack stack, IBlockState state) {
        if (!stack.isEmpty() && stack.getItem() instanceof ItemTool) {
            ItemTool tool = (ItemTool) stack.getItem();
            return tool.getDestroySpeed(stack, state);
        }
        return 1;
    }

}
