/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.report;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.report.AbuseReportType;

@Environment(value=EnvType.CLIENT)
static class AbuseReportReason.1 {
    static final /* synthetic */ int[] field_53037;

    static {
        field_53037 = new int[AbuseReportType.values().length];
        try {
            AbuseReportReason.1.field_53037[AbuseReportType.CHAT.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            AbuseReportReason.1.field_53037[AbuseReportType.SKIN.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
