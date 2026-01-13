/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.report.AbuseReport
 *  com.mojang.authlib.minecraft.report.AbuseReportLimits
 *  com.mojang.authlib.minecraft.report.ReportedEntity
 *  com.mojang.datafixers.util.Either
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.session.report;

import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.minecraft.report.ReportedEntity;
import com.mojang.datafixers.util.Either;
import java.time.Instant;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.report.AbuseReport;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.AbuseReportType;
import net.minecraft.client.session.report.UsernameAbuseReport;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class UsernameAbuseReport.Builder
extends AbuseReport.Builder<UsernameAbuseReport> {
    public UsernameAbuseReport.Builder(UsernameAbuseReport report, AbuseReportLimits limits) {
        super(report, limits);
    }

    public UsernameAbuseReport.Builder(UUID reportedPlayerUuid, String username, AbuseReportLimits limits) {
        super(new UsernameAbuseReport(UUID.randomUUID(), Instant.now(), reportedPlayerUuid, username), limits);
    }

    @Override
    public boolean hasEnoughInfo() {
        return StringUtils.isNotEmpty((CharSequence)this.getOpinionComments());
    }

    @Override
    public  @Nullable AbuseReport.ValidationError validate() {
        if (((UsernameAbuseReport)this.report).opinionComments.length() > this.limits.maxOpinionCommentsLength()) {
            return AbuseReport.ValidationError.COMMENTS_TOO_LONG;
        }
        return super.validate();
    }

    @Override
    public Either<AbuseReport.ReportWithId, AbuseReport.ValidationError> build(AbuseReportContext context) {
        AbuseReport.ValidationError validationError = this.validate();
        if (validationError != null) {
            return Either.right((Object)validationError);
        }
        ReportedEntity reportedEntity = new ReportedEntity(((UsernameAbuseReport)this.report).reportedPlayerUuid);
        AbuseReport abuseReport = AbuseReport.name((String)((UsernameAbuseReport)this.report).opinionComments, (ReportedEntity)reportedEntity, (Instant)((UsernameAbuseReport)this.report).currentTime);
        return Either.left((Object)new AbuseReport.ReportWithId(((UsernameAbuseReport)this.report).reportId, AbuseReportType.USERNAME, abuseReport));
    }
}
