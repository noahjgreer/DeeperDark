/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.ArgumentBuilder
 */
package net.minecraft.network.packet.s2c.play;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;

static interface CommandTreeS2CPacket.SuggestableNode {
    public <S> ArgumentBuilder<S, ?> createArgumentBuilder(CommandRegistryAccess var1, CommandTreeS2CPacket.NodeFactory<S> var2);

    public void write(PacketByteBuf var1);
}
