package net.noahsarch.deeperdark.duck;

import java.util.ArrayList;

public interface ChunkTimeDataAccessor {
    long deeperdark$getLastTick();
    void deeperdark$setLastTick(long tick);
    long deeperdark$getSimulationVersion();
    void deeperdark$setSimulationVersion(long ver);
    ArrayList<Long> deeperdark$getSimulationBlocks();
    void deeperdark$setSimulationBlocks(ArrayList<Long> positions);
    void deeperdark$addSimulationBlock(long blockPos);
    void deeperdark$removeSimulationBlock(long blockPos);
}
