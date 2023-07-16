package betterwithmods.common.items;

import betterwithmods.api.BWMAPI;
import betterwithmods.api.IMultiLocations;
import betterwithmods.api.tile.IAxle;
import betterwithmods.client.BWCreativeTabs;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.EnumTier;
import betterwithmods.common.blocks.mechanical.BlockAxle;
import betterwithmods.common.blocks.mechanical.BlockAxleGenerator;
import betterwithmods.common.blocks.mechanical.BlockWaterwheel;
import betterwithmods.common.blocks.mechanical.BlockWindmill;
import betterwithmods.util.DirUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemAxleGenerator extends Item implements IMultiLocations {
    private final String[] names = {"windmill", "waterwheel", "windmill_vertical"};

    public ItemAxleGenerator() {
        super();
        this.setCreativeTab(BWCreativeTabs.BWTAB);
        this.setMaxStackSize(16);
        this.setHasSubtypes(true);
    }

    @Override
    public String[] getLocations() {
        return names;
    }
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (names[stack.getMetadata()].contains("windmill")) {
            tooltip.add(I18n.format("tooltip.windmill.name"));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof BlockAxle) {
            EnumFacing.Axis axis = state.getValue(DirUtils.AXIS);
            EnumTier tier = block == BWMBlocks.WOODEN_AXLE ? EnumTier.WOOD : EnumTier.STEEL;
            if (axis == EnumFacing.Axis.Y && stack.getItemDamage() == 2) {
                if (isVerticalWindmillValid(player, world, pos, hitY, tier))
                    stack.shrink(1);
            } else if (axis != EnumFacing.Axis.Y && stack.getItemDamage() != 2) {
                if (isHorizontalDeviceValid(player, world, pos, stack.getItemDamage(), axis, tier))
                    stack.shrink(1);
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    private boolean isHorizontalDeviceValid(EntityPlayer player, World world, BlockPos pos, int meta, EnumFacing.Axis axis, EnumTier tier) {
        boolean valid = false;
        if (meta == 1 && validateWaterwheel(world, pos, axis)) {
            if (axis != EnumFacing.Axis.Y) {
                world.setBlockState(pos, ((BlockWaterwheel) BWMBlocks.WATERWHEEL).getAxisState(axis).withProperty(EnumTier.TIER, tier));
                valid = true;
            }
        } else if (meta == 1) {
            if (world.isRemote)
                player.sendMessage(new TextComponentTranslation("bwm.message.waterwheel.0"));
        }
        if (meta == 0 && validateWindmill(world, pos, axis)) {
            if (axis != EnumFacing.Axis.Y) {
                world.setBlockState(pos, ((BlockWindmill) BWMBlocks.WINDMILL).getAxisState(axis).withProperty(EnumTier.TIER, tier));
                valid = true;
            }
        } else if (meta == 0) {
            if (world.isRemote)
                player.sendMessage(new TextComponentTranslation("bwm.message.windmill.0"));
        }
        return valid;
    }

    public boolean validateWaterwheel(World world, BlockPos pos, EnumFacing.Axis axis) {
        return validateHorizontal(world, pos, 2, axis, true);
    }

    public boolean validateWindmill(World world, BlockPos pos, EnumFacing.Axis axis) {
        return validateHorizontal(world, pos, 6, axis, false);
    }

    public boolean validateHorizontal(World world, BlockPos pos, int radius, EnumFacing.Axis axis, boolean isWheel) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        boolean valid = true;
        for (int vert = -radius; vert <= radius; vert++) {
            for (int i = -radius; i <= radius; i++) {
                int xP = axis == EnumFacing.Axis.Z ? i : 0;
                int zP = axis == EnumFacing.Axis.X ? i : 0;
                int xPos = x + xP;
                int yPos = y + vert;
                int zPos = z + zP;
                BlockPos checkPos = new BlockPos(xPos, yPos, zPos);

                if (yPos == y - radius && isWheel) {
                    valid = (world.isAirBlock(checkPos) || world.isMaterialInBB(new AxisAlignedBB(xPos, yPos, zPos, xPos + 1, yPos + 1, zPos + 1), Material.WATER)) && !isNearMechMachine(world, checkPos, axis);
                } else if (xP == 0 && yPos == y && zP == 0)
                    continue;
                else {
                    if (isWheel && (xP == -radius || xP == radius || zP == -radius || zP == radius))
                        valid = (world.isAirBlock(checkPos) || world.isMaterialInBB(new AxisAlignedBB(xPos, yPos, zPos, xPos + 1, yPos + 1, zPos + 1), Material.WATER)) && !isNearMechMachine(world, checkPos, axis);
                    else
                        valid = world.isAirBlock(checkPos) && !isNearMechMachine(world, checkPos, axis);
                }
                if (!valid)
                    break;
            }
            if (!valid)
                break;
        }
        return valid;
    }

    private boolean isNearMechMachine(World world, BlockPos pos, EnumFacing.Axis axis) {
//        for (int i = -3; i < 4; i++) {
//            int xP = axis == EnumFacing.Axis.X ? i : 0;
//            int zP = axis == EnumFacing.Axis.Z ? i : 0;
//            BlockPos check = pos.add(xP, 0, zP);
//            if (world.getBlockState(check).getBlock() instanceof BlockAxleGenerator) {
//                return true;
//            }
//        }
        return false;
    }

    private boolean isVerticalWindmillValid(EntityPlayer player, World world, BlockPos pos, float flY, EnumTier tier) {
        boolean valid = false;
        int yPos = 0;
        if (flY > 0.5F)
            yPos += 3;
        else
            yPos -= 3;
        BlockPos target = new BlockPos(pos.getX(), pos.getY() + yPos, pos.getZ());
        if (checkForSupportingAxles(world, target)) {
            if (validateArea(player, world, target)) {
                world.setBlockState(target, BWMBlocks.WINDMILL.getDefaultState().withProperty(DirUtils.AXIS, EnumFacing.Axis.Y).withProperty(EnumTier.TIER, tier));
                valid = true;
            } else if (world.isRemote)
                player.sendMessage(new TextComponentTranslation("bwm.message.vertical_windmill.0"));
        } else {
            if (world.isRemote) {
                player.sendMessage(new TextComponentTranslation("bwm.message.vertical_windmill.1"));
            }
        }
        return valid;
    }

    public boolean validateArea(EntityPlayer player, World world, BlockPos pos) {
        boolean clear = true;
        for (int yP = -4; yP < 5; yP++) {
            for (int xP = -4; xP < 5; xP++) {
                for (int zP = -4; zP < 5; zP++) {
                    int x = pos.getX();
                    int y = pos.getY();
                    int z = pos.getZ();
                    BlockPos target = new BlockPos(x + xP, y + yP, z + zP);
                    if (x + xP != x && z + zP != z && yP != -4 && yP != 4)
                        clear = world.isAirBlock(target);
                    else if ((Math.sqrt(xP * xP) == 4 || Math.sqrt(zP * zP) == 4) && Math.sqrt(yP * yP) != 4) {
                        clear = world.isAirBlock(target) && isVertClear(world, target, xP, zP);
                    }
                    if ((yP == -4 || yP == 4) && (xP == 0 && zP == 0)) {
                        clear = !(world.getBlockState(target).getBlock() instanceof BlockWindmill);
                    }
                    if (!clear) {
                        if (world.isRemote)
                            player.sendMessage(new TextComponentTranslation("Blockage at x:", (x + xP), " y:", (y + yP), " z:", (z + zP)));
                        break;
                    }
                }
                if (!clear)
                    break;
            }
            if (!clear)
                break;
        }
        for (int i = -8; i < 8; i++) {
            for (int j = -8; j < 7; j++) {
                for (int k = -8; k < 8; k++) {
                    if (!clear)
                        break;
                    BlockPos target = pos.add(i, j, k);
                    if (i > -5 && i < 5 && k > -5 && k < 5) {
                        continue;
                    } else if (world.getBlockState(target).getBlock() instanceof BlockAxleGenerator)
                        clear = false;
                    if (!clear)
                        break;
                }
                if (!clear)
                    break;
            }
            if (!clear)
                break;
        }
        for (int i = 5; i < 8 && clear; i++) {
            int yMin = -i;
            BlockPos minPos = pos.add(0, yMin, 0);
            BlockPos maxPos = pos.add(0, i, 0);
            if (world.getBlockState(minPos).getBlock() instanceof BlockWindmill)
                clear = false;
            if (!clear)
                break;
            if (world.getBlockState(maxPos).getBlock() instanceof BlockWindmill)
                clear = false;
            if (!clear)
                break;
        }
        return clear;
    }

    private boolean isVertClear(World world, BlockPos pos, int xP, int zP) {
        if (xP == -4 || xP == 4) {
            if (!vertCheck(world, pos, EnumFacing.Axis.X, xP < 0))
                return false;
        }
        if (zP == -4 || zP == 4) {
            if (!vertCheck(world, pos, EnumFacing.Axis.Z, zP < 0))
                return false;
        }
        return true;
    }

    private boolean vertCheck(World world, BlockPos pos, EnumFacing.Axis axis, boolean negative) {
        for (int i = 0; i < 4; i++) {
            int xP = axis == EnumFacing.Axis.X ? i : 0;
            int zP = axis == EnumFacing.Axis.Z ? i : 0;
            if (negative) {
                xP *= -1;
                zP *= -1;
            }
            BlockPos check = pos.add(xP, 0, zP);
            if (world.getBlockState(check).getBlock() instanceof BlockAxleGenerator)
                return false;
        }
        return true;
    }

    private boolean checkForSupportingAxles(World world, BlockPos pos) {
        for (int i = -3; i <= 3; i++) {
            BlockPos target = pos.add(0, i, 0);
            IAxle axle = BWMAPI.IMPLEMENTATION.getAxle(world,target,EnumFacing.UP);
            if (axle == null) return false;
            if (axle.getAxis() != EnumFacing.Axis.Y) return false;
        }
        return true;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab))
            for (int i = 0; i < 3; i++) {
                items.add(new ItemStack(this, 1, i));
            }
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + "." + names[stack.getItemDamage()];
    }

    @Override
    public int getItemBurnTime(ItemStack itemStack) {
        return 300;
    }
}
