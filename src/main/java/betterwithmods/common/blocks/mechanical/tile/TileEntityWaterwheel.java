package betterwithmods.common.blocks.mechanical.tile;

import betterwithmods.api.block.IWaterCurrent;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.mechanical.BlockWaterwheel;
import betterwithmods.util.DirUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;

public class TileEntityWaterwheel extends TileAxleGenerator {
    public static HashMap<Block, IWaterCurrent> WATER_BLOCKS = new HashMap<>();

    public TileEntityWaterwheel() {
        super();
    }

    public static void registerWater(Block block) {
        if (block instanceof BlockLiquid)
            registerWater(block, IWaterCurrent.VANILLA_LIQUID);
        else if (block instanceof BlockFluidBase)
            registerWater(block, IWaterCurrent.FORGE_LIQUID);
        else
            registerWater(block, IWaterCurrent.NO_FLOW);
    }

    public static void registerWater(Block block, IWaterCurrent handler) {
        WATER_BLOCKS.put(block, handler);
    }

    @Override
    public int getMinimumInput(EnumFacing facing) {
        return 0;
    }

    public static boolean isWater(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return isVanillaWater(state) || isForgeFluid(state.getBlock()) || WATER_BLOCKS.containsKey(state.getBlock());
    }

    public static boolean isWater(IBlockState state) {
        return isVanillaWater(state) || isForgeFluid(state.getBlock()) || WATER_BLOCKS.containsKey(state.getBlock());
    }

    public IWaterCurrent getCurrentHandler(IBlockState state) {
        if (isVanillaWater(state))
            return IWaterCurrent.VANILLA_LIQUID;
        if (isForgeFluid(state.getBlock()))
            return IWaterCurrent.FORGE_LIQUID;
        return WATER_BLOCKS.get(state.getBlock());
    }

    private static boolean isVanillaWater(IBlockState state) {
        return state.getBlock() instanceof BlockLiquid && state.getMaterial() == Material.WATER;
    }

    private static boolean isForgeFluid(Block block) {
        return block instanceof BlockFluidBase && ((BlockFluidBase) block).getFluid() == FluidRegistry.WATER;
    }

    @Override
    public void verifyIntegrity() {
        boolean isAir = true;
        boolean hasWater = true;
        if (getBlockWorld().getBlockState(pos).getBlock() == BWMBlocks.WATERWHEEL) {
            EnumFacing.Axis axis = getBlockWorld().getBlockState(pos).getValue(DirUtils.AXIS);
            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    int xPos = (axis == EnumFacing.Axis.Z ? i : 0);
                    int zPos = (axis == EnumFacing.Axis.X ? i : 0);
                    BlockPos offset = pos.add(xPos, j, zPos);
                    if (j == -2)
                        hasWater = isWater(world, offset);
                    if (!hasWater) {
                        hasWater = sidesHaveWater();
                        if (!hasWater)
                            break;
                    }
                    if (i == 0 && j == 0)
                        continue;
                    else if (j > -2) {
                        IBlockState state = world.getBlockState(offset);
                        boolean replaceable = state.getBlock().isReplaceable(world, offset);
                        if (i == -2 || i == 2) {
                            isAir = replaceable || isWater(world, offset);
                        } else
                            isAir = replaceable;
                    }
                    if (!isAir)
                        break;
                }
                if (!isAir || !hasWater)
                    break;
            }
        }
        isValid = isAir && hasWater;
    }

    public boolean sidesHaveWater() {
        EnumFacing.Axis axis = getBlockWorld().getBlockState(pos).getValue(DirUtils.AXIS);
        int leftWater = 0;
        int rightWater = 0;
        boolean bottomIsUnobstructed = true;
        for (int i = -2; i <= 2; i++) {
            int xLeft = axis == EnumFacing.Axis.Z ? -2 : 0;
            int xRight = axis == EnumFacing.Axis.Z ? 2 : 0;
            int zLeft = axis == EnumFacing.Axis.X ? -2 : 0;
            int zRight = axis == EnumFacing.Axis.X ? 2 : 0;
            BlockPos leftPos = pos.add(xLeft, i, zLeft);
            BlockPos rightPos = pos.add(xRight, i, zRight);
            if (isWater(world, leftPos))
                leftWater++;
            else if (isWater(world, rightPos))
                rightWater++;

            int xP = axis == EnumFacing.Axis.Z ? i : 0;
            int yP = -2;
            int zP = axis == EnumFacing.Axis.X ? i : 0;
            BlockPos bPos = pos.add(xP, yP, zP);
            bottomIsUnobstructed = getBlockWorld().isAirBlock(bPos) || isWater(world, bPos);
            if (!bottomIsUnobstructed)
                break;
        }
        return bottomIsUnobstructed && (leftWater != 0 || rightWater != 0) && (leftWater < rightWater || leftWater > rightWater);
    }

    @Override
    public void calculatePower() {
        byte power = 0;
        if (isValid()) {
            Vec3d overallFlow = Vec3d.ZERO;
            EnumFacing.Axis axis = getBlockWorld().getBlockState(pos).getValue(DirUtils.AXIS);
            int leftWater = 0;
            int rightWater = 0;
            for (int i = 0; i < 3; i++) {
                int metaPos = i - 1;
                int xP = axis == EnumFacing.Axis.Z ? metaPos : 0;
                int zP = axis == EnumFacing.Axis.X ? metaPos : 0;
                BlockPos lowPos = pos.add(xP, -2, zP);
                IBlockState lowState = getBlockWorld().getBlockState(lowPos);
                IWaterCurrent current = getCurrentHandler(lowState);
                if (current != null)
                    overallFlow = overallFlow.add(current.getFlowDirection(getBlockWorld(), lowPos, lowState));
            }
            for (int i = -1; i < 3; i++) {
                int xLeft = axis == EnumFacing.Axis.Z ? -2 : 0;
                int xRight = axis == EnumFacing.Axis.Z ? 2 : 0;
                int zLeft = axis == EnumFacing.Axis.X ? -2 : 0;
                int zRight = axis == EnumFacing.Axis.X ? 2 : 0;
                BlockPos leftPos = pos.add(xLeft, i, zLeft);
                BlockPos rightPos = pos.add(xRight, i, zRight);
                if (isWater(world, leftPos))
                    leftWater++;
                if (isWater(world, rightPos))
                    rightWater++;
            }
            int xFlow = Math.abs(overallFlow.x) > 2 ? (int) Math.signum(overallFlow.x) : 0;
            int zFlow = Math.abs(overallFlow.z) > 2 ? (int) Math.signum(overallFlow.z) : 0;
            int relevantFlow = 0;
            if (axis == EnumFacing.Axis.X)
                relevantFlow = zFlow;
            if (axis == EnumFacing.Axis.Z)
                relevantFlow = xFlow;
            if (leftWater > rightWater || (relevantFlow > 0 && leftWater >= rightWater))
                waterMod = -1;
            else if (rightWater > leftWater || (relevantFlow < 0 && rightWater >= leftWater))
                waterMod = 1;
            else {
                waterMod = 0;
            }
            if (waterMod != 0) {
                power = 1;
            }
        }
        if (power != this.power) {
            setPower(power);
        }
    }


    //Extend the bounding box if the TESR is bigger than the occupying block.
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        IBlockState state = getBlockWorld().getBlockState(pos);
        if (!(state.getBlock() instanceof BlockWaterwheel))
            return Block.FULL_BLOCK_AABB;

        EnumFacing.Axis axis = state.getValue(DirUtils.AXIS);
        EnumFacing facing = (axis == EnumFacing.Axis.Z) ? EnumFacing.SOUTH : EnumFacing.EAST;
        Vec3i vec = facing.getDirectionVec();
        int xP = axis == EnumFacing.Axis.Z ? getRadius() : 0;
        int yP = getRadius();
        int zP = axis == EnumFacing.Axis.X ? getRadius() : 0;

        return new AxisAlignedBB(-xP, -yP, -zP, xP, yP, zP).offset(0.5, 0.5, 0.5).offset(pos).expand(vec.getX(), vec.getY(), vec.getZ());
    }

    @Override
    public Block getBlock() {
        return getBlockType();
    }

    @Override
    public int getRadius() {
        return 2;
    }
}
