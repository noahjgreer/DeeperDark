/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 */
package net.minecraft.command.argument.serialize;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;

public final class IntegerArgumentSerializer.Properties
implements ArgumentSerializer.ArgumentTypeProperties<IntegerArgumentType> {
    final int min;
    final int max;

    IntegerArgumentSerializer.Properties(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public IntegerArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
        return IntegerArgumentType.integer((int)this.min, (int)this.max);
    }

    @Override
    public ArgumentSerializer<IntegerArgumentType, ?> getSerializer() {
        return IntegerArgumentSerializer.this;
    }

    @Override
    public /* synthetic */ ArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
        return this.createType(commandRegistryAccess);
    }
}
