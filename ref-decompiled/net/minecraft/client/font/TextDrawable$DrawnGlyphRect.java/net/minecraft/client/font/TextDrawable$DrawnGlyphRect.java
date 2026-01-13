/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.GlyphRect;
import net.minecraft.client.font.TextDrawable;

@Environment(value=EnvType.CLIENT)
public static interface TextDrawable.DrawnGlyphRect
extends GlyphRect,
TextDrawable {
    @Override
    default public float getLeft() {
        return this.getEffectiveMinX();
    }

    @Override
    default public float getTop() {
        return this.getEffectiveMinY();
    }

    @Override
    default public float getRight() {
        return this.getEffectiveMaxX();
    }

    @Override
    default public float getBottom() {
        return this.getEffectiveMaxY();
    }
}
