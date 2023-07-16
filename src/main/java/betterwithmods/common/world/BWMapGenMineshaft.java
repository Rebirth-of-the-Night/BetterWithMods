package betterwithmods.common.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Random;

public class BWMapGenMineshaft extends MapGenMineshaft {
    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {

        Biome biome = world.getBiome(new BlockPos((chunkX << 4) + 8, 64, (chunkZ << 4) + 8));
        MapGenMineshaft.Type type = BiomeDictionary.hasType(biome, BiomeDictionary.Type.MESA) ? MapGenMineshaft.Type.MESA : MapGenMineshaft.Type.NORMAL;
        return new BWStructureMineshaftStart(this.world, this.rand, chunkX, chunkZ, type);
    }

    public static class BWStructureMineshaftStart extends StructureStart
    {
        private MapGenMineshaft.Type mineShaftType;

        public BWStructureMineshaftStart() {
        }

        public BWStructureMineshaftStart(World world, Random random, int chunkX, int chunkZ, MapGenMineshaft.Type type)
        {
            super(chunkX, chunkZ);
            this.mineShaftType = type;
            BWStructureMineshaftPieces.Room room = new BWStructureMineshaftPieces.Room(0, random, (chunkX << 4) + 2, (chunkZ << 4) + 2, this.mineShaftType);
            this.components.add(room);
            room.buildComponent(room, this.components, random);
            this.updateBoundingBox();

            if (type == MapGenMineshaft.Type.MESA)
            {
                int yoffset = world.getSeaLevel() - this.boundingBox.maxY + this.boundingBox.getYSize() / 2 + 5;
                this.boundingBox.offset(0, yoffset, 0);

                for (StructureComponent structurecomponent : this.components)
                {
                    structurecomponent.offset(0, yoffset, 0);
                }
            }
            else
            {
                this.markAvailableHeight(world, random, 10);
            }
        }
    }
}
