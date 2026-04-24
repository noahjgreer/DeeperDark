package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainerFactory;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.duck.ChunkTimeDataAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
public abstract class UnloadedActivityLevelChunkMixin extends ChunkAccess {

    @Shadow @Final
    Level level;

    public UnloadedActivityLevelChunkMixin(ChunkPos chunkPos, UpgradeData upgradeData, LevelHeightAccessor levelHeightAccessor, PalettedContainerFactory palettedContainerFactory, long l, @Nullable LevelChunkSection[] levelChunkSections, @Nullable BlendingData blendingData) {
        super(chunkPos, upgradeData, levelHeightAccessor, palettedContainerFactory, l, levelChunkSections, blendingData);
    }

    @Inject(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;"
            ),
            method = "setBlockState"
    )
    public void deeperdark$blockChanged(BlockPos blockPos, BlockState blockState, int flags, CallbackInfoReturnable<BlockState> cir) {
        if (level.isClientSide() || !DeeperDarkConfig.get().unloadedActivityRememberBlockPositions) return;
        if (!blockState.isRandomlyTicking()) return;
        ((ChunkTimeDataAccessor) this).deeperdark$addSimulationBlock(blockPos.asLong());
    }

    @Inject(
            method = "<init>(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ProtoChunk;Lnet/minecraft/world/level/chunk/LevelChunk$PostLoadProcessor;)V",
            at = @At("RETURN")
    )
    private void deeperdark$initLevelChunk(ServerLevel level, ProtoChunk protoChunk, LevelChunk.PostLoadProcessor postLoadProcessor, CallbackInfo ci) {
        ChunkTimeDataAccessor proto = (ChunkTimeDataAccessor) protoChunk;
        ChunkTimeDataAccessor self = (ChunkTimeDataAccessor) (Object) this;
        if (proto.deeperdark$getLastTick() == 0) {
            self.deeperdark$setLastTick(level.getGameTime());
        } else {
            self.deeperdark$setLastTick(proto.deeperdark$getLastTick());
        }
        self.deeperdark$setSimulationVersion(proto.deeperdark$getSimulationVersion());
        self.deeperdark$setSimulationBlocks(proto.deeperdark$getSimulationBlocks());
    }
}
