/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 */
package net.minecraft.command.argument.serialize;

import com.mojang.brigadier.arguments.ArgumentType;
import java.util.function.Function;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;

public final class ConstantArgumentSerializer.Properties
implements ArgumentSerializer.ArgumentTypeProperties<A> {
    private final Function<CommandRegistryAccess, A> typeSupplier;

    public ConstantArgumentSerializer.Properties(Function<CommandRegistryAccess, A> typeSupplier) {
        this.typeSupplier = typeSupplier;
    }

    @Override
    public A createType(CommandRegistryAccess commandRegistryAccess) {
        return (ArgumentType)this.typeSupplier.apply(commandRegistryAccess);
    }

    @Override
    public ArgumentSerializer<A, ?> getSerializer() {
        return ConstantArgumentSerializer.this;
    }
}
