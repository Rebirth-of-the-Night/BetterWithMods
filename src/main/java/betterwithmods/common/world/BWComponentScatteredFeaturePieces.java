package betterwithmods.common.world;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.BlockAesthetic;
import betterwithmods.common.blocks.mechanical.BlockCookingPot;
import betterwithmods.module.hardcore.world.HCStructures;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.Random;

/**
 * Created by blueyu2 on 11/27/16.
 */
public class BWComponentScatteredFeaturePieces {
    public static class DesertPyramid extends ComponentScatteredFeaturePieces.DesertPyramid {
        private int worldX, worldZ;

        public DesertPyramid() {
        }

        DesertPyramid(Random random, int x, int z) {
            super(random, x, z);
            this.worldX = x;
            this.worldZ = z;
        }

        @Override
        public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
            boolean result = super.addComponentParts(worldIn, randomIn, structureBoundingBoxIn);

            //Replace clay with obsidian
            this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), 10, 0, 7, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), 10, 0, 8, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), 9, 0, 9, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), 11, 0, 9, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), 8, 0, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), 12, 0, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), 7, 0, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), 13, 0, 10, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), 9, 0, 11, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), 11, 0, 11, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), 10, 0, 12, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), 10, 0, 13, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), 10, 0, 10, structureBoundingBoxIn);
            for (int j2 = 0; j2 <= this.width - 1; j2 += this.width - 1) {
                this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), j2, 2, 2, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), j2, 3, 2, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), j2, 4, 1, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), j2, 4, 3, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), j2, 5, 2, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), j2, 6, 1, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), j2, 6, 3, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), j2, 7, 1, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), j2, 7, 2, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), j2, 7, 3, structureBoundingBoxIn);
            }
            for (int k2 = 2; k2 <= this.width - 3; k2 += this.width - 3 - 2) {
                this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), k2, 2, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), k2, 3, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), k2 - 1, 4, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), k2 + 1, 4, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), k2, 5, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), k2 - 1, 6, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), k2 + 1, 6, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), k2 - 1, 7, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), k2, 7, 0, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), k2 + 1, 7, 0, structureBoundingBoxIn);
            }
            this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), 9, 5, 0, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.OBSIDIAN.getDefaultState(), 11, 5, 0, structureBoundingBoxIn);

            //Replace Pressure Plate
            this.setBlockState(worldIn, Blocks.WOODEN_PRESSURE_PLATE.getDefaultState(), 10, -11, 10, structureBoundingBoxIn);

            if (HCStructures.isInRadius(worldIn, worldX, worldZ)) {
                //Dig hole
                this.setAir(worldIn, 10, 0, 10, structureBoundingBoxIn);
                this.setAir(worldIn, 11, 0, 10, structureBoundingBoxIn);
                this.setAir(worldIn, 10, 0, 9, structureBoundingBoxIn);
                this.setAir(worldIn, 10, -11, 10, structureBoundingBoxIn);
                for (int x = 0; x < 3; x++) {
                    for (int z = 0; z < 3; z++) {
                        this.setAir(worldIn, 9 + x, -12, 9 + z, structureBoundingBoxIn);
                        this.setAir(worldIn, 9 + x, -13, 9 + z, structureBoundingBoxIn);
                    }
                }

                //Create ladder
                for (int i = 0; i >= -13; i--)
                    this.setBlockState(worldIn, Blocks.LADDER.getDefaultState().withProperty(BlockLadder.FACING, EnumFacing.WEST), 11, i, 9, structureBoundingBoxIn);

                //Remove chest loot
                for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                    int k1 = enumfacing.getXOffset() * 2;
                    int l1 = enumfacing.getZOffset() * 2;
                    TileEntity tileentity = worldIn.getTileEntity(new BlockPos(this.getXWithOffset(10 + k1, 10 + l1), this.getYWithOffset(-11), this.getZWithOffset(10 + k1, 10 + l1)));
                    if (tileentity instanceof TileEntityChest)
                        ((TileEntityChest) tileentity).setLootTable(null, randomIn.nextLong());
                }
            } else
                this.setBlockState(worldIn, Blocks.ENCHANTING_TABLE.getDefaultState(), 10, 1, 10, structureBoundingBoxIn);

            return result;
        }

        private void setAir(World world, int x, int y, int z, StructureBoundingBox structureBoundingBox) {
            this.setBlockState(world, Blocks.AIR.getDefaultState(), x, y, z, structureBoundingBox);
        }
    }

    public static class SwampHut extends ComponentScatteredFeaturePieces.SwampHut {
        private boolean hasWitch;

        public SwampHut() {
        }

        public SwampHut(Random random, int x, int y) {
            super(random, x, y);
        }

        @Override
        protected void writeStructureToNBT(NBTTagCompound tagCompound) {
            super.writeStructureToNBT(tagCompound);
            tagCompound.setBoolean("Witch", this.hasWitch);
        }

        @Override
        protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager template) {
            super.readStructureFromNBT(tagCompound, template);
            this.hasWitch = tagCompound.getBoolean("Witch");
        }

        @Override
        public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
            if (!this.offsetToAverageGroundLevel(worldIn, structureBoundingBoxIn, 0)) {
                return false;
            } else {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 5, 1, 7, Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE), Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 2, 5, 4, 7, Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE), Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 0, 4, 1, 0, Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE), Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 2, 2, 3, 3, 2, Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE), Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 3, 1, 3, 6, Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE), Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 2, 3, 5, 3, 6, Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE), Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 2, 7, 4, 3, 7, Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE), Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 2, 1, 3, 2, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 0, 2, 5, 3, 2, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 7, 1, 3, 7, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 0, 7, 5, 3, 7, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
                this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 2, 3, 2, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 3, 3, 7, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 1, 3, 4, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 5, 3, 4, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.AIR.getDefaultState(), 5, 3, 5, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.FLOWER_POT.getDefaultState().withProperty(BlockFlowerPot.CONTENTS, BlockFlowerPot.EnumFlowerType.MUSHROOM_RED), 1, 3, 5, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.CRAFTING_TABLE.getDefaultState(), 3, 2, 6, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.CAULDRON.getDefaultState(), 4, 2, 6, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 1, 2, 1, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.OAK_FENCE.getDefaultState(), 5, 2, 1, structureBoundingBoxIn);
                IBlockState iblockstate = Blocks.SPRUCE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH);
                IBlockState iblockstate1 = Blocks.SPRUCE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
                IBlockState iblockstate2 = Blocks.SPRUCE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
                IBlockState iblockstate3 = Blocks.SPRUCE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 1, 6, 4, 1, iblockstate, iblockstate, false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 2, 0, 4, 7, iblockstate1, iblockstate1, false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 4, 2, 6, 4, 7, iblockstate2, iblockstate2, false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 4, 8, 6, 4, 8, iblockstate3, iblockstate3, false);

                for (int i = 2; i <= 7; i += 5) {
                    for (int j = 1; j <= 5; j += 4) {
                        this.replaceAirAndLiquidDownwards(worldIn, Blocks.LOG.getDefaultState(), j, -1, i, structureBoundingBoxIn);
                    }
                }

                if (!this.hasWitch) {
                    int amount = randomIn.nextInt(3);
                    for (int i = 0; i < amount + 1; i++) {
                        int x = this.getXWithOffset(i * 3, 5);
                        int y = this.getYWithOffset(2);
                        int z = this.getZWithOffset(i * 3, 5);

                        if (structureBoundingBoxIn.isVecInside(new BlockPos(x, y, z))) {
                            EntityWitch entitywitch = new EntityWitch(worldIn);
                            entitywitch.setLocationAndAngles((double) x + 0.5D, (double) y, (double) z + 0.5D, 0.0F, 0.0F);
                            entitywitch.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(x, y, z)), null);
                            entitywitch.enablePersistence();
                            worldIn.spawnEntity(entitywitch);
                        }
                    }
                    this.hasWitch = true;
                }
            }

            this.setBlockState(worldIn, Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE), 2, 2, 6, structureBoundingBoxIn);
            this.setBlockState(worldIn, Blocks.BREWING_STAND.getDefaultState(), 2, 3, 6, structureBoundingBoxIn);

            return true;
        }
    }

    public static class JunglePyramid extends ComponentScatteredFeaturePieces.JunglePyramid {
        private int worldX, worldZ;

        public JunglePyramid() {
        }

        JunglePyramid(Random rand, int x, int z) {
            super(rand, x, z);
            this.worldX = x;
            this.worldZ = z;
        }

        @Override
        public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
            boolean result = super.addComponentParts(worldIn, randomIn, structureBoundingBoxIn);
            this.setBlockState(worldIn, BWMBlocks.AESTHETIC.getDefaultState().withProperty(BlockAesthetic.TYPE, BlockAesthetic.EnumType.CHOPBLOCKBLOOD), 5, 4, 11, structureBoundingBoxIn);
            this.setBlockState(worldIn, BWMBlocks.AESTHETIC.getDefaultState().withProperty(BlockAesthetic.TYPE, BlockAesthetic.EnumType.CHOPBLOCKBLOOD), 6, 4, 11, structureBoundingBoxIn);
            this.setAir(worldIn, 6, 3, 10, structureBoundingBoxIn);
            this.setAir(worldIn, 5, 3, 10, structureBoundingBoxIn);

            if (HCStructures.isInRadius(worldIn, worldX, worldZ)) {
                //Remove hooks
                this.setAir(worldIn, 1, -3, 8, structureBoundingBoxIn);
                this.setAir(worldIn, 4, -3, 8, structureBoundingBoxIn);
                //Remove tripwire
                this.setAir(worldIn, 2, -3, 8, structureBoundingBoxIn);
                this.setAir(worldIn, 3, -3, 8, structureBoundingBoxIn);
                //Re-add hooks
                this.setBlockState(worldIn, Blocks.TRIPWIRE_HOOK.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.EAST), 1, -3, 8, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.TRIPWIRE_HOOK.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.WEST), 4, -3, 8, structureBoundingBoxIn);

                //Remove hooks
                this.setAir(worldIn, 7, -3, 1, structureBoundingBoxIn);
                this.setAir(worldIn, 7, -3, 5, structureBoundingBoxIn);
                //Remove tripwire
                this.setAir(worldIn, 7, -3, 2, structureBoundingBoxIn);
                this.setAir(worldIn, 7, -3, 3, structureBoundingBoxIn);
                this.setAir(worldIn, 7, -3, 4, structureBoundingBoxIn);
                //Re-add hooks
                this.setBlockState(worldIn, Blocks.TRIPWIRE_HOOK.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.NORTH), 7, -3, 1, structureBoundingBoxIn);
                this.setBlockState(worldIn, Blocks.TRIPWIRE_HOOK.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.SOUTH), 7, -3, 5, structureBoundingBoxIn);

                //Remove puzzle
                this.setAir(worldIn, 9, -2, 12, structureBoundingBoxIn);
                this.setAir(worldIn, 9, -2, 11, structureBoundingBoxIn);
                this.setAir(worldIn, 9, -3, 11, structureBoundingBoxIn);

                //Remove Chest Loot
                removeChest(worldIn, 8, -3, 3, randomIn);
                removeChest(worldIn, 9, -3, 10, randomIn);
            } else {
                this.setBlockState(worldIn, BWMBlocks.HAND_CRANK.getDefaultState(), 5, 3, 10, structureBoundingBoxIn);
                this.setBlockState(worldIn, BWMBlocks.COOKING_POTS.getDefaultState().withProperty(BlockCookingPot.TYPE,BlockCookingPot.EnumType.DRAGONVESSEL), 6, 3, 10, structureBoundingBoxIn);
            }

            return result;
        }

        private void setAir(World world, int x, int y, int z, StructureBoundingBox structureBoundingBox) {
            this.setBlockState(world, Blocks.AIR.getDefaultState(), x, y, z, structureBoundingBox);
        }

        private void removeChest(World worldIn, int x, int y, int z, Random randomIn) {
            TileEntity tileentity = worldIn.getTileEntity(new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z)));
            if (tileentity instanceof TileEntityChest)
                ((TileEntityChest) tileentity).setLootTable(null, randomIn.nextLong());
        }
    }
}
