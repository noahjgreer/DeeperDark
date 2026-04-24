package net.noahsarch.deeperdark.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.PalettedContainerFactory;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import net.minecraft.server.level.ServerLevel;
import net.noahsarch.deeperdark.duck.ChunkTimeDataAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(value = SerializableChunkData.class, priority = 999)
public abstract class UnloadedActivitySerializableChunkDataMixin {

    @Unique
    private long deeperdark$lastTick = 0;
    @Unique
    private long deeperdark$ver = 0;
    @Unique
    private long[] deeperdark$simBlocks = {};
    @Unique
    private boolean deeperdark$needsSaving = false;

    @Inject(method = "write", at = @At("RETURN"))
    public void deeperdark$write(CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag nbt = cir.getReturnValue();
        CompoundTag chunkData = new CompoundTag();
        chunkData.putLong("last_tick", deeperdark$lastTick);
        chunkData.putLong("ver", deeperdark$ver);
        chunkData.putLongArray("sim_blocks", deeperdark$simBlocks);
        nbt.put("deeperdark_ua", chunkData);
    }

    @Inject(method = "read", at = @At("RETURN"))
    public void deeperdark$read(ServerLevel level, PoiManager poiManager, RegionStorageInfo key, ChunkPos expectedPos, CallbackInfoReturnable<ProtoChunk> cir) {
        ProtoChunk protoChunkTemp = cir.getReturnValue();
        ChunkAccess chunk = (protoChunkTemp instanceof ImposterProtoChunk imposter) ? imposter.getWrapped() : protoChunkTemp;
        ChunkTimeDataAccessor accessor = (ChunkTimeDataAccessor) chunk;
        accessor.deeperdark$setLastTick(deeperdark$lastTick);
        accessor.deeperdark$setSimulationVersion(deeperdark$ver);
        ArrayList<Long> blocks = new ArrayList<>();
        for (long l : deeperdark$simBlocks) blocks.add(l);
        accessor.deeperdark$setSimulationBlocks(blocks);
        if (deeperdark$lastTick == 0) {
            chunk.markUnsaved();
            accessor.deeperdark$setLastTick(level.getGameTime());
        } else if (deeperdark$needsSaving) {
            chunk.markUnsaved();
        }
    }

    @Inject(method = "copyOf", at = @At("RETURN"))
    private static void deeperdark$fromChunk(ServerLevel level, ChunkAccess chunk, CallbackInfoReturnable<SerializableChunkData> cir) {
        UnloadedActivitySerializableChunkDataMixin serialized = (UnloadedActivitySerializableChunkDataMixin) (Object) cir.getReturnValue();
        if (serialized == null) return;
        ChunkTimeDataAccessor accessor = (ChunkTimeDataAccessor) chunk;
        serialized.deeperdark$lastTick = accessor.deeperdark$getLastTick();
        serialized.deeperdark$ver = accessor.deeperdark$getSimulationVersion();
        java.util.ArrayList<Long> simBlocks = accessor.deeperdark$getSimulationBlocks();
        serialized.deeperdark$simBlocks = simBlocks.stream().mapToLong(l -> l).toArray();
    }

    @Inject(method = "parse", at = @At("RETURN"))
    private static void deeperdark$fromNbt(LevelHeightAccessor world, PalettedContainerFactory palettesFactory, CompoundTag nbt, CallbackInfoReturnable<SerializableChunkData> cir) {
        UnloadedActivitySerializableChunkDataMixin serialized = (UnloadedActivitySerializableChunkDataMixin) (Object) cir.getReturnValue();
        if (serialized == null) return;
        CompoundTag chunkData = nbt.getCompoundOrEmpty("deeperdark_ua");
        if (chunkData.isEmpty()) {
            serialized.deeperdark$needsSaving = true;
        } else {
            serialized.deeperdark$lastTick = chunkData.getLongOr("last_tick", 0L);
            serialized.deeperdark$ver = chunkData.getLongOr("ver", 0L);
            serialized.deeperdark$simBlocks = chunkData.getLongArray("sim_blocks").orElse(new long[]{});
        }
    }
}
