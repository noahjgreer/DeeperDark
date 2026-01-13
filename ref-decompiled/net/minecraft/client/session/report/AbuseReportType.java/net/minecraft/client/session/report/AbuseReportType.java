/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.report;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class AbuseReportType
extends Enum<AbuseReportType> {
    public static final /* enum */ AbuseReportType CHAT = new AbuseReportType("chat");
    public static final /* enum */ AbuseReportType SKIN = new AbuseReportType("skin");
    public static final /* enum */ AbuseReportType USERNAME = new AbuseReportType("username");
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
