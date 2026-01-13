/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static final class CloudRenderer.ViewMode
extends Enum<CloudRenderer.ViewMode> {
    public static final /* enum */ CloudRenderer.ViewMode ABOVE_CLOUDS = new CloudRenderer.ViewMode();
    public static final /* enum */ CloudRenderer.ViewMode INSIDE_CLOUDS = new CloudRenderer.ViewMode();
    public static final /* enum */ CloudRenderer.ViewMode BELOW_CLOUDS = new CloudRenderer.ViewMode();
    private static final /* synthetic */ CloudRenderer.ViewMode[] field_53063;

    public static CloudRenderer.ViewMode[] values() {
        return (CloudRenderer.ViewMode[])field_53063.clone();
    }

    public static CloudRenderer.ViewMode valueOf(String string) {
        return Enum.valueOf(CloudRenderer.ViewMode.class, string);
    }

    private static /* synthetic */ CloudRenderer.ViewMode[] method_62182() {
        return new CloudRenderer.ViewMode[]{ABOVE_CLOUDS, INSIDE_CLOUDS, BELOW_CLOUDS};
    }

    static {
        field_53063 = CloudRenderer.ViewMode.method_62182();
    }
}
