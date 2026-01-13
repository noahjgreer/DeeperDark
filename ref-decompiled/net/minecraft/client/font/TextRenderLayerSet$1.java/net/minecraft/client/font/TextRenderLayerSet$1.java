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
import net.minecraft.client.font.TextRenderer;

@Environment(value=EnvType.CLIENT)
static class TextRenderLayerSet.1 {
    static final /* synthetic */ int[] field_34000;

    static {
        field_34000 = new int[TextRenderer.TextLayerType.values().length];
        try {
            TextRenderLayerSet.1.field_34000[TextRenderer.TextLayerType.NORMAL.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TextRenderLayerSet.1.field_34000[TextRenderer.TextLayerType.SEE_THROUGH.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            TextRenderLayerSet.1.field_34000[TextRenderer.TextLayerType.POLYGON_OFFSET.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
