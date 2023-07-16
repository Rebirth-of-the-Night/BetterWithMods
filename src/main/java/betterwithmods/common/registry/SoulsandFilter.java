package betterwithmods.common.registry;

import betterwithmods.BWMod;
import betterwithmods.common.blocks.mechanical.tile.TileEntityFilteredHopper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class SoulsandFilter extends HopperFilter{

    public SoulsandFilter(Ingredient filter, List<Ingredient> filtered) {
        super(BWMod.MODID+":soul_sand", filter, filtered);
    }

    @Override
    public void onInsert(World world, BlockPos pos, TileEntityFilteredHopper tile, Entity entity) {
        if (entity instanceof EntityXPOrb && !tile.isXPFull()) {
            EntityXPOrb orb = (EntityXPOrb) entity;
            int remaining = tile.getMaxExperienceCount() - tile.getExperienceCount();
            int value = orb.getXpValue();
            if (remaining > 0) {
                if (value <= remaining) {
                    tile.setExperienceCount(tile.getExperienceCount()+value);
                    orb.setDead();
                    return;
                }
                orb.xpValue -= remaining;
                tile.setExperienceCount(tile.getMaxExperienceCount());
            }
        }
    }
}
