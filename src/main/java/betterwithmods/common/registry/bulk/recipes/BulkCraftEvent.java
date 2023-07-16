package betterwithmods.common.registry.bulk.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.items.ItemStackHandler;

public class BulkCraftEvent extends Event {
    private TileEntity tile;
    private World world;
    private ItemStackHandler inventory;
    private BulkRecipe recipe;
    private NonNullList<ItemStack> outputs;


    public BulkCraftEvent(TileEntity tile, World world, ItemStackHandler inventory, BulkRecipe recipe, NonNullList<ItemStack> outputs) {
        this.tile = tile;
        this.world = world;
        this.inventory = inventory;
        this.recipe = recipe;
        this.outputs = outputs;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }


    public TileEntity getTile() {
        return tile;
    }

    public World getWorld() {
        return world;
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public BulkRecipe getRecipe() {
        return recipe;
    }

    public NonNullList<ItemStack> getOutputs() {
        return outputs;
    }

    public static NonNullList<ItemStack> fireOnCraft(TileEntity tile, World world, ItemStackHandler inventory, BulkRecipe recipe, NonNullList<ItemStack> outputs) {
        BulkCraftEvent event = new BulkCraftEvent(tile, world, inventory, recipe, outputs);

        if (MinecraftForge.EVENT_BUS.post(event)) {
            return NonNullList.create();
        } else {
            return outputs;
        }
    }
}
