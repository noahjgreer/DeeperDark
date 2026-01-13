/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.server.dedicated.management.dispatch;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.command.permission.PermissionLevel;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.dedicated.management.RpcPlayer;

public record OperatorsRpcDispatcher.RpcEntry(RpcPlayer player, Optional<PermissionLevel> permissionLevel, Optional<Boolean> bypassesPlayerLimit) {
    public static final MapCodec<OperatorsRpcDispatcher.RpcEntry> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)RpcPlayer.CODEC.codec().fieldOf("player").forGetter(OperatorsRpcDispatcher.RpcEntry::player), (App)PermissionLevel.NUMERIC_CODEC.optionalFieldOf("permissionLevel").forGetter(OperatorsRpcDispatcher.RpcEntry::permissionLevel), (App)Codec.BOOL.optionalFieldOf("bypassesPlayerLimit").forGetter(OperatorsRpcDispatcher.RpcEntry::bypassesPlayerLimit)).apply((Applicative)instance, OperatorsRpcDispatcher.RpcEntry::new));

    public static OperatorsRpcDispatcher.RpcEntry of(OperatorEntry operator) {
        return new OperatorsRpcDispatcher.RpcEntry(RpcPlayer.of(Objects.requireNonNull((PlayerConfigEntry)operator.getKey())), Optional.of(operator.getLevel().getLevel()), Optional.of(operator.canBypassPlayerLimit()));
    }
}
