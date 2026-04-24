package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.duck.ChunkTimeDataAccessor;
import net.noahsarch.deeperdark.ported.UnloadedActivityTimeMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(value = ServerLevel.class, priority = 1001)
public abstract class UnloadedActivityServerLevelMixin extends Level {

    @Unique
    private int deeperdark$updateCount = 0;
    @Unique
    private int deeperdark$knownUpdateCount = 0;
    @Unique
    private boolean deeperdark$hasSlept = false;

    protected UnloadedActivityServerLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    @Inject(method = "tickChunk", at = @At("HEAD"))
    private void deeperdark$tickChunk(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        if (!DeeperDarkConfig.get().unloadedActivityEnabled) return;
        if (this.isClientSide()) return;

        ChunkTimeDataAccessor accessor = (ChunkTimeDataAccessor) chunk;
        long lastTick = accessor.deeperdark$getLastTick();
        long currentTime = this.getGameTime();

        if (lastTick != 0) {
            long timeDifference = Math.max(currentTime - lastTick, 0);
            DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

            if (timeDifference > config.unloadedActivityTickDifferenceThreshold) {
                boolean isKnown = accessor.deeperdark$getSimulationVersion() == UnloadedActivityTimeMachine.CHUNK_SIM_VER;
                if (isKnown) {
                    if (deeperdark$knownUpdateCount < config.unloadedActivityMaxKnownChunkUpdates || deeperdark$hasSlept) {
                        ++deeperdark$knownUpdateCount;
                        UnloadedActivityTimeMachine.simulateChunk(timeDifference, ServerLevel.class.cast(this), chunk, randomTickSpeed);
                    } else {
                        return;
                    }
                } else {
                    if (deeperdark$updateCount < config.unloadedActivityMaxChunkUpdates || deeperdark$hasSlept) {
                        ++deeperdark$updateCount;
                        UnloadedActivityTimeMachine.simulateChunk(timeDifference, ServerLevel.class.cast(this), chunk, randomTickSpeed);
                    } else {
                        return;
                    }
                }
            }
        }

        accessor.deeperdark$setLastTick(currentTime);

        if (!DeeperDarkConfig.get().unloadedActivityRememberBlockPositions) {
            accessor.deeperdark$setSimulationVersion(0);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void deeperdark$tick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        deeperdark$updateCount = 0;
        deeperdark$knownUpdateCount = 0;
        deeperdark$hasSlept = false;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;wakeUpAllPlayers()V"))
    private void deeperdark$wakeyWakey(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        if (DeeperDarkConfig.get().unloadedActivityUpdateAllChunksWhenSleep) {
            deeperdark$hasSlept = true;
        }
    }
}
