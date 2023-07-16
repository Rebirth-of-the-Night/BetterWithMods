package betterwithmods.common.blocks;

import betterwithmods.common.BWMBlocks;
import betterwithmods.module.tweaks.MushroomFarming;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

@Deprecated
public class BlockMushroom extends net.minecraft.block.BlockMushroom {
    int maxLightLevel;

    public BlockMushroom(int maxLightLevel) {
        super();
        this.maxLightLevel = maxLightLevel;
        setHardness(0.0F);
        setSoundType(SoundType.PLANT);
        setTranslationKey("mushroom");
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        IBlockState soil = worldIn.getBlockState(pos.down());
        if(worldIn.getLight(pos) <= maxLightLevel || MushroomFarming.SPREAD_ON_MYCELLIUM && MushroomFarming.isMushroomSoil(soil)) {
            int growthChance = MushroomFarming.GROW_FAST_ON_DUNG && isDung(soil) ? 12 : 25;
            if (rand.nextInt(growthChance) == 0) {
                int max_mushrooms = 5;
                int tries = 4;

                for (BlockPos checkpos : BlockPos.getAllInBoxMutable(pos.add(-4, -1, -4), pos.add(4, 1, 4))) {
                    if (worldIn.getBlockState(checkpos).getBlock() == this) {
                        --max_mushrooms;

                        if (max_mushrooms <= 0) {
                            return;
                        }
                    }
                }

                BlockPos grow_pos = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);

                for (int k = 0; k < tries; ++k) {
                    if (worldIn.isAirBlock(grow_pos) && this.canBlockStay(worldIn, grow_pos, this.getDefaultState())) {
                        pos = grow_pos;
                    }

                    grow_pos = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);
                }

                if (worldIn.isAirBlock(grow_pos) && this.canBlockStay(worldIn, grow_pos, this.getDefaultState())) {
                    worldIn.setBlockState(grow_pos, this.getDefaultState(), 3);
                }
            }
        }
    }

    private boolean isDung(IBlockState state)
    {
        return state.getBlock() == BWMBlocks.AESTHETIC && state.getValue(BlockAesthetic.TYPE) == BlockAesthetic.EnumType.DUNG;
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        if (pos.getY() >= 0 && pos.getY() < 256)
        {
            IBlockState soil = worldIn.getBlockState(pos.down());

            if(MushroomFarming.isMushroomSoil(soil))
                return true;
            else
                return worldIn.getLight(pos) <= maxLightLevel && soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), EnumFacing.UP, this);
        }
        else
        {
            return false;
        }
    }
}
