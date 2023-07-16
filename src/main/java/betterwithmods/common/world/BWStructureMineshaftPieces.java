package betterwithmods.common.world;

import betterwithmods.module.tweaks.MineshaftGeneration;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureMineshaftPieces;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BWStructureMineshaftPieces {
    private static StructureMineshaftPieces.Peice createRandomShaftPiece(List<StructureComponent> structureComponents, Random random, int x, int y, int z, @Nullable EnumFacing facing, int componentType, MapGenMineshaft.Type type) {
        int i = random.nextInt(100);

        if (i >= 80) {
            StructureBoundingBox structureboundingbox = BWStructureMineshaftPieces.Cross.findCrossing(structureComponents, random, x, y, z, facing);

            if (structureboundingbox != null)
                return new BWStructureMineshaftPieces.Cross(componentType, random, structureboundingbox, facing, type);
        } else if (i >= 70) {
            StructureBoundingBox structureboundingbox1 = BWStructureMineshaftPieces.Stairs.findStairs(structureComponents, random, x, y, z, facing);

            if (structureboundingbox1 != null)
                return new BWStructureMineshaftPieces.Stairs(componentType, random, structureboundingbox1, facing, type);
        } else {
            StructureBoundingBox structureboundingbox2 = BWStructureMineshaftPieces.Corridor.findCorridorSize(structureComponents, random, x, y, z, facing);

            if (structureboundingbox2 != null)
                return new BWStructureMineshaftPieces.Corridor(componentType, random, structureboundingbox2, facing, type);
        }

        return null;
    }

    private static StructureMineshaftPieces.Peice generateAndAddPiece(StructureComponent component, List<StructureComponent> structureComponents, Random random, int x, int y, int z, EnumFacing facing, int componentType) {
        if (componentType > 8) {
            return null;
        } else if (Math.abs(x - component.getBoundingBox().minX) <= 80 && Math.abs(z - component.getBoundingBox().minZ) <= 80) {
            MapGenMineshaft.Type type = ((StructureMineshaftPieces.Peice) component).mineShaftType;
            StructureMineshaftPieces.Peice piece = createRandomShaftPiece(structureComponents, random, x, y, z, facing, componentType + 1, type);

            if (piece != null) {
                structureComponents.add(piece);
                piece.buildComponent(component, structureComponents, random);
            }

            return piece;
        } else {
            return null;
        }
    }

    public static class Corridor extends StructureMineshaftPieces.Corridor {
        public Corridor() {
        }

        public Corridor(int componentType, Random random, StructureBoundingBox boundingBox, EnumFacing facing, MapGenMineshaft.Type type) {
            super(componentType, random, boundingBox, facing, type);
        }

        @Override
        public void buildComponent(StructureComponent component, List<StructureComponent> structureComponents, Random rand) {
            int componentType = this.getComponentType();
            int size = rand.nextInt(4);
            EnumFacing enumfacing = this.getCoordBaseMode();

            if (enumfacing != null) {
                switch (enumfacing) {
                    case NORTH:
                    default:

                        if (size <= 1) {
                            BWStructureMineshaftPieces.generateAndAddPiece(component, structureComponents, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, enumfacing, componentType);
                        } else if (size == 2) {
                            BWStructureMineshaftPieces.generateAndAddPiece(component, structureComponents, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, EnumFacing.WEST, componentType);
                        } else {
                            BWStructureMineshaftPieces.generateAndAddPiece(component, structureComponents, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, EnumFacing.EAST, componentType);
                        }

                        break;
                    case SOUTH:

                        if (size <= 1) {
                            BWStructureMineshaftPieces.generateAndAddPiece(component, structureComponents, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, enumfacing, componentType);
                        } else if (size == 2) {
                            BWStructureMineshaftPieces.generateAndAddPiece(component, structureComponents, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ - 3, EnumFacing.WEST, componentType);
                        } else {
                            BWStructureMineshaftPieces.generateAndAddPiece(component, structureComponents, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ - 3, EnumFacing.EAST, componentType);
                        }

                        break;
                    case WEST:

                        if (size <= 1) {
                            BWStructureMineshaftPieces.generateAndAddPiece(component, structureComponents, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, enumfacing, componentType);
                        } else if (size == 2) {
                            BWStructureMineshaftPieces.generateAndAddPiece(component, structureComponents, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, EnumFacing.NORTH, componentType);
                        } else {
                            BWStructureMineshaftPieces.generateAndAddPiece(component, structureComponents, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, EnumFacing.SOUTH, componentType);
                        }

                        break;
                    case EAST:

                        if (size <= 1) {
                            BWStructureMineshaftPieces.generateAndAddPiece(component, structureComponents, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, enumfacing, componentType);
                        } else if (size == 2) {
                            BWStructureMineshaftPieces.generateAndAddPiece(component, structureComponents, rand, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, EnumFacing.NORTH, componentType);
                        } else {
                            BWStructureMineshaftPieces.generateAndAddPiece(component, structureComponents, rand, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, EnumFacing.SOUTH, componentType);
                        }
                }
            }

            if (componentType < 8) {
                if (enumfacing != EnumFacing.NORTH && enumfacing != EnumFacing.SOUTH) {
                    for (int i1 = this.boundingBox.minX + 3; i1 + 3 <= this.boundingBox.maxX; i1 += 5) {
                        int j1 = rand.nextInt(5);

                        if (j1 == 0) {
                            BWStructureMineshaftPieces.generateAndAddPiece(component, structureComponents, rand, i1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, componentType + 1);
                        } else if (j1 == 1) {
                            BWStructureMineshaftPieces.generateAndAddPiece(component, structureComponents, rand, i1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, componentType + 1);
                        }
                    }
                } else {
                    for (int k = this.boundingBox.minZ + 3; k + 3 <= this.boundingBox.maxZ; k += 5) {
                        int l = rand.nextInt(5);

                        if (l == 0) {
                            BWStructureMineshaftPieces.generateAndAddPiece(component, structureComponents, rand, this.boundingBox.minX - 1, this.boundingBox.minY, k, EnumFacing.WEST, componentType + 1);
                        } else if (l == 1) {
                            BWStructureMineshaftPieces.generateAndAddPiece(component, structureComponents, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, k, EnumFacing.EAST, componentType + 1);
                        }
                    }
                }
            }
        }

        @Override
        protected IBlockState getFenceBlock() {
            return MineshaftGeneration.supports.getBlockState(this);
        }

        @Override
        protected IBlockState getPlanksBlock() {
            return MineshaftGeneration.planks.getBlockState(this);
        }

        protected IBlockState getRailBlock() { return MineshaftGeneration.rail.getBlockState(this); }

        public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
            boolean success = super.addComponentParts(worldIn, randomIn, structureBoundingBoxIn);

            if (success && this.hasRails) {
                IBlockState rail = getRailBlock();

                for (int i = 0; i <= this.sectionCount * 5 - 1; ++i) {
                    IBlockState railBlock = this.getBlockStateFromPos(worldIn, 1, 0, i, structureBoundingBoxIn);

                    if (railBlock.getBlock() instanceof BlockRailBase) {
                        float skyBrightness = this.getSkyBrightness(worldIn, 1, 0, i, structureBoundingBoxIn) > 8 ? 0.9F : 0.7F;
                        this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, skyBrightness, 1, 0, i, rail);
                    }
                }

            }

            return success;
        }
    }

    public static class Cross extends StructureMineshaftPieces.Cross {
        public Cross() {
        }

        public Cross(int componentType, Random random, StructureBoundingBox boundingBox, @Nullable EnumFacing facing, MapGenMineshaft.Type type) {
            super(componentType, random, boundingBox, facing, type);
        }

        @Override
        public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
            int i = this.getComponentType();

            switch (this.corridorDirection) {
                case NORTH:
                default:
                    BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, i);
                    BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, i);
                    BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, i);
                    break;
                case SOUTH:
                    BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i);
                    BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, i);
                    BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, i);
                    break;
                case WEST:
                    BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, i);
                    BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i);
                    BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, i);
                    break;
                case EAST:
                    BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, i);
                    BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i);
                    BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, i);
            }

            if (this.isMultipleFloors) {
                if (rand.nextBoolean()) {
                    BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ - 1, EnumFacing.NORTH, i);
                }

                if (rand.nextBoolean()) {
                    BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, EnumFacing.WEST, i);
                }

                if (rand.nextBoolean()) {
                    BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, EnumFacing.EAST, i);
                }

                if (rand.nextBoolean()) {
                    BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i);
                }
            }
        }

        @Override
        protected IBlockState getFenceBlock() {
            return MineshaftGeneration.supports.getBlockState(this);
        }

        @Override
        protected IBlockState getPlanksBlock() {
            return MineshaftGeneration.planks.getBlockState(this);
        }
    }

    public static class Room extends StructureMineshaftPieces.Room {
        public Room() {
        }

        public Room(int componentType, Random random, int chunkX, int chunkZ, MapGenMineshaft.Type type) {
            super(componentType, random, chunkX, chunkZ, type);
        }

        public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
            int i = this.getComponentType();
            int j = this.boundingBox.getYSize() - 3 - 1;

            if (j <= 0) {
                j = 1;
            }

            int k;

            for (k = 0; k < this.boundingBox.getXSize(); k = k + 4) {
                k = k + rand.nextInt(this.boundingBox.getXSize());

                if (k + 3 > this.boundingBox.getXSize()) {
                    break;
                }

                StructureMineshaftPieces.Peice piece = BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + k, this.boundingBox.minY + rand.nextInt(j) + 1, this.boundingBox.minZ - 1, EnumFacing.NORTH, i);

                if (piece != null) {
                    StructureBoundingBox boundingBox = piece.getBoundingBox();
                    this.connectedRooms.add(new StructureBoundingBox(boundingBox.minX, boundingBox.minY, this.boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, this.boundingBox.minZ + 1));
                }
            }

            for (k = 0; k < this.boundingBox.getXSize(); k = k + 4) {
                k = k + rand.nextInt(this.boundingBox.getXSize());

                if (k + 3 > this.boundingBox.getXSize()) {
                    break;
                }

                StructureMineshaftPieces.Peice piece = BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX + k, this.boundingBox.minY + rand.nextInt(j) + 1, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i);

                if (piece != null) {
                    StructureBoundingBox boundingBox = piece.getBoundingBox();
                    this.connectedRooms.add(new StructureBoundingBox(boundingBox.minX, boundingBox.minY, this.boundingBox.maxZ - 1, boundingBox.maxX, boundingBox.maxY, this.boundingBox.maxZ));
                }
            }

            for (k = 0; k < this.boundingBox.getZSize(); k = k + 4) {
                k = k + rand.nextInt(this.boundingBox.getZSize());

                if (k + 3 > this.boundingBox.getZSize()) {
                    break;
                }

                StructureMineshaftPieces.Peice piece = BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY + rand.nextInt(j) + 1, this.boundingBox.minZ + k, EnumFacing.WEST, i);

                if (piece != null) {
                    StructureBoundingBox boundingBox = piece.getBoundingBox();
                    this.connectedRooms.add(new StructureBoundingBox(this.boundingBox.minX, boundingBox.minY, boundingBox.minZ, this.boundingBox.minX + 1, boundingBox.maxY, boundingBox.maxZ));
                }
            }

            for (k = 0; k < this.boundingBox.getZSize(); k = k + 4) {
                k = k + rand.nextInt(this.boundingBox.getZSize());

                if (k + 3 > this.boundingBox.getZSize()) {
                    break;
                }

                StructureComponent structurecomponent = BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + rand.nextInt(j) + 1, this.boundingBox.minZ + k, EnumFacing.EAST, i);

                if (structurecomponent != null) {
                    StructureBoundingBox boundingBox = structurecomponent.getBoundingBox();
                    this.connectedRooms.add(new StructureBoundingBox(this.boundingBox.maxX - 1, boundingBox.minY, boundingBox.minZ, this.boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ));
                }
            }
        }

        protected IBlockState getFloorBlock() {
            return MineshaftGeneration.roomFloor.getBlockState(this);
        }

        public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
        {
            boolean success = super.addComponentParts(worldIn, randomIn, structureBoundingBoxIn);

            if (success)
            {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX, this.boundingBox.minY, this.boundingBox.maxZ, getFloorBlock(), Blocks.AIR.getDefaultState(), true);
            }

            return success;
        }
    }

    public static class Stairs extends StructureMineshaftPieces.Stairs {
        public Stairs() {
        }

        public Stairs(int componentType, Random random, StructureBoundingBox boundingBox, EnumFacing facing, MapGenMineshaft.Type type) {
            super(componentType, random, boundingBox, facing, type);
        }

        public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
            int i = this.getComponentType();
            EnumFacing enumfacing = this.getCoordBaseMode();

            if (enumfacing != null) {
                switch (enumfacing) {
                    case NORTH:
                    default:
                        BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, i);
                        break;
                    case SOUTH:
                        BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, i);
                        break;
                    case WEST:
                        BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.WEST, i);
                        break;
                    case EAST:
                        BWStructureMineshaftPieces.generateAndAddPiece(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.EAST, i);
                }
            }
        }

    }
}
