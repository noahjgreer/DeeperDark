/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.platform.DestFactor
 *  com.mojang.blaze3d.platform.SourceFactor
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.util.annotation.DeobfuscateClass
 */
package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.annotation.DeobfuscateClass;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public record BlendFunction(SourceFactor sourceColor, DestFactor destColor, SourceFactor sourceAlpha, DestFactor destAlpha) {
    private final SourceFactor sourceColor;
    private final DestFactor destColor;
    private final SourceFactor sourceAlpha;
    private final DestFactor destAlpha;
    public static final BlendFunction LIGHTNING = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE);
    public static final BlendFunction GLINT = new BlendFunction(SourceFactor.SRC_COLOR, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
    public static final BlendFunction OVERLAY = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ONE, DestFactor.ZERO);
    public static final BlendFunction TRANSLUCENT = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
    public static final BlendFunction TRANSLUCENT_PREMULTIPLIED_ALPHA = new BlendFunction(SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
    public static final BlendFunction ADDITIVE = new BlendFunction(SourceFactor.ONE, DestFactor.ONE);
    public static final BlendFunction ENTITY_OUTLINE_BLIT = new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
    public static final BlendFunction INVERT = new BlendFunction(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);

    public BlendFunction(SourceFactor source, DestFactor dest) {
        this(source, dest, source, dest);
    }

    public BlendFunction(SourceFactor sourceColor, DestFactor destColor, SourceFactor sourceAlpha, DestFactor destAlpha) {
        this.sourceColor = sourceColor;
        this.destColor = destColor;
        this.sourceAlpha = sourceAlpha;
        this.destAlpha = destAlpha;
    }

    public SourceFactor sourceColor() {
        return this.sourceColor;
    }

    public DestFactor destColor() {
        return this.destColor;
    }

    public SourceFactor sourceAlpha() {
        return this.sourceAlpha;
    }

    public DestFactor destAlpha() {
        return this.destAlpha;
    }
}

