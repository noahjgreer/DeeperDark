/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.session.report.AbuseReport
 *  net.minecraft.client.session.report.AbuseReportContext
 *  net.minecraft.client.session.report.AbuseReportReason
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.session.report;

import java.time.Instant;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.AbuseReportReason;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class AbuseReport {
    protected final UUID reportId;
    protected final Instant currentTime;
    protected final UUID reportedPlayerUuid;
    protected String opinionComments = "";
    protected @Nullable AbuseReportReason reason;
    protected boolean attested;

    public AbuseReport(UUID reportId, Instant currentTime, UUID reportedPlayerUuid) {
        this.reportId = reportId;
        this.currentTime = currentTime;
        this.reportedPlayerUuid = reportedPlayerUuid;
    }

    public boolean playerUuidEquals(UUID uuid) {
        return uuid.equals(this.reportedPlayerUuid);
    }

    public abstract AbuseReport copy();

    public abstract Screen createReportScreen(Screen var1, AbuseReportContext var2);
}

