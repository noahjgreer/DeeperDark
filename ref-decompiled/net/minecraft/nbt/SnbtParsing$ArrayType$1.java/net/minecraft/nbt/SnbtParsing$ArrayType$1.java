/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DynamicOps
 *  it.unimi.dsi.fastutil.bytes.ByteArrayList
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.nbt;

import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.nbt.SnbtParsing;
import net.minecraft.util.packrat.ParsingState;
import org.jspecify.annotations.Nullable;

final class SnbtParsing.ArrayType.1
extends SnbtParsing.ArrayType {
    private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(new byte[0]);

    SnbtParsing.ArrayType.1(SnbtParsing.NumericType numericType, SnbtParsing.NumericType ... numericTypes) {
    }

    @Override
    public <T> T createEmpty(DynamicOps<T> ops) {
        return (T)ops.createByteList(EMPTY_BUFFER);
    }

    @Override
    public <T> @Nullable T decode(DynamicOps<T> ops, List<SnbtParsing.IntValue> values, ParsingState<?> state) {
        ByteArrayList byteList = new ByteArrayList();
        for (SnbtParsing.IntValue intValue : values) {
            Number number = this.decode(intValue, state);
            if (number == null) {
                return null;
            }
            byteList.add(number.byteValue());
        }
        return (T)ops.createByteList(ByteBuffer.wrap(byteList.toByteArray()));
    }
}
