/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.util;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public static final class RealmsTextureManager.RealmsTexture
extends Record {
    private final String image;
    final Identifier textureId;

    public RealmsTextureManager.RealmsTexture(String image, Identifier textureId) {
        this.image = image;
        this.textureId = textureId;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{RealmsTextureManager.RealmsTexture.class, "image;textureId", "image", "textureId"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RealmsTextureManager.RealmsTexture.class, "image;textureId", "image", "textureId"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RealmsTextureManager.RealmsTexture.class, "image;textureId", "image", "textureId"}, this, object);
    }

    public String image() {
        return this.image;
    }

    public Identifier textureId() {
        return this.textureId;
    }
}
