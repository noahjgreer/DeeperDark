/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.session.report.AbuseReportType
 */
package net.minecraft.client.session.report;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class AbuseReportType
extends Enum<AbuseReportType> {
    public static final /* enum */ AbuseReportType CHAT = new AbuseReportType("CHAT", 0, "chat");
    public static final /* enum */ AbuseReportType SKIN = new AbuseReportType("SKIN", 1, "skin");
    public static final /* enum */ AbuseReportType USERNAME = new AbuseReportType("USERNAME", 2, "username");
    private final String name;
    private static final /* synthetic */ AbuseReportType[] field_46068;

    public static AbuseReportType[] values() {
        return (AbuseReportType[])field_46068.clone();
    }

    public static AbuseReportType valueOf(String string) {
        return Enum.valueOf(AbuseReportType.class, string);
    }

    private AbuseReportType(String name) {
        this.name = name.toUpperCase(Locale.ROOT);
    }

    public String getName() {
        return this.name;
    }

    private static /* synthetic */ AbuseReportType[] method_53617() {
        return new AbuseReportType[]{CHAT, SKIN, USERNAME};
    }

    static {
        field_46068 = AbuseReportType.method_53617();
    }
}

