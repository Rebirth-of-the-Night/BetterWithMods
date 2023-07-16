package betterwithmods.common.blocks;

import betterwithmods.api.block.IMultiVariants;
import betterwithmods.client.BWCreativeTabs;
import betterwithmods.common.BWSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockChime extends BWMBlock implements IMultiVariants {
    public static final PropertyBool ACTIVE = PropertyBool.create("active");
    private static final AxisAlignedBB CHIME_AABB = new AxisAlignedBB(0.3125D, 0.375D, 0.3125D, 0.6875D, 0.875D, 0.6875D);

    public BlockChime(Material material) {
        super(material);

        this.setHardness(2.0F);
        this.setCreativeTab(BWCreativeTabs.BWTAB);
        this.setDefaultState(this.blockState.getBaseState().withProperty(ACTIVE, false).withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.OAK));
        this.setSoundType(SoundType.WOOD);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(I18n.format("tooltip.chime.name"));
        super.addInformation(stack, player, tooltip, advanced);
    }

    @Override
    public String[] getVariants() {
        ArrayList<String> variants = new ArrayList<>();
        for (BlockPlanks.EnumType blockplanks$enumtype : BlockPlanks.EnumType.values()) {
            variants.add("active=false,variant=" + blockplanks$enumtype.getName());
        }
        return variants.toArray(new String[BlockPlanks.EnumType.values().length]);
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (BlockPlanks.EnumType type : BlockPlanks.EnumType.values()) {
            items.add(new ItemStack(this, 1, type.getMetadata()));
        }
    }


    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote)
            return true;
        else {
            if (!state.getValue(ACTIVE)) {
                world.setBlockState(pos, state.withProperty(ACTIVE, true));
                world.playSound(null, pos, state.getMaterial() == Material.IRON ? BWSounds.METALCHIME : BWSounds.WOODCHIME, SoundCategory.BLOCKS, 0.4F, 1.0F);
                for (EnumFacing facing : EnumFacing.VALUES)
                    world.notifyNeighborsOfStateChange(pos.offset(facing), this, false);
            }
            return true;
        }
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(BlockPlanks.VARIANT).getMetadata();
    }

    @Override
    public int tickRate(World world) {
        return 20;
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
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return world.getBlockState(pos.up()).isSideSolid(world, pos.up(), EnumFacing.DOWN) || world.getBlockState(pos.up()).getBlock() instanceof BlockFence || world.getBlockState(pos.up()).getBlock() instanceof net.minecraft.block.BlockPane || world.getBlockState(pos.up()).getBlock() instanceof BlockMultiPane || world.getBlockState(pos.up()).getBlock() instanceof BlockRope;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        world.scheduleBlockUpdate(pos, this, tickRate(world), 5);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if (state.getValue(ACTIVE)) {
            for (EnumFacing facing : EnumFacing.VALUES)
                world.notifyNeighborsOfStateChange(pos.offset(facing), this, false);
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos other) {
        if (!canPlaceBlockAt(world, pos)) {
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        } else {
            world.scheduleBlockUpdate(pos, this, tickRate(world), 5);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return CHIME_AABB;
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        boolean storm = detectStorming(world, pos) || isEntityColliding(world, pos);
        boolean isActive = state.getValue(ACTIVE);

        if (storm != isActive) {
            world.setBlockState(pos, state.withProperty(ACTIVE, storm));
            world.notifyNeighborsOfStateChange(pos, this, false);
            for (EnumFacing facing : EnumFacing.VALUES)
                world.notifyNeighborsOfStateChange(pos.offset(facing), this, false);
        }
        if (storm)
            world.playSound(null, pos, state.getMaterial() == Material.IRON ? BWSounds.METALCHIME : BWSounds.WOODCHIME, SoundCategory.BLOCKS, 0.25F + (rand.nextFloat() - rand.nextFloat() * 0.1F), 1.0F);
        world.scheduleBlockUpdate(pos, this, tickRate(world), 5);
    }

    private boolean detectStorming(World world, BlockPos pos) {
        return (world.isRaining() || world.isThundering()) && isNearOpenAir(world, pos);
    }

    private boolean isNearOpenAir(World world, BlockPos pos) {
        for (int x = -5; x < 6; x++) {
            for (int y = -2; y < 4; y++) {
                for (int z = -5; z < 6; z++) {
                    BlockPos check = pos.add(x, y, z);
                    if (world.canBlockSeeSky(check))
                        return true;
                }
            }
        }
        return false;
    }

    private boolean isEntityColliding(World world, BlockPos pos) {
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.getX() + 0.3125D, pos.getY() + 0.375D, pos.getZ() + 0.3125D, pos.getX() + 0.6875D, pos.getY() + 1.0D, pos.getZ() + 0.6875D));
        return !entities.isEmpty();
    }

    @Override
    public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (!state.getValue(ACTIVE)) {
            world.setBlockState(pos, state.withProperty(ACTIVE, true));
            world.notifyNeighborsOfStateChange(pos, this, false);
            for (EnumFacing facing : EnumFacing.VALUES)
                world.notifyNeighborsOfStateChange(pos.offset(facing), this, false);
            world.playSound(null, pos, state.getMaterial() == Material.IRON ? BWSounds.METALCHIME : BWSounds.WOODCHIME, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return getWeakPower(state, world, pos, side);
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing facing) {
        if (state.getValue(ACTIVE))
            return 15;
        return 0;
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(BlockPlanks.VARIANT).getMetadata();
        return meta + (state.getValue(ACTIVE) ? 8 : 0);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        boolean active = meta > 7;
        if (active)
            meta -= 8;
        return this.getDefaultState().withProperty(ACTIVE, active).withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.byMetadata(meta));
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVE, BlockPlanks.VARIANT);
    }
}
