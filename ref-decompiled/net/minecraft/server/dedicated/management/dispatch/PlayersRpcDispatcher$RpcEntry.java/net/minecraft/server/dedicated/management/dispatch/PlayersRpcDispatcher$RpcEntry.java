/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.server.dedicated.management.dispatch;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import net.minecraft.server.dedicated.management.RpcKickReason;
import net.minecraft.server.dedicated.management.RpcPlayer;

public static final class PlayersRpcDispatcher.RpcEntry
extends Record {
    private final RpcPlayer player;
    final Optional<RpcKickReason> message;
    public static final MapCodec<PlayersRpcDispatcher.RpcEntry> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)RpcPlayer.CODEC.codec().fieldOf("player").forGetter(PlayersRpcDispatcher.RpcEntry::player), (App)RpcKickReason.CODEC.optionalFieldOf("message").forGetter(PlayersRpcDispatcher.RpcEntry::message)).apply((Applicative)instance, PlayersRpcDispatcher.RpcEntry::new));

    public PlayersRpcDispatcher.RpcEntry(RpcPlayer player, Optional<RpcKickReason> message) {
        this.player = player;
        this.message = message;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{PlayersRpcDispatcher.RpcEntry.class, "player;message", "player", "message"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PlayersRpcDispatcher.RpcEntry.class, "player;message", "player", "message"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PlayersRpcDispatcher.RpcEntry.class, "player;message", "player", "message"}, this, object);
    }

    public RpcPlayer player() {
        return this.player;
    }

    public Optional<RpcKickReason> message() {
        return this.message;
    }
}
