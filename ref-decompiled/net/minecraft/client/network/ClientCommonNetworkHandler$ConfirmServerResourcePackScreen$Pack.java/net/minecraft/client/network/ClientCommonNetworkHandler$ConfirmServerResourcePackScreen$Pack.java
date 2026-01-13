/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.net.URL;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static final class ClientCommonNetworkHandler.ConfirmServerResourcePackScreen.Pack
extends Record {
    final UUID id;
    final URL url;
    final String hash;

    ClientCommonNetworkHandler.ConfirmServerResourcePackScreen.Pack(UUID id, URL url, String hash) {
        this.id = id;
        this.url = url;
        this.hash = hash;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ClientCommonNetworkHandler.ConfirmServerResourcePackScreen.Pack.class, "id;url;hash", "id", "url", "hash"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ClientCommonNetworkHandler.ConfirmServerResourcePackScreen.Pack.class, "id;url;hash", "id", "url", "hash"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ClientCommonNetworkHandler.ConfirmServerResourcePackScreen.Pack.class, "id;url;hash", "id", "url", "hash"}, this, object);
    }

    public UUID id() {
        return this.id;
    }

    public URL url() {
        return this.url;
    }

    public String hash() {
        return this.hash;
    }
}
