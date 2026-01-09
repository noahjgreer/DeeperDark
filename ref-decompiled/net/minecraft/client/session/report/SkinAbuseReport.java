package net.minecraft.client.session.report;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.minecraft.report.ReportedEntity;
import com.mojang.datafixers.util.Either;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.report.SkinReportScreen;
import net.minecraft.client.util.SkinTextures;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class SkinAbuseReport extends AbuseReport {
   final Supplier skinSupplier;

   SkinAbuseReport(UUID reportId, Instant currentTime, UUID reportedPlayerUuid, Supplier skinSupplier) {
      super(reportId, currentTime, reportedPlayerUuid);
      this.skinSupplier = skinSupplier;
   }

   public Supplier getSkinSupplier() {
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

   // $FF: synthetic method
   public AbuseReport copy() {
      return this.copy();
   }

   @Environment(EnvType.CLIENT)
   public static class Builder extends AbuseReport.Builder {
      public Builder(SkinAbuseReport report, AbuseReportLimits limits) {
         super(report, limits);
      }

      public Builder(UUID reportedPlayerUuid, Supplier skinSupplier, AbuseReportLimits limits) {
         super(new SkinAbuseReport(UUID.randomUUID(), Instant.now(), reportedPlayerUuid, skinSupplier), limits);
      }

      public boolean hasEnoughInfo() {
         return StringUtils.isNotEmpty(this.getOpinionComments()) || this.getReason() != null;
      }

      @Nullable
      public AbuseReport.ValidationError validate() {
         if (((SkinAbuseReport)this.report).reason == null) {
            return AbuseReport.ValidationError.NO_REASON;
         } else {
            return ((SkinAbuseReport)this.report).opinionComments.length() > this.limits.maxOpinionCommentsLength() ? AbuseReport.ValidationError.COMMENTS_TOO_LONG : super.validate();
         }
      }

      public Either build(AbuseReportContext context) {
         AbuseReport.ValidationError validationError = this.validate();
         if (validationError != null) {
            return Either.right(validationError);
         } else {
            String string = ((AbuseReportReason)Objects.requireNonNull(((SkinAbuseReport)this.report).reason)).getId();
            ReportedEntity reportedEntity = new ReportedEntity(((SkinAbuseReport)this.report).reportedPlayerUuid);
            SkinTextures skinTextures = (SkinTextures)((SkinAbuseReport)this.report).skinSupplier.get();
            String string2 = skinTextures.textureUrl();
            com.mojang.authlib.minecraft.report.AbuseReport abuseReport = com.mojang.authlib.minecraft.report.AbuseReport.skin(((SkinAbuseReport)this.report).opinionComments, string, string2, reportedEntity, ((SkinAbuseReport)this.report).currentTime);
            return Either.left(new AbuseReport.ReportWithId(((SkinAbuseReport)this.report).reportId, AbuseReportType.SKIN, abuseReport));
         }
      }
   }
}
