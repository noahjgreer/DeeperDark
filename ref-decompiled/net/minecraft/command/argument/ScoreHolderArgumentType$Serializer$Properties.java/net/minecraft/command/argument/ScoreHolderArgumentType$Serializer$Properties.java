/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ScoreHolderArgumentType;
import net.minecraft.command.argument.serialize.ArgumentSerializer;

public final class ScoreHolderArgumentType.Serializer.Properties
implements ArgumentSerializer.ArgumentTypeProperties<ScoreHolderArgumentType> {
    final boolean multiple;

    ScoreHolderArgumentType.Serializer.Properties(boolean multiple) {
        this.multiple = multiple;
    }

    @Override
    public ScoreHolderArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
        return new ScoreHolderArgumentType(this.multiple);
    }

    @Override
    public ArgumentSerializer<ScoreHolderArgumentType, ?> getSerializer() {
        return Serializer.this;
    }

    @Override
    public /* synthetic */ ArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
        return this.createType(commandRegistryAccess);
    }
}
