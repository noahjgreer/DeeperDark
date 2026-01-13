/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 */
package net.minecraft.command.argument.serialize;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;

public static interface ArgumentSerializer.ArgumentTypeProperties<A extends ArgumentType<?>> {
    public A createType(CommandRegistryAccess var1);

    public ArgumentSerializer<A, ?> getSerializer();
}
