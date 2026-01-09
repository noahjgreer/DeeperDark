package net.minecraft.network;

import java.util.Optional;
import net.minecraft.text.Text;

public record DisconnectionInfo(Text reason, Optional report, Optional bugReportLink) {
   public DisconnectionInfo(Text reason) {
      this(reason, Optional.empty(), Optional.empty());
   }

   public DisconnectionInfo(Text text, Optional optional, Optional optional2) {
      this.reason = text;
      this.report = optional;
      this.bugReportLink = optional2;
   }

   public Text reason() {
      return this.reason;
   }

   public Optional report() {
      return this.report;
   }

   public Optional bugReportLink() {
      return this.bugReportLink;
   }
}
