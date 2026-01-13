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
import net.minecraft.text.Style;

@Environment(value=EnvType.CLIENT)
public interface GlyphRect {
    public Style style();

    public float getLeft();

    public float getTop();

    public float getRight();

    public float getBottom();
}
