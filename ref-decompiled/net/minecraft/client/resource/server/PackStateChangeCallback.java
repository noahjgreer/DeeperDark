package net.minecraft.client.resource.server;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface PackStateChangeCallback {
   void onStateChanged(UUID id, State state);

   void onFinish(UUID id, FinishState state);

   @Environment(EnvType.CLIENT)
   public static enum FinishState {
      DECLINED,
      APPLIED,
      DISCARDED,
      DOWNLOAD_FAILED,
      ACTIVATION_FAILED;

      // $FF: synthetic method
      private static FinishState[] method_55548() {
         return new FinishState[]{DECLINED, APPLIED, DISCARDED, DOWNLOAD_FAILED, ACTIVATION_FAILED};
      }
   }

   @Environment(EnvType.CLIENT)
   public static enum State {
      ACCEPTED,
      DOWNLOADED;

      // $FF: synthetic method
      private static State[] method_55621() {
         return new State[]{ACCEPTED, DOWNLOADED};
      }
   }
}
