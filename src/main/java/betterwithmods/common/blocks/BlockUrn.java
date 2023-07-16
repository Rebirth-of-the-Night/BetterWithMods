package betterwithmods.common.blocks;

import betterwithmods.api.block.IMultiVariants;
import betterwithmods.api.block.ISoulSensitive;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.mechanical.BlockMechMachines;
import betterwithmods.util.InvUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

import static net.minecraft.util.EnumFacing.UP;

public class BlockUrn extends BWMBlock implements ISoulSensitive, IMultiVariants {
    public static final PropertyEnum<EnumType> TYPE = PropertyEnum.create("urntype", EnumType.class);
    public static final PropertyBool UNDERHOPPER = PropertyBool.create("underhopper");
    private static final double OFFSET = 0.375D;
    private static final AxisAlignedBB URN_AABB = new AxisAlignedBB(0.3125D, 0, 0.3125D, 0.6875D, 0.625D, 0.6875D);
    private static final AxisAlignedBB UNDER_HOPPER_AABB = URN_AABB.offset(0, OFFSET, 0);


    public BlockUrn() {
        super(Material.ROCK);
        this.setHardness(2.0F);
        this.setDefaultState(
                this.blockState.getBaseState().withProperty(TYPE, EnumType.EMPTY).withProperty(UNDERHOPPER, false));
    }

    public static ItemStack getStack(EnumType type, int count) {
        return new ItemStack(BWMBlocks.URN, count, type.getMeta());
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        BlockPos down = pos.down();
        BlockFaceShape blockfaceshape = worldIn.getBlockState(down).getBlockFaceShape(worldIn, down, UP);
        boolean below = blockfaceshape != BlockFaceShape.BOWL && blockfaceshape != BlockFaceShape.UNDEFINED;

        BlockPos up = pos.up();
        IBlockState state = worldIn.getBlockState(up);
        boolean above = state.getBlock() == BWMBlocks.SINGLE_MACHINES && worldIn.getBlockState(up).getValue(BlockMechMachines.TYPE) == BlockMechMachines.EnumType.HOPPER;
        return below || above;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canPlaceBlockAt(worldIn, pos)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    @Override
    public int damageDropped(IBlockState state) {
        int meta = state.getValue(TYPE).getMeta();
        return meta > 7 ? meta : 0;
    }

    @Override
    public String[] getVariants() {
        return new String[]{"underhopper=false,urntype=empty", "underhopper=false,urntype=12",
                "underhopper=false,urntype=25", "underhopper=false,urntype=37", "underhopper=false,urntype=50",
                "underhopper=false,urntype=62", "underhopper=false,urntype=75", "underhopper=false,urntype=87",
                "underhopper=false,urntype=full", "underhopper=false,urntype=void"};
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        state = state.getActualState(world, pos);
        if (state.getValue(UNDERHOPPER)) return UNDER_HOPPER_AABB;
        else return URN_AABB;
    }

    @Override
    public boolean isSoulSensitive(IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Override
    public int getMaximumSoulIntake(IBlockAccess world, BlockPos pos) {
        int meta = world.getBlockState(pos).getValue(TYPE).getMeta();
        if (meta < 8)
            return 8 - meta;
        else if (meta == 9)
            return 64;
        return 0;
    }

    @Override
    public int processSouls(World world, BlockPos pos, int souls) {
        return Math.min(getMaximumSoulIntake(world, pos), souls);
    }

    @Override
    public boolean consumeSouls(World world, BlockPos pos, int souls) {
        int meta = world.getBlockState(pos).getValue(TYPE).getMeta();
        if (souls > 1 && meta != 9) {
            int newMeta = meta + souls;
            if (newMeta == 8) {
                InvUtils.ejectStackWithOffset(world, pos, new ItemStack(BWMBlocks.URN, 1, 8));
                return world.setBlockToAir(pos);
            } else
                return world.setBlockState(pos, getDefaultState().withProperty(TYPE, EnumType.byMeta(meta)));
        } else if (meta == 9)
            return true;
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        int meta = world.getBlockState(pos).getValue(TYPE).getMeta();
        if (meta > 0) {
            int[] souls = {1, 2, 3, 4, 5, 6, 7, 8, 64};
            int chance = 80 - souls[meta - 1] / 2;
            if (rand.nextInt(chance) == 0) {
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                        SoundEvents.ENTITY_GHAST_AMBIENT, SoundCategory.BLOCKS, 1.0F, rand.nextFloat() * 0.1F + 0.45F,
                        false);
                float flX = x + rand.nextFloat();
                float flY = y + rand.nextFloat() * 0.5F + 0.625F;
                float flZ = z + rand.nextFloat();
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, flX, flY, flZ, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        BlockPos up = pos.up();
        Block block = world.getBlockState(up).getBlock();
        if (block != null && block == BWMBlocks.SINGLE_MACHINES) {
            if (world.getBlockState(up).getValue(BlockMechMachines.TYPE) == BlockMechMachines.EnumType.HOPPER) {
                return state.withProperty(UNDERHOPPER, true);
            }
        }
        return state;
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (EnumType type : EnumType.VALUES)
            if (type.used)
                items.add(getStack(type, 1));
    }


    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE, UNDERHOPPER);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, EnumType.byMeta(meta));
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.DOWN ? BlockFaceShape.CENTER : BlockFaceShape.UNDEFINED;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).getMeta();
    }

    public enum EnumType implements IStringSerializable {
        EMPTY("empty", 0, true), ONE("12", 1, false), TWO("25", 2, false), THREE("37", 3, false), FOUR("50", 4, false), FIVE("62", 5, false), SIX("75",
                6, false), SEVEN("87", 7, false), FULL("full", 8, true), VOID("void", 9, true);

        private static final EnumType[] VALUES = values();
        private String name;
        private int meta;
        private boolean used;

        EnumType(String name, int meta, boolean used) {
            this.name = name;
            this.meta = meta;
            this.used = used;
        }

        public static EnumType byMeta(int meta) {
            return VALUES[meta % 10];
        }

        @Override
        public String getName() {
            return name;
        }

        public int getMeta() {
            return meta;
        }

    }
}
