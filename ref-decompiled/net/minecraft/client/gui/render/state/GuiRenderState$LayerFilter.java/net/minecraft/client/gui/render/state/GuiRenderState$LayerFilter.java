/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.render.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class GuiRenderState.LayerFilter
extends Enum<GuiRenderState.LayerFilter> {
    public static final /* enum */ GuiRenderState.LayerFilter ALL = new GuiRenderState.LayerFilter();
    public static final /* enum */ GuiRenderState.LayerFilter BEFORE_BLUR = new GuiRenderState.LayerFilter();
    public static final /* enum */ GuiRenderState.LayerFilter AFTER_BLUR = new GuiRenderState.LayerFilter();
    private static final /* synthetic */ GuiRenderState.LayerFilter[] field_60318;

    public static GuiRenderState.LayerFilter[] values() {
        return (GuiRenderState.LayerFilter[])field_60318.clone();
    }

    public static GuiRenderState.LayerFilter valueOf(String string) {
        return Enum.valueOf(GuiRenderState.LayerFilter.class, string);
    }

    private static /* synthetic */ GuiRenderState.LayerFilter[] method_71300() {
        return new GuiRenderState.LayerFilter[]{ALL, BEFORE_BLUR, AFTER_BLUR};
    }

    static {
        field_60318 = GuiRenderState.LayerFilter.method_71300();
    }
}
