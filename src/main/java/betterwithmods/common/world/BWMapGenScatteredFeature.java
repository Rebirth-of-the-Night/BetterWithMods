package betterwithmods.common.world;

import betterwithmods.module.GlobalConfig;
import com.google.common.collect.Sets;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;

/**
 * Created by blueyu2 on 11/27/16.
 */
public class BWMapGenScatteredFeature extends MapGenScatteredFeature {
    public static HashMap<String, Set<Biome>> STRUCTURE_BIOMES = new HashMap<>();

    static {
        STRUCTURE_BIOMES.put("WitchHut", Sets.newHashSet(Biomes.SWAMPLAND, Biomes.MUTATED_SWAMPLAND, Biomes.ROOFED_FOREST, Biomes.MUTATED_ROOFED_FOREST, Biomes.EXTREME_HILLS));
        STRUCTURE_BIOMES.put("SandTemple", Sets.newHashSet(Biomes.DESERT, Biomes.DESERT_HILLS, Biomes.MUTATED_DESERT, Biomes.SAVANNA, Biomes.MESA));
        STRUCTURE_BIOMES.put("Igloo", Sets.newHashSet(Biomes.ICE_PLAINS, Biomes.MUTATED_ICE_FLATS, Biomes.ICE_MOUNTAINS, Biomes.COLD_TAIGA, Biomes.COLD_TAIGA_HILLS, Biomes.MUTATED_TAIGA_COLD));
        STRUCTURE_BIOMES.put("StoneTemple", Sets.newHashSet(Biomes.JUNGLE, Biomes.JUNGLE_EDGE, Biomes.JUNGLE_HILLS, Biomes.MUTATED_JUNGLE, Biomes.MUTATED_JUNGLE_EDGE, Biomes.PLAINS, Biomes.MUTATED_REDWOOD_TAIGA, Biomes.FOREST));
    }

    public static StructureComponent getComponent(World world, Random random, int chunkX, int chunkZ, Biome biome) {
        if (GlobalConfig.debug)
            System.out.printf("/tp %s ~ %s\n", chunkX * 16, chunkZ * 16);
        for (String structure : STRUCTURE_BIOMES.keySet()) {
            if (STRUCTURE_BIOMES.get(structure).contains(biome)) {
                switch (structure) {
                    case "WitchHut":
                        return new BWComponentScatteredFeaturePieces.SwampHut(random, chunkX * 16, chunkZ * 16);
                    case "SandTemple":
                        return new BWComponentScatteredFeaturePieces.DesertPyramid(random, chunkX * 16, chunkZ * 16);
                    case "Igloo":
                        return new ComponentScatteredFeaturePieces.Igloo(random, chunkX * 16, chunkZ * 16);
                    case "StoneTemple":
                        return new BWComponentScatteredFeaturePieces.JunglePyramid(random, chunkX * 16, chunkZ * 16);
                }
            }
        }
        return null;
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return new BWMapGenScatteredFeature.Start(this.world, this.rand, chunkX, chunkZ);
    }

    public static class Start extends MapGenScatteredFeature.Start {
        public Start() {
        }

        Start(World worldIn, Random random, int chunkX, int chunkZ) {
            this(worldIn, random, chunkX, chunkZ, worldIn.getBiome(new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8)));
        }

        Start(World worldIn, Random random, int chunkX, int chunkZ, Biome biomeIn) {
            StructureComponent component = getComponent(worldIn, random, chunkX, chunkZ, biomeIn);
            if (component != null)
                components.add(component);
            this.updateBoundingBox();
        }
    }
}
