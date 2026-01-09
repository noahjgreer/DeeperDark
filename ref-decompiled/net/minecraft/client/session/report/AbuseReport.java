package net.minecraft.client.session.report;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.datafixers.util.Either;
import java.time.Instant;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public abstract class AbuseReport {
   protected final UUID reportId;
   protected final Instant currentTime;
   protected final UUID reportedPlayerUuid;
   protected String opinionComments = "";
   @Nullable
   protected AbuseReportReason reason;
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

   public abstract Screen createReportScreen(Screen parent, AbuseReportContext context);

   @Environment(EnvType.CLIENT)
   public static record ValidationError(Text message) {
      public static final ValidationError NO_REASON = new ValidationError(Text.translatable("gui.abuseReport.send.no_reason"));
      public static final ValidationError NO_REPORTED_MESSAGES = new ValidationError(Text.translatable("gui.chatReport.send.no_reported_messages"));
      public static final ValidationError TOO_MANY_MESSAGES = new ValidationError(Text.translatable("gui.chatReport.send.too_many_messages"));
      public static final ValidationError COMMENTS_TOO_LONG = new ValidationError(Text.translatable("gui.abuseReport.send.comment_too_long"));
      public static final ValidationError NOT_ATTESTED = new ValidationError(Text.translatable("gui.abuseReport.send.not_attested"));

      public ValidationError(Text text) {
         this.message = text;
      }

      public Tooltip createTooltip() {
         return Tooltip.of(this.message);
      }

      public Text message() {
         return this.message;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record ReportWithId(UUID id, AbuseReportType reportType, com.mojang.authlib.minecraft.report.AbuseReport report) {
      public ReportWithId(UUID uUID, AbuseReportType abuseReportType, com.mojang.authlib.minecraft.report.AbuseReport abuseReport) {
         this.id = uUID;
         this.reportType = abuseReportType;
         this.report = abuseReport;
      }

      public UUID id() {
         return this.id;
      }

      public AbuseReportType reportType() {
         return this.reportType;
      }

      public com.mojang.authlib.minecraft.report.AbuseReport report() {
         return this.report;
      }
   }

   @Environment(EnvType.CLIENT)
   public abstract static class Builder {
      protected final AbuseReport report;
      protected final AbuseReportLimits limits;

      protected Builder(AbuseReport report, AbuseReportLimits limits) {
         this.report = report;
         this.limits = limits;
      }

      public AbuseReport getReport() {
         return this.report;
      }

      public UUID getReportedPlayerUuid() {
         return this.report.reportedPlayerUuid;
      }

      public String getOpinionComments() {
         return this.report.opinionComments;
      }

      public boolean isAttested() {
         return this.getReport().attested;
      }

      public void setOpinionComments(String opinionComments) {
         this.report.opinionComments = opinionComments;
      }

      @Nullable
      public AbuseReportReason getReason() {
         return this.report.reason;
      }

      public void setReason(AbuseReportReason reason) {
         this.report.reason = reason;
      }

      public void setAttested(boolean attested) {
         this.report.attested = attested;
      }

      public abstract boolean hasEnoughInfo();

      @Nullable
      public ValidationError validate() {
         return !this.getReport().attested ? AbuseReport.ValidationError.NOT_ATTESTED : null;
      }

      public abstract Either build(AbuseReportContext context);
   }
}
