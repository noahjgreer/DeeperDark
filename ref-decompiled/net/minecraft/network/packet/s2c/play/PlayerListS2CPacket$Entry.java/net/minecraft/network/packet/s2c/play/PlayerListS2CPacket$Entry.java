/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.network.packet.s2c.play;

import com.mojang.authlib.GameProfile;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.UUID;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Nullables;
import net.minecraft.world.GameMode;
import org.jspecify.annotations.Nullable;

public static final class PlayerListS2CPacket.Entry
extends Record {
    private final UUID profileId;
    private final @Nullable GameProfile profile;
    private final boolean listed;
    private final int latency;
    private final GameMode gameMode;
    private final @Nullable Text displayName;
    final boolean showHat;
    final int listOrder;
    final @Nullable PublicPlayerSession.Serialized chatSession;

    PlayerListS2CPacket.Entry(ServerPlayerEntity player) {
        this(player.getUuid(), player.getGameProfile(), true, player.networkHandler.getLatency(), player.getGameMode(), player.getPlayerListName(), player.isModelPartVisible(PlayerModelPart.HAT), player.getPlayerListOrder(), Nullables.map(player.getSession(), PublicPlayerSession::toSerialized));
    }

    public PlayerListS2CPacket.Entry(UUID profileId, @Nullable GameProfile profile, boolean listed, int latency, GameMode gameMode, @Nullable Text displayName, boolean showHat, int listOrder, @Nullable PublicPlayerSession.Serialized chatSession) {
        this.profileId = profileId;
        this.profile = profile;
        this.listed = listed;
        this.latency = latency;
        this.gameMode = gameMode;
        this.displayName = displayName;
        this.showHat = showHat;
        this.listOrder = listOrder;
        this.chatSession = chatSession;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{PlayerListS2CPacket.Entry.class, "profileId;profile;listed;latency;gameMode;displayName;showHat;listOrder;chatSession", "profileId", "profile", "listed", "latency", "gameMode", "displayName", "showHat", "listOrder", "chatSession"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PlayerListS2CPacket.Entry.class, "profileId;profile;listed;latency;gameMode;displayName;showHat;listOrder;chatSession", "profileId", "profile", "listed", "latency", "gameMode", "displayName", "showHat", "listOrder", "chatSession"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PlayerListS2CPacket.Entry.class, "profileId;profile;listed;latency;gameMode;displayName;showHat;listOrder;chatSession", "profileId", "profile", "listed", "latency", "gameMode", "displayName", "showHat", "listOrder", "chatSession"}, this, object);
    }

    public UUID profileId() {
        return this.profileId;
    }

    public @Nullable GameProfile profile() {
        return this.profile;
    }

    public boolean listed() {
        return this.listed;
    }

    public int latency() {
        return this.latency;
    }

    public GameMode gameMode() {
        return this.gameMode;
    }

    public @Nullable Text displayName() {
        return this.displayName;
    }

    public boolean showHat() {
        return this.showHat;
    }

    public int listOrder() {
        return this.listOrder;
    }

    public @Nullable PublicPlayerSession.Serialized chatSession() {
        return this.chatSession;
    }
}
