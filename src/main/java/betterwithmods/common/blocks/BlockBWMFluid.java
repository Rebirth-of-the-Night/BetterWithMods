package betterwithmods.common.blocks;

import betterwithmods.client.BWCreativeTabs;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Created by primetoxinz on 6/9/17.
 */
public class BlockBWMFluid extends BlockFluidClassic {

    public BlockBWMFluid(Fluid fluid, Material material) {
        super(fluid, material);
        setCreativeTab(BWCreativeTabs.BWTAB);
    }



    @Override
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
        return getFluid().getName().equals("hellfire");
    }

    @Override
    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return getFluid().getName().equals("hellfire");
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
        switch(getFluid().getName()) {
            case "soulforged_steel":
                worldIn.playSound(null,pos, SoundEvents.ENTITY_GHAST_AMBIENT, SoundCategory.AMBIENT,0.5f,random.nextFloat());
                break;
        }
        super.randomTick(worldIn, pos, state, random);
    }

    @Nonnull
    @Override
    public String getTranslationKey() {
        Fluid fluid = FluidRegistry.getFluid(fluidName);
        if(fluid != null) {
            return fluid.getUnlocalizedName();
        }
        return super.getTranslationKey();
    }
}