package betterwithmods.common.blocks;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.registry.KilnStructureManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockFireStoked extends BlockFire {
    public BlockFireStoked() {
        super();
        this.disableStats();
        this.setLightLevel(1.0F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0).withProperty(NORTH, Boolean.FALSE).withProperty(EAST, Boolean.FALSE).withProperty(SOUTH, Boolean.FALSE).withProperty(WEST, Boolean.FALSE).withProperty(UPPER, Boolean.FALSE));
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        entityIn.setFire(40);
    }

    @Override
    public int tickRate(World world) {
        return 52;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        world.scheduleBlockUpdate(pos, this, tickRate(world), 5);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!canPlaceBlockAt(world, pos))
            world.setBlockToAir(pos);

        BlockPos below = pos.down();
        Block belowBlock = world.getBlockState(below).getBlock();

        if (belowBlock == BWMBlocks.HIBACHI) {
            if (!belowBlock.isFireSource(world, below, EnumFacing.UP))
                world.setBlockToAir(pos);
        } else {
            world.setBlockState(pos, Blocks.FIRE.getDefaultState());
            return;
        }
        BlockPos above = pos.up();

        KilnStructureManager.createKiln(world,above);
        
        if (world.getBlockState(pos).getBlock() == BWMBlocks.STOKED_FLAME) { //Attempt at alleviating an OptiFine crash
            int meta = world.getBlockState(pos).getValue(AGE);

            if (meta < 15) {
                meta++;
                world.setBlockState(pos, world.getBlockState(pos).withProperty(AGE, meta));
            }

            if (world.getGameRules().getBoolean("doFireTick")) {
                boolean flag1 = world.isBlockinHighHumidity(pos);

                this.setFire(world, pos.east(), 300, rand, meta, EnumFacing.WEST);
                this.setFire(world, pos.west(), 300, rand, meta, EnumFacing.EAST);
                this.setFire(world, pos.down(), 250, rand, meta, EnumFacing.UP);
                this.setFire(world, pos.up(), 250, rand, meta, EnumFacing.DOWN);
                this.setFire(world, pos.north(), 300, rand, meta, EnumFacing.SOUTH);
                this.setFire(world, pos.south(), 300, rand, meta, EnumFacing.NORTH);
                for (int i1 = -1; i1 <= 1; ++i1) {
                    for (int j1 = 1; j1 <= 1; ++j1) {
                        for (int k1 = 1; k1 <= 4; ++k1) {
                            int l1 = 100;
                            if (k1 > 1) {
                                l1 += (k1 - (1)) * 100;
                            }
                            BlockPos blockpos = pos.add(i1, k1, j1);
                            int i2 = this.getNeighborEncouragement(world, blockpos);
                            if (i2 > 0) {
                                int j2 = (i2 + 40 + world.getDifficulty().getId() * 7) / (meta + 30);
                                if (flag1) {
                                    j2 /= 2;
                                }
                                if (j2 > 0 && rand.nextInt(l1) <= j2 && (!world.isRaining() || !this.canDie(world, blockpos))) {
                                    int k2 = meta + rand.nextInt(5) / 4;
                                    if (k2 > 15) {
                                        k2 = 15;
                                    }
                                    world.setBlockState(blockpos, Blocks.FIRE.getDefaultState().withProperty(AGE, k2));
                                }
                            }
                        }
                    }
                }
            }

            if (meta >= 3) {
                world.setBlockState(pos, Blocks.FIRE.getDefaultState().withProperty(AGE, 15), 3);
                return;
            }

            world.scheduleBlockUpdate(pos, this, tickRate(world) + world.rand.nextInt(10), 5);
        }
    }

    public int getNeighborEncouragement(World worldIn, BlockPos pos)
    {
        if (!worldIn.isAirBlock(pos))
        {
            return 0;
        }
        else
        {
            int i = 0;

            for (EnumFacing enumfacing : EnumFacing.values())
            {
                i = Math.max(worldIn.getBlockState(pos.offset(enumfacing)).getBlock().getFireSpreadSpeed(worldIn, pos.offset(enumfacing), enumfacing.getOpposite()), i);
            }

            return i;
        }
    }


    private void setFire(World worldIn, BlockPos pos, int chance, Random random, int age, EnumFacing face) {
        int i = worldIn.getBlockState(pos).getBlock().getFlammability(worldIn, pos, face);

        if (random.nextInt(chance) < i) {
            IBlockState iblockstate = worldIn.getBlockState(pos);

            if (random.nextInt(age + 10) < 5 && !worldIn.isRainingAt(pos)) {
                int j = age + random.nextInt(5) / 4;

                if (j > 15) {
                    j = 15;
                }

                worldIn.setBlockState(pos, Blocks.FIRE.getDefaultState().withProperty(AGE, j), 3);
            } else {
                worldIn.setBlockToAir(pos);
            }

            if (iblockstate.getBlock() == Blocks.TNT) {
                Blocks.TNT.onPlayerDestroy(worldIn, pos, iblockstate.withProperty(BlockTNT.EXPLODE, true));
            }
        }
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AGE, NORTH, EAST, SOUTH, WEST, UPPER);
    }
}
