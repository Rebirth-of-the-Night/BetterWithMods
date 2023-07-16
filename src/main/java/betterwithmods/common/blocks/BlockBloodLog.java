package betterwithmods.common.blocks;

import betterwithmods.client.BWCreativeTabs;
import betterwithmods.common.BWSounds;
import betterwithmods.common.world.gen.feature.WorldGenBloodTree;
import betterwithmods.util.DirUtils;
import net.minecraft.block.BlockLog;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockBloodLog extends BlockLog {
    public static final PropertyBool EXPANDABLE = PropertyBool.create("expandable");

    public static final SoundType BLOODWOOD = new SoundType(1.0F, 1.0F, BWSounds.BLOODWOOD_BREAK, SoundEvents.BLOCK_WOOD_STEP, SoundEvents.BLOCK_WOOD_PLACE, SoundEvents.BLOCK_WOOD_HIT, SoundEvents.BLOCK_WOOD_FALL);

    public BlockBloodLog() {
        this.setDefaultState(this.blockState.getBaseState().withProperty(LOG_AXIS, EnumAxis.Y).withProperty(EXPANDABLE, false));
        this.setTickRandomly(true);
        this.setCreativeTab(BWCreativeTabs.BWTAB);
        this.setSoundType(SoundType.SLIME);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        world.playSound(null, pos, SoundEvents.ENTITY_GHAST_HURT, SoundCategory.BLOCKS, 1f,0.2f);
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        return false;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote && world.provider.isNether() && state.getValue(EXPANDABLE)) {

            for (EnumFacing face : DirUtils.NOT_DOWN) {
                if (rand.nextInt(20) == 0)
                    expandTree(world, pos, face);
            }
        }
    }

    private void expandTree(World world, BlockPos pos, EnumFacing facing) {
        WorldGenBloodTree tree = new WorldGenBloodTree();
        tree.generateBranch(world, pos, facing);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = this.getDefaultState().withProperty(EXPANDABLE, (meta & 3) % 4 == 1);
        switch (meta & 12)
        {
            case 0:
                state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y);
                break;
            case 4:
                state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.X);
                break;
            case 8:
                state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z);
                break;
            default:
                state = state.withProperty(LOG_AXIS, BlockLog.EnumAxis.NONE);
        }
        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(EXPANDABLE) ? 1 : 0;

        switch (state.getValue(LOG_AXIS))
        {
            case X:
                meta |= 4;
                break;
            case Z:
                meta |= 8;
                break;
            default:
                meta |= 12;
        }
        return meta;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, EXPANDABLE, LOG_AXIS);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {


        worldIn.setBlockState(pos, state.withProperty(EXPANDABLE, true));
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }
}
