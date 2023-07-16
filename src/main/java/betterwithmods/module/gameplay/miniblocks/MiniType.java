package betterwithmods.module.gameplay.miniblocks;

import betterwithmods.module.gameplay.miniblocks.blocks.BlockCorner;
import betterwithmods.module.gameplay.miniblocks.blocks.BlockMini;
import betterwithmods.module.gameplay.miniblocks.blocks.BlockMoulding;
import betterwithmods.module.gameplay.miniblocks.blocks.BlockSiding;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

public enum MiniType {
    SIDING(BlockSiding.class, "siding"),
    MOULDING(BlockMoulding.class, "moulding"),
    CORNER(BlockCorner.class, "corner"),
    UNKNOWN(null, "");
    public static MiniType[] VALUES = values();

    private Class<? extends BlockMini> block;
    private String name;

    MiniType(Class<? extends BlockMini> block, String name) {
        this.block = block;
        this.name = name;
    }

    public static boolean matches(MiniType type, ItemStack stack) {
        return fromStack(stack).equals(type);
    }

    public static MiniType fromName(String name) {
        return Arrays.stream(VALUES).filter(t -> t.isName(name)).findFirst().orElse(null);
    }

    public static MiniType fromBlock(BlockMini block) {
        return Arrays.stream(VALUES).filter(t -> t.isBlock(block)).findFirst().orElse(null);
    }

    public static MiniType fromStack(ItemStack stack) {
        if (stack.getItem() instanceof ItemMini) {
            BlockMini mini = (BlockMini) ((ItemMini) stack.getItem()).getBlock();
            return fromBlock(mini);
        }
        return UNKNOWN;
    }

    private boolean isName(String name) {
        return this.name.equalsIgnoreCase(name);
    }

    private boolean isBlock(BlockMini mini) {
        return this.block.isAssignableFrom(mini.getClass());
    }
}
