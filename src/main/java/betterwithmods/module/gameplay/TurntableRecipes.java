package betterwithmods.module.gameplay;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.blocks.BlockUnfiredPottery;
import betterwithmods.common.registry.TurntableRotationManager;
import betterwithmods.module.Feature;
import com.google.common.collect.Lists;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/**
 * Created by primetoxinz on 5/16/17.
 */
public class TurntableRecipes extends Feature {
    public TurntableRecipes() {
        canDisable = false;
    }

    @Override
    public void init(FMLInitializationEvent event) {

        BWRegistry.TURNTABLE.addDefaultRecipe(new ItemStack(Blocks.CLAY), BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.CRUCIBLE), Lists.newArrayList(new ItemStack(Items.CLAY_BALL)));
        BWRegistry.TURNTABLE.addDefaultRecipe(BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.CRUCIBLE), BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.PLANTER));
        BWRegistry.TURNTABLE.addDefaultRecipe(BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.PLANTER), BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.VASE), Lists.newArrayList(new ItemStack(Items.CLAY_BALL)));
        BWRegistry.TURNTABLE.addDefaultRecipe(BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.VASE), BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.URN), Lists.newArrayList(new ItemStack(Items.CLAY_BALL)));
        BWRegistry.TURNTABLE.addDefaultRecipe(BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.URN), Blocks.AIR.getDefaultState(), Lists.newArrayList(new ItemStack(Items.CLAY_BALL)));


        TurntableRotationManager.addAttachment(b -> b instanceof BlockTorch);
        TurntableRotationManager.addAttachment(b -> b instanceof BlockLever);
        TurntableRotationManager.addAttachment(b -> b instanceof BlockLadder);
        TurntableRotationManager.addAttachment(b -> b instanceof BlockButton);
        TurntableRotationManager.addAttachment(b -> b instanceof BlockWallSign);
        TurntableRotationManager.addAttachment(b -> b instanceof BlockTripWireHook);

        TurntableRotationManager.addRotationHandler(block -> block instanceof BlockTorch, (world, pos) -> {
            IBlockState state = world.getBlockState(pos);
            return state.getValue(BlockTorch.FACING).getAxis().isVertical();
        });
        TurntableRotationManager.addRotationHandler(Blocks.LEVER, (world, pos) -> {
            IBlockState state = world.getBlockState(pos);
            return state.getValue(BlockLever.FACING).getFacing().getAxis().isVertical();
        });
        TurntableRotationManager.addRotationHandler(BWMBlocks.SAW, new AdvancedRotation());
        TurntableRotationManager.addRotationHandler(BWMBlocks.BELLOWS, new AdvancedRotation());
        TurntableRotationManager.addRotationBlacklist(block -> block instanceof BlockPistonExtension);
        TurntableRotationManager.addRotationHandler(block -> block instanceof BlockPistonBase, (world, pos) -> !world.getBlockState(pos).getValue(BlockPistonBase.EXTENDED));
    }

    private static class AdvancedRotation implements TurntableRotationManager.IRotation {
        @Override
        public boolean isValid(World world, BlockPos pos) {
            return true;
        }

        @Override
        public boolean rotate(World world, BlockPos pos, Rotation rotation) {
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            return block.rotateBlock(world,pos, EnumFacing.UP);
        }
    }
}
