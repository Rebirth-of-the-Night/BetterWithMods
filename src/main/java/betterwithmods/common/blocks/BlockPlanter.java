package betterwithmods.common.blocks;

import betterwithmods.api.block.IMultiVariants;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWMRecipes;
import betterwithmods.common.registry.crafting.IngredientTool;
import betterwithmods.util.InvUtils;
import betterwithmods.util.StackIngredient;
import betterwithmods.util.player.PlayerHelper;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.Random;

import static betterwithmods.common.blocks.BlockPlanter.EnumType.*;

public class BlockPlanter extends BWMBlock implements IMultiVariants, IGrowable {
    public static final PropertyEnum<EnumType> TYPE = PropertyEnum.create("plantertype", EnumType.class);

    public BlockPlanter() {
        super(Material.ROCK);
        this.setTickRandomly(true);
        this.setHardness(1.0F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, EMPTY));
        this.setHarvestLevel("pickaxe", 0);
    }

    public static ItemStack getStack(EnumType type) {
        return new ItemStack(BWMBlocks.PLANTER, 1, type.getMeta());
    }

    @Override
    public String[] getVariants() {
        return new String[]{"plantertype=empty", "plantertype=farmland", "plantertype=grass", "plantertype=soul_sand", "plantertype=fertile", "plantertype=sand", "plantertype=water_still", "plantertype=gravel", "plantertype=red_sand", "plantertype=dirt"};
    }

    public int colorMultiplier(IBlockState state, IBlockAccess world, BlockPos pos, int tintIndex) {
        return (state.getValue(TYPE) == GRASS && tintIndex > -1) ? world != null && pos != null ? BiomeColorHelper.getGrassColorAtPos(world, pos) : ColorizerGrass.getGrassColor(0.5D, 1.0D) : -1;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(TYPE).getMeta();
    }

    private EnumType getTypeFromStack(ItemStack stack) {
        for (EnumType type : EnumType.VALUES) {
            if (type.apply(stack)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = PlayerHelper.getHolding(player, hand);
        EnumType type = world.getBlockState(pos).getValue(TYPE);

        EnumType newType = getTypeFromStack(heldItem);
        switch (type) {
            case EMPTY:
                if (newType != null && newType != EMPTY && newType != FARMLAND && newType != FERTILE) {
                    if (world.isRemote)
                        return true;
                    if (player.isCreative() || InvUtils.usePlayerItem(player, EnumFacing.UP, heldItem, 1)) {
                        world.playSound(null, pos, newType == WATER ? SoundEvents.ITEM_BUCKET_EMPTY : newType.getState().getBlock().getSoundType(state, world, pos, player).getPlaceSound(), SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
                        player.swingArm(hand);
                        return world.setBlockState(pos, state.withProperty(TYPE, newType));
                    }
                }
                break;
            case WATER:
                if (heldItem.isItemEqual(new ItemStack(Items.BUCKET))) {
                    if (world.isRemote)
                        return true;
                    if (InvUtils.usePlayerItem(player, EnumFacing.UP, heldItem, 1))
                        InvUtils.givePlayer(player, EnumFacing.UP, InvUtils.asNonnullList(new ItemStack(Items.WATER_BUCKET)));
                    player.swingArm(hand);
                    world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
                    world.setBlockState(pos, state.withProperty(TYPE, EMPTY));
                }
                break;

            case GRASS:
            case DIRT:
                if (newType == FERTILE) {
                    ItemDye.applyBonemeal(heldItem, world, pos, player, hand);
                }
                if (newType == FARMLAND) {
                    heldItem.damageItem(1, player);
                    player.swingArm(hand);
                    world.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    world.setBlockState(pos, state.withProperty(TYPE, newType));
                    break;
                }
            case SOULSAND:
                break;
            case FERTILE:
            case FARMLAND:
                if (newType == FERTILE) {
                    if (world.isRemote)
                        return true;
                    if (InvUtils.usePlayerItem(player, EnumFacing.UP, heldItem, 1)) {
                        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.25F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                        world.setBlockState(pos, state.withProperty(TYPE, newType));
                        world.playEvent(2005, pos.up(), 0);
                    }
                    break;
                }
            case SAND:
            case GRAVEL:
            case REDSAND:
                if (newType == EMPTY) {
                    if (!player.isCreative()) {
                        InvUtils.givePlayer(player, EnumFacing.UP, InvUtils.asNonnullList(BWMRecipes.getStackFromState(type.getState())));
                    }
                    heldItem.damageItem(1, player);
                    world.playSound(null, pos, type.getState().getBlock().getSoundType(state, world, pos, player).getBreakSound(), SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
                    world.setBlockState(pos, state.withProperty(TYPE, newType));
                }
                break;
        }
        return false;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote) {
            EnumType type = world.getBlockState(pos).getValue(TYPE);
            BlockPos up = pos.up();
            if (world.isAirBlock(up)) {
                switch (type) {
                    case DIRT:
                        if (world.getLight(up) > 8) {
                            int xP = rand.nextInt(3) - 1;
                            int yP = rand.nextInt(3) - 1;
                            int zP = rand.nextInt(3) - 1;
                            BlockPos checkPos = pos.add(xP, yP, zP);
                            if (world.getBlockState(checkPos).getBlock() == Blocks.GRASS)
                                world.setBlockState(pos, this.getDefaultState().withProperty(TYPE, GRASS));
                        }
                        break;
                    case GRASS:
                        if (rand.nextInt(100) == 0) {
                            world.getBiome(pos).plantFlower(world, rand, up);
                            if (world.getLight(up) > 8) {
                                for (int i = 0; i < 4; i++) {
                                    int xP = rand.nextInt(3) - 1;
                                    int yP = rand.nextInt(3) - 1;
                                    int zP = rand.nextInt(3) - 1;
                                    BlockPos checkPos = pos.add(xP, yP, zP);
                                    if (world.getBlockState(checkPos) == Blocks.DIRT && world.getBlockState(checkPos) == Blocks.DIRT.getDefaultState())
                                        world.setBlockState(checkPos, Blocks.GRASS.getDefaultState());
                                }
                            }
                        }
                    case FERTILE:
                        if (world.getBlockState(up).getBlock() instanceof IPlantable) {
                            IPlantable plant = (IPlantable) world.getBlockState(up).getBlock();
                            if (this.canSustainPlant(world.getBlockState(pos), world, pos, EnumFacing.UP, plant) && world.getBlockState(up).getBlock().getTickRandomly()) {
                                IBlockState cropState = world.getBlockState(up);
                                world.getBlockState(up).getBlock().updateTick(world, up, cropState, rand);
                                world.getBlockState(up).getBlock().updateTick(world, up, cropState, rand);
                                if (rand.nextInt(100) == 0)
                                    world.setBlockState(pos, this.getDefaultState().withProperty(TYPE, EnumType.DIRT));
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
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
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public boolean isFertile(World world, BlockPos pos) {
        return world.getBlockState(pos).getValue(TYPE) == EnumType.FERTILE;
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing dir, IPlantable plant) {
        BlockPos up = pos.up();
        EnumPlantType plantType = plant.getPlantType(world, up);
        return dir == EnumFacing.UP && world.getBlockState(pos).getValue(TYPE).isType(plantType);
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (EnumType type : EnumType.VALUES)
            items.add(getStack(type));
    }

    @Override
    public void onPlantGrow(IBlockState state, World world, BlockPos pos, BlockPos source) {
        if (state.getValue(TYPE) == GRASS && source.getY() == pos.getY() + 1)
            world.setBlockState(pos, state.withProperty(TYPE, EnumType.DIRT));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(TYPE, EnumType.byMeta(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).getMeta();
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        if (face != EnumFacing.UP)
            return face == EnumFacing.DOWN ? BlockFaceShape.CENTER_BIG : BlockFaceShape.UNDEFINED;
        switch (state.getValue(TYPE)) {
            case EMPTY:
            case WATER:
                return BlockFaceShape.BOWL;
            default:
                return BlockFaceShape.SOLID;
        }
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return state.getValue(TYPE) == GRASS;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return canGrow(worldIn, pos, state, worldIn.isRemote);
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        //Borrowed from BlockGrass
        BlockPos blockpos = pos.up();

        for (int i = 0; i < 128; ++i) {
            BlockPos blockpos1 = blockpos;
            int j = 0;

            while (true) {
                if (j >= i / 16) {
                    if (worldIn.isAirBlock(blockpos1)) {
                        if (rand.nextInt(8) == 0) {
                            worldIn.getBiome(blockpos1).plantFlower(worldIn, rand, blockpos1);
                        } else {
                            IBlockState iblockstate1 = Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS);

                            if (Blocks.TALLGRASS.canBlockStay(worldIn, blockpos1, iblockstate1)) {
                                worldIn.setBlockState(blockpos1, iblockstate1, 3);
                            }
                        }
                    }

                    break;
                }

                blockpos1 = blockpos1.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

                if (worldIn.getBlockState(blockpos1.down()).getBlock() != Blocks.GRASS || worldIn.getBlockState(blockpos1).isNormalCube()) {
                    break;
                }

                ++j;
            }
        }
    }

    public enum EnumType implements IStringSerializable {
        EMPTY("empty", new IngredientTool("shovel"), Blocks.AIR.getDefaultState(), 0, new EnumPlantType[0]),
        FARMLAND("farmland", new IngredientTool(s -> s.getItem() instanceof ItemHoe, ItemStack.EMPTY), Blocks.DIRT.getDefaultState(), 1, new EnumPlantType[]{EnumPlantType.Crop, EnumPlantType.Plains}),
        GRASS("grass", StackIngredient.fromStacks(new ItemStack(Blocks.GRASS)), Blocks.GRASS.getDefaultState(), 2, new EnumPlantType[]{EnumPlantType.Plains}),
        SOULSAND("soul_sand", StackIngredient.fromStacks(new ItemStack(Blocks.SOUL_SAND)), Blocks.SOUL_SAND.getDefaultState(), 3, new EnumPlantType[]{EnumPlantType.Nether}),
        FERTILE("fertile", StackIngredient.fromStacks(new ItemStack(Items.DYE, 1, EnumDyeColor.WHITE.getDyeDamage())), Blocks.DIRT.getDefaultState(), 4, new EnumPlantType[]{EnumPlantType.Crop, EnumPlantType.Plains}),
        SAND("sand", StackIngredient.fromStacks(new ItemStack(Blocks.SAND)), Blocks.SAND.getDefaultState(), 5, new EnumPlantType[]{EnumPlantType.Desert, EnumPlantType.Beach}),
        WATER("water_still", StackIngredient.fromStacks(new ItemStack(Items.WATER_BUCKET)), Blocks.WATER.getDefaultState(), 6, new EnumPlantType[]{EnumPlantType.Water}),
        GRAVEL("gravel", StackIngredient.fromStacks(new ItemStack(Blocks.GRAVEL)), Blocks.GRAVEL.getDefaultState(), 7, new EnumPlantType[]{EnumPlantType.Cave}),
        REDSAND("red_sand", StackIngredient.fromStacks(new ItemStack(Blocks.SAND, 1, BlockSand.EnumType.RED_SAND.getMetadata())), Blocks.SAND.getDefaultState().withProperty(BlockSand.VARIANT, BlockSand.EnumType.RED_SAND), 8, new EnumPlantType[]{EnumPlantType.Desert, EnumPlantType.Beach}),
        DIRT("dirt", StackIngredient.fromStacks(new ItemStack(Blocks.DIRT)), Blocks.DIRT.getDefaultState(), 9, new EnumPlantType[]{EnumPlantType.Plains});

        private static final EnumType[] VALUES = values();

        private String name;
        private IBlockState state;
        private int meta;
        private EnumPlantType[] type;
        private Ingredient ingredient;

        EnumType(String name, Ingredient ingredient, IBlockState state, int meta, EnumPlantType[] type) {
            this.name = name;
            this.ingredient = ingredient;
            this.state = state;
            this.meta = meta;
            this.type = type;
        }

        public static EnumType byMeta(int meta) {
            return VALUES[meta];
        }

        @Override
        public String getName() {
            return name;
        }

        public int getMeta() {
            return meta;
        }

        public boolean isType(EnumPlantType type) {
            return this.type.length != 0 && Arrays.asList(this.type).contains(type);
        }

        public IBlockState getState() {
            return state;
        }

        public boolean apply(ItemStack stack) {
            return ingredient.apply(stack);
        }

        public ItemStack getStack() {
            return Lists.newArrayList(ingredient.getMatchingStacks()).stream().findFirst().orElse(ItemStack.EMPTY);
        }
    }
}
