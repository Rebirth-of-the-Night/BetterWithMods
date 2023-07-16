package betterwithmods.common.blocks;

import betterwithmods.api.block.IMultiVariants;
import betterwithmods.util.DirUtils;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

public class BlockWicker extends BlockPane implements IMultiVariants {


    public BlockWicker() {
        super(Material.WOOD);
        this.setHardness(2.0F);
        this.setDefaultState(getDefaultState());
        this.setSoundType(SoundType.WOOD);
    }
    
    @Override
    public String[] getVariants() {
        return new String[]{"east=false,north=true,south=true,west=false"};
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState();
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DirUtils.SOUTH, DirUtils.EAST, DirUtils.NORTH, DirUtils.WEST);
    }

}
