package betterwithmods.common.blocks.behaviors;

import betterwithmods.module.hardcore.world.HCBuckets;
import betterwithmods.util.FluidUtils;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;

public class BehaviorFluidContainer extends BehaviorDefaultDispenseItem {

    private static final BehaviorFluidContainer INSTANCE = new BehaviorFluidContainer();
    private final BehaviorDefaultDispenseItem dispenseBehavior = new BehaviorDefaultDispenseItem();

    private BehaviorFluidContainer() {
    }

    public static BehaviorFluidContainer getInstance() {
        return INSTANCE;
    }

    /**
     * Dispense the specified stack, play the dispense sound and spawn particles.
     */
    @Override
    @Nonnull
    public ItemStack dispenseStack(@Nonnull IBlockSource source, @Nonnull ItemStack stack) {
        if (FluidUtil.getFluidContained(stack) != null) {
            return dumpContainer(source, stack);
        } else {
            if (HCBuckets.stopDispenserFillBehavior) {
                return super.dispenseStack(source, stack);
            }
            return fillContainer(source, stack);
        }
    }

    /**
     * Picks up fluid in front of a Dispenser and fills a container with it.
     */
    @Nonnull
    private ItemStack fillContainer(@Nonnull IBlockSource source, @Nonnull ItemStack stack) {

        World world = source.getWorld();
        EnumFacing dispenserFacing = source.getBlockState().getValue(BlockDispenser.FACING);
        BlockPos blockpos = source.getBlockPos().offset(dispenserFacing);

        FluidActionResult actionResult = FluidUtils.tryPickUpFluid(stack, null, world, blockpos, dispenserFacing.getOpposite());
        ItemStack resultStack = actionResult.getResult();

        if (!actionResult.isSuccess() || resultStack.isEmpty()) {
            return super.dispenseStack(source, stack);
        }

        if (stack.getCount() == 1) {
            return resultStack;
        } else if (((TileEntityDispenser) source.getBlockTileEntity()).addItemStack(resultStack) < 0) {
            this.dispenseBehavior.dispense(source, resultStack);
        }

        ItemStack stackCopy = stack.copy();
        stackCopy.shrink(1);
        return stackCopy;
    }

    /**
     * Drains a filled container and places the fluid in front of the Dispenser.
     */
    @Nonnull
    private ItemStack dumpContainer(IBlockSource source, @Nonnull ItemStack stack) {
        ItemStack singleStack = stack.copy();
        singleStack.setCount(1);
        IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(singleStack);
        if (fluidHandler == null) {
            return super.dispenseStack(source, stack);
        }

        FluidStack fluidStack = fluidHandler.drain(Fluid.BUCKET_VOLUME, false);
        EnumFacing dispenserFacing = source.getBlockState().getValue(BlockDispenser.FACING);
        BlockPos blockpos = source.getBlockPos().offset(dispenserFacing);
        FluidActionResult result = fluidStack != null ? FluidUtils.tryPlaceFluid(null, source.getWorld(), blockpos, stack, fluidStack) : FluidActionResult.FAILURE;

        if (result.isSuccess()) {
            ItemStack drainedStack = result.getResult();

            if (drainedStack.getCount() == 1) {
                return drainedStack;
            } else if (!drainedStack.isEmpty() && ((TileEntityDispenser) source.getBlockTileEntity()).addItemStack(drainedStack) < 0) {
                this.dispenseBehavior.dispense(source, drainedStack);
            }

            ItemStack stackCopy = drainedStack.copy();
            stackCopy.shrink(1);
            return stackCopy;
        } else {
            return this.dispenseBehavior.dispense(source, stack);
        }
    }
}
