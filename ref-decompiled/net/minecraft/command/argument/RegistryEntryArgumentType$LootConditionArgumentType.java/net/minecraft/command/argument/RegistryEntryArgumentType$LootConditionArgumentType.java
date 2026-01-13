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
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.registry.RegistryKeys;
import org.jspecify.annotations.Nullable;

public static class RegistryEntryArgumentType.LootConditionArgumentType
extends RegistryEntryArgumentType<LootCondition> {
    protected RegistryEntryArgumentType.LootConditionArgumentType(CommandRegistryAccess registryAccess) {
        super(registryAccess, RegistryKeys.PREDICATE, LootCondition.CODEC);
    }

    @Override
    public /* synthetic */ @Nullable Object parse(StringReader stringReader) throws CommandSyntaxException {
        return super.parse(stringReader);
    }
}
