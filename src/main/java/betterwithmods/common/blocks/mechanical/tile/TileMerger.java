package betterwithmods.common.blocks.mechanical.tile;

import betterwithmods.api.BWMAPI;
import com.google.common.collect.Lists;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class TileMerger extends TileGearbox {
    public TileMerger(int maxPower) {
        super(maxPower);
    }

    @SuppressWarnings("unused")
    private List<Integer> leftHistory = Lists.newArrayList(0, 0, 0, 0, 0), rightHistory = Lists.newArrayList(0, 0, 0, 0, 0);

    @Override
    public void onChanged() {
        if (this.getBlockWorld().getTotalWorldTime() % 20L != 0L)
            return;

        if (BWMAPI.IMPLEMENTATION.isRedstonePowered(world, pos)) {
            setPower(0);
            markDirty();
            return;
        }
        int left = this.getMechanicalInput(getFacing());
        int right = this.getMechanicalInput(getFacing().getOpposite());

//        if (leftHistory.size() >= 5)
//            leftHistory.remove(0);
//        leftHistory.add(left);
//        if (rightHistory.size() >= 5)
//            rightHistory.remove(0);
//        rightHistory.add(right);
//
//        int leftAverage = (int) Math.floor(leftHistory.stream().mapToDouble(i -> i).average().orElse(0));
//        int rightAverage = (int) Math.floor(rightHistory.stream().mapToDouble(i -> i).average().orElse(0));
        if(left < 1 ||  right < 1)
            return;
        if(left != right)
            getBlock().overpower(world,pos);
        else {
            int power = (int) (3* left);
            if (power != this.power) {
                setPower(power);
            }
            markDirty();
        }

    }


    @Override
    public int getMechanicalOutput(EnumFacing facing) {
        if (facing != getFacing() && facing != getFacing().getOpposite() && BWMAPI.IMPLEMENTATION.isAxle(world, pos.offset(facing), facing.getOpposite()))
            return Math.min(power, maxPower);
        return -1;
    }

    @Override
    public int getMechanicalInput(EnumFacing facing) {
        BlockPos pos = getBlockPos().offset(facing);
        if (BWMAPI.IMPLEMENTATION.getMechanicalPower(world, pos, facing.getOpposite()) != null && !(BWMAPI.IMPLEMENTATION.getMechanicalPower(world, pos, facing.getOpposite()) instanceof TileGearbox))
            return BWMAPI.IMPLEMENTATION.getPowerOutput(world, pos, facing.getOpposite());
        return 0;
    }

}
