/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.properties.Property
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.texture;

import com.mojang.authlib.properties.Property;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static final class PlayerSkinProvider.Key
extends Record {
    final UUID profileId;
    private final @Nullable Property packedTextures;

    PlayerSkinProvider.Key(UUID profileId, @Nullable Property packedTextures) {
        this.profileId = profileId;
        this.packedTextures = packedTextures;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{PlayerSkinProvider.Key.class, "profileId;packedTextures", "profileId", "packedTextures"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PlayerSkinProvider.Key.class, "profileId;packedTextures", "profileId", "packedTextures"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PlayerSkinProvider.Key.class, "profileId;packedTextures", "profileId", "packedTextures"}, this, object);
    }

    public UUID profileId() {
        return this.profileId;
    }

    public @Nullable Property packedTextures() {
        return this.packedTextures;
    }
}
