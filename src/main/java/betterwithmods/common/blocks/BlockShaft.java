package betterwithmods.common.blocks;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Set;

public class BlockShaft extends BlockStickBase {
    public BlockShaft() {
        super(Material.WOOD);
    }

    @Override
    public IBlockState getConnections(IBlockState state, IBlockAccess world, BlockPos pos) {
        IBlockState above = world.getBlockState(pos.up());
        Block block = above.getBlock();
        if (block instanceof BlockTorch || block instanceof BlockSkull || block instanceof BlockCandle) {
            return state.withProperty(CONNECTION, Connection.CONNECTED);
        }

        return state;
    }

    @Override
    public double getHeight(IBlockState state) {
        Connection c = state.getValue(CONNECTION);
        return c == Connection.DISCONNECTED ? 12d / 16d : 1;
    }

    private static final Set<Material> grounds = Sets.newHashSet(Material.GRASS, Material.SAND, Material.GROUND, Material.SNOW);

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        Material material = worldIn.getBlockState(pos.down()).getMaterial();
        return grounds.contains(material);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return Block.NULL_AABB;
    }


}
