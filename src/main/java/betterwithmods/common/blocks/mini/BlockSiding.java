package betterwithmods.common.blocks.mini;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

@Deprecated
public class BlockSiding extends BlockMini {
    
    public BlockSiding(Material mat) {
        super(mat);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0).withProperty(getOrientationProperty(), 0));
    }

    @Override
    public PropertyOrientation getOrientationProperty() {
        return SIDING_ORIENTATION;
    }

    @Override
    public IBlockState getStateForAdvancedRotationPlacement(IBlockState defaultState, EnumFacing facing, float hitX, float hitY, float hitZ) {
        PropertyInteger facingProperty = getOrientationProperty();
        IBlockState state = defaultState;
        float hitXFromCenter = hitX - 0.5F;
        float hitYFromCenter = hitY - 0.5F;
        float hitZFromCenter = hitZ - 0.5F;
        switch (facing.getAxis()) {
            case Y:
                if (inCenter(hitXFromCenter, hitZFromCenter, 0.25f)) {
                    state = state.withProperty(facingProperty, facing.getOpposite().getIndex());
                } else if (isMax(hitXFromCenter, hitZFromCenter)) {
                    state = state.withProperty(facingProperty, ((hitXFromCenter > 0) ? EnumFacing.EAST : EnumFacing.WEST).getIndex());
                } else {
                    state = state.withProperty(facingProperty, ((hitZFromCenter > 0) ? EnumFacing.SOUTH : EnumFacing.NORTH).getIndex());
                }
                break;
            case X:
                if (inCenter(hitYFromCenter, hitZFromCenter, 0.25f)) {
                    state = state.withProperty(facingProperty, facing.getOpposite().getIndex());
                } else if (isMax(hitYFromCenter, hitZFromCenter)) {
                    state = state.withProperty(facingProperty, ((hitYFromCenter > 0) ? EnumFacing.UP : EnumFacing.DOWN).getIndex());
                } else {
                    state = state.withProperty(facingProperty, ((hitZFromCenter > 0) ? EnumFacing.SOUTH : EnumFacing.NORTH).getIndex());
                }
                break;
            case Z:
                if (inCenter(hitYFromCenter, hitXFromCenter, 0.25f)) {
                    state = state.withProperty(facingProperty, facing.getOpposite().getIndex());
                } else if (isMax(hitYFromCenter, hitXFromCenter)) {
                    state = state.withProperty(facingProperty, ((hitYFromCenter > 0) ? EnumFacing.UP : EnumFacing.DOWN).getIndex());
                } else {
                    state = state.withProperty(facingProperty, ((hitXFromCenter > 0) ? EnumFacing.EAST : EnumFacing.WEST).getIndex());
                }
                break;
            default:
                state = state.withProperty(facingProperty, facing.getOpposite().getIndex());
                break;
        }

        return state;
    }

    private static final AxisAlignedBB[] boxes = new AxisAlignedBB[]{
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D),
            new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.5D),
            new AxisAlignedBB(0.0D, 0.0D, 0.5D, 1.0D, 1.0D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5D, 1.0D, 1.0D),
            new AxisAlignedBB(0.5D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D),
    };

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        int i = getActualState(state, source, pos).getValue(getOrientationProperty());
        return boxes[i];
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        EnumFacing facing = EnumFacing.byIndex(state.getValue(getOrientationProperty()));
        return facing == face ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }
}
