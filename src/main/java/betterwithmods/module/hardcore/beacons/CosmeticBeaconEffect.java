package betterwithmods.module.hardcore.beacons;

import betterwithmods.common.registry.block.recipe.BlockIngredient;
import betterwithmods.util.ColorUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CosmeticBeaconEffect extends BeaconEffect {

    private Map<BlockPos, float[]> colorCache;

    public CosmeticBeaconEffect(String name, BlockIngredient structureBlock) {
        super(name, structureBlock, EntityLivingBase.class);
        this.colorCache = new HashMap<>();
        this.setBaseBeamColor(Color.white);
    }

    @Override
    public NonNullList<EntityLivingBase> getEntitiesInRange(World world, BlockPos pos, int beaconLevel) {
        return NonNullList.create();
    }

    @Override
    public void onBeaconCreate(@Nonnull World world, @Nonnull BlockPos pos, int beaconLevel) {

        float[] colors = null;
        for (int r = 1; r <= beaconLevel; r++) {
            for (int x = -r; x <= r; x++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos glassPos = new BlockPos(pos.getX() + x, pos.getY() - r, pos.getZ() + z);
                    if (colors == null)
                        colors = ColorUtils.getColorFromBlock(world, glassPos, pos);
                    else
                        colors = ColorUtils.average(colors, ColorUtils.getColorFromBlock(world, glassPos, pos));
                }
            }
        }
        this.colorCache.put(pos, colors);
    }

    @Override
    public float[] getBaseBeaconBeamColor(BlockPos beaconPos) {
        return colorCache.containsKey(beaconPos) ? colorCache.get(beaconPos) : super.getBaseBeaconBeamColor(beaconPos);
    }

    @Override
    public void apply(NonNullList<EntityLivingBase> entitiesInRange, @Nonnull World world, @Nonnull BlockPos pos, int beaconLevel) {

    }

    @Override
    public boolean onPlayerInteracted(World world, BlockPos pos, int level, EntityPlayer player, EnumHand hand, ItemStack stack) {
        return false;
    }

    @Override
    public void onBeaconBreak(World world, BlockPos pos, int level) {

    }
}
