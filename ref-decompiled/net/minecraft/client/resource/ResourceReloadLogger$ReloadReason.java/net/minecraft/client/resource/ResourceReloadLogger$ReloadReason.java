/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class ResourceReloadLogger.ReloadReason
extends Enum<ResourceReloadLogger.ReloadReason> {
    public static final /* enum */ ResourceReloadLogger.ReloadReason INITIAL = new ResourceReloadLogger.ReloadReason("initial");
    public static final /* enum */ ResourceReloadLogger.ReloadReason MANUAL = new ResourceReloadLogger.ReloadReason("manual");
    public static final /* enum */ ResourceReloadLogger.ReloadReason UNKNOWN = new ResourceReloadLogger.ReloadReason("unknown");
    final String name;
    private static final /* synthetic */ ResourceReloadLogger.ReloadReason[] field_33706;

    public static ResourceReloadLogger.ReloadReason[] values() {
        return (ResourceReloadLogger.ReloadReason[])field_33706.clone();
    }

    public static ResourceReloadLogger.ReloadReason valueOf(String string) {
        return Enum.valueOf(ResourceReloadLogger.ReloadReason.class, string);
    }

    private ResourceReloadLogger.ReloadReason(String name) {
        this.name = name;
    }

    private static /* synthetic */ ResourceReloadLogger.ReloadReason[] method_36867() {
        return new ResourceReloadLogger.ReloadReason[]{INITIAL, MANUAL, UNKNOWN};
    }

    static {
        field_33706 = ResourceReloadLogger.ReloadReason.method_36867();
    }
}
