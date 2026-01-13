/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.report.UsernameReportScreen
 *  net.minecraft.client.session.report.AbuseReport
 *  net.minecraft.client.session.report.AbuseReportContext
 *  net.minecraft.client.session.report.UsernameAbuseReport
 */
package net.minecraft.client.session.report;

import java.time.Instant;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.report.UsernameReportScreen;
import net.minecraft.client.session.report.AbuseReport;
import net.minecraft.client.session.report.AbuseReportContext;

@Environment(value=EnvType.CLIENT)
public class UsernameAbuseReport
extends AbuseReport {
    private final String username;

    UsernameAbuseReport(UUID reportId, Instant currentTime, UUID reportedPlayerUuid, String username) {
        super(reportId, currentTime, reportedPlayerUuid);
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public UsernameAbuseReport copy() {
        UsernameAbuseReport usernameAbuseReport = new UsernameAbuseReport(this.reportId, this.currentTime, this.reportedPlayerUuid, this.username);
        usernameAbuseReport.opinionComments = this.opinionComments;
        usernameAbuseReport.attested = this.attested;
        return usernameAbuseReport;
    }

    public Screen createReportScreen(Screen parent, AbuseReportContext context) {
        return new UsernameReportScreen(parent, context, this);
    }

    public /* synthetic */ AbuseReport copy() {
        return this.copy();
    }
}

