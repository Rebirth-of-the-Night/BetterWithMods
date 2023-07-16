package betterwithmods.module.industry;

/*
import betterwithmods.common.blocks.BlockUrn;
import betterwithmods.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

import javax.annotation.Nullable;

/**
 * Created by primetoxinz on 7/25/17.
 */
/*
public class WeatherBeaconEffect implements IBeaconEffect {
    private static final int MAX_WATER_CONSUMPTION = 64;

    @Override
    public int getTickSpeed() {
        return 10;
    }

    @Override
    public void effect(World world, BlockPos pos, int level) {
        if (world.isThundering()) {

        }
    }

    @Override
    public boolean processInteractions(World world, BlockPos pos, int level, EntityPlayer player, ItemStack stack) {
        if (stack.isItemEqual(BlockUrn.getStack(BlockUrn.EnumType.FULL, 1))) {

            BlockPos origin = findClosestWater(world, pos, level + 1);
            BlockPos next;
            int consume = 0;
            if (origin != null) {
                if (consumeWater(world, origin))
                    consume++;
                while (consume < MAX_WATER_CONSUMPTION) {
                    System.out.println(consume);
                    next = findNeighboringWater(world, origin);
                    if (next != null) {
                        if (consumeWater(world, next)) {
                            origin = next;
                            consume++;
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (consume > 0) {
                    int scale = 200;
                    WorldInfo worldinfo = world.getWorldInfo();
                    int time = consume * scale;
                    //TODO more variety per level.
                    switch (level) {
                        case 1:
                        case 2:
                            worldinfo.setCleanWeatherTime(0);
                            worldinfo.setThunderTime(time);
                            worldinfo.setRaining(true);
                            worldinfo.setThundering(false);
                            worldinfo.setRainTime(time);
                        case 3:
                        case 4:
                            worldinfo.setCleanWeatherTime(0);
                            worldinfo.setThunderTime(time);
                            worldinfo.setRaining(true);
                            worldinfo.setThundering(true);
                            worldinfo.setRainTime(time);
                    }

                }
                stack.shrink(1);
                return true;
            }
            return false;
        }
        return false;
    }

    @Nullable
    public BlockPos findClosestWater(World world, BlockPos pos, int level) {
        int r;
        for (r = 0; r <= level; r++) {
            for (int x = -(r + 1); x <= (r + 1); x++) {
                for (int z = -(r + 1); z <= (r + 1); z++) {
                    BlockPos p = pos.add(x, -r, z);
                    if (WorldUtils.isWaterSource(world, p)) {
                        return p;
                    }
                }
            }
        }
        return null;
    }


    @Nullable
    public BlockPos findNeighboringWater(World world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (WorldUtils.isWater(world, pos.offset(facing))) {
                return pos.offset(facing);
            }
        }
        return null;
    }

    public boolean consumeWater(World world, BlockPos pos) {
        if (WorldUtils.isWater(world, pos)) {
            world.setBlockToAir(pos);
            world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.BLOCKS, 0.75f, world.rand.nextFloat() * 0.1F + 0.45F);
            return true;
        }
        return false;
    }


}
*/