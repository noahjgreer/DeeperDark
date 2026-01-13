/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class DrawContext.HoverType
extends Enum<DrawContext.HoverType> {
    public static final /* enum */ DrawContext.HoverType NONE = new DrawContext.HoverType(false, false);
    public static final /* enum */ DrawContext.HoverType TOOLTIP_ONLY = new DrawContext.HoverType(true, false);
    public static final /* enum */ DrawContext.HoverType TOOLTIP_AND_CURSOR = new DrawContext.HoverType(true, true);
    public final boolean tooltip;
    public final boolean cursor;
    private static final /* synthetic */ DrawContext.HoverType[] field_63855;

    public static DrawContext.HoverType[] values() {
        return (DrawContext.HoverType[])field_63855.clone();
    }

    public static DrawContext.HoverType valueOf(String string) {
        return Enum.valueOf(DrawContext.HoverType.class, string);
    }

    private DrawContext.HoverType(boolean tooltip, boolean cursor) {
        this.tooltip = tooltip;
        this.cursor = cursor;
    }

    public static DrawContext.HoverType fromTooltip(boolean tooltip) {
        return tooltip ? TOOLTIP_ONLY : NONE;
    }

    private static /* synthetic */ DrawContext.HoverType[] method_75789() {
        return new DrawContext.HoverType[]{NONE, TOOLTIP_ONLY, TOOLTIP_AND_CURSOR};
    }

    static {
        field_63855 = DrawContext.HoverType.method_75789();
    }
}
