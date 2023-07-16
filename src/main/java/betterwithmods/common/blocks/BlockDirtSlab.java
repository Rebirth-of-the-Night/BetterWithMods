package betterwithmods.common.blocks;

import betterwithmods.api.block.IMultiVariants;
import betterwithmods.common.BWMItems;
import betterwithmods.module.tweaks.GrassPath;
import betterwithmods.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
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
import java.util.Random;

import static net.minecraft.block.BlockDirt.SNOWY;

/**
 * @author Koward
 */
public class BlockDirtSlab extends BlockSimpleSlab implements IMultiVariants {
    public static final PropertyEnum<DirtSlabType> VARIANT = PropertyEnum.create("variant", BlockDirtSlab.DirtSlabType.class);
    private static final EnumFacing[] CHECKED_FACINGS_FOR_SNOW = new EnumFacing[]{EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST};

    public BlockDirtSlab() {
        super(Material.GROUND);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, DirtSlabType.DIRT).withProperty(SNOWY, false));
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        this.setTickRandomly(true);
        this.setHarvestLevel("shovel", 0);
    }

    @Override
    public boolean getUseNeighborBrightness(IBlockState state) {
        return true;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this, 1, 0);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (state.getValue(VARIANT) == DirtSlabType.PATH)
            return new AxisAlignedBB(0, 0, 0, 1, 7 / 16d, 1);
        return super.getBoundingBox(state, source, pos);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack held = playerIn.getHeldItem(hand);
        if (state.getValue(VARIANT) != DirtSlabType.PATH && GrassPath.getShovelQuality(held) > 1) {
            worldIn.playSound(playerIn, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
            playerIn.swingArm(hand);
            if (!worldIn.isRemote) {
                worldIn.setBlockState(pos, state.withProperty(VARIANT, DirtSlabType.PATH), 11);
                held.damageItem(1, playerIn);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return isOverSupport(worldIn, pos);
    }

    private boolean isOverSupport(World worldIn, BlockPos pos) {
        IBlockState state = worldIn.getBlockState(pos.down());
        return state.getBlock() == Blocks.GRASS_PATH || state.isSideSolid(worldIn, pos.down(), EnumFacing.UP)
                || state.getBlock().canPlaceTorchOnTop(state, worldIn, pos.down())
                || state.isNormalCube()
                || state.isFullCube()
                || state.isFullBlock();
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos other) {
        if (!isOverSupport(worldIn, pos)) {
            worldIn.destroyBlock(pos, false);
            spawnAsEntity(worldIn, pos, new ItemStack(BWMItems.DIRT_PILE));
        }
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> drops = new ArrayList<>();
        drops.add(new ItemStack(this));
        return drops;
    }

    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess world, BlockPos pos) {
        return (state.getValue(VARIANT)).getColor();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        if (state.getValue(VARIANT) == DirtSlabType.PATH)
            return state;
        boolean snowy = false;
        for (EnumFacing facing : CHECKED_FACINGS_FOR_SNOW) {
            BlockPos checkedPos = pos.offset(facing);
            Block block = worldIn.getBlockState(checkedPos).getBlock();
            Block blockOver = worldIn.getBlockState(checkedPos.up()).getBlock();
            if (block == Blocks.SNOW || block == Blocks.SNOW_LAYER ||
                    blockOver == Blocks.SNOW || blockOver == Blocks.SNOW_LAYER) {
                snowy = true;
            }
        }
        state = state.withProperty(SNOWY, snowy);

        return state;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, DirtSlabType.DIRT.getMetadata()));
        list.add(new ItemStack(this, 1, DirtSlabType.GRASS.getMetadata()));
        list.add(new ItemStack(this, 1, DirtSlabType.MYCELIUM.getMetadata()));
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(this, 1, state.getValue(VARIANT).getMetadata());
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, DirtSlabType.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT, SNOWY);
    }

    @Override
    public Material getMaterial(IBlockState state) {
        return state.getValue(VARIANT).getMaterial();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public String[] getVariants() {
        ArrayList<String> variants = new ArrayList<>();
        for (DirtSlabType enumtype : DirtSlabType.values()) {
            variants.add("snowy=false,variant=" + enumtype.getName());
        }
        return variants.toArray(new String[DirtSlabType.values().length]);
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        if (state.getValue(SNOWY)) return SoundType.SNOW;
        switch (state.getValue(VARIANT)) {
            case DIRT:
                return SoundType.GROUND;
            case GRASS:
            case MYCELIUM:
            default:
                return SoundType.PLANT;
        }

    }

    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        switch (blockState.getValue(VARIANT)) {
            case DIRT:
                return 0.5F;
            case GRASS:
            case MYCELIUM:
            default:
                return 0.6F;
        }
    }

    private void handleSubtypeChange(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote) {
            int shrinkLight = WorldUtils.getNaturalLightFromNeighbors(world, pos.up());
            int growthLight = shrinkLight - world.getSkylightSubtracted();
            BlockPos check = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);
            Block block = world.getBlockState(check).getBlock();
            if (block == Blocks.GRASS || (block == this && world.getBlockState(check).getValue(VARIANT) == DirtSlabType.GRASS)) {
                if (growthLight >= 11)
                    world.setBlockState(pos, state.withProperty(VARIANT, DirtSlabType.GRASS));
            } else if (block == Blocks.MYCELIUM || (block == this && world.getBlockState(check).getValue(VARIANT) == DirtSlabType.MYCELIUM)) {
                if (growthLight >= 9)
                    world.setBlockState(pos, state.withProperty(VARIANT, DirtSlabType.MYCELIUM));
            }
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote) {
            if (state.getValue(VARIANT) == DirtSlabType.GRASS) {
                BlockGrassCustom.handleGrassSpreading(worldIn, pos, rand, getDefaultState().withProperty(VARIANT, DirtSlabType.DIRT));
            } else if (state.getValue(VARIANT) == DirtSlabType.MYCELIUM) {
                BlockMyceliumCustom.handleMyceliumSpreading(worldIn, pos, rand, getDefaultState().withProperty(VARIANT, DirtSlabType.DIRT));
            } else if (state.getValue(VARIANT) == DirtSlabType.DIRT)
                handleSubtypeChange(worldIn, pos, state, rand);
        }
    }

    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        super.randomDisplayTick(stateIn, worldIn, pos, rand);
        if (!(stateIn.getValue(VARIANT) == DirtSlabType.MYCELIUM)) {
            return;
        }

        if (rand.nextInt(10) == 0) {
            worldIn.spawnParticle(EnumParticleTypes.TOWN_AURA, (double) ((float) pos.getX() + rand.nextFloat()), (double) ((float) pos.getY() + 1.1F), (double) ((float) pos.getZ() + rand.nextFloat()), 0.0D, 0.0D, 0.0D);
        }
    }

    public enum DirtSlabType implements IStringSerializable {
        DIRT(0, "dirt", Material.GROUND),
        GRASS(1, "grass", Material.GRASS),
        MYCELIUM(2, "mycelium", MapColor.PURPLE, Material.GRASS),
        PATH(3, "path", Material.GROUND);
        private static final DirtSlabType[] METADATA_LOOKUP = new DirtSlabType[values().length];

        static {
            for (DirtSlabType blockdirt$dirttype : values()) {
                METADATA_LOOKUP[blockdirt$dirttype.getMetadata()] = blockdirt$dirttype;
            }
        }

        private final int metadata;
        private final String name;
        private final MapColor color;
        private final Material material;

        DirtSlabType(int metadataIn, String nameIn, Material material) {
            this(metadataIn, nameIn, material.getMaterialMapColor(), material);
        }

        DirtSlabType(int metadataIn, String nameIn, MapColor color, Material material) {
            this.metadata = metadataIn;
            this.name = nameIn;
            this.color = color;
            this.material = material;
        }

        public static DirtSlabType byMetadata(int metadata) {
            if (metadata < 0 || metadata >= METADATA_LOOKUP.length) {
                metadata = 0;
            }

            return METADATA_LOOKUP[metadata];
        }

        public int getMetadata() {
            return this.metadata;
        }

        public MapColor getColor() {
            return this.color;
        }

        public Material getMaterial() {
            return this.material;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }
    }
}
