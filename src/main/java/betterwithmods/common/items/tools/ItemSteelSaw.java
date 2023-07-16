package betterwithmods.common.items.tools;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.mechanical.IBlockActive;
import betterwithmods.module.hardcore.creatures.HCEnchanting;
import betterwithmods.util.DirUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSteelSaw extends ItemBlock {

    public ItemSteelSaw(Block block) {
        super(block);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);

        if (state.getBlock() == BWMBlocks.STEEL_AXLE) {
            EnumFacing.Axis axis = state.getValue(DirUtils.AXIS);
            worldIn.setBlockState(pos, BWMBlocks.STEEL_SAW.getDefaultState().withProperty(DirUtils.AXIS,axis).withProperty(IBlockActive.ACTIVE,false));
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return HCEnchanting.canEnchantSteel(enchantment);
    }
}
