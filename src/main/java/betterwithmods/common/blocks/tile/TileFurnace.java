package betterwithmods.common.blocks.tile;

import betterwithmods.module.hardcore.crafting.HCFurnace;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry.ItemStackHolder;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Map.Entry;

//Borrowing caching logic from https://github.com/Shadows-of-Fire/FastFurnace

public class TileFurnace extends TileEntityFurnace {

    public static final int INPUT = 0;
    public static final int FUEL = 1;
    public static final int OUTPUT = 2;

    protected ItemStack recipeKey = ItemStack.EMPTY;
    protected ItemStack recipeOutput = ItemStack.EMPTY;
    protected ItemStack failedMatch = ItemStack.EMPTY;


    @ItemStackHolder(value = "minecraft:sponge", meta = 1)
    public static final ItemStack WET_SPONGE = ItemStack.EMPTY;

    public TileFurnace() {
        this.totalCookTime = 200;
    }


    @Override
    public int getCookTime(ItemStack stack) {
        return HCFurnace.getCookingTime(stack).orElse(200);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setInteger("BurnTime", this.furnaceBurnTime);
        compound.setInteger("CookTime", this.cookTime);
        compound.setInteger("CookTimeTotal", this.totalCookTime);
        return compound;
    }

    @Override
    public void update() {
        if (world.isRemote && isBurning()) {
            furnaceBurnTime--;
            return;
        } else if (world.isRemote) return;

        ItemStack fuel = ItemStack.EMPTY;
        boolean canSmelt = canSmelt();

        if ((HCFurnace.CONSUME_FUEL_WHEN_IDLE || canSmelt ) && !this.isBurning() && !(fuel = furnaceItemStacks.get(FUEL)).isEmpty()) {
            burnFuel(fuel, false);
        }

        boolean wasBurning = isBurning();

        if (this.isBurning()) {
            furnaceBurnTime--;
            if (canSmelt) smelt();
            else cookTime = 0;
        }

        if ((HCFurnace.CONSUME_FUEL_WHEN_IDLE || canSmelt()) && !this.isBurning() && !(fuel = furnaceItemStacks.get(FUEL)).isEmpty()) {
            burnFuel(fuel, wasBurning);
        }

        if (wasBurning && !isBurning())
            world.setBlockState(pos, Blocks.FURNACE.getDefaultState().withProperty(BlockFurnace.FACING, world.getBlockState(pos).getValue(BlockFurnace.FACING)));
    }

    private void smelt() {
        cookTime++;
        if (this.cookTime == this.totalCookTime) {
            this.cookTime = 0;
            this.totalCookTime = this.getCookTime(this.furnaceItemStacks.get(INPUT));
            this.smeltItem();
        }
    }

    private void burnFuel(ItemStack fuel, boolean burnedThisTick) {
        currentItemBurnTime = (furnaceBurnTime = getItemBurnTime(fuel));
        if (this.isBurning()) {
            Item item = fuel.getItem();
            fuel.shrink(1);
            if (fuel.isEmpty())
                furnaceItemStacks.set(FUEL, item.getContainerItem(fuel));
            if (!burnedThisTick)
                world.setBlockState(pos, Blocks.LIT_FURNACE.getDefaultState().withProperty(BlockFurnace.FACING, world.getBlockState(pos).getValue(BlockFurnace.FACING)));
        }
    }


    public boolean canSmelt() {
        ItemStack input = furnaceItemStacks.get(INPUT);
        ItemStack output = furnaceItemStacks.get(OUTPUT);
        if (input.isEmpty() || input == failedMatch) return false;

        if (recipeKey.isEmpty() || !OreDictionary.itemMatches(recipeKey, input, false)) {
            boolean matched = false;
            for (Entry<ItemStack, ItemStack> e : FurnaceRecipes.instance().getSmeltingList().entrySet()) {
                if (OreDictionary.itemMatches(e.getKey(), input, false)) {
                    recipeKey = e.getKey();
                    recipeOutput = e.getValue();
                    matched = true;
                    failedMatch = ItemStack.EMPTY;
                    break;
                }
            }
            if (!matched) {
                recipeKey = ItemStack.EMPTY;
                recipeOutput = ItemStack.EMPTY;
                failedMatch = input;
                return false;
            }
        }

        return !recipeOutput.isEmpty() && (output.isEmpty() || (ItemHandlerHelper.canItemStacksStack(recipeOutput, output) && (recipeOutput.getCount() + output.getCount() <= output.getMaxStackSize())));
    }

    @Override
    public void smeltItem() {
        ItemStack input = this.furnaceItemStacks.get(INPUT);
        ItemStack recipeOutput = FurnaceRecipes.instance().getSmeltingList().get(recipeKey);
        ItemStack output = this.furnaceItemStacks.get(OUTPUT);

        if (output.isEmpty()) this.furnaceItemStacks.set(OUTPUT, recipeOutput.copy());
        else if (ItemHandlerHelper.canItemStacksStack(output, recipeOutput)) output.grow(recipeOutput.getCount());

        if (input.isItemEqual(WET_SPONGE) && this.furnaceItemStacks.get(FUEL).getItem() == Items.BUCKET)
            this.furnaceItemStacks.set(FUEL, new ItemStack(Items.WATER_BUCKET));

        input.shrink(1);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        if (oldState.getBlock() == Blocks.FURNACE && newState.getBlock() == Blocks.LIT_FURNACE) return false;
        else if (oldState.getBlock() == Blocks.LIT_FURNACE && newState.getBlock() == Blocks.FURNACE) return false;
        return true;
    }
}
