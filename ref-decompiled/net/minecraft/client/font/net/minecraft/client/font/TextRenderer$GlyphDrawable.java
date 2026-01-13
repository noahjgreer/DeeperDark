/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.ScreenRect;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static interface TextRenderer.GlyphDrawable {
    public void draw(TextRenderer.GlyphDrawer var1);

    public @Nullable ScreenRect getScreenRect();
}
