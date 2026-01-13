/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.command.argument.serialize.ArgumentSerializer;

public final class TimeArgumentType.Serializer.Properties
implements ArgumentSerializer.ArgumentTypeProperties<TimeArgumentType> {
    final int minimum;

    TimeArgumentType.Serializer.Properties(int minimum) {
        this.minimum = minimum;
    }

    @Override
    public TimeArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
        return TimeArgumentType.time(this.minimum);
    }

    @Override
    public ArgumentSerializer<TimeArgumentType, ?> getSerializer() {
        return Serializer.this;
    }

    @Override
    public /* synthetic */ ArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
        return this.createType(commandRegistryAccess);
    }
}
