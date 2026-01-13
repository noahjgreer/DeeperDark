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
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.report.AbuseReport;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.AbuseReportType;
import net.minecraft.client.session.report.SkinAbuseReport;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.AssetInfo;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class SkinAbuseReport.Builder
extends AbuseReport.Builder<SkinAbuseReport> {
    public SkinAbuseReport.Builder(SkinAbuseReport report, AbuseReportLimits limits) {
        super(report, limits);
    }

    public SkinAbuseReport.Builder(UUID reportedPlayerUuid, Supplier<SkinTextures> skinSupplier, AbuseReportLimits limits) {
        super(new SkinAbuseReport(UUID.randomUUID(), Instant.now(), reportedPlayerUuid, skinSupplier), limits);
    }

    @Override
    public boolean hasEnoughInfo() {
        return StringUtils.isNotEmpty((CharSequence)this.getOpinionComments()) || this.getReason() != null;
    }

    @Override
    public  @Nullable AbuseReport.ValidationError validate() {
        if (((SkinAbuseReport)this.report).reason == null) {
            return AbuseReport.ValidationError.NO_REASON;
        }
        if (((SkinAbuseReport)this.report).opinionComments.length() > this.limits.maxOpinionCommentsLength()) {
            return AbuseReport.ValidationError.COMMENTS_TOO_LONG;
        }
        return super.validate();
    }

    @Override
    public Either<AbuseReport.ReportWithId, AbuseReport.ValidationError> build(AbuseReportContext context) {
        String string;
        AbuseReport.ValidationError validationError = this.validate();
        if (validationError != null) {
            return Either.right((Object)validationError);
        }
        String string2 = Objects.requireNonNull(((SkinAbuseReport)this.report).reason).getId();
        ReportedEntity reportedEntity = new ReportedEntity(((SkinAbuseReport)this.report).reportedPlayerUuid);
        SkinTextures skinTextures = ((SkinAbuseReport)this.report).skinSupplier.get();
        AssetInfo.TextureAsset textureAsset = skinTextures.body();
        if (textureAsset instanceof AssetInfo.SkinAssetInfo) {
            AssetInfo.SkinAssetInfo skinAssetInfo = (AssetInfo.SkinAssetInfo)textureAsset;
            string = skinAssetInfo.url();
        } else {
            string = null;
        }
        String string22 = string;
        AbuseReport abuseReport = AbuseReport.skin((String)((SkinAbuseReport)this.report).opinionComments, (String)string2, (String)string22, (ReportedEntity)reportedEntity, (Instant)((SkinAbuseReport)this.report).currentTime);
        return Either.left((Object)new AbuseReport.ReportWithId(((SkinAbuseReport)this.report).reportId, AbuseReportType.SKIN, abuseReport));
    }
}
