/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.RedirectModifier
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 */
package net.minecraft.command;

import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import net.minecraft.command.Forkable;

public static interface Forkable.RedirectModifier<T>
extends RedirectModifier<T>,
Forkable<T> {
    default public Collection<T> apply(CommandContext<T> context) throws CommandSyntaxException {
        throw new UnsupportedOperationException("This function should not run");
    }
}
