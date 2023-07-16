package betterwithmods.util.item;

import com.google.common.base.Objects;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;


/**
 * Purpose:
 *
 * @author primetoxinz
 * @version 2/25/17
 */
public class Stack {
    private Item item;
    private int meta;
    private Type type;


    public Stack(ItemStack stack) {
        if (stack.getItem() instanceof ItemBlock) {
            this.item = stack.getItem();
            this.type = Type.BLOCK;
        } else {
            this.item = stack.getItem();
            this.type = Type.ITEM;
        }
        this.meta = stack.getMetadata();
    }

    public Stack(Item item, int meta) {
        this.item = item;
        this.meta = meta;
        this.type = Type.ITEM;
    }

    public Stack(Block block, int meta) {
        this.item = Item.getItemFromBlock(block);
        this.meta = meta;
        this.type = Type.BLOCK;
    }


    public Item getItem() {
        return this.item;
    }

    public int getMeta() {
        return meta;
    }

    public Type getType() {
        return type;
    }

    public ItemStack getItemStack() {
        return new ItemStack(getItem(), 1, getMeta());
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Stack))
            return false;
        Stack stack = (Stack) o;
        boolean wild = meta == OreDictionary.WILDCARD_VALUE || stack.meta == OreDictionary.WILDCARD_VALUE;
        return wild ? stack.item.equals(this.item) : (stack.item.equals(this.item) && this.meta == stack.meta);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(item);
    }

    private enum Type {
        BLOCK,
        ITEM
    }

    @Override
    public String toString() {
        return String.format("%s->%s:%s:%s", getType(), getItem().getTranslationKey(new ItemStack(item, 0, meta)), getMeta(), hashCode());
    }
}
