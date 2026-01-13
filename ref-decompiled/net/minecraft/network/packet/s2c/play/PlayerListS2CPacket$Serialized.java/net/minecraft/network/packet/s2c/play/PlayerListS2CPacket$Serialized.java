/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.network.packet.s2c.play;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.jspecify.annotations.Nullable;

static class PlayerListS2CPacket.Serialized {
    final UUID profileId;
    @Nullable GameProfile gameProfile;
    boolean listed;
    int latency;
    GameMode gameMode = GameMode.DEFAULT;
    @Nullable Text displayName;
    boolean showHat;
    int listOrder;
     @Nullable PublicPlayerSession.Serialized session;

    PlayerListS2CPacket.Serialized(UUID profileId) {
        this.profileId = profileId;
    }

    PlayerListS2CPacket.Entry toEntry() {
        return new PlayerListS2CPacket.Entry(this.profileId, this.gameProfile, this.listed, this.latency, this.gameMode, this.displayName, this.showHat, this.listOrder, this.session);
    }
}
