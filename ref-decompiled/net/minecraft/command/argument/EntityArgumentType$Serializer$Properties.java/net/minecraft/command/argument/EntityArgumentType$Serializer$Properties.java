/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.serialize.ArgumentSerializer;

public final class EntityArgumentType.Serializer.Properties
implements ArgumentSerializer.ArgumentTypeProperties<EntityArgumentType> {
    final boolean single;
    final boolean playersOnly;

    EntityArgumentType.Serializer.Properties(boolean single, boolean playersOnly) {
        this.single = single;
        this.playersOnly = playersOnly;
    }

    @Override
    public EntityArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
        return new EntityArgumentType(this.single, this.playersOnly);
    }

    @Override
    public ArgumentSerializer<EntityArgumentType, ?> getSerializer() {
        return Serializer.this;
    }

    @Override
    public /* synthetic */ ArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
        return this.createType(commandRegistryAccess);
    }
}
