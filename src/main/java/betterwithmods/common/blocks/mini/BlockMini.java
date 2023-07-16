package betterwithmods.common.blocks.mini;

import betterwithmods.api.block.IAdvancedRotationPlacement;
import betterwithmods.api.block.IMultiVariants;
import betterwithmods.api.block.IRenderRotationPlacement;
import betterwithmods.client.ClientEventHandler;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWMRecipes;
import betterwithmods.common.blocks.BlockAesthetic;
import betterwithmods.common.blocks.BlockRotate;
import betterwithmods.util.InvUtils;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public abstract class BlockMini extends BlockRotate implements IMultiVariants, IAdvancedRotationPlacement, IDamageDropped, IRenderRotationPlacement {
    public static final Material MINI = new Material(MapColor.WOOD);
    public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 15);
    public static final PropertyOrientation SIDING_ORIENTATION = PropertyOrientation.create("orientation", 0, 5);
    public static final PropertyOrientation MOULDING_ORIENTATION = PropertyOrientation.create("orientation", 0, 11);
    public static final PropertyOrientation CORNER_ORIENTATION = PropertyOrientation.create("orientation", 0, 7);

    public BlockMini(Material material) {
        super(material);
    }

    public abstract PropertyOrientation getOrientationProperty();

    @Override
    public void nextState(World world, BlockPos pos, IBlockState state) {
        world.setBlockState(pos, state.cycleProperty(getOrientationProperty()));
    }

    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        Material mat = getMaterial(blockState);
        if (mat == Material.WOOD || mat == MINI)
            return 2.0F;
        return 3.0F;
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        Material mat = getMaterial(state);
        if (mat == Material.WOOD || mat == MINI)
            return SoundType.WOOD;
        return SoundType.STONE;
    }

    @Override
    public boolean isToolEffective(String type, IBlockState state) {
        return type != null && type.equals(getHarvestTool(state));
    }

    @Nullable
    @Override
    public String getHarvestTool(IBlockState state) {
        Material mat = getMaterial(state);
        if (mat == Material.WOOD || mat == MINI)
            return "axe";
        return "pickaxe";
    }

    @Override
    public int getHarvestLevel(IBlockState state) {
        return 1;
    }

    public int getUsedTypes() {
        return 6;
    }

    @Override
    public String[] getVariants() {
        ArrayList<String> variants = Lists.newArrayList();
        for (int i = 0; i < getUsedTypes(); i++) {
            variants.add(String.format("orientation=3,type=%s", i));
        }
        return variants.toArray(new String[variants.size()]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }


    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(TYPE);
    }

    @Override
    public int damageDropped(IBlockState state, World world, BlockPos pos) {
        TileEntityMultiType tile = getTile(world, pos);
        if (tile != null) {
            return tile.getType();
        }
        return 0;
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        TileEntityMultiType tile = getTile(world, pos);
        if (tile != null) {
            tile.setOrientation((tile.getOrientation() + 1) % (getOrientationProperty().getMax() + 1));
            IBlockState state = world.getBlockState(pos);
            world.setBlockState(pos, getActualState(state, world, pos));
            return true;
        }
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        boolean emptyHands = player.getHeldItem(EnumHand.MAIN_HAND).isEmpty() && player.getHeldItem(EnumHand.OFF_HAND).isEmpty() && player.isSneaking();
        if (emptyHands) {
            if (rotateBlock(world, pos, facing)) {
                world.playSound(null, pos, this.getSoundType(state, world, pos, player).getPlaceSound(), SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
                world.notifyNeighborsOfStateChange(pos, this, false);
                world.scheduleBlockUpdate(pos, this, 10, 1);
            }
            return true;
        }
        return false;
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
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getStateForAdvancedRotationPlacement(getDefaultState(), facing, hitX, hitY, hitZ);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntityMultiType tile = getTile(worldIn, pos);
        if (tile != null) {
            tile.setType(stack.getMetadata());
            tile.setOrientation(state.getValue(getOrientationProperty()));
        }
    }

    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        TileEntityMultiType tile = getTile(world, pos);
        if (tile != null) {
            return new ItemStack(this, 1, tile.getType());
        }
        return new ItemStack(this, 1, 0);
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
//        for (int i = 0; i < getUsedTypes(); i++) {
//            items.add(new ItemStack(this, 1, i));
//        }
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (!player.isCreative())
            InvUtils.ejectStackWithOffset(worldIn, pos, getDrops(worldIn, pos, state, 0));
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this, 1, getActualState(state, world, pos).getValue(TYPE));
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntityMultiType tile = getTile(world, pos);
        if (tile != null) {
            return Lists.newArrayList(new ItemStack(this, 1, tile.getType()));
        }
        return Lists.newArrayList();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityMultiType();
    }


    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntityMultiType tile = getTile(worldIn, pos);
        if (tile != null) {
            int o = tile.getOrientation() % (getOrientationProperty().getMax() + 1);
            return state.withProperty(TYPE, tile.getType()).withProperty(getOrientationProperty(), o);
        }
        return state;
    }


    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE, getOrientationProperty());
    }


    @Override
    public AxisAlignedBB getBounds(World world, BlockPos pos, EnumFacing facing, float flX, float flY, float flZ, ItemStack stack, EntityLivingBase placer) {
        return getStateForPlacement(world, pos, facing, flX, flY, flZ, stack.getMetadata(), placer).withProperty(TYPE, stack.getMetadata()).getBoundingBox(world, pos);
    }

    @Override
    public RenderFunction getRenderFunction() {
        return ClientEventHandler::renderMiniBlock;
    }

    public TileEntityMultiType getTile(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityMultiType)
            return (TileEntityMultiType) tile;
        return null;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add("This item is deprecated. It is replaced with a new version to allow more texture variants. It has a recipe to convert directly to the new version.");
    }

    public enum EnumType {

        STONE(0, "stone", Blocks.STONE),
        STONEBRICK(1, "stone_brick", Blocks.STONEBRICK),
        WHITESTONE(2, "whitestone", new ItemStack(BWMBlocks.AESTHETIC, 1, BlockAesthetic.EnumType.WHITESTONE.getMeta())),
        NETHERBRICK(3, "nether_brick", Blocks.NETHER_BRICK),
        BRICK(4, "brick", Blocks.BRICK_BLOCK),
        SANDSTONE(5, "sandstone", Blocks.SANDSTONE);

        public static final BlockMini.EnumType[] VALUES = values();

        private final int meta;
        private final String name;
        private final ItemStack block;
        private final IBlockState state;

        EnumType(int metaIn, String nameIn, Block blockIn) {
            this(metaIn, nameIn, new ItemStack(blockIn));
        }

        EnumType(int metaIn, String nameIn, ItemStack blockIn) {
            this.meta = metaIn;
            this.name = nameIn;
            this.block = blockIn;
            this.state = BWMRecipes.getStateFromStack(blockIn);
        }

        public int getMetadata() {
            return this.meta;
        }

        public String getName() {
            return this.name;
        }

        public ItemStack getBlock() {
            return this.block;
        }

        public IBlockState getState() {
            return this.state;
        }
    }

    public static class PropertyOrientation extends PropertyInteger {
        private int min, max;

        private PropertyOrientation(String name, int min, int max) {
            super(name, min, max);
            this.min = min;
            this.max = max;
        }

        public static PropertyOrientation create(String name, int min, int max) {
            return new PropertyOrientation(name, min, max);
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }
    }
}
