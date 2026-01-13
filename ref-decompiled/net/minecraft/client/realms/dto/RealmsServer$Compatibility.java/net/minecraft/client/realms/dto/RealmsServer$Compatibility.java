/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.dto;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class RealmsServer.Compatibility
extends Enum<RealmsServer.Compatibility> {
    public static final /* enum */ RealmsServer.Compatibility UNVERIFIABLE = new RealmsServer.Compatibility();
    public static final /* enum */ RealmsServer.Compatibility INCOMPATIBLE = new RealmsServer.Compatibility();
    public static final /* enum */ RealmsServer.Compatibility RELEASE_TYPE_INCOMPATIBLE = new RealmsServer.Compatibility();
    public static final /* enum */ RealmsServer.Compatibility NEEDS_DOWNGRADE = new RealmsServer.Compatibility();
    public static final /* enum */ RealmsServer.Compatibility NEEDS_UPGRADE = new RealmsServer.Compatibility();
    public static final /* enum */ RealmsServer.Compatibility COMPATIBLE = new RealmsServer.Compatibility();
    private static final /* synthetic */ RealmsServer.Compatibility[] field_46702;

    public static RealmsServer.Compatibility[] values() {
        return (RealmsServer.Compatibility[])field_46702.clone();
    }

    public static RealmsServer.Compatibility valueOf(String string) {
        return Enum.valueOf(RealmsServer.Compatibility.class, string);
    }

    public boolean isCompatible() {
        return this == COMPATIBLE;
    }

    public boolean needsUpgrade() {
        return this == NEEDS_UPGRADE;
    }

    public boolean needsDowngrade() {
        return this == NEEDS_DOWNGRADE;
    }

    private static /* synthetic */ RealmsServer.Compatibility[] method_54368() {
        return new RealmsServer.Compatibility[]{UNVERIFIABLE, INCOMPATIBLE, RELEASE_TYPE_INCOMPATIBLE, NEEDS_DOWNGRADE, NEEDS_UPGRADE, COMPATIBLE};
    }

    static {
        field_46702 = RealmsServer.Compatibility.method_54368();
    }
}
