/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  it.unimi.dsi.fastutil.ints.IntList
 */
package net.minecraft.server.function;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import net.minecraft.command.MacroInvocation;
import net.minecraft.command.SourcedCommandAction;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.Macro;
import net.minecraft.server.function.MacroException;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

static class Macro.VariableLine<T extends AbstractServerCommandSource<T>>
implements Macro.Line<T> {
    private final MacroInvocation invocation;
    private final IntList variableIndices;
    private final T source;

    public Macro.VariableLine(MacroInvocation invocation, IntList variableIndices, T source) {
        this.invocation = invocation;
        this.variableIndices = variableIndices;
        this.source = source;
    }

    @Override
    public IntList getDependentVariables() {
        return this.variableIndices;
    }

    @Override
    public SourcedCommandAction<T> instantiate(List<String> args, CommandDispatcher<T> dispatcher, Identifier id) throws MacroException {
        String string = this.invocation.apply(args);
        try {
            return CommandFunction.parse(dispatcher, this.source, new StringReader(string));
        }
        catch (CommandSyntaxException commandSyntaxException) {
            throw new MacroException(Text.translatable("commands.function.error.parse", Text.of(id), string, commandSyntaxException.getMessage()));
        }
    }
}
