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

public record RegistryEntryArgumentType.DirectParser<T, O>(O value) implements RegistryEntryArgumentType.EntryParser<T, O>
{
    @Override
    public RegistryEntry<T> parse(ImmutableStringReader reader, RegistryWrapper.WrapperLookup registries, DynamicOps<O> ops, Codec<T> codec, RegistryWrapper.Impl<T> registryAccess) throws CommandSyntaxException {
        return RegistryEntry.of(codec.parse(registries.getOps(ops), this.value).getOrThrow(error -> FAILED_TO_PARSE_EXCEPTION.createWithContext(reader, error)));
    }
}
