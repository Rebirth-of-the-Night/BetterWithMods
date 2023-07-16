package betterwithmods.common.blocks.mechanical;

import betterwithmods.BWMod;
import betterwithmods.api.block.IMultiVariants;
import betterwithmods.api.block.IOverpower;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.BWMBlock;
import betterwithmods.common.blocks.mechanical.tile.TileEntityFilteredHopper;
import betterwithmods.common.blocks.mechanical.tile.TileEntityMill;
import betterwithmods.common.blocks.mechanical.tile.TileEntityPulley;
import betterwithmods.common.blocks.mechanical.tile.TileEntityTurntable;
import betterwithmods.module.GlobalConfig;
import betterwithmods.util.InvUtils;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Random;

public class BlockMechMachines extends BWMBlock implements IBlockActive, IMultiVariants, IOverpower {

    public static final PropertyEnum<BlockMechMachines.EnumType> TYPE = PropertyEnum.create("type", BlockMechMachines.EnumType.class);

    public BlockMechMachines() {
        super(Material.ROCK);
        this.setTickRandomly(true);
        this.setHardness(3.5F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BlockMechMachines.EnumType.MILL).withProperty(ACTIVE, false));
        this.setLightOpacity(255);
        this.useNeighborBrightness = true;
    }

    public static ItemStack getStack(EnumType type) {
        return new ItemStack(BWMBlocks.SINGLE_MACHINES, 1, type.getMeta());
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(TYPE)) {
            case HOPPER:
                return new AxisAlignedBB(0, 4 / 16d, 0, 1,  0.99d, 1);
            default:
                return super.getBoundingBox(state, source, pos);
        }
    }

    @Override
    public String[] getVariants() {
        return new String[]{
                "active=false,type=mill",
                "active=false,type=pulley",
                "active=false,type=hopper",
                "active=false,type=turntable",
                "active=true,type=mill",
                "active=true,type=pulley",
                "active=true,type=hopper",
                "active=true,type=turntable",
        };
    }

    @SuppressWarnings("deprecation")
    @Override
    public Material getMaterial(IBlockState state) {
        switch (state.getValue(TYPE)) {
            case HOPPER:
            case PULLEY:
                setHarvestLevel("axe", 0, state);
                return Material.WOOD;
            default:
                setHarvestLevel("pickaxe", 0, state);
                return super.getMaterial(state);
        }
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        switch (state.getValue(TYPE)) {
            case HOPPER:
            case PULLEY:
                return SoundType.WOOD;
            default:
                return super.getSoundType(state, world, pos, entity);
        }
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        if(state.getBlock() == this) {
            BlockMechMachines.EnumType type = state.getValue(TYPE);
            return type == EnumType.MILL || type == EnumType.PULLEY || type == EnumType.TURNTABLE || (type ==EnumType.HOPPER && side == EnumFacing.UP);
        }
        return false;
    }

    public int tickRateForMeta(EnumType type) {
        if (type == EnumType.MILL)
            return 1;
        return 10;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
        return state.getValue(TYPE) == EnumType.TURNTABLE &&  (side != null && side.getAxis().isHorizontal());
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        BlockMechMachines.EnumType type = world.getBlockState(pos).getValue(TYPE);
        world.scheduleBlockUpdate(pos, this, tickRateForMeta(type), 5);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public int damageDropped(IBlockState state) {
        BlockMechMachines.EnumType type = state.getValue(TYPE);
        return type.getMeta();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        if(GlobalConfig.blocksAreOpaque)
            return state.getValue(TYPE).getSolidity();
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return state.getValue(TYPE).getSolidity();
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return state.getValue(TYPE).getSolidity();
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        return state.getValue(TYPE).getSolidity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        } else {

            boolean isInventory = Arrays.stream(EnumFacing.VALUES).anyMatch( f -> world.getTileEntity(pos).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, f));

            if (!player.isSneaking() && world.getTileEntity(pos) != null && isInventory) {
                player.openGui(BWMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
            } else {
                if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityTurntable && hand == EnumHand.MAIN_HAND) {
                    return ((TileEntityTurntable) world.getTileEntity(pos)).processRightClick(player);
                }
            }
            return true;
        }
    }

    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)) {
            if (tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)) {
                return InvUtils.calculateComparatorLevel(tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP));
            }
        }
        return 0;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.getTileEntity(pos) instanceof TileEntityTurntable) {
            if (!world.getGameRules().getBoolean("doDaylightCycle"))
                ((TileEntityTurntable) world.getTileEntity(pos)).toggleAsynchronous(null);
        }
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        EnumType type = state.getValue(TYPE);
        if(type == EnumType.HOPPER) {
            if(!worldIn.isRemote) {
                TileEntity tile = worldIn.getTileEntity(pos);
                if (tile instanceof TileEntityFilteredHopper) {
                    TileEntityFilteredHopper hopper = (TileEntityFilteredHopper) tile;
                    hopper.insert(entityIn);
                }
            }
            if(entityIn instanceof EntityItem) {
                entityIn.setPosition(entityIn.posX, entityIn.posY + 0.1, entityIn.posZ); //Fix to stop items being caught on this
            }
        }

    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        switch (state.getValue(TYPE)) {
            case MILL:
                return new TileEntityMill();
            case PULLEY:
                return new TileEntityPulley();
            case HOPPER:
                return new TileEntityFilteredHopper();
            case TURNTABLE:
                return new TileEntityTurntable();
        }
        return null;
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (EnumType type : EnumType.META_LOOKUP)
            items.add(getStack(type));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if(state.getValue(TYPE) == EnumType.MILL && isActive(state)) {
            emitSmoke(world,pos,rand,5);
        }

    }

    private void emitSmoke(World world, BlockPos pos, Random rand, int heat) {
        for (int i = 0; i < heat; i++) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            float fX = x + rand.nextFloat();
            float fY = y + rand.nextFloat() * 0.5F + 1.0F;
            float fZ = z + rand.nextFloat();
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, fX, fY, fZ, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVE, TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, EnumType.byMeta(meta)).withProperty(ACTIVE, meta > 3);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).getMeta() + (state.getValue(ACTIVE) ? 4 : 0);
    }

    public static final ResourceLocation MILLSTONE = LootTableList.register(new ResourceLocation(BWMod.MODID, "block/mill"));
    public static final ResourceLocation PULLEY = LootTableList.register(new ResourceLocation(BWMod.MODID, "block/pulley"));
    public static final ResourceLocation HOPPER = LootTableList.register(new ResourceLocation(BWMod.MODID, "block/hopper"));
    public static final ResourceLocation TURNTABLE = LootTableList.register(new ResourceLocation(BWMod.MODID, "block/turntable"));

    @Override
    public void overpower(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        overpowerSound(world,pos);
        breakBlock(world,pos, state);
        switch(state.getValue(TYPE)) {
            case MILL:
                InvUtils.ejectBrokenItems(world,pos.offset(EnumFacing.random(world.rand)),MILLSTONE);
                world.setBlockToAir(pos);
                break;
            case PULLEY:
                InvUtils.ejectBrokenItems(world, pos.offset(EnumFacing.random(world.rand)), PULLEY);
                world.setBlockToAir(pos);
                break;
            case HOPPER:
                InvUtils.ejectBrokenItems(world, pos.offset(EnumFacing.random(world.rand)), HOPPER);
                world.setBlockToAir(pos);
                break;
            case TURNTABLE:
                InvUtils.ejectBrokenItems(world, pos.offset(EnumFacing.random(world.rand)), TURNTABLE);
                world.setBlockToAir(pos);
                break;
        }
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        switch(state.getValue(TYPE)) {
            case HOPPER: return face == EnumFacing.UP ? BlockFaceShape.BOWL : BlockFaceShape.UNDEFINED;
            default: return BlockFaceShape.SOLID;
        }
    }


    public enum EnumType implements IStringSerializable {
        MILL(0, "mill", true),
        PULLEY(1, "pulley", true),
        HOPPER(2, "hopper"),
        TURNTABLE(3, "turntable", true);

        private static final BlockMechMachines.EnumType[] META_LOOKUP = values();

        private int meta;
        private String name;
        private boolean solidity;

        EnumType(int meta, String name) {
            this(meta, name, false);
        }

        EnumType(int meta, String name, boolean solid) {
            this.meta = meta;
            this.name = name;
            this.solidity = solid;
        }

        public static BlockMechMachines.EnumType byMeta(int meta) {
            return META_LOOKUP[meta % META_LOOKUP.length];
        }

        @Override
        public String getName() {
            return name;
        }

        public int getMeta() {
            return meta;
        }

        public boolean getSolidity() {
            return solidity;
        }
    }
}
