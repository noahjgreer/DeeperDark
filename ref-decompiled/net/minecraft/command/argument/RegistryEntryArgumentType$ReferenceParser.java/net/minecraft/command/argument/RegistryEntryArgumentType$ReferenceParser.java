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
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;

public record RegistryEntryArgumentType.ReferenceParser<T, O>(RegistryKey<T> key) implements RegistryEntryArgumentType.EntryParser<T, O>
{
    @Override
    public RegistryEntry<T> parse(ImmutableStringReader reader, RegistryWrapper.WrapperLookup registries, DynamicOps<O> ops, Codec<T> codec, RegistryWrapper.Impl<T> registryAccess) throws CommandSyntaxException {
        return registryAccess.getOptional(this.key).orElseThrow(() -> NO_SUCH_ELEMENT_EXCEPTION.createWithContext(reader, (Object)this.key.getValue(), (Object)this.key.getRegistry()));
    }
}
