/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class InactivityFpsLimiter.LimitReason
extends Enum<InactivityFpsLimiter.LimitReason> {
    public static final /* enum */ InactivityFpsLimiter.LimitReason NONE = new InactivityFpsLimiter.LimitReason();
    public static final /* enum */ InactivityFpsLimiter.LimitReason WINDOW_ICONIFIED = new InactivityFpsLimiter.LimitReason();
    public static final /* enum */ InactivityFpsLimiter.LimitReason LONG_AFK = new InactivityFpsLimiter.LimitReason();
    public static final /* enum */ InactivityFpsLimiter.LimitReason SHORT_AFK = new InactivityFpsLimiter.LimitReason();
    public static final /* enum */ InactivityFpsLimiter.LimitReason OUT_OF_LEVEL_MENU = new InactivityFpsLimiter.LimitReason();
    private static final /* synthetic */ InactivityFpsLimiter.LimitReason[] field_55848;

    public static InactivityFpsLimiter.LimitReason[] values() {
        return (InactivityFpsLimiter.LimitReason[])field_55848.clone();
    }

    public static InactivityFpsLimiter.LimitReason valueOf(String string) {
        return Enum.valueOf(InactivityFpsLimiter.LimitReason.class, string);
    }

    private static /* synthetic */ InactivityFpsLimiter.LimitReason[] method_66516() {
        return new InactivityFpsLimiter.LimitReason[]{NONE, WINDOW_ICONIFIED, LONG_AFK, SHORT_AFK, OUT_OF_LEVEL_MENU};
    }

    static {
        field_55848 = InactivityFpsLimiter.LimitReason.method_66516();
    }
}
