/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.ArgumentBuilder
 */
package net.minecraft.network.packet.s2c.play;

import com.mojang.brigadier.builder.ArgumentBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;

record CommandTreeS2CPacket.LiteralNode(String literal) implements CommandTreeS2CPacket.SuggestableNode
{
    @Override
    public <S> ArgumentBuilder<S, ?> createArgumentBuilder(CommandRegistryAccess commandRegistryAccess, CommandTreeS2CPacket.NodeFactory<S> nodeFactory) {
        return nodeFactory.literal(this.literal);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeString(this.literal);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{CommandTreeS2CPacket.LiteralNode.class, "id", "literal"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CommandTreeS2CPacket.LiteralNode.class, "id", "literal"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CommandTreeS2CPacket.LiteralNode.class, "id", "literal"}, this, object);
    }
}
