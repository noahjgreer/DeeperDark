/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 */
package net.minecraft.command.argument.serialize;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;

public final class DoubleArgumentSerializer.Properties
implements ArgumentSerializer.ArgumentTypeProperties<DoubleArgumentType> {
    final double min;
    final double max;

    DoubleArgumentSerializer.Properties(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public DoubleArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
        return DoubleArgumentType.doubleArg((double)this.min, (double)this.max);
    }

    @Override
    public ArgumentSerializer<DoubleArgumentType, ?> getSerializer() {
        return DoubleArgumentSerializer.this;
    }

    @Override
    public /* synthetic */ ArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
        return this.createType(commandRegistryAccess);
    }
}
