package net.noahsarch.deeperdark.ported;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.duck.ChunkTimeDataAccessor;

import java.util.ArrayList;
import java.util.Collections;

public final class UnloadedActivityTimeMachine {

    public static final long CHUNK_SIM_VER = 1L;

    private UnloadedActivityTimeMachine() {
    }

    public static void simulateChunk(long timeDifference, ServerLevel level, LevelChunk chunk, int randomTickSpeed) {
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        simulateRandomTicks(timeDifference, level, chunk, randomTickSpeed, config);
    }

    private static void simulateRandomTicks(long timeDifference, ServerLevel level, LevelChunk chunk, int randomTickSpeed, DeeperDarkConfig.ConfigInstance config) {
        int minY = level.getMinY();
        int maxY = level.getMaxY();

        ChunkTimeDataAccessor accessor = (ChunkTimeDataAccessor) chunk;
        ChunkPos chunkPos = chunk.getPos();

        ArrayList<BlockPos> blockPosArray = new ArrayList<>();
        ArrayList<Long> newSimulationBlocks = new ArrayList<>();

        if (config.unloadedActivityRememberBlockPositions && accessor.deeperdark$getSimulationVersion() == CHUNK_SIM_VER) {
            ArrayList<Long> currentSimulationBlocks = accessor.deeperdark$getSimulationBlocks();
            boolean removedSomething = false;

            for (long longPos : currentSimulationBlocks) {
                BlockPos pos = BlockPos.of(longPos);
                BlockState state = level.getBlockState(pos);
                if (state.isRandomlyTicking()) {
                    newSimulationBlocks.add(longPos);
                    blockPosArray.add(pos);
                } else {
                    removedSomething = true;
                }
            }

            if (removedSomething) {
                accessor.deeperdark$setSimulationBlocks(newSimulationBlocks);
            }
        } else {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    for (int y = minY; y < maxY; y++) {
                        BlockPos chunkBlockPos = new BlockPos(x, y, z);
                        BlockPos worldBlockPos = chunkBlockPos.offset(chunkPos.x() * 16, 0, chunkPos.z() * 16);
                        BlockState state = chunk.getBlockState(chunkBlockPos);
                        if (state.isRandomlyTicking()) {
                            blockPosArray.add(worldBlockPos);
                            if (config.unloadedActivityRememberBlockPositions) {
                                newSimulationBlocks.add(worldBlockPos.asLong());
                            }
                        }
                    }
                }
            }

            if (config.unloadedActivityRememberBlockPositions) {
                accessor.deeperdark$setSimulationBlocks(newSimulationBlocks);
                accessor.deeperdark$setSimulationVersion(CHUNK_SIM_VER);
                chunk.markUnsaved();
            }
        }

        if (config.unloadedActivityRandomizeBlockUpdates) {
            Collections.shuffle(blockPosArray);
        }

        double randomPickOdds = getRandomPickOdds(randomTickSpeed);

        for (BlockPos blockPos : blockPosArray) {
            simulateBlock(blockPos, level, timeDifference, randomPickOdds, config);
        }
    }

    private static void simulateBlock(BlockPos pos, ServerLevel level, long timeDifference, double randomPickOdds, DeeperDarkConfig.ConfigInstance config) {
        BlockState state = level.getBlockState(pos);
        if (!state.isRandomlyTicking()) return;

        int occurrences = getOccurrencesBinomial(timeDifference, randomPickOdds, config.unloadedActivityMaxOccurrencesPerBlock, level.getRandom());

        for (int i = 0; i < occurrences; i++) {
            state = level.getBlockState(pos);
            if (!state.isRandomlyTicking()) break;
            state.randomTick(level, pos, level.getRandom());
        }
    }

    public static double getRandomPickOdds(int randomTickSpeed) {
        return 1.0 - Math.pow(1.0 - 1.0 / 4096.0, randomTickSpeed);
    }

    public static int getOccurrencesBinomial(long cycles, double odds, int maxOccurrences, net.minecraft.util.RandomSource random) {
        if (odds <= 0) return 0;
        if (maxOccurrences <= 0) return 0;

        double choose = 1;
        double invertedOdds = 1 - odds;
        double totalProbability = 0;
        double randomDouble = random.nextDouble();

        for (int i = 0; i < maxOccurrences; i++) {
            if (i == cycles) return i;
            if (i != 0) {
                choose *= (double) (cycles - (i - 1)) / i;
            }
            double finalProbability = choose * Math.pow(odds, i) * Math.pow(invertedOdds, cycles - i);
            totalProbability += finalProbability;
            if (randomDouble < totalProbability) {
                return i;
            }
        }
        return maxOccurrences;
    }
}
