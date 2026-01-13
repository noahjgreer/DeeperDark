/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.network.encryption;

import com.mojang.authlib.GameProfile;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.encryption.SignatureVerifier;

public record PublicPlayerSession.Serialized(UUID sessionId, PlayerPublicKey.PublicKeyData publicKeyData) {
    public static PublicPlayerSession.Serialized fromBuf(PacketByteBuf buf) {
        return new PublicPlayerSession.Serialized(buf.readUuid(), new PlayerPublicKey.PublicKeyData(buf));
    }

    public static void write(PacketByteBuf buf, PublicPlayerSession.Serialized serialized) {
        buf.writeUuid(serialized.sessionId);
        serialized.publicKeyData.write(buf);
    }

    public PublicPlayerSession toSession(GameProfile gameProfile, SignatureVerifier servicesSignatureVerifier) throws PlayerPublicKey.PublicKeyException {
        return new PublicPlayerSession(this.sessionId, PlayerPublicKey.verifyAndDecode(servicesSignatureVerifier, gameProfile.id(), this.publicKeyData));
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{PublicPlayerSession.Serialized.class, "sessionId;profilePublicKey", "sessionId", "publicKeyData"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PublicPlayerSession.Serialized.class, "sessionId;profilePublicKey", "sessionId", "publicKeyData"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PublicPlayerSession.Serialized.class, "sessionId;profilePublicKey", "sessionId", "publicKeyData"}, this, object);
    }
}
