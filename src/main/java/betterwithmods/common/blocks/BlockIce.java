package betterwithmods.common.blocks;

import betterwithmods.util.FluidUtils;
import betterwithmods.util.item.ToolsManager;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockIce extends net.minecraft.block.BlockIce {

    private static final PropertyBool NATURAL = PropertyBool.create("natural");

    public BlockIce() {
        super();
        setHardness(0.5F);
        setLightOpacity(3);
        setSoundType(SoundType.GLASS);
        setTranslationKey("ice");
        ToolsManager.setPickaxesAsEffectiveAgainst(this);
        setDefaultState(getDefaultState().withProperty(NATURAL, true));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, NATURAL);
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.005F);

        if (this.canSilkHarvest(worldIn, pos, state, player) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0) {
            java.util.List<ItemStack> items = new java.util.ArrayList<ItemStack>();
            items.add(this.getSilkTouchDrop(state));

            net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, 0, 1.0f, true, player);
            for (ItemStack is : items)
                spawnAsEntity(worldIn, pos, is);
        } else {
            if (worldIn.provider.doesWaterVaporize()) {
                worldIn.setBlockToAir(pos);
                return;
            }


            int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
            harvesters.set(player);
            this.dropBlockAsItem(worldIn, pos, state, i);
            harvesters.set(null);
            Material material = worldIn.getBlockState(pos.down()).getMaterial();

            if (material.blocksMovement() || material.isLiquid()) {
                setWater(worldIn, pos, state);
            }
        }
    }

    @Override
    protected void turnIntoWater(@Nonnull World worldIn, @Nonnull BlockPos pos) {
        if (worldIn.provider.doesWaterVaporize()) {
            worldIn.setBlockToAir(pos);
        } else {
            IBlockState state = worldIn.getBlockState(pos);
            this.dropBlockAsItem(worldIn, pos, state, 0);
            setWater(worldIn, pos, state);
        }
    }

    private void setWater(World world, BlockPos pos, IBlockState state) {
        if(state.getBlock() != this)
            return;
        if (state.getValue(NATURAL)) {
            world.setBlockState(pos, Blocks.FLOWING_WATER.getDefaultState());
        } else {
            FluidUtils.setLiquid(world, pos, Blocks.WATER, 14, true);
            for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                FluidUtils.setLiquid(world, pos.offset(facing), Blocks.WATER, 10, false);
            }
        }

        world.neighborChanged(pos, Blocks.WATER, pos);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(NATURAL) ? 1 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(NATURAL, meta == 1);
    }

    @Override
    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return new ItemStack(this, 1, getMetaFromState(state));
    }
}
