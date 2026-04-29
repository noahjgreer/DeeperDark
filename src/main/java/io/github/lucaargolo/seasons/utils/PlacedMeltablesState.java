package io.github.lucaargolo.seasons.utils;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlacedMeltablesState extends SavedData {

    private static final Logger LOGGER = LoggerFactory.getLogger("Fabric Seasons");
    private static final Identifier ID = Identifier.fromNamespaceAndPath("seasons", "seasons_placed_meltables");

    Long2ObjectArrayMap<LongArraySet> chunkToPlaced = new Long2ObjectArrayMap<>();

    public boolean isManuallyPlaced(BlockPos blockPos) {
        long chunkLong = chunkLong(blockPos);
        LongArraySet longArray = chunkToPlaced.get(chunkLong);
        return longArray != null && longArray.contains(blockPos.asLong());
    }

    public void setManuallyPlaced(BlockPos blockPos, Boolean manuallyPlaced) {
        long chunkLong = chunkLong(blockPos);
        LongArraySet longArray = chunkToPlaced.get(chunkLong);
        if (longArray != null) {
            if (manuallyPlaced) {
                longArray.add(blockPos.asLong());
            } else {
                longArray.remove(blockPos.asLong());
                if (longArray.isEmpty()) {
                    chunkToPlaced.remove(chunkLong);
                }
            }
        } else if (manuallyPlaced) {
            longArray = new LongArraySet();
            longArray.add(blockPos.asLong());
            chunkToPlaced.put(chunkLong, longArray);
        }
        setDirty();
    }

    private static long chunkLong(BlockPos pos) {
        int cx = pos.getX() >> 4;
        int cz = pos.getZ() >> 4;
        return (long) cx & 0xFFFFFFFFL | ((long) cz & 0xFFFFFFFFL) << 32;
    }

    private static CompoundTag toNbt(PlacedMeltablesState state) {
        CompoundTag nbt = new CompoundTag();
        state.chunkToPlaced.long2ObjectEntrySet().fastForEach(entry -> {
            if (!entry.getValue().isEmpty()) {
                nbt.put(entry.getLongKey() + "", new LongArrayTag(entry.getValue().toLongArray()));
            }
        });
        return nbt;
    }

    private static PlacedMeltablesState fromNbt(CompoundTag nbt) {
        PlacedMeltablesState state = new PlacedMeltablesState();
        nbt.keySet().forEach(key -> {
            try {
                long longKey = Long.parseLong(key);
                nbt.getLongArray(key).ifPresent(arr -> state.chunkToPlaced.put(longKey, new LongArraySet(arr)));
            } catch (NumberFormatException e) {
                LOGGER.error("[Fabric Seasons] Error reading manually placed meltable blocks at " + key, e);
            }
        });
        return state;
    }

    public static final Codec<PlacedMeltablesState> CODEC = CompoundTag.CODEC.xmap(
        PlacedMeltablesState::fromNbt,
        PlacedMeltablesState::toNbt
    );

    public static final SavedDataType<PlacedMeltablesState> TYPE = new SavedDataType<>(
        ID,
        PlacedMeltablesState::new,
        CODEC,
        DataFixTypes.SAVED_DATA_SCOREBOARD
    );
}
