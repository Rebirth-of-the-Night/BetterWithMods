package betterwithmods.common.blocks.mini;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

@Deprecated
public class BlockCorner extends BlockMini {

    public BlockCorner(Material mat) {
        super(mat);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, 0).withProperty(CORNER_ORIENTATION, 0));
    }

    @Override
    public PropertyOrientation getOrientationProperty() {
        return CORNER_ORIENTATION;
    }

    private static final AxisAlignedBB[] bounds = new AxisAlignedBB[]{
            new AxisAlignedBB(0.0D, 0.0D, 0.5D, 0.5D, 0.5D, 1.0D),
            new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5D, 0.5D, 0.5D),
            new AxisAlignedBB(0.5D, 0.0D, 0.5D, 1.0D, 0.5D, 1.0D),
            new AxisAlignedBB(0.5D, 0.0D, 0.0D, 1.0D, 0.5D, 0.5D),
            new AxisAlignedBB(0.0D, 0.5D, 0.5D, 0.5D, 1.0D, 1.0D),
            new AxisAlignedBB(0.0D, 0.5D, 0.0D, 0.5D, 1.0D, 0.5D),
            new AxisAlignedBB(0.5D, 0.5D, 0.5D, 1.0D, 1.0D, 1.0D),
            new AxisAlignedBB(0.5D, 0.5D, 0.0D, 1.0D, 1.0D, 0.5D)
    };

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        int ori = getActualState(state,source,pos).getValue(getOrientationProperty());
        if (ori > 7 || ori < 1)
            return bounds[0];
        return bounds[ori];
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
                int corner = getCorner(hitXFromCenter, hitZFromCenter);
                if (corner != -1) {
                    int[] corners = hitYFromCenter > 0 ? new int[]{2, 3,1, 0} : new int[]{6,7,5,4};
                    state = state.withProperty(facingProperty, corners[corner]);
                }
                break;
            case X:
                corner = getCorner(hitYFromCenter, hitZFromCenter);
                if (corner != -1) {
                    int[] corners = hitXFromCenter > 0 ? new int[]{4, 5, 1, 0} : new int[]{6, 7,3, 2};
                    state = state.withProperty(facingProperty, corners[corner]);
                }
                break;
            case Z:
                corner = getCorner(hitYFromCenter, hitXFromCenter);
                if (corner != -1) {
                    int[] corners = hitZFromCenter > 0 ? new int[]{7, 5, 1, 3} : new int[]{6, 4,0,2};
                    state = state.withProperty(facingProperty, corners[corner]);
                }
                break;
            default:
                state = state.withProperty(facingProperty, facing.getOpposite().getIndex());
                break;
        }

        return state;
    }

    private int getCorner(float hitXFromCenter, float hitZFromCenter) {
        boolean positiveX = hitXFromCenter > 0, positiveZ = hitZFromCenter > 0;
        if (positiveX && positiveZ)
            return 0;
        if (positiveX && !positiveZ)
            return 1;
        if (!positiveX && !positiveZ)
            return 2;
        if (!positiveX && positiveZ)
            return 3;
        return -1;
    }
}
