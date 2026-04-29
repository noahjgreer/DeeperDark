package io.github.lucaargolo.seasons.utils;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplacedMeltablesState extends SavedData {

    private static final Logger LOGGER = LoggerFactory.getLogger("Fabric Seasons");
    private static final Identifier ID = Identifier.fromNamespaceAndPath("seasons", "seasons_replaced_meltables");

    Long2ObjectArrayMap<Long2ObjectArrayMap<BlockState>> chunkToReplaced = new Long2ObjectArrayMap<>();

    public BlockState getReplaced(BlockPos blockPos) {
        long chunkLong = chunkLong(blockPos);
        Long2ObjectArrayMap<BlockState> posToReplaced = chunkToReplaced.get(chunkLong);
        if (posToReplaced != null) {
            return posToReplaced.get(blockPos.asLong());
        } else {
            return null;
        }
    }

    public void setReplaced(BlockPos blockPos, BlockState replacedState) {
        long chunkLong = chunkLong(blockPos);
        Long2ObjectArrayMap<BlockState> posToReplaced = chunkToReplaced.get(chunkLong);
        if (posToReplaced != null) {
            if (replacedState != null) {
                posToReplaced.put(blockPos.asLong(), replacedState);
            } else {
                posToReplaced.remove(blockPos.asLong());
                if (posToReplaced.isEmpty()) {
                    chunkToReplaced.remove(chunkLong);
                }
            }
        } else if (replacedState != null) {
            posToReplaced = new Long2ObjectArrayMap<>();
            posToReplaced.put(blockPos.asLong(), replacedState);
            chunkToReplaced.put(chunkLong, posToReplaced);
        }
        setDirty();
    }

    private static long chunkLong(BlockPos pos) {
        int cx = pos.getX() >> 4;
        int cz = pos.getZ() >> 4;
        return (long) cx & 0xFFFFFFFFL | ((long) cz & 0xFFFFFFFFL) << 32;
    }

    private static CompoundTag toNbt(ReplacedMeltablesState state) {
        CompoundTag nbt = new CompoundTag();
        state.chunkToReplaced.long2ObjectEntrySet().fastForEach(entry -> {
            if (!entry.getValue().isEmpty()) {
                CompoundTag innerNbt = new CompoundTag();
                entry.getValue().long2ObjectEntrySet().fastForEach(innerEntry -> {
                    BlockState.CODEC.encode(innerEntry.getValue(), NbtOps.INSTANCE, NbtOps.INSTANCE.empty())
                        .ifSuccess(element -> innerNbt.put(innerEntry.getLongKey() + "", element));
                });
                nbt.put(entry.getLongKey() + "", innerNbt);
            }
        });
        return nbt;
    }

    private static ReplacedMeltablesState fromNbt(CompoundTag nbt) {
        ReplacedMeltablesState state = new ReplacedMeltablesState();
        nbt.keySet().forEach(key -> {
            try {
                long longKey = Long.parseLong(key);
                Long2ObjectArrayMap<BlockState> posToReplaced = new Long2ObjectArrayMap<>();
                nbt.getCompound(key).ifPresent(innerNbt -> innerNbt.keySet().forEach(innerKey -> {
                    try {
                        long innerLongKey = Long.parseLong(innerKey);
                        Tag tag = innerNbt.get(innerKey);
                        if (tag != null) {
                            BlockState.CODEC.decode(NbtOps.INSTANCE, tag)
                                .ifSuccess(pair -> posToReplaced.put(innerLongKey, pair.getFirst()));
                        }
                    } catch (NumberFormatException ignored) {}
                }));
                state.chunkToReplaced.put(longKey, posToReplaced);
            } catch (NumberFormatException e) {
                LOGGER.error("[Fabric Seasons] Error reading replaced meltable blocks at " + key, e);
            }
        });
        return state;
    }

    public static final Codec<ReplacedMeltablesState> CODEC = CompoundTag.CODEC.xmap(
        ReplacedMeltablesState::fromNbt,
        ReplacedMeltablesState::toNbt
    );

    public static final SavedDataType<ReplacedMeltablesState> TYPE = new SavedDataType<>(
        ID,
        ReplacedMeltablesState::new,
        CODEC,
        DataFixTypes.SAVED_DATA_SCOREBOARD
    );
}
