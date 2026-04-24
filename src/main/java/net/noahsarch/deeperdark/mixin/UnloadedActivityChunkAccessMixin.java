package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.level.chunk.ChunkAccess;
import net.noahsarch.deeperdark.duck.ChunkTimeDataAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;

@Mixin(ChunkAccess.class)
public abstract class UnloadedActivityChunkAccessMixin implements ChunkTimeDataAccessor {

    @Unique
    private long deeperdark$lastTick = 0;

    @Unique
    private long deeperdark$simulationVersion = 0;

    @Unique
    private ArrayList<Long> deeperdark$simulationBlocks = new ArrayList<>();

    @Override
    public long deeperdark$getLastTick() {
        return this.deeperdark$lastTick;
    }

    @Override
    public void deeperdark$setLastTick(long tick) {
        this.deeperdark$lastTick = tick;
    }

    @Override
    public long deeperdark$getSimulationVersion() {
        return this.deeperdark$simulationVersion;
    }

    @Override
    public void deeperdark$setSimulationVersion(long ver) {
        this.deeperdark$simulationVersion = ver;
    }

    @Override
    public ArrayList<Long> deeperdark$getSimulationBlocks() {
        return this.deeperdark$simulationBlocks;
    }

    @Override
    public void deeperdark$setSimulationBlocks(ArrayList<Long> positions) {
        this.deeperdark$simulationBlocks = positions;
    }

    @Override
    public void deeperdark$addSimulationBlock(long blockPos) {
        if (!this.deeperdark$simulationBlocks.contains(blockPos)) {
            this.deeperdark$simulationBlocks.add(blockPos);
        }
    }

    @Override
    public void deeperdark$removeSimulationBlock(long blockPos) {
        int idx = this.deeperdark$simulationBlocks.indexOf(blockPos);
        if (idx >= 0) {
            this.deeperdark$simulationBlocks.remove(idx);
        }
    }
}
