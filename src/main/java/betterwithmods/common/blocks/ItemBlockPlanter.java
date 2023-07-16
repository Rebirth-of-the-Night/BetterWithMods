package betterwithmods.common.blocks;

import betterwithmods.common.blocks.BlockPlanter.EnumType;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockPlanter extends ItemBlockMeta {
    public ItemBlockPlanter(Block block) {
        super(block);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    public int getColorFromItemStack(ItemStack stack, int colorIndex) {
        if (stack.getItemDamage() == 2 && block instanceof BlockPlanter) {
            BlockPlanter planter = (BlockPlanter) block;
            return planter.colorMultiplier(
                    planter.getDefaultState().withProperty(BlockPlanter.TYPE, EnumType.byMeta(stack.getItemDamage())),
                    null, null, colorIndex);
        }
        return -1;
    }
}
