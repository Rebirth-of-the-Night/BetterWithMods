package betterwithmods.common.items;

import betterwithmods.module.ModuleLoader;
import betterwithmods.module.hardcore.creatures.chicken.HCChickens;
import betterwithmods.util.InvUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

public class ItemHempSeed extends ItemBlock implements IPlantable {
    public ItemHempSeed(Block block) {
        super(block);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
        if (ModuleLoader.isFeatureEnabled(HCChickens.class))
            return true;
        if (target instanceof EntityChicken) {
            EntityChicken chicken = (EntityChicken) target;
            if (chicken.getGrowingAge() == 0 && !chicken.isInLove()) {
                chicken.setInLove(playerIn);
                InvUtils.usePlayerItem(playerIn, EnumFacing.UP, stack, 1);
                return true;
            }
        }
        return false;
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Crop;
    }

    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
        return this.getBlock().getDefaultState();
    }
}
