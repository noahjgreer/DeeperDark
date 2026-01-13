/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static final class RealmsMainScreen.LoadStatus
extends Enum<RealmsMainScreen.LoadStatus> {
    public static final /* enum */ RealmsMainScreen.LoadStatus LOADING = new RealmsMainScreen.LoadStatus();
    public static final /* enum */ RealmsMainScreen.LoadStatus NO_REALMS = new RealmsMainScreen.LoadStatus();
    public static final /* enum */ RealmsMainScreen.LoadStatus LIST = new RealmsMainScreen.LoadStatus();
    private static final /* synthetic */ RealmsMainScreen.LoadStatus[] field_45226;

    public static RealmsMainScreen.LoadStatus[] values() {
        return (RealmsMainScreen.LoadStatus[])field_45226.clone();
    }

    public static RealmsMainScreen.LoadStatus valueOf(String string) {
        return Enum.valueOf(RealmsMainScreen.LoadStatus.class, string);
    }

    private static /* synthetic */ RealmsMainScreen.LoadStatus[] method_52650() {
        return new RealmsMainScreen.LoadStatus[]{LOADING, NO_REALMS, LIST};
    }

    static {
        field_45226 = RealmsMainScreen.LoadStatus.method_52650();
    }
}
