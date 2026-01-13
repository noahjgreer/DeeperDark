/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKeys;
import org.jspecify.annotations.Nullable;

public static class RegistryEntryArgumentType.LootTableArgumentType
extends RegistryEntryArgumentType<LootTable> {
    protected RegistryEntryArgumentType.LootTableArgumentType(CommandRegistryAccess registryAccess) {
        super(registryAccess, RegistryKeys.LOOT_TABLE, LootTable.CODEC);
    }

    @Override
    public /* synthetic */ @Nullable Object parse(StringReader stringReader) throws CommandSyntaxException {
        return super.parse(stringReader);
    }
}
