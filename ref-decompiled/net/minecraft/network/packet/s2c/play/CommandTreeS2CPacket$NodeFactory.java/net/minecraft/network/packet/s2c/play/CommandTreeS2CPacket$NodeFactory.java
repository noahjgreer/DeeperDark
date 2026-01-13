/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.network.packet.s2c.play;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

public static interface CommandTreeS2CPacket.NodeFactory<S> {
    public ArgumentBuilder<S, ?> literal(String var1);

    public ArgumentBuilder<S, ?> argument(String var1, ArgumentType<?> var2, @Nullable Identifier var3);

    public ArgumentBuilder<S, ?> modifyNode(ArgumentBuilder<S, ?> var1, boolean var2, boolean var3);
}
