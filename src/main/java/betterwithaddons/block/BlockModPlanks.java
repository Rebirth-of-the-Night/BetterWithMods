package betterwithaddons.block;

import betterwithaddons.BetterWithAddons;
import betterwithaddons.lib.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockModPlanks extends Block {
    ModWoods woodVariant;

    public BlockModPlanks(ModWoods variant)
    {
        super(Material.WOOD);
        this.setHardness(2.0F).setResistance(5.0F);
        this.setSoundType(SoundType.WOOD);
        this.setHarvestLevel("axe", 0);

        woodVariant = variant;

        this.setTranslationKey("planks_"+woodVariant.getName());
        this.setRegistryName(new ResourceLocation(Reference.MOD_ID, "planks_"+woodVariant.getName()));
        this.setCreativeTab(BetterWithAddons.instance.creativeTab);
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return Blocks.PLANKS.getFlammability(world, pos, face);
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return Blocks.PLANKS.getFireSpreadSpeed(world, pos, face);
    }
}
