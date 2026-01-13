/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 */
package net.minecraft.nbt;

import com.mojang.brigadier.StringReader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.nbt.SnbtOperation;
import net.minecraft.util.packrat.ParsingState;
import net.minecraft.util.packrat.Suggestable;

class SnbtOperation.3
implements Suggestable<StringReader> {
    private final Set<String> values = Stream.concat(Stream.of("false", "true"), OPERATIONS.keySet().stream().map(SnbtOperation.Type::id)).collect(Collectors.toSet());

    SnbtOperation.3() {
    }

    @Override
    public Stream<String> possibleValues(ParsingState<StringReader> parsingState) {
        return this.values.stream();
    }
}
