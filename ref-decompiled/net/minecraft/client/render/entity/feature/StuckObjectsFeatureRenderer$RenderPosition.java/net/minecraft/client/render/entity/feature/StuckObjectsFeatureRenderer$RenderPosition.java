/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class StuckObjectsFeatureRenderer.RenderPosition
extends Enum<StuckObjectsFeatureRenderer.RenderPosition> {
    public static final /* enum */ StuckObjectsFeatureRenderer.RenderPosition IN_CUBE = new StuckObjectsFeatureRenderer.RenderPosition();
    public static final /* enum */ StuckObjectsFeatureRenderer.RenderPosition ON_SURFACE = new StuckObjectsFeatureRenderer.RenderPosition();
    private static final /* synthetic */ StuckObjectsFeatureRenderer.RenderPosition[] field_53234;

    public static StuckObjectsFeatureRenderer.RenderPosition[] values() {
        return (StuckObjectsFeatureRenderer.RenderPosition[])field_53234.clone();
    }

    public static StuckObjectsFeatureRenderer.RenderPosition valueOf(String string) {
        return Enum.valueOf(StuckObjectsFeatureRenderer.RenderPosition.class, string);
    }

    private static /* synthetic */ StuckObjectsFeatureRenderer.RenderPosition[] method_62598() {
        return new StuckObjectsFeatureRenderer.RenderPosition[]{IN_CUBE, ON_SURFACE};
    }

    static {
        field_53234 = StuckObjectsFeatureRenderer.RenderPosition.method_62598();
    }
}
