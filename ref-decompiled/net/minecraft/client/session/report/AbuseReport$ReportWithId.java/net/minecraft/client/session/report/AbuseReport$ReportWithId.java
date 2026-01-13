/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.report.AbuseReport
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.report;

import com.mojang.authlib.minecraft.report.AbuseReport;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.report.AbuseReportType;

@Environment(value=EnvType.CLIENT)
public record AbuseReport.ReportWithId(UUID id, AbuseReportType reportType, AbuseReport report) {
}
