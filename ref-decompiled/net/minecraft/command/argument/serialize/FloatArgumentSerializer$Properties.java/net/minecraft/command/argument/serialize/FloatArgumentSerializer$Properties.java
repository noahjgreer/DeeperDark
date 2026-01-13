/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.FloatArgumentType
 */
package net.minecraft.command.argument.serialize;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;

public final class FloatArgumentSerializer.Properties
implements ArgumentSerializer.ArgumentTypeProperties<FloatArgumentType> {
    final float min;
    final float max;

    FloatArgumentSerializer.Properties(float min, float max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public FloatArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
        return FloatArgumentType.floatArg((float)this.min, (float)this.max);
    }

    @Override
    public ArgumentSerializer<FloatArgumentType, ?> getSerializer() {
        return FloatArgumentSerializer.this;
    }

    @Override
    public /* synthetic */ ArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
        return this.createType(commandRegistryAccess);
    }
}
