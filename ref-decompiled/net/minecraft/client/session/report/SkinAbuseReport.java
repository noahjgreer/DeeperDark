/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.report.SkinReportScreen
 *  net.minecraft.client.session.report.AbuseReport
 *  net.minecraft.client.session.report.AbuseReportContext
 *  net.minecraft.client.session.report.SkinAbuseReport
 *  net.minecraft.entity.player.SkinTextures
 */
package net.minecraft.client.session.report;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.report.SkinReportScreen;
import net.minecraft.client.session.report.AbuseReport;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.entity.player.SkinTextures;

@Environment(value=EnvType.CLIENT)
public class SkinAbuseReport
extends AbuseReport {
    final Supplier<SkinTextures> skinSupplier;

    SkinAbuseReport(UUID reportId, Instant currentTime, UUID reportedPlayerUuid, Supplier<SkinTextures> skinSupplier) {
        super(reportId, currentTime, reportedPlayerUuid);
        this.skinSupplier = skinSupplier;
    }

    public Supplier<SkinTextures> getSkinSupplier() {
        return this.skinSupplier;
    }

    public SkinAbuseReport copy() {
        SkinAbuseReport skinAbuseReport = new SkinAbuseReport(this.reportId, this.currentTime, this.reportedPlayerUuid, this.skinSupplier);
        skinAbuseReport.opinionComments = this.opinionComments;
        skinAbuseReport.reason = this.reason;
        skinAbuseReport.attested = this.attested;
        return skinAbuseReport;
    }

    public Screen createReportScreen(Screen parent, AbuseReportContext context) {
        return new SkinReportScreen(parent, context, this);
    }

    public /* synthetic */ AbuseReport copy() {
        return this.copy();
    }
}

