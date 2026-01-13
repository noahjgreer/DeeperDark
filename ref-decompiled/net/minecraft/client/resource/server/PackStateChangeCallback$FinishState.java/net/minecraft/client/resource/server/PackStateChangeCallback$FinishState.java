/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class PackStateChangeCallback.FinishState
extends Enum<PackStateChangeCallback.FinishState> {
    public static final /* enum */ PackStateChangeCallback.FinishState DECLINED = new PackStateChangeCallback.FinishState();
    public static final /* enum */ PackStateChangeCallback.FinishState APPLIED = new PackStateChangeCallback.FinishState();
    public static final /* enum */ PackStateChangeCallback.FinishState DISCARDED = new PackStateChangeCallback.FinishState();
    public static final /* enum */ PackStateChangeCallback.FinishState DOWNLOAD_FAILED = new PackStateChangeCallback.FinishState();
    public static final /* enum */ PackStateChangeCallback.FinishState ACTIVATION_FAILED = new PackStateChangeCallback.FinishState();
    private static final /* synthetic */ PackStateChangeCallback.FinishState[] field_47628;

    public static PackStateChangeCallback.FinishState[] values() {
        return (PackStateChangeCallback.FinishState[])field_47628.clone();
    }

    public static PackStateChangeCallback.FinishState valueOf(String string) {
        return Enum.valueOf(PackStateChangeCallback.FinishState.class, string);
    }

    private static /* synthetic */ PackStateChangeCallback.FinishState[] method_55548() {
        return new PackStateChangeCallback.FinishState[]{DECLINED, APPLIED, DISCARDED, DOWNLOAD_FAILED, ACTIVATION_FAILED};
    }

    static {
        field_47628 = PackStateChangeCallback.FinishState.method_55548();
    }
}
