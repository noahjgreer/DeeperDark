/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.TextureTransform
 *  net.minecraft.util.Util
 *  org.joml.Matrix4f
 */
package net.minecraft.client.render;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;
import org.joml.Matrix4f;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class TextureTransform {
    public static final double field_64067 = 8.0;
    private final String name;
    private final Supplier<Matrix4f> transformSupplier;
    public static final TextureTransform DEFAULT_TEXTURING = new TextureTransform("default_texturing", Matrix4f::new);
    public static final TextureTransform GLINT_TEXTURING = new TextureTransform("glint_texturing", () -> TextureTransform.getGlintTransformation((float)8.0f));
    public static final TextureTransform ENTITY_GLINT_TEXTURING = new TextureTransform("entity_glint_texturing", () -> TextureTransform.getGlintTransformation((float)0.5f));
    public static final TextureTransform ARMOR_ENTITY_GLINT_TEXTURING = new TextureTransform("armor_entity_glint_texturing", () -> TextureTransform.getGlintTransformation((float)0.16f));

    public TextureTransform(String name, Supplier<Matrix4f> transformSupplier) {
        this.name = name;
        this.transformSupplier = transformSupplier;
    }

    public Matrix4f getTransformSupplier() {
        return (Matrix4f)this.transformSupplier.get();
    }

    public String toString() {
        return "TexturingStateShard[" + this.name + "]";
    }

    private static Matrix4f getGlintTransformation(float scale) {
        long l = (long)((double)Util.getMeasuringTimeMs() * (Double)MinecraftClient.getInstance().options.getGlintSpeed().getValue() * 8.0);
        float f = (float)(l % 110000L) / 110000.0f;
        float g = (float)(l % 30000L) / 30000.0f;
        Matrix4f matrix4f = new Matrix4f().translation(-f, g, 0.0f);
        matrix4f.rotateZ(0.17453292f).scale(scale);
        return matrix4f;
    }
}

