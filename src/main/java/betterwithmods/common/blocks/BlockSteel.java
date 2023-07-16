package betterwithmods.common.blocks;

import betterwithmods.api.block.IMultiVariants;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWSounds;
import betterwithmods.common.items.ItemMaterial;
import betterwithmods.common.items.tools.ItemHacksaw;
import betterwithmods.common.items.tools.ItemSoulforgeArmor;
import betterwithmods.util.InvUtils;
import betterwithmods.util.player.PlayerHelper;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


import javax.annotation.Nullable;
import java.util.List;

public class BlockSteel extends BWMBlock implements IMultiVariants{

    public static final PropertyInteger HEIGHT = PropertyInteger.create("height", 0, 15);

    public BlockSteel() {
        super(Material.IRON);
        setSoundType(SoundType.METAL);
        setHardness(100F);
        setResistance(4000F);
        setDefaultState(getDefaultState().withProperty(HEIGHT, 0));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0,0,0,1, (16-state.getValue(HEIGHT))/16d,1);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(HEIGHT);
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return state.getValue(HEIGHT) == 0;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return state.getValue(HEIGHT) == 0;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return state.getValue(HEIGHT) == 0;
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        if(state.getValue(HEIGHT) == 0)
            return 255;
        return 0;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, HEIGHT);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(HEIGHT);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(HEIGHT, meta);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote)
            return true;
        ItemStack stack = playerIn.getHeldItemMainhand();
        if (stack.getItem() instanceof ItemHacksaw) {
            int height = state.getValue(HEIGHT);
            if (height < 15)
                worldIn.setBlockState(pos, state.withProperty(HEIGHT, height + 1));
            else
                worldIn.setBlockToAir(pos);
            if(!PlayerHelper.hasPart(playerIn, EntityEquipmentSlot.HEAD, ItemSoulforgeArmor.class)) {
                worldIn.playSound(null,pos,BWSounds.METAL_HACKSAW, SoundCategory.BLOCKS,1.0f,0.80f);
                worldIn.playSound(null,pos, SoundEvents.ENTITY_GHAST_SCREAM, SoundCategory.BLOCKS,0.5f,worldIn.rand.nextFloat() * 0.5f + worldIn.rand.nextFloat()*0.5f);
            }
            worldIn.spawnParticle(EnumParticleTypes.FLAME,pos.getX()-1,pos.getY()+1, pos.getZ()-1,0,0,0);
            stack.damageItem(1,playerIn);
            InvUtils.ejectStackWithOffset(worldIn, pos, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.PLATE_STEEL));
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return state.getValue(HEIGHT) != 0 || entity instanceof EntityPlayer;
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
        if (world.getBlockState(pos).getValue(HEIGHT) != 0) {
            super.onBlockExploded(world, pos, explosion);
        }
    }

    @Override
    public int getHarvestLevel(IBlockState state) {
        return 4;
    }

    @Override
    public String[] getVariants() {
        return new String[]{"height=0","height=1","height=2","height=3","height=4","height=5","height=6","height=7","height=8","height=9","height=10","height=11","height=12","height=13","height=14","height=15"};
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        if(stack.getMetadata() > 0)
            tooltip.add(String.format("Height:%s", 16-stack.getMetadata()));
    }

    public static IBlockState getBlock(int height) {
        return BWMBlocks.STEEL_BLOCK.getDefaultState().withProperty(HEIGHT, height);
    }

    //Cannot be pushed by a piston
    @Override
    public EnumPushReaction getPushReaction(IBlockState state) {
        return EnumPushReaction.IGNORE;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face != EnumFacing.DOWN && state.getValue(HEIGHT) > 0 ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
    }
}
