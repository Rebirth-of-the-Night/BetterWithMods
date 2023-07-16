package betterwithmods.module.hardcore.beacons;

import betterwithmods.common.blocks.tile.TileEnderchest;
import betterwithmods.common.registry.block.recipe.BlockIngredient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class EnderBeaconEffect extends BeaconEffect {

    public EnderBeaconEffect() {
        super("ender", new BlockIngredient("blockEnder"), EntityPlayer.class);
    }

    @Override
    public void onBeaconCreate(@Nonnull World world, @Nonnull BlockPos pos, int beaconLevel) {
    }

    @Override
    public void apply(NonNullList<EntityLivingBase> entitiesInRange, @Nonnull World world, @Nonnull BlockPos pos, int beaconLevel) {
        for (int r = -1; r < beaconLevel; r++) {
            for (int x = -(r + 1); x <= (r + 1); x++) {
                for (int z = -(r + 1); z <= (r + 1); z++) {
                    if (Math.abs(x) > r || Math.abs(z) > r) {
                        BlockPos p = pos.add(x, -r, z);
                        TileEntity tile = world.getTileEntity(p);
                        if (tile instanceof TileEnderchest) {
                            TileEnderchest.Type type = TileEnderchest.Type.VALUES[r + 1];
                            if (type != null) {
                                ((TileEnderchest) tile).setType(type);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onPlayerInteracted(World world, BlockPos pos, int level, EntityPlayer player, EnumHand hand, ItemStack stack) {
        return false;
    }

    @Override
    public void onBeaconBreak(World world, BlockPos pos, int level) {
        for (int r = 0; r <= level; r++) {
            for (int x = -(r + 1); x <= (r + 1); x++) {
                for (int z = -(r + 1); z <= (r + 1); z++) {
                    if (Math.abs(x) > r || Math.abs(z) > r) {
                        BlockPos p = pos.add(x, -r, z);
                        TileEntity tile = world.getTileEntity(p);
                        if (tile instanceof TileEnderchest) {
                            ((TileEnderchest) tile).setType(TileEnderchest.Type.NONE);
                        }
                    }
                }
            }
        }
    }


}

