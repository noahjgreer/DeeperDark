/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureStitcher;

@Environment(value=EnvType.CLIENT)
public static interface TextureStitcher.SpriteConsumer<T extends TextureStitcher.Stitchable> {
    public void load(T var1, int var2, int var3, int var4);
}
