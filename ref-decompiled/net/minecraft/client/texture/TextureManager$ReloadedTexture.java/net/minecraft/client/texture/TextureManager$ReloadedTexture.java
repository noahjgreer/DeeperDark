/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.ReloadableTexture;
import net.minecraft.client.texture.TextureContents;

@Environment(value=EnvType.CLIENT)
static final class TextureManager.ReloadedTexture
extends Record {
    final ReloadableTexture texture;
    final CompletableFuture<TextureContents> newContents;

    TextureManager.ReloadedTexture(ReloadableTexture texture, CompletableFuture<TextureContents> newContents) {
        this.texture = texture;
        this.newContents = newContents;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{TextureManager.ReloadedTexture.class, "texture;newContents", "texture", "newContents"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TextureManager.ReloadedTexture.class, "texture;newContents", "texture", "newContents"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TextureManager.ReloadedTexture.class, "texture;newContents", "texture", "newContents"}, this, object);
    }

    public ReloadableTexture texture() {
        return this.texture;
    }

    public CompletableFuture<TextureContents> newContents() {
        return this.newContents;
    }
}
