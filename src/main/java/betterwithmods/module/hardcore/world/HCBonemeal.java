package betterwithmods.module.hardcore.world;

import betterwithmods.common.BWMItems;
import betterwithmods.common.BWMRecipes;
import betterwithmods.common.blocks.BlockPlanter;
import betterwithmods.common.items.ItemFertilizer;
import betterwithmods.module.Feature;
import betterwithmods.util.StackIngredient;
import betterwithmods.util.player.PlayerHelper;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by primetoxinz on 5/14/17.
 */
public class HCBonemeal extends Feature {
    public static StackIngredient FERTILIZERS = StackIngredient.fromStacks(new ItemStack(BWMItems.FERTILIZER), new ItemStack(Items.DYE, 1,EnumDyeColor.WHITE.getDyeDamage()));
    private static boolean removeBonemealRecipe;


    //TODO find a better solution to adding valid stacks to an ingredient.
    @Deprecated
    public static void registerFertilzier(ItemStack stack) {
        FERTILIZERS = StackIngredient.mergeStacked(Lists.newArrayList(FERTILIZERS, StackIngredient.fromStacks(stack)));
    }

    @Override
    public void setupConfig() {
        removeBonemealRecipe = loadPropBool("Remove Bonemeal Crafting Recipe", "Removes Bonemeal from Crafting Table", true);
    }

    @Override
    public String getFeatureDescription() {
        return "Removes the ability to instant-grow crops and trees with bonemeal";
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        if (removeBonemealRecipe)
            BWMRecipes.removeRecipe(new ItemStack(Items.DYE, 3, EnumDyeColor.WHITE.getDyeDamage()));
    }

    @SubscribeEvent
    public void onBonemeal(BonemealEvent e) {
        if (!PlayerHelper.isSurvival(e.getEntityPlayer()))
            return;
        if (!(e.getBlock().getBlock() instanceof BlockGrass) && !(e.getBlock().getBlock() instanceof BlockPlanter) && e.getBlock().getBlock() instanceof IGrowable) {
            IBlockState below = e.getWorld().getBlockState(e.getPos().down());
            below.getBlock().onBlockClicked(e.getWorld(), e.getPos().down(), e.getEntityPlayer());
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onItemUse(PlayerInteractEvent.RightClickBlock e) {
        ItemStack stack = e.getItemStack();

        if (!FERTILIZERS.apply(stack))
            return;

        World world = e.getWorld();
        BlockPos pos = e.getPos();
        Block block = world.getBlockState(pos).getBlock();
        EntityPlayer player = e.getEntityPlayer();

        if (block != null && block instanceof IPlantable) {
            Block below = world.getBlockState(pos.down()).getBlock();
            if (ItemFertilizer.processBlock(below, world, pos.down())) {
                if (!player.capabilities.isCreativeMode)
                    stack.shrink(1);
            }
        } else if (ItemFertilizer.processBlock(block, world, pos)) {
            if (!player.capabilities.isCreativeMode)
                stack.shrink(1);
        }
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }
}
