/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.fog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class FogRenderer.FogType
extends Enum<FogRenderer.FogType> {
    public static final /* enum */ FogRenderer.FogType NONE = new FogRenderer.FogType();
    public static final /* enum */ FogRenderer.FogType WORLD = new FogRenderer.FogType();
    private static final /* synthetic */ FogRenderer.FogType[] field_20947;

    public static FogRenderer.FogType[] values() {
        return (FogRenderer.FogType[])field_20947.clone();
    }

    public static FogRenderer.FogType valueOf(String string) {
        return Enum.valueOf(FogRenderer.FogType.class, string);
    }

    private static /* synthetic */ FogRenderer.FogType[] method_36914() {
        return new FogRenderer.FogType[]{NONE, WORLD};
    }

    static {
        field_20947 = FogRenderer.FogType.method_36914();
    }
}
