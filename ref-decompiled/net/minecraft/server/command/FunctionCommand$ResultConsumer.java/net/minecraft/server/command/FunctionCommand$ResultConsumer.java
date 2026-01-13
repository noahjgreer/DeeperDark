/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

import net.minecraft.util.Identifier;

public static interface FunctionCommand.ResultConsumer<T> {
    public void accept(T var1, Identifier var2, int var3);
}
