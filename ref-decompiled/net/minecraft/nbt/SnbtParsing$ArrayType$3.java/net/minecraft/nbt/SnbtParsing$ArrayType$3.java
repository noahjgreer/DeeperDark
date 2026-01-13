/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DynamicOps
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.nbt;

import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.stream.LongStream;
import net.minecraft.nbt.SnbtParsing;
import net.minecraft.util.packrat.ParsingState;
import org.jspecify.annotations.Nullable;

final class SnbtParsing.ArrayType.3
extends SnbtParsing.ArrayType {
    SnbtParsing.ArrayType.3(SnbtParsing.NumericType numericType, SnbtParsing.NumericType ... numericTypes) {
    }

    @Override
    public <T> T createEmpty(DynamicOps<T> ops) {
        return (T)ops.createLongList(LongStream.empty());
    }

    @Override
    public <T> @Nullable T decode(DynamicOps<T> ops, List<SnbtParsing.IntValue> values, ParsingState<?> state) {
        LongStream.Builder builder = LongStream.builder();
        for (SnbtParsing.IntValue intValue : values) {
            Number number = this.decode(intValue, state);
            if (number == null) {
                return null;
            }
            builder.add(number.longValue());
        }
        return (T)ops.createLongList(builder.build());
    }
}
