/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntLists
 */
package net.minecraft.server.function;

import com.mojang.brigadier.CommandDispatcher;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import java.util.List;
import net.minecraft.command.SourcedCommandAction;
import net.minecraft.server.function.Macro;
import net.minecraft.util.Identifier;

static class Macro.FixedLine<T>
implements Macro.Line<T> {
    private final SourcedCommandAction<T> action;

    public Macro.FixedLine(SourcedCommandAction<T> action) {
        this.action = action;
    }

    @Override
    public IntList getDependentVariables() {
        return IntLists.emptyList();
    }

    @Override
    public SourcedCommandAction<T> instantiate(List<String> args, CommandDispatcher<T> dispatcher, Identifier id) {
        return this.action;
    }
}
