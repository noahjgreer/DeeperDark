/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.UserApiService
 *  com.mojang.authlib.minecraft.report.AbuseReport
 *  com.mojang.authlib.minecraft.report.AbuseReportLimits
 *  com.mojang.datafixers.util.Unit
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.session.report.AbuseReportSender
 *  net.minecraft.client.session.report.AbuseReportSender$Impl
 *  net.minecraft.client.session.report.AbuseReportType
 *  net.minecraft.client.session.report.ReporterEnvironment
 */
package net.minecraft.client.session.report;

import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.datafixers.util.Unit;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.report.AbuseReportSender;
import net.minecraft.client.session.report.AbuseReportType;
import net.minecraft.client.session.report.ReporterEnvironment;

@Environment(value=EnvType.CLIENT)
public interface AbuseReportSender {
    public static AbuseReportSender create(ReporterEnvironment environment, UserApiService userApiService) {
        return new Impl(environment, userApiService);
    }

    public CompletableFuture<Unit> send(UUID var1, AbuseReportType var2, AbuseReport var3);

    public boolean canSendReports();

    default public AbuseReportLimits getLimits() {
        return AbuseReportLimits.DEFAULTS;
    }
}

