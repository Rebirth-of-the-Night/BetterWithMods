package betterwithmods.common.world.gen.village;

import betterwithmods.common.BWMBlocks;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.List;
import java.util.Random;

/**
 * Created by primetoxinz on 5/21/17.
 */
public class Hut extends AbandonedVillagePiece {
    private boolean isTallHouse;
    private int tablePosition;

    public Hut() {
    }

    public Hut(StructureVillagePieces.Start start, int type, Random rand, StructureBoundingBox structurebb, EnumFacing facing) {
        super(start, type);
        this.setCoordBaseMode(facing);
        this.boundingBox = structurebb;
        this.isTallHouse = rand.nextBoolean();
        this.tablePosition = rand.nextInt(3);
    }

    @Override
    public Class<?> getComponentClass() {
        return Hut.class;
    }
    /**
     * (abstract) Helper method to write subclass data to NBT
     */
    protected void writeStructureToNBT(NBTTagCompound tagCompound) {
        super.writeStructureToNBT(tagCompound);
        tagCompound.setInteger("T", this.tablePosition);
        tagCompound.setBoolean("C", this.isTallHouse);
    }

    /**
     * (abstract) Helper method to read subclass data from NBT
     */
    protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
        super.readStructureFromNBT(tagCompound, p_143011_2_);
        this.tablePosition = tagCompound.getInteger("T");
        this.isTallHouse = tagCompound.getBoolean("C");
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes
     * Mineshafts at the end, it adds Fences...
     */
    public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
        if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);

            if (this.averageGroundLvl < 0) {
                return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 6 - 1, 0);
        }

        IBlockState primary = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
        IBlockState secondary = this.getBiomeSpecificBlockState(Blocks.PLANKS.getDefaultState());
        IBlockState stair = this.getBiomeSpecificBlockState(Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH));
        IBlockState supports = this.getBiomeSpecificBlockState(Blocks.LOG.getDefaultState());
        IBlockState table = this.getBiomeSpecificBlockState(BWMBlocks.WOOD_TABLE.getDefaultState());
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 3, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 3, 0, 4, primary, primary, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 1, 2, 0, 3, Blocks.DIRT.getDefaultState(), Blocks.DIRT.getDefaultState(), false);

        if (this.isTallHouse) {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 1, 2, 4, 3, supports, supports, false);
        } else {
            this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 1, 2, 5, 3, supports, supports, false);
        }

        this.setBlockState(worldIn, supports, 1, 4, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, supports, 2, 4, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, supports, 1, 4, 4, structureBoundingBoxIn);
        this.setBlockState(worldIn, supports, 2, 4, 4, structureBoundingBoxIn);
        this.setBlockState(worldIn, supports, 0, 4, 1, structureBoundingBoxIn);
        this.setBlockState(worldIn, supports, 0, 4, 2, structureBoundingBoxIn);
        this.setBlockState(worldIn, supports, 0, 4, 3, structureBoundingBoxIn);
        this.setBlockState(worldIn, supports, 3, 4, 1, structureBoundingBoxIn);
        this.setBlockState(worldIn, supports, 3, 4, 2, structureBoundingBoxIn);
        this.setBlockState(worldIn, supports, 3, 4, 3, structureBoundingBoxIn);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 0, 0, 3, 0, supports, supports, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 0, 3, 3, 0, supports, supports, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 4, 0, 3, 4, supports, supports, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 4, 3, 3, 4, supports, supports, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3, 3, secondary, secondary, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 1, 3, 3, 3, secondary, secondary, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 0, 2, 3, 0, secondary, secondary, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 4, 2, 3, 4, secondary, secondary, false);
        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 0, 2, 2, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 3, 2, 2, structureBoundingBoxIn);
        if (tablePosition > 0)
            this.setBlockState(worldIn, table, this.tablePosition, 1, 3, structureBoundingBoxIn);

        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 1, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 2, 0, structureBoundingBoxIn);

        if (this.getBlockStateFromPos(worldIn, 1, 0, -1, structureBoundingBoxIn).getMaterial() == Material.AIR && this.getBlockStateFromPos(worldIn, 1, -1, -1, structureBoundingBoxIn).getMaterial() != Material.AIR) {
            this.setBlockState(worldIn, stair, 1, 0, -1, structureBoundingBoxIn);

            if (this.getBlockStateFromPos(worldIn, 1, -1, -1, structureBoundingBoxIn).getBlock() == Blocks.GRASS_PATH) {
                this.setBlockState(worldIn, Blocks.GRASS.getDefaultState(), 1, -1, -1, structureBoundingBoxIn);
            }
        }

        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 4; ++j) {
                this.clearCurrentPositionBlocksUpwards(worldIn, j, 6, i, structureBoundingBoxIn);
                this.replaceAirAndLiquidDownwards(worldIn, primary, j, -1, i, structureBoundingBoxIn);
            }
        }

        this.spawnVillagers(worldIn, structureBoundingBoxIn, 1, 1, 2, 1);
        return true;
    }

    @Override
    public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int size) {
        return new StructureVillagePieces.PieceWeight(Hut.class, 3, MathHelper.getInt(random, 2 + size, 5 + size * 3));
    }

    @Override
    public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 4, 6, 5, facing);
        return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(pieces, structureboundingbox) == null ? new Hut(startPiece, p5, random, structureboundingbox, facing) : null;
    }


}