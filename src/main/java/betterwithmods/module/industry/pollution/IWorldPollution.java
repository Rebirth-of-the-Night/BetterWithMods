package betterwithmods.module.industry.pollution;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;

public interface IWorldPollution {
    void calculatePollutionReduction();

    void calculatePollutionSpread();

    void calculateLeafCount();

    void setPollution(ChunkPos pos, float newPollution);

    float getPollution(ChunkPos pos);

    void removePollution(ChunkPos pos);

    void readNBT(ChunkPos pos, NBTTagCompound tag);

    NBTTagCompound writeNBT(ChunkPos pos, NBTTagCompound tag);

    byte getLeafCount(ChunkPos pos);

    void setLeafCount(ChunkPos pos, byte leafCount);
}
