package betterwithmods.common.world.gen.village;

import betterwithmods.module.hardcore.world.HCVillages;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;

import java.util.List;
import java.util.Random;

/**
 * Created by primetoxinz on 5/25/17.
 */
public class Hut2 extends AbandonedVillagePiece {

    public Hut2() {
    }

    public Hut2(StructureVillagePieces.Start start, int p_i45566_2_, Random rand, StructureBoundingBox p_i45566_4_, EnumFacing facing) {
        super(start, p_i45566_2_);
        this.setCoordBaseMode(facing);
        this.boundingBox = p_i45566_4_;
    }


    public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
        if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);

            if (this.averageGroundLvl < 0) {
                return true;
            }

            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 6 - 1, 0);
        }

        IBlockState iblockstate = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
        IBlockState iblockstate1 = this.getBiomeSpecificBlockState(Blocks.PLANKS.getDefaultState());
        IBlockState iblockstate2 = this.getBiomeSpecificBlockState(Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH));
        IBlockState iblockstate3 = this.getBiomeSpecificBlockState(Blocks.LOG.getDefaultState());
        IBlockState iblockstate4 = this.getBiomeSpecificBlockState(Blocks.OAK_FENCE.getDefaultState());
        IBlockState iblockstate5 = Blocks.LADDER.getDefaultState().withProperty(BlockLadder.FACING, EnumFacing.SOUTH);
        if (HCVillages.disableAllComplexBlocks) {
            iblockstate2 = iblockstate;
            iblockstate4 = Blocks.AIR.getDefaultState();
            iblockstate5 = Blocks.AIR.getDefaultState();
        }
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 4, 0, 4, iblockstate, iblockstate, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 0, 4, 4, 4, iblockstate3, iblockstate3, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 1, 3, 4, 3, iblockstate1, iblockstate1, false);
        this.setBlockState(worldIn, iblockstate, 0, 1, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate, 0, 2, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate, 0, 3, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate, 4, 1, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate, 4, 2, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate, 4, 3, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate, 0, 1, 4, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate, 0, 2, 4, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate, 0, 3, 4, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate, 4, 1, 4, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate, 4, 2, 4, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate, 4, 3, 4, structureBoundingBoxIn);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 1, 1, 0, 3, 3, iblockstate1, iblockstate1, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 1, 1, 4, 3, 3, iblockstate1, iblockstate1, false);
        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 4, 3, 3, 4, iblockstate1, iblockstate1, false);
//        this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 0, 2, 2, structureBoundingBoxIn);
//        this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 2, 2, 4, structureBoundingBoxIn);
//        this.setBlockState(worldIn, Blocks.GLASS_PANE.getDefaultState(), 4, 2, 2, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 0, 2, 2, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 2, 2, 4, structureBoundingBoxIn);
        this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 4, 2, 2, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate1, 1, 1, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate1, 1, 2, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate1, 1, 3, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate1, 2, 3, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate1, 3, 3, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate1, 3, 2, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate1, 3, 1, 0, structureBoundingBoxIn);

        if (this.getBlockStateFromPos(worldIn, 2, 0, -1, structureBoundingBoxIn).getMaterial() == Material.AIR && this.getBlockStateFromPos(worldIn, 2, -1, -1, structureBoundingBoxIn).getMaterial() != Material.AIR) {
            this.setBlockState(worldIn, iblockstate2, 2, 0, -1, structureBoundingBoxIn);

            if (this.getBlockStateFromPos(worldIn, 2, -1, -1, structureBoundingBoxIn).getBlock() == Blocks.GRASS_PATH) {
                this.setBlockState(worldIn, Blocks.GRASS.getDefaultState(), 2, -1, -1, structureBoundingBoxIn);
            }
        }

        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 3, 3, 3, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);


        this.setBlockState(worldIn, iblockstate4, 0, 5, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate4, 1, 5, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate4, 2, 5, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate4, 3, 5, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate4, 4, 5, 0, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate4, 0, 5, 4, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate4, 1, 5, 4, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate4, 2, 5, 4, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate4, 3, 5, 4, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate4, 4, 5, 4, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate4, 4, 5, 1, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate4, 4, 5, 2, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate4, 4, 5, 3, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate4, 0, 5, 1, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate4, 0, 5, 2, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate4, 0, 5, 3, structureBoundingBoxIn);

        this.setBlockState(worldIn, iblockstate5, 3, 1, 3, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate5, 3, 2, 3, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate5, 3, 3, 3, structureBoundingBoxIn);
        this.setBlockState(worldIn, iblockstate5, 3, 4, 3, structureBoundingBoxIn);


        this.placeTorch(worldIn, EnumFacing.NORTH, 2, 3, 1, structureBoundingBoxIn);

        for (int j = 0; j < 5; ++j) {
            for (int i = 0; i < 5; ++i) {
                this.clearCurrentPositionBlocksUpwards(worldIn, i, 6, j, structureBoundingBoxIn);
                this.replaceAirAndLiquidDownwards(worldIn, iblockstate, i, -1, j, structureBoundingBoxIn);
            }
        }

        this.spawnVillagers(worldIn, structureBoundingBoxIn, 1, 1, 2, 1);
        return true;
    }

    @Override
    public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int size) {
        return new StructureVillagePieces.PieceWeight(Hut2.class, 4, MathHelper.getInt(random, 2 + size, 4 + size * 2));
    }

    @Override
    public Class<?> getComponentClass() {
        return Hut2.class;
    }

    @Override
    public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 5, 6, 5, facing);
        return StructureComponent.findIntersecting(pieces, structureboundingbox) != null ? null : new Hut2(startPiece, p5, random, structureboundingbox, facing);
    }
}
