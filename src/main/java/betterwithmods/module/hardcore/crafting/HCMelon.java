package betterwithmods.module.hardcore.crafting;

import betterwithmods.common.entity.EntityFallingGourd;
import betterwithmods.module.Feature;
import betterwithmods.util.player.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockMelon;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class HCMelon extends Feature {
    @Override
    public String getFeatureDescription() {
        return "Makes Melons have gravity, makes for cool automation abilities";
    }

    @Override
    public boolean requiresMinecraftRestartToEnable() {
        return true;
    }

    @Override
    public void init(FMLInitializationEvent event) {
        Blocks.MELON_STEM.setHardness(0.2F);
        Blocks.PUMPKIN_STEM.setHardness(0.2F);

        Blocks.MELON_BLOCK.setHarvestLevel("axe", 1);
        Blocks.PUMPKIN.setHarvestLevel("axe", 1);
    }

    @SubscribeEvent
    public void onHarvest(BlockEvent.HarvestDropsEvent event) {
        Block block = event.getState().getBlock();

        //Require an axe for melons and pumpkins
        if (block.equals(Blocks.MELON_BLOCK) || block.equals(Blocks.PUMPKIN)) {
            if (event.isSilkTouching() || event.getResult().equals(Event.Result.DENY))
                return;

            //Drop melon blocks when harvesting
            if (event.getDrops().removeIf(x -> x.getItem().equals(Items.MELON)))
                event.getDrops().add(new ItemStack(Blocks.MELON_BLOCK, 1));

            if (!PlayerHelper.isCurrentToolEffectiveOnBlock(event.getHarvester(), event.getPos(), event.getState())) {
                event.getDrops().clear();
                if (block.equals(Blocks.MELON_BLOCK)) {
                    event.getDrops().add(new ItemStack(Items.MELON_SEEDS));
                } else {
                    event.getDrops().add(new ItemStack(Items.PUMPKIN_SEEDS));
                }
            }
        }

        if (block.equals(Blocks.MELON_STEM) || block.equals(Blocks.PUMPKIN_STEM)) {
            event.getDrops().clear();
        }


    }

    @SubscribeEvent
    public void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
        World world = event.getWorld();
        makeGourdFall(world, event.getPos());
        makeGourdFall(world, event.getPos().up());
    }

    private void makeGourdFall(World world, BlockPos pos) {
        IBlockState blockstate = world.getBlockState(pos);
        Block block = blockstate.getBlock();
        if (block instanceof BlockMelon || block instanceof BlockPumpkin) {
            BlockPos bottompos = pos.down();
            IBlockState bottomstate = world.getBlockState(bottompos);
            if (world.isAirBlock(bottompos) || BlockFalling.canFallThrough(bottomstate)) {
                if (!world.isRemote && world.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32))) {
                    EntityFallingGourd entitygourd = new EntityFallingGourd(world, (double) pos.getX() + 0.5, pos.getY(), (double) pos.getZ() + 0.5, blockstate);
                    if (block instanceof BlockPumpkin) entitygourd.setSeedStack(new ItemStack(Items.PUMPKIN_SEEDS));
                    else entitygourd.setSeedStack(new ItemStack(Items.MELON_SEEDS));
                    world.spawnEntity(entitygourd);
                }
            }
        }
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }
}
