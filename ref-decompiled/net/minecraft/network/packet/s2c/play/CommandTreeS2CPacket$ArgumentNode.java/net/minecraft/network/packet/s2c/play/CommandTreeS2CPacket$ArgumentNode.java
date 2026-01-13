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
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

record CommandTreeS2CPacket.ArgumentNode(String name, ArgumentSerializer.ArgumentTypeProperties<?> properties, @Nullable Identifier id) implements CommandTreeS2CPacket.SuggestableNode
{
    @Override
    public <S> ArgumentBuilder<S, ?> createArgumentBuilder(CommandRegistryAccess commandRegistryAccess, CommandTreeS2CPacket.NodeFactory<S> nodeFactory) {
        Object argumentType = this.properties.createType(commandRegistryAccess);
        return nodeFactory.argument(this.name, (ArgumentType<?>)argumentType, this.id);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeString(this.name);
        CommandTreeS2CPacket.ArgumentNode.write(buf, this.properties);
        if (this.id != null) {
            buf.writeIdentifier(this.id);
        }
    }

    private static <A extends ArgumentType<?>> void write(PacketByteBuf buf, ArgumentSerializer.ArgumentTypeProperties<A> properties) {
        CommandTreeS2CPacket.ArgumentNode.write(buf, properties.getSerializer(), properties);
    }

    private static <A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> void write(PacketByteBuf buf, ArgumentSerializer<A, T> serializer, ArgumentSerializer.ArgumentTypeProperties<A> properties) {
        buf.writeVarInt(Registries.COMMAND_ARGUMENT_TYPE.getRawId(serializer));
        serializer.writePacket(properties, buf);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{CommandTreeS2CPacket.ArgumentNode.class, "id;argumentType;suggestionId", "name", "properties", "id"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CommandTreeS2CPacket.ArgumentNode.class, "id;argumentType;suggestionId", "name", "properties", "id"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CommandTreeS2CPacket.ArgumentNode.class, "id;argumentType;suggestionId", "name", "properties", "id"}, this, object);
    }
}
