/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.serialization.DynamicOps
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.nbt;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.SnbtOperation;
import net.minecraft.util.packrat.ParsingState;
import org.jspecify.annotations.Nullable;

class SnbtOperation.1
implements SnbtOperation.Operator {
    SnbtOperation.1() {
    }

    @Override
    public <T> T apply(DynamicOps<T> ops, List<T> args, ParsingState<StringReader> state) {
        Boolean boolean_ = SnbtOperation.1.asBoolean(ops, args.getFirst());
        if (boolean_ == null) {
            state.getErrors().add(state.getCursor(), EXPECTED_NUMBER_OR_BOOLEAN_EXCEPTION);
            return null;
        }
        return (T)ops.createBoolean(boolean_.booleanValue());
    }

    private static <T> @Nullable Boolean asBoolean(DynamicOps<T> ops, T value) {
        Optional optional = ops.getBooleanValue(value).result();
        if (optional.isPresent()) {
            return (Boolean)optional.get();
        }
        Optional optional2 = ops.getNumberValue(value).result();
        if (optional2.isPresent()) {
            return ((Number)optional2.get()).doubleValue() != 0.0;
        }
        return null;
    }
}
