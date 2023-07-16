package betterwithmods.module.gameplay;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWMItems;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.blocks.*;
import betterwithmods.common.blocks.mechanical.BlockCookingPot;
import betterwithmods.common.registry.KilnStructureManager;
import betterwithmods.common.registry.heat.BWMHeatRegistry;
import betterwithmods.module.Feature;
import betterwithmods.module.ModuleLoader;
import betterwithmods.module.hardcore.needs.HCCooking;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by primetoxinz on 5/16/17.
 */
public class KilnRecipes extends Feature {
    public KilnRecipes() {
        canDisable = false;
    }

    @Override
    public void init(FMLInitializationEvent event) {

        BWRegistry.KILN.addStokedRecipe(BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.CRUCIBLE), BlockCookingPot.getStack(BlockCookingPot.EnumType.CRUCIBLE));
        BWRegistry.KILN.addStokedRecipe(BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.PLANTER), BlockPlanter.getStack(BlockPlanter.EnumType.EMPTY));
        BWRegistry.KILN.addStokedRecipe(BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.URN), BlockUrn.getStack(BlockUrn.EnumType.EMPTY, 1));
        BWRegistry.KILN.addStokedRecipe(BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.VASE), BlockVase.getStack(EnumDyeColor.WHITE));
        BWRegistry.KILN.addStokedRecipe(BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.BRICK), new ItemStack(Items.BRICK));
        BWRegistry.KILN.addStokedRecipe(BlockUnfiredPottery.getStack(BlockUnfiredPottery.EnumType.NETHER_BRICK), new ItemStack(Items.NETHERBRICK));
        
        BWRegistry.KILN.addStokedRecipe(new ItemStack(Blocks.CLAY), new ItemStack(Blocks.HARDENED_CLAY));
        BWRegistry.KILN.addStokedRecipe(new ItemStack(BWMBlocks.NETHER_CLAY), BlockAesthetic.getStack(BlockAesthetic.EnumType.NETHERCLAY));

        int foodModifier = ModuleLoader.isFeatureEnabled(HCCooking.class) ? 1 : 2;
        BWRegistry.KILN.addStokedRecipe(BlockRawPastry.getStack(BlockRawPastry.EnumType.CAKE), IntStream.range(0,foodModifier).mapToObj(i -> new ItemStack(Items.CAKE)).collect(Collectors.toList()));
        BWRegistry.KILN.addStokedRecipe(BlockRawPastry.getStack(BlockRawPastry.EnumType.BREAD), new ItemStack(Items.BREAD,foodModifier));
        BWRegistry.KILN.addStokedRecipe(BlockRawPastry.getStack(BlockRawPastry.EnumType.COOKIE), new ItemStack(Items.COOKIE, 8*foodModifier));
        BWRegistry.KILN.addStokedRecipe(BlockRawPastry.getStack(BlockRawPastry.EnumType.PUMPKIN), new ItemStack(Items.PUMPKIN_PIE, foodModifier));
        BWRegistry.KILN.addStokedRecipe(BlockRawPastry.getStack(BlockRawPastry.EnumType.APPLE), new ItemStack(BWMItems.APPLE_PIE, foodModifier));

    }

    @SubscribeEvent
    public void formKiln(BlockEvent.NeighborNotifyEvent event) {
        BlockPos up = event.getPos().up();
        World world = event.getWorld();
        if (KilnStructureManager.isKilnBlock(world.getBlockState(up)) && BWMHeatRegistry.getHeat(world, event.getPos()) > 0) {
            KilnStructureManager.createKiln(world, up);
        }
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public void onKilnPlace(BlockEvent.PlaceEvent event) {
        if (event.getPlacedBlock().getBlock() != Blocks.AIR)
            KilnStructureManager.createKiln(event.getWorld(), event.getPos());
    }


    @Override
    public boolean hasSubscriptions() {
        return true;
    }
}

