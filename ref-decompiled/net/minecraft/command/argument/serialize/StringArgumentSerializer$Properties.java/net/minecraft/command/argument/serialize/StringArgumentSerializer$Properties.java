/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType$StringType
 */
package net.minecraft.command.argument.serialize;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;

public final class StringArgumentSerializer.Properties
implements ArgumentSerializer.ArgumentTypeProperties<StringArgumentType> {
    final StringArgumentType.StringType type;

    public StringArgumentSerializer.Properties(StringArgumentType.StringType type) {
        this.type = type;
    }

    @Override
    public StringArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
        return switch (this.type) {
            default -> throw new MatchException(null, null);
            case StringArgumentType.StringType.SINGLE_WORD -> StringArgumentType.word();
            case StringArgumentType.StringType.QUOTABLE_PHRASE -> StringArgumentType.string();
            case StringArgumentType.StringType.GREEDY_PHRASE -> StringArgumentType.greedyString();
        };
    }

    @Override
    public ArgumentSerializer<StringArgumentType, ?> getSerializer() {
        return StringArgumentSerializer.this;
    }

    @Override
    public /* synthetic */ ArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
        return this.createType(commandRegistryAccess);
    }
}
