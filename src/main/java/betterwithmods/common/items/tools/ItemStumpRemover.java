package betterwithmods.common.items.tools;

import betterwithmods.client.BWCreativeTabs;
import betterwithmods.common.items.ItemAltName;
import betterwithmods.module.gameplay.Gameplay;
import betterwithmods.module.hardcore.world.stumping.HCStumping;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author Koward
 */
public class ItemStumpRemover extends ItemAltName {
    public ItemStumpRemover() {
        super();
        this.setCreativeTab(BWCreativeTabs.BWTAB);
        this.setMaxDamage(0);
        this.setHasSubtypes(false);
        this.maxStackSize = 16;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (playerIn != null) {
            ItemStack stack = playerIn.getHeldItem(hand);
            if (!playerIn.canPlayerEdit(pos, facing, stack)) {
                return EnumActionResult.FAIL;
            } else if (stack.getCount() == 0) {
                return EnumActionResult.FAIL;
            } else {
                IBlockState state = worldIn.getBlockState(pos);
                IBlockState below = worldIn.getBlockState(pos.down());
                if (worldIn.isAirBlock(pos.up()) && HCStumping.isLog(state) && HCStumping.isSoil(below, worldIn, pos.down())) {
                    if (!worldIn.isRemote) {
                        worldIn.playSound(null, pos, SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.BLOCKS, 1, 1);
                        state.getBlock().harvestBlock(worldIn, playerIn, pos, state, null, stack);
                        worldIn.setBlockToAir(pos);
                    }

                    stack.shrink(1);
                    return EnumActionResult.SUCCESS;
                } else {
                    return EnumActionResult.FAIL;
                }
            }
        }
        return EnumActionResult.FAIL;
    }

    @Override
    public String[] getLocations() {
        if (Gameplay.kidFriendly)
            return new String[]{"stump_remover_kf"};
        else
            return new String[]{"stump_remover"};
    }
}
