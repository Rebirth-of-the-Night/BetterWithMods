package betterwithmods.common.fluid;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class FluidTankRestricted extends FluidTank {
    protected final Fluid restriction;

    public FluidTankRestricted(FluidStack fluid, int capacity) {
        super(capacity);
        if (fluid != null)
            restriction = fluid.getFluid();
        else
            restriction = null;
    }

    @Override
    public boolean canFillFluidType(FluidStack resource) {
        return super.canFillFluidType(resource) && (restriction == null || (resource != null && areFluidsIdentical(restriction, resource.getFluid())));
    }

    private boolean areFluidsIdentical(Fluid fluid1, Fluid fluid2) {
        if (fluid1 == null)
            return fluid2 == null;
        return fluid2 != null && (fluid1 == fluid2 || fluid1.getName().equals(fluid2.getName()));
    }
}
