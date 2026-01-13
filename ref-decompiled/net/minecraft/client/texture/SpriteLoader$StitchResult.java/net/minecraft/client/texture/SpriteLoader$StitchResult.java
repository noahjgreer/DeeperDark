/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.renderer.v1.sprite.FabricStitchResult
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.texture;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.sprite.FabricStitchResult;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record SpriteLoader.StitchResult(int width, int height, int mipLevel, Sprite missing, Map<Identifier, Sprite> sprites, CompletableFuture<Void> readyForUpload) implements FabricStitchResult
{
    public @Nullable Sprite getSprite(Identifier id) {
        return this.sprites.get(id);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SpriteLoader.StitchResult.class, "width;height;mipLevel;missing;regions;readyForUpload", "width", "height", "mipLevel", "missing", "sprites", "readyForUpload"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SpriteLoader.StitchResult.class, "width;height;mipLevel;missing;regions;readyForUpload", "width", "height", "mipLevel", "missing", "sprites", "readyForUpload"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SpriteLoader.StitchResult.class, "width;height;mipLevel;missing;regions;readyForUpload", "width", "height", "mipLevel", "missing", "sprites", "readyForUpload"}, this, object);
    }
}
