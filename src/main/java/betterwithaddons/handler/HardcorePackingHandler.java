package betterwithaddons.handler;

import betterwithaddons.crafting.ICraftingResult;
import betterwithaddons.crafting.manager.CraftingManagerPacking;
import betterwithaddons.crafting.recipes.PackingRecipe;
import betterwithaddons.util.ItemUtil;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HardcorePackingHandler {
    Set<TileEntityPiston> activePistons =  Collections.synchronizedSet(new HashSet<>());

    @SubscribeEvent
    public void hardcorePackingInit(AttachCapabilitiesEvent<TileEntity> event) {
        TileEntity te = event.getObject();
        if (te instanceof TileEntityPiston) {
            activePistons.add((TileEntityPiston) te);
        }
    }

    @SubscribeEvent
    public void hardcorePackingCompress(TickEvent.WorldTickEvent event) {
        if(event.world == null || event.world.isRemote)
            return;
        TileEntityPiston[] toIterate = activePistons.toArray(new TileEntityPiston[activePistons.size()]);
        HashSet<TileEntityPiston> toRemove = new HashSet<>();
        for (TileEntityPiston piston: toIterate) {
            World world = piston.getWorld();
            toRemove.add(piston);

            if(world != null && !world.isRemote && piston != null && piston.isExtending())
            {
                EnumFacing facing = piston.getFacing();
                BlockPos shovePos = piston.getPos();
                // IBlockState shoveState = piston.getPistonState();

                BlockPos compressPos = shovePos.offset(facing);
                IBlockState compressState = world.getBlockState(compressPos);
                if(isEmpty(world, compressPos, compressState) && isSurrounded(world,compressPos,facing.getOpposite())) {
                    AxisAlignedBB blockMask = new AxisAlignedBB(shovePos).union(new AxisAlignedBB(compressPos));
                    List<EntityItem> entities = world.getEntitiesWithinAABB(EntityItem.class, blockMask);
                    List<ItemStack> stacks = entities.stream().map(EntityItem::getItem).collect(Collectors.toList());
                    for (PackingRecipe recipe : CraftingManagerPacking.getInstance().getRecipes()) {
                        if (recipe.consume(stacks,compressState,true)) {
                            ICraftingResult result = recipe.getOutput(stacks, compressState);
                            result.apply(world,compressPos);
                            result.spawnItems(world, new Vec3d(compressPos).add(0.5,0.5,0.5));
                            recipe.consume(stacks,compressState,false);
                            ItemUtil.consumeItems(entities);
                        }
                    }
                }
        }
        }

        activePistons.removeAll(toRemove);
    }

    public boolean isEmpty(World world, BlockPos shovePos, IBlockState shoveState) {
        return shoveState.getBlock().isAir(shoveState,world,shovePos) || shoveState.getBlock().isReplaceable(world,shovePos);
    }

    public boolean isSurrounded(World world, BlockPos pos, EnumFacing except)
    {
        for (EnumFacing facing : EnumFacing.VALUES) {
            if(facing == except)
                continue;
            BlockPos wallPos = pos.offset(facing);
            IBlockState wallState = world.getBlockState(wallPos);
            if(wallState.getBlockFaceShape(world, wallPos,facing.getOpposite()) != BlockFaceShape.SOLID)
                return false;
        }

        return true;
    }
}
