/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.UserApiService
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.session.ProfileKeys
 *  net.minecraft.client.session.ProfileKeysImpl
 *  net.minecraft.client.session.Session
 *  net.minecraft.network.encryption.PlayerKeyPair
 */
package net.minecraft.client.session;

import com.mojang.authlib.minecraft.UserApiService;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.ProfileKeysImpl;
import net.minecraft.client.session.Session;
import net.minecraft.network.encryption.PlayerKeyPair;

@Environment(value=EnvType.CLIENT)
public interface ProfileKeys {
    public static final ProfileKeys MISSING = new /* Unavailable Anonymous Inner Class!! */;

    public static ProfileKeys create(UserApiService userApiService, Session session, Path root) {
        return new ProfileKeysImpl(userApiService, session.getUuidOrNull(), root);
    }

    public CompletableFuture<Optional<PlayerKeyPair>> fetchKeyPair();

    public boolean isExpired();
}

