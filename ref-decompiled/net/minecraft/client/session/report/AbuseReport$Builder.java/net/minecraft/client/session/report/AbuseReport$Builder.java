/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.report.AbuseReportLimits
 *  com.mojang.datafixers.util.Either
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.session.report;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.datafixers.util.Either;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.report.AbuseReport;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.AbuseReportReason;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static abstract class AbuseReport.Builder<R extends AbuseReport> {
    protected final R report;
    protected final AbuseReportLimits limits;

    protected AbuseReport.Builder(R report, AbuseReportLimits limits) {
        this.report = report;
        this.limits = limits;
    }

    public R getReport() {
        return this.report;
    }

    public UUID getReportedPlayerUuid() {
        return ((AbuseReport)this.report).reportedPlayerUuid;
    }

    public String getOpinionComments() {
        return ((AbuseReport)this.report).opinionComments;
    }

    public boolean isAttested() {
        return ((AbuseReport)this.getReport()).attested;
    }

    public void setOpinionComments(String opinionComments) {
        ((AbuseReport)this.report).opinionComments = opinionComments;
    }

    public @Nullable AbuseReportReason getReason() {
        return ((AbuseReport)this.report).reason;
    }

    public void setReason(AbuseReportReason reason) {
        ((AbuseReport)this.report).reason = reason;
    }

    public void setAttested(boolean attested) {
        ((AbuseReport)this.report).attested = attested;
    }

    public abstract boolean hasEnoughInfo();

    public @Nullable AbuseReport.ValidationError validate() {
        if (!((AbuseReport)this.getReport()).attested) {
            return AbuseReport.ValidationError.NOT_ATTESTED;
        }
        return null;
    }

    public abstract Either<AbuseReport.ReportWithId, AbuseReport.ValidationError> build(AbuseReportContext var1);
}
