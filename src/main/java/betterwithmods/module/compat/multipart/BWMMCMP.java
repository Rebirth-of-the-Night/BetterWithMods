package betterwithmods.module.compat.multipart;

import betterwithmods.common.BWMBlocks;
import betterwithmods.module.gameplay.miniblocks.ItemMini;
import betterwithmods.module.gameplay.miniblocks.MiniType;
import betterwithmods.module.gameplay.miniblocks.blocks.BlockMini;
import betterwithmods.module.gameplay.miniblocks.blocks.BlockSiding;
import mcmultipart.api.addon.IMCMPAddon;
import mcmultipart.api.addon.IWrappedBlock;
import mcmultipart.api.addon.MCMPAddon;
import mcmultipart.api.multipart.IMultipart;
import mcmultipart.api.multipart.IMultipartRegistry;
import mcmultipart.api.slot.EnumCenterSlot;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@MCMPAddon
public class BWMMCMP implements IMCMPAddon {

    @Override
    public void registerParts(IMultipartRegistry registry) {
        // MiniBlocks.MINI_MATERIAL_BLOCKS.keySet()
        // for (MiniType type : Sets.newHashSet(MiniType.SIDING)) {
        //     for (BlockMini block : MiniBlocks.MINI_MATERIAL_BLOCKS.get(type).values()) {
        //         IMultipart part = fromType(block, type);
        //         if (part != null) {
        //             register(registry, block, part).setBlockPlacementLogic(this::placeSiding);
        //         }
        //     }
        // }

        register(registry, BWMBlocks.WOODEN_AXLE, new MultipartProxy(BWMBlocks.WOODEN_AXLE, state -> EnumCenterSlot.CENTER));
        register(registry, BWMBlocks.STEEL_AXLE, new MultipartProxy(BWMBlocks.STEEL_AXLE, state -> EnumCenterSlot.CENTER));
    }

    @SuppressWarnings("unused")
    private boolean placeSiding(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!stack.isEmpty() && stack.getItem() instanceof ItemMini && player.canPlayerEdit(pos, facing, stack) && world.mayPlace(newState.getBlock(), pos, false, facing, (Entity) null)) {

            if (ItemMini.placeBlockAt((ItemMini) stack.getItem(), stack, player, world, pos, facing, hitX, hitY, hitZ, newState)) {
                newState = world.getBlockState(pos);
                SoundType soundtype = newState.getBlock().getSoundType(newState, world, pos, player);
                world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                if (!player.isCreative())
                    stack.shrink(1);
            }
            return true;
        }
        return false;
    }

    public IWrappedBlock register(IMultipartRegistry registry, Block block, IMultipart proxy) {
        registry.registerPartWrapper(block, proxy);
        return registry.registerStackWrapper(Item.getItemFromBlock(block), i -> true, block);
    }

    public IMultipart fromType(BlockMini mini, MiniType type) {
        switch (type) {
            case SIDING:
                return new MultipartSiding((BlockSiding) mini);
            case MOULDING:
                break;
            case CORNER:
                break;
            case UNKNOWN:
                break;
        }
        return null;
    }
}
