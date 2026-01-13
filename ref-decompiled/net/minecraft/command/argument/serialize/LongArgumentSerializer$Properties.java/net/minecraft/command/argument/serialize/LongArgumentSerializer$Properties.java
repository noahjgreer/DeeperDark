/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.LongArgumentType
 */
package net.minecraft.command.argument.serialize;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;

public final class LongArgumentSerializer.Properties
implements ArgumentSerializer.ArgumentTypeProperties<LongArgumentType> {
    final long min;
    final long max;

    LongArgumentSerializer.Properties(long min, long max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public LongArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
        return LongArgumentType.longArg((long)this.min, (long)this.max);
    }

    @Override
    public ArgumentSerializer<LongArgumentType, ?> getSerializer() {
        return LongArgumentSerializer.this;
    }

    @Override
    public /* synthetic */ ArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
        return this.createType(commandRegistryAccess);
    }
}
