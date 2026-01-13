/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.TextureTransform;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public static final class TextureTransform.OffsetTexturing
extends TextureTransform {
    public TextureTransform.OffsetTexturing(float du, float dv) {
        super("offset_texturing", () -> new Matrix4f().translation(du, dv, 0.0f));
    }
}
