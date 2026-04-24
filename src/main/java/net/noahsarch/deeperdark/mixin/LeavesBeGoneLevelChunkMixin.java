package net.noahsarch.deeperdark.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainerFactory;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.noahsarch.deeperdark.duck.LeafDecayTickerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.jetbrains.annotations.Nullable;

@Mixin(LevelChunk.class)
public abstract class LeavesBeGoneLevelChunkMixin extends ChunkAccess {

    @Unique
    private final LevelChunkTicks<Block> deeperdark$leafDecayTicks = new LevelChunkTicks<>();

    public LeavesBeGoneLevelChunkMixin(ChunkPos chunkPos, UpgradeData upgradeData, LevelHeightAccessor levelHeightAccessor, PalettedContainerFactory containerFactory, long inhabitedTime, @Nullable LevelChunkSection[] sections, @Nullable BlendingData blendingData) {
        super(chunkPos, upgradeData, levelHeightAccessor, containerFactory, inhabitedTime, sections, blendingData);
    }

    @Inject(method = "unpackTicks", at = @At("TAIL"))
    private void deeperdark$unpackLeafDecayTicks(long gameTime, CallbackInfo ci) {
        this.deeperdark$leafDecayTicks.unpack(gameTime);
    }

    @Inject(method = "registerTickContainerInLevel", at = @At("TAIL"))
    private void deeperdark$registerLeafDecayTickContainer(ServerLevel level, CallbackInfo ci) {
        if (level instanceof LeafDecayTickerLevel tickerLevel) {
            tickerLevel.deeperdark$getLeafDecayTicks().addContainer(this.chunkPos, this.deeperdark$leafDecayTicks);
        }
    }

    @Inject(method = "unregisterTickContainerFromLevel", at = @At("TAIL"))
    private void deeperdark$unregisterLeafDecayTickContainer(ServerLevel level, CallbackInfo ci) {
        if (level instanceof LeafDecayTickerLevel tickerLevel) {
            tickerLevel.deeperdark$getLeafDecayTicks().removeContainer(this.chunkPos);
        }
    }
}
