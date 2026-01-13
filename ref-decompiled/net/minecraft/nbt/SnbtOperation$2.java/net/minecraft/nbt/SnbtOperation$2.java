/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.nbt;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import net.minecraft.nbt.SnbtOperation;
import net.minecraft.util.Uuids;
import net.minecraft.util.packrat.ParsingState;

class SnbtOperation.2
implements SnbtOperation.Operator {
    SnbtOperation.2() {
    }

    @Override
    public <T> T apply(DynamicOps<T> ops, List<T> args, ParsingState<StringReader> state) {
        UUID uUID;
        Optional optional = ops.getStringValue(args.getFirst()).result();
        if (optional.isEmpty()) {
            state.getErrors().add(state.getCursor(), EXPECTED_STRING_UUID_EXCEPTION);
            return null;
        }
        try {
            uUID = UUID.fromString((String)optional.get());
        }
        catch (IllegalArgumentException illegalArgumentException) {
            state.getErrors().add(state.getCursor(), EXPECTED_STRING_UUID_EXCEPTION);
            return null;
        }
        return (T)ops.createIntList(IntStream.of(Uuids.toIntArray(uUID)));
    }
}
