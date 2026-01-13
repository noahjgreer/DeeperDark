/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;

public static sealed interface RegistryEntryArgumentType.EntryParser<T, O>
permits RegistryEntryArgumentType.DirectParser, RegistryEntryArgumentType.ReferenceParser {
    public RegistryEntry<T> parse(ImmutableStringReader var1, RegistryWrapper.WrapperLookup var2, DynamicOps<O> var3, Codec<T> var4, RegistryWrapper.Impl<T> var5) throws CommandSyntaxException;
}
