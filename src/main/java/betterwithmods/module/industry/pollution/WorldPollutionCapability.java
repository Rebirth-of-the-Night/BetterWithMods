package betterwithmods.module.industry.pollution;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class WorldPollutionCapability {
    @CapabilityInject(IWorldPollution.class)
    public static Capability<IWorldPollution> POLLUTION = null;

    public static class Impl implements Capability.IStorage<IWorldPollution> {
        @Override
        public NBTBase writeNBT(Capability<IWorldPollution> capability, IWorldPollution pollution, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<IWorldPollution> capability, IWorldPollution pollution, EnumFacing side, NBTBase nbt) {

        }
    }

    public static class Default implements IWorldPollution {
        private HashMap<ChunkPos, Float> pollution = new HashMap<>();
        private HashMap<ChunkPos, Byte> leaves = new HashMap<>();
        private World world;

        public Default() {
        }

        public Default(World world) {
            this.world = world;
        }

        @Override
        public void calculatePollutionReduction() {
            if (!pollution.isEmpty()) {
                for (ChunkPos pos : pollution.keySet()) {
                    Chunk chunk = world.getChunkProvider().getLoadedChunk(pos.x, pos.z);
                    if (chunk != null) {
                        float pollutionMod = 1.0F;
                        Biome biome = Biome.getBiome(chunk.getBiomeArray()[127], Biomes.PLAINS);
                        for (BiomeDictionary.Type type : BiomeDictionary.getTypes(biome)) {
                            pollutionMod *= getPollutionReduction(type);
                        }
                        if (world.isRaining() && biome.canRain())
                            pollutionMod *= 0.8F;
                        float stat = pollution.get(pos) * pollutionMod;
                        if (getLeafCount(pos) > 0) {
                            float reduction = getLeafCount(pos) * 0.001F;
                            stat -= stat * reduction;
                        }
                        pollution.put(pos, stat);
                    }
                }
            }
        }

        @Override
        public void calculateLeafCount() {
            if (!leaves.isEmpty()) {
                for (ChunkPos pos : leaves.keySet()) {
                    Chunk chunk = world.getChunkProvider().getLoadedChunk(pos.x, pos.z);
                    if (chunk != null) {
                        byte leafCount = 0;
                        for (int x = 0; x < 16; x++) {
                            for (int z = 0; z < 16; z++) {
                                BlockPos p = pos.getBlock(x, 255, z);
                                p = world.getHeight(p).down();
                                if (world.getBlockState(p).getBlock() instanceof BlockLeaves) {
                                    IBlockState state = world.getBlockState(p);
                                    if (state.getProperties().containsKey(BlockLeaves.DECAYABLE) && state.getValue(BlockLeaves.DECAYABLE))
                                        leafCount++;
                                }

                            }
                        }
                        setLeafCount(pos, leafCount);
                    }
                }
            }
        }

        private float getPollutionReduction(BiomeDictionary.Type type) {
            return Pollution.handler.biomeMods.getOrDefault(type.getName(), 1.0F);
        }

        @Override
        public float getPollution(ChunkPos pos) {
            return pollution.containsKey(pos) ? pollution.get(pos) : -1;
        }

        @Override
        public void removePollution(ChunkPos pos) {
            if (pollution.containsKey(pos))
                pollution.remove(pos);
            if (leaves.containsKey(pos))
                leaves.remove(pos);
        }

        @Override
        public void setPollution(ChunkPos pos, float value) {
            if (value < 0) value = 0F;
            pollution.put(pos, value);
        }

        @Override
        public void calculatePollutionSpread() {
            if (!pollution.isEmpty()) {
                List<ChunkPos> toUpdate = new ArrayList<>();
                for (ChunkPos pos : pollution.keySet()) {
                    Chunk chunk = world.getChunk(pos.x, pos.z);
                    if (chunk.isLoaded()) {
                        Random rand = chunk.getRandomWithSeed(9850327L);
                        if (rand.nextInt(30) == 0) {
                            toUpdate.add(pos);
                        }
                    }
                }
                if (!toUpdate.isEmpty()) {
                    calculatePollutionSpread(toUpdate);
                }
            }
        }

        @Override
        public void setLeafCount(ChunkPos pos, byte leafCount) {
            if (leafCount < 0) leafCount = 0;
            leaves.put(pos, leafCount);
        }

        @Override
        public byte getLeafCount(ChunkPos pos) {
            return leaves.containsKey(pos) ? leaves.get(pos) : -1;
        }

        private void calculatePollutionSpread(List<ChunkPos> pos) {
            List<ChunkPos> finalPos = new ArrayList<>();
            for (ChunkPos p : pos) {
                Chunk chunk = world.getChunkProvider().getLoadedChunk(p.x, p.z);
                if (chunk == null || !chunk.isLoaded()) {
                    continue;
                }
                boolean valid = true;
                for (int x = -1; x < 2; x++) {
                    for (int z = -1; z < 2; z++) {
                        if (x * x != z * z) {
                            Chunk toCheck = world.getChunkProvider().getLoadedChunk(p.x + x, p.z + z);
                            if (toCheck != null && toCheck.isLoaded()) {
                                if (finalPos.contains(toCheck.getPos()))
                                    valid = false;
                            }
                        }
                    }
                }
                if (valid)
                    finalPos.add(p);
            }
            finalPos.forEach(p -> calculatePollutionSpread(world, p));
        }

        private void calculatePollutionSpread(World world, ChunkPos pos) {
            float pollution = getPollution(pos);
            List<ChunkPos> validChunks = new ArrayList<>();
            for (int x = -1; x < 2; x++) {
                for (int z = -1; z < 2; z++) {
                    if (x * x != z * z) {
                        Chunk chunk = world.getChunkProvider().getLoadedChunk(pos.x + x, pos.z + z);
                        if (chunk != null) {
                            if (chunk.isLoaded() && this.pollution.containsKey(chunk.getPos())) {
                                validChunks.add(chunk.getPos());
                            }
                        }
                    }
                }
            }
            if (!validChunks.isEmpty()) {
                for (ChunkPos p : validChunks) {
                    float pollutionCheck = getPollution(p);
                    if (pollution == pollutionCheck) {
                    } else if (pollution > pollutionCheck) {
                        calculateNewPollution(pos, p);
                    } else {
                        calculateNewPollution(p, pos);
                    }
                }
            }
        }

        //TODO: Numbers may need to be adjusted.
        private void calculateNewPollution(ChunkPos fromChunk, ChunkPos toChunk) {
            float from = getPollution(fromChunk);
            float to = getPollution(toChunk);
            if (to / from > 0.2F) {
                float change = from * 0.2F;
                from -= change;
                to += change;
            } else {
                float difference = to / from;
                if (difference == 0) difference = from / 4;
                else difference /= 3;
                from -= difference;
                to += difference;
            }
            setPollution(fromChunk, from);
            setPollution(toChunk, to);
        }

        @Override
        public void readNBT(ChunkPos pos, NBTTagCompound tag) {
            float pollution = 0F;
            byte leafCount = 0;
            if (tag.hasKey("pollution"))
                pollution = tag.getFloat("pollution");
            if (tag.hasKey("leaves"))
                leafCount = tag.getByte("leaves");
            setPollution(pos, pollution);
            setLeafCount(pos, leafCount);
        }

        @Override
        public NBTTagCompound writeNBT(ChunkPos pos, NBTTagCompound tag) {
            float pollution = getPollution(pos);
            byte leafCount = getLeafCount(pos);
            if (pollution < 0) pollution = 0;
            if (leafCount < 0) leafCount = 0;
            tag.setFloat("pollution", pollution);
            tag.setByte("leaves", leafCount);
            return tag;
        }
    }
}
