package betterwithmods.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by primetoxinz on 6/24/17.
 */
public abstract class BlockRotate extends BWMBlock {
    public BlockRotate(Material material) {
        super(material);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(I18n.format("tooltip.rotate_with_hand.name"));
        super.addInformation(stack, player, tooltip, advanced);
    }

    public abstract void nextState(World world, BlockPos pos, IBlockState state);

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        boolean emptyHands = player.getHeldItem(EnumHand.MAIN_HAND).isEmpty() && player.getHeldItem(EnumHand.OFF_HAND).isEmpty() && player.isSneaking();

        if (emptyHands) {
            world.playSound(null, pos, this.getSoundType(state, world, pos, player).getPlaceSound(), SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
            nextState(world, pos, state);
            world.notifyNeighborsOfStateChange(pos, this, false);
            world.scheduleBlockUpdate(pos, this, 10, 5);
            return true;
        }
        return false;
    }
}
