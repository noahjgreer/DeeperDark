/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.argument.NumberRangeArgumentType;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.command.ServerCommandSource;

public static class NumberRangeArgumentType.FloatRangeArgumentType
implements NumberRangeArgumentType<NumberRange.DoubleRange> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0..5.2", "0", "-5.4", "-100.76..", "..100");

    public static NumberRange.DoubleRange getRangeArgument(CommandContext<ServerCommandSource> context, String name) {
        return (NumberRange.DoubleRange)context.getArgument(name, NumberRange.DoubleRange.class);
    }

    public NumberRange.DoubleRange parse(StringReader stringReader) throws CommandSyntaxException {
        return NumberRange.DoubleRange.parse(stringReader);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader reader) throws CommandSyntaxException {
        return this.parse(reader);
    }
}
