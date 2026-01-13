/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.network.encryption.PlayerKeyPair;

@Environment(value=EnvType.CLIENT)
class ProfileKeys.1
implements ProfileKeys {
    ProfileKeys.1() {
    }

    @Override
    public CompletableFuture<Optional<PlayerKeyPair>> fetchKeyPair() {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public boolean isExpired() {
        return false;
    }
}
