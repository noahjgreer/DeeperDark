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

@Environment(value=EnvType.CLIENT)
public static final class TextRenderer.TextLayerType
extends Enum<TextRenderer.TextLayerType> {
    public static final /* enum */ TextRenderer.TextLayerType NORMAL = new TextRenderer.TextLayerType();
    public static final /* enum */ TextRenderer.TextLayerType SEE_THROUGH = new TextRenderer.TextLayerType();
    public static final /* enum */ TextRenderer.TextLayerType POLYGON_OFFSET = new TextRenderer.TextLayerType();
    private static final /* synthetic */ TextRenderer.TextLayerType[] field_33996;

    public static TextRenderer.TextLayerType[] values() {
        return (TextRenderer.TextLayerType[])field_33996.clone();
    }

    public static TextRenderer.TextLayerType valueOf(String string) {
        return Enum.valueOf(TextRenderer.TextLayerType.class, string);
    }

    private static /* synthetic */ TextRenderer.TextLayerType[] method_37344() {
        return new TextRenderer.TextLayerType[]{NORMAL, SEE_THROUGH, POLYGON_OFFSET};
    }

    static {
        field_33996 = TextRenderer.TextLayerType.method_37344();
    }
}
