/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  it.unimi.dsi.fastutil.ints.IntList
 */
package net.minecraft.server.function;

import com.mojang.brigadier.CommandDispatcher;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import net.minecraft.command.SourcedCommandAction;
import net.minecraft.server.function.MacroException;
import net.minecraft.util.Identifier;

static interface Macro.Line<T> {
    public IntList getDependentVariables();

    public SourcedCommandAction<T> instantiate(List<String> var1, CommandDispatcher<T> var2, Identifier var3) throws MacroException;
}
