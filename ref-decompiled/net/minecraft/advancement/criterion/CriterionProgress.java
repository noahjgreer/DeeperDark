package net.minecraft.advancement.criterion;

import java.time.Instant;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

public class CriterionProgress {
   @Nullable
   private Instant obtainedTime;

   public CriterionProgress() {
   }

   public CriterionProgress(Instant obtainedTime) {
      this.obtainedTime = obtainedTime;
   }

   public boolean isObtained() {
      return this.obtainedTime != null;
   }

   public void obtain() {
      this.obtainedTime = Instant.now();
   }

   public void reset() {
      this.obtainedTime = null;
   }

   @Nullable
   public Instant getObtainedTime() {
      return this.obtainedTime;
   }

   public String toString() {
      Object var10000 = this.obtainedTime == null ? "false" : this.obtainedTime;
      return "CriterionProgress{obtained=" + String.valueOf(var10000) + "}";
   }

   public void toPacket(PacketByteBuf buf) {
      buf.writeNullable(this.obtainedTime, PacketByteBuf::writeInstant);
   }

   public static CriterionProgress fromPacket(PacketByteBuf buf) {
      CriterionProgress criterionProgress = new CriterionProgress();
      criterionProgress.obtainedTime = (Instant)buf.readNullable(PacketByteBuf::readInstant);
      return criterionProgress;
   }
}
