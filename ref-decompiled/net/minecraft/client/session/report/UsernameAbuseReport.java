package net.minecraft.client.session.report;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.authlib.minecraft.report.ReportedEntity;
import com.mojang.datafixers.util.Either;
import java.time.Instant;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.report.UsernameReportScreen;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class UsernameAbuseReport extends AbuseReport {
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

   // $FF: synthetic method
   public AbuseReport copy() {
      return this.copy();
   }

   @Environment(EnvType.CLIENT)
   public static class Builder extends AbuseReport.Builder {
      public Builder(UsernameAbuseReport report, AbuseReportLimits limits) {
         super(report, limits);
      }

      public Builder(UUID reportedPlayerUuid, String username, AbuseReportLimits limits) {
         super(new UsernameAbuseReport(UUID.randomUUID(), Instant.now(), reportedPlayerUuid, username), limits);
      }

      public boolean hasEnoughInfo() {
         return StringUtils.isNotEmpty(this.getOpinionComments());
      }

      @Nullable
      public AbuseReport.ValidationError validate() {
         return ((UsernameAbuseReport)this.report).opinionComments.length() > this.limits.maxOpinionCommentsLength() ? AbuseReport.ValidationError.COMMENTS_TOO_LONG : super.validate();
      }

      public Either build(AbuseReportContext context) {
         AbuseReport.ValidationError validationError = this.validate();
         if (validationError != null) {
            return Either.right(validationError);
         } else {
            ReportedEntity reportedEntity = new ReportedEntity(((UsernameAbuseReport)this.report).reportedPlayerUuid);
            com.mojang.authlib.minecraft.report.AbuseReport abuseReport = com.mojang.authlib.minecraft.report.AbuseReport.name(((UsernameAbuseReport)this.report).opinionComments, reportedEntity, ((UsernameAbuseReport)this.report).currentTime);
            return Either.left(new AbuseReport.ReportWithId(((UsernameAbuseReport)this.report).reportId, AbuseReportType.USERNAME, abuseReport));
         }
      }
   }
}
